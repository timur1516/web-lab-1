package ru.timur;

import java.util.Arrays;
import java.util.List;

public record RequestData(double x, double y, double r) {

    public static RequestData parseRequestData(String requestString) throws RequestDataParseException {
        double x;
        double y;
        double r;
        List<String> data;
        try {
            data = Arrays.stream(requestString.split("&"))
                    .map(o -> o.split("=")[1])
                    .toList();
        } catch (Exception e) {
            throw new RequestDataParseException("Corrupted request!");
        }
        try {
            x = Double.parseDouble(data.get(0));
            y = Double.parseDouble(data.get(1));
            r = Double.parseDouble(data.get(2));
        } catch (NumberFormatException | NullPointerException e) {
            throw new RequestDataParseException("Parse error!", e);
        }
        return new RequestData(x, y, r);
    }
}
