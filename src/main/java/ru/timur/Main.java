package ru.timur;

import com.fastcgi.FCGIInterface;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Main {
    private static final String HTTP_RESPONSE = "Content-Type: application/json\nContent-Length: %d\n\n%s";
    private static FileWriter logger;
    public static void main(String[] args) throws IOException {
        var fcgiInterface = new FCGIInterface();
        logger = new FileWriter("log.txt");

        while (fcgiInterface.FCGIaccept() >= 0) {
            Properties request = FCGIInterface.request.params;

            if (!request.getProperty("REQUEST_METHOD").equals("GET")) {
                logger.write(String.format("Received request type %s\n", request.getProperty("REQUEST_METHOD")));
                logger.flush();
                continue;
            }

            String requestString = request.getProperty("QUERY_STRING");
            logger.write(String.format("Received request: %s\n", requestString));
            logger.flush();

            String answer = getAnswer(requestString);
            
            System.out.printf(HTTP_RESPONSE, answer.getBytes(StandardCharsets.UTF_8).length, answer);
        }
        logger.close();
    }
    
    private static String getAnswer(String requestString) throws IOException {
        RequestData requestData;
        try {
            requestData = RequestData.parseRequestData(requestString);
        } catch (RequestDataParseException e){
            logger.write(e.getMessage());
            logger.flush();
            return getJSONMessage(StateCode.ERROR, false);
        }
        return getJSONMessage(StateCode.OK, checkIntersection(requestData));
    }

    private static String getJSONMessage(StateCode stateCode, boolean answer){
        return String.format("{\"stateCode\": \"%s\",\n\"answer\": %b}", stateCode, answer);
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