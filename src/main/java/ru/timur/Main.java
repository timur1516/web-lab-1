package ru.timur;

import com.fastcgi.FCGIInterface;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        FileWriter logger = new FileWriter("log.txt");
        String httpResponse = "Content-Type: application/json\nContent-Length: %d\n\n%s";
        var fcgiInterface = new FCGIInterface();
        while (fcgiInterface.FCGIaccept() >= 0) {
            String request = FCGIInterface.request.params.getProperty("QUERY_STRING");
            logger.write("Received request: " + request + "\n");
            logger.flush();

            List<String> data = Arrays.asList(request.split("&"));
            List<Integer> int_data = data
                    .stream()
                    .map(o -> o.split("=")[1])
                    .map(Integer::parseInt)
                    .toList();

            int x = int_data.get(0);
            int r = int_data.get(1);
            int y = int_data.get(2);

            boolean result =
                    ((x * x + y * y) <= r * r && x <= 0 && y >= 0) ||
                            (x >= 0 && x <= r && y >= 0 && y <= r) ||
                            (y <= 0 && y >= -r && x*2 <= r && y >= 2*x - r);

            String answer = String.format("{\"answer\": %b}", result);

            System.out.printf(httpResponse, answer.getBytes(StandardCharsets.UTF_8).length, answer);
        }
        logger.close();
    }
}