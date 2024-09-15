package ru.timur;

import java.text.ParseException;

public class RequestDataParseException extends Exception {

    public RequestDataParseException() {
    }

    public RequestDataParseException(String s) {
        super(s);
    }

    public RequestDataParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestDataParseException(Throwable cause) {
        super(cause);
    }
}
