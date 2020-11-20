package io.github.thecarisma;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Test_Section_Java {

    @Test
    public void testKonfigerStreamWithSectionString() throws IOException, InvalidEntryException {
        KonfigerStream ks = KonfigerStream.builder()
                .withString("details=true\n" +
                        "[user]\n\n" +
                        "name = thecarisma\n" +
                        "gender = Unknown\n" +
                        "\n" +
                        "[social]\r\n" +
                        "twitter = iamthecarisma\n" +
                        "twitch = amsiraceht")
                .build();

        Assert.assertTrue(ks.hasNext()); String[] next1 = ks.next();
        Assert.assertTrue(ks.hasNext()); String[] next2 = ks.next();
        Assert.assertTrue(ks.hasNext()); String[] next3 = ks.next();
        Assert.assertTrue(ks.hasNext()); String[] next4 = ks.next();
        Assert.assertTrue(ks.hasNext()); String[] next5 = ks.next();
        Assert.assertArrayEquals(next1, new String[]{"details", "true", "__global__"});
        Assert.assertArrayEquals(next2, new String[]{"name", "thecarisma", "user"});
        Assert.assertArrayEquals(next3, new String[]{"gender", "Unknown", "user"});
        Assert.assertArrayEquals(next4, new String[]{"twitter", "iamthecarisma", "social"});
        Assert.assertArrayEquals(next5, new String[]{"twitch", "amsiraceht", "social"});
    }

    @Test
    public void testKonfigerStreamWithSectionFile() throws IOException, InvalidEntryException {
        KonfigerStream ks = KonfigerStream.builder()
                .withFile(new File("src/test/resources/payroll.conf"))
                .build();

        while (ks.hasNext()) {
            System.out.println(Arrays.toString(ks.next()));
        }

    }

}
