package ru.timur;

import java.text.ParseException;

public class RequestDataParseException extends Exception {
    public RequestDataParseException(String s) {
        super(s);
    }

    public RequestDataParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
