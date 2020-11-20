package io.github.thecarisma;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 */
public class InvalidEntryException extends Exception {
    InvalidEntryException(String message, int line, int column) {
        super(message + " line " + line + ":" + column);
    }
}
