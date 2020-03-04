package io.github.thecarisma;

import java.io.IOException;
import java.util.Arrays;

public class TestKonfigerStringStream {

    public static void main(String[] args) throws InvalidEntryException, IOException {
        KonfigerStream ks = new KonfigerStream("\n" +
                "Ones:11111111111\n" +
                "Twos:2222222222222\n" +
                "Threes:3333333333333\n" +
                "Fours:444444444444\r\n" +
                "Fives:5555555555555\n", ':', '\n');
        while (ks.hasNext()) {
            System.out.println(Arrays.toString(ks.next()));
        }
    }

}
