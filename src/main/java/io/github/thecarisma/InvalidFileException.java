package io.github.thecarisma;

import java.io.IOException;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 */
public class InvalidFileException extends RuntimeException {
    InvalidFileException(IOException ex) {
        super(ex);
    }
}
