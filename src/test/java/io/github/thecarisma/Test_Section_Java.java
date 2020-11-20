package io.github.thecarisma;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

public class Test_Section_Java {

    @Test
    public void test_KonfigerStream_Section_String() throws IOException, InvalidEntryException {
        KonfigerStream ks = new KonfigerStream("[user]\n" +
                "name = thecarisma\n" +
                "gender = Unknown\n" +
                "\n" +
                "[social]\n" +
                "twitter = iamthecarisma\n" +
                "twitch = amsiraceht");
        while (ks.hasNext()) {
            System.out.println(Arrays.toString(ks.next()));
        }
    }

}
