package io.github.thecarisma;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class TestKonfigerFileStream {

    public static void main(String[] args) throws InvalidEntryException, FileNotFoundException {
        KonfigerStream ks = new KonfigerStream(new File("src/test/resources/test.txt"), ':', '\n');
        while (ks.hasNext()) {
            System.out.println(Arrays.toString(ks.next()));
        }
    }

}
