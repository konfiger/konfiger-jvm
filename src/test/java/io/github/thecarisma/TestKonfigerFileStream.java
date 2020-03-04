package io.github.thecarisma;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class TestKonfigerFileStream {

    public static void main(String[] args) throws InvalidEntryException, IOException {
        KonfigerStream ks = new KonfigerStream(new File("src/test/resources/test.txt"), '=', '\n');
        while (ks.hasNext()) {
            String[] ret = ks.next();
            System.out.println(Arrays.toString(ret));
        }
    }

}
