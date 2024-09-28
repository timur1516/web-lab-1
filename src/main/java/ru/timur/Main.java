package ru.timur;

import com.fastcgi.FCGIInterface;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.logging.*;

public class Main {
    private static final String HTTP_RESPONSE = "Status: %s\nContent-Type: application/json\nContent-Length: %d\n\n%s";
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {
        var fcgiInterface = new FCGIInterface();

        logger.setLevel(Level.ALL);
        logger.info("Server started");

        while (fcgiInterface.FCGIaccept() >= 0) {
            Properties request = FCGIInterface.request.params;
            try {
                processRequest(request);
            } catch (RequestDataParseException e) {
                logger.severe(e.getMessage());
                sendResponse(Status.BAD_REQUEST, "");
            }
        }
    }

    private static void processRequest(Properties request) throws RequestDataParseException {
        long startTime = System.nanoTime() / 1000;

        String requestString = request.getProperty("QUERY_STRING");
        logger.info(String.format("Received request: %s", requestString));
        RequestData requestData = RequestData.parseRequestData(requestString);
        boolean hit = checkIntersection(requestData);

        long executionTime = System.nanoTime() / 1000 - startTime;

        String response = String.format("{\"hit\": %b, \"executionTime\": %d}", hit, executionTime);
        logger.info(String.format("Calculated answer for request: %s", response));

        sendResponse(Status.OK, response);
    }

    private static void sendResponse(Status status, String content){
        System.out.printf(HTTP_RESPONSE, status, content.getBytes(StandardCharsets.UTF_8).length, content);
        logger.info("Answer sent");
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