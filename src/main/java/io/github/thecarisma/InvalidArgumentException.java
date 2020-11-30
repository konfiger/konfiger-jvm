package io.github.thecarisma;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 */
public class InvalidArgumentException extends RuntimeException {
    InvalidArgumentException(String message, int line, int column) {
        super(message + " line " + line + ":" + column);
    }
    InvalidArgumentException(String message) {
        super(message);
    }
}
