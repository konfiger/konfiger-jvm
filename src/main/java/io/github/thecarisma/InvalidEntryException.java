package io.github.thecarisma;

class InvalidEntryException extends Exception {
    InvalidEntryException(String message, int line, int column) {
        super(message + ". Line " + line + ":" + column);
    }
}
