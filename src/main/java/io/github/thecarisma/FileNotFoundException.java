package io.github.thecarisma;

import java.io.IOException;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 */
public class FileNotFoundException extends RuntimeException {
    FileNotFoundException(String message) {
        super(message);
    }
    FileNotFoundException(IOException ex) {
        super(ex);
    }
}
