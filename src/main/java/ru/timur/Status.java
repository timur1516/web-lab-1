package ru.timur;

public enum Status {
    OK{
        @Override
        public String toString() {
            return "200 OK";
        }
    },
    NOT_IMPLEMENTED{
        @Override
        public String toString() {
            return "501 Not Implemented";
        }
    },
    BAD_REQUEST{
        @Override
        public String toString() {
            return "400 Bad Request";
        }
    }
}
