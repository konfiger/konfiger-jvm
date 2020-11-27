package io.github.thecarisma;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 */
public class NestedSectionException extends RuntimeException {
    NestedSectionException(String message, int line, int column) {
        super(message + " line " + line + ":" + column);
    }
    NestedSectionException(String message) {
        super(message);
    }
}
