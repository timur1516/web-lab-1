package ru.timur;

import com.fastcgi.FCGIInterface;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.logging.*;

public class Main {
    private static final String HTTP_RESPONSE = "Status: %s\nContent-Type: application/json\nContent-Length: %d\n\n%s";
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private static long startTime;

    public static void main(String[] args) throws IOException {
        var fcgiInterface = new FCGIInterface();

        FileHandler fileHandler = new FileHandler("CGI.log", true);
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);
        logger.setLevel(Level.ALL);

        while (fcgiInterface.FCGIaccept() >= 0) {
            startTime = System.currentTimeMillis();
            Properties request = FCGIInterface.request.params;

            String requestString = request.getProperty("QUERY_STRING");
            logger.info(String.format("Received request: %s", requestString));

            try {
                String answer = getAnswer(requestString);
                logger.info(String.format("Calculated answer for request: %s", answer));
                sendResponse(Status.OK, answer);
            } catch (RequestDataParseException e) {
                logger.severe(e.getMessage());
                sendResponse(Status.BAD_REQUEST, "");
            }
        }
    }

    private static void sendResponse(Status status, String content){
        System.out.printf(HTTP_RESPONSE, status, content.getBytes(StandardCharsets.UTF_8).length, content);
        logger.info("Answer sent");
    }
    
    private static String getAnswer(String requestString) throws RequestDataParseException {
        RequestData requestData = RequestData.parseRequestData(requestString);
        return getJSONMessage(checkIntersection(requestData));
    }

    private static String getJSONMessage(boolean hit){
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        return String.format("{\"hit\": %b, \"executionTime\": %d}", hit, executionTime);
    }

    private static boolean checkIntersection(RequestData requestData) {
        double x = requestData.x();
        double y = requestData.y();
        double r = requestData.r();
        return ((x * x + y * y) <= r * r && x <= 0 && y >= 0) ||
                (x >= 0 && x <= r && y >= 0 && y <= r) ||
                (y <= 0 && y >= -r && x * 2 <= r && y >= 2 * x - r);
    }
}