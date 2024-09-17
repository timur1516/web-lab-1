package ru.timur;

public enum Status {
    OK{
        @Override
        public String toString() {
            return "200 OK";
        }
    },
    BAD_REQUEST{
        @Override
        public String toString() {
            return "400 Bad Request";
        }
    }
}
