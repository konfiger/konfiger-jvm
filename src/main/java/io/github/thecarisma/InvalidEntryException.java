package io.github.thecarisma;

public class InvalidEntryException extends Exception {
    InvalidEntryException(String message, int line, int column) {
        super(message + ". Line " + line + ":" + column);
    }
}
