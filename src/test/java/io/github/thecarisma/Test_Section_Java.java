package io.github.thecarisma;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class Test_Section_Java {

    @Test
    public void testKonfigerStreamWithSectionString() throws IOException, InvalidEntryException {
        KonfigerStream ks = KonfigerStream.builder()
                .withString(";comment for details\ndetails = true; yahoo\n" +
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
        Assert.assertArrayEquals(next1, new String[]{"details", "true", "comment for details", " yahoo", "__global__", ""});
        Assert.assertArrayEquals(next2, new String[]{"name", "thecarisma", "", "", "user", ""});
        Assert.assertArrayEquals(next3, new String[]{"gender", "Unknown", "", "", "user", ""});
        Assert.assertArrayEquals(next4, new String[]{"twitter", "iamthecarisma", "", "", "social", ""});
        Assert.assertArrayEquals(next5, new String[]{"twitch", "amsiraceht", "", "", "social", ""});
    }

    @Test
    public void testStreamEntryString() throws IOException, InvalidEntryException {
        KonfigerStream ks = KonfigerStream.builder()
                .withString(";comment for details\ndetails = true ; yahoo\n" +
                        "[user]\n" +
                        "name = thecarisma\n" +
                        "gender = Unknown\n" +
                        "\n" +
                        "[social]\r\n" +
                        "twitter = iamthecarisma\n" +
                        "twitch = amsiraceht")
                .build();

        Assert.assertTrue(ks.hasNext()); Entry sectionEntry1 = ks.nextEntry();
        Assert.assertTrue(ks.hasNext()); Entry sectionEntry2 = ks.nextEntry();
        Assert.assertTrue(ks.hasNext()); Entry sectionEntry3 = ks.nextEntry();
        Assert.assertTrue(ks.hasNext()); Entry sectionEntry4 = ks.nextEntry();
        Assert.assertTrue(ks.hasNext()); Entry sectionEntry5 = ks.nextEntry();
        Assert.assertEquals(sectionEntry1.toString(), ";comment for details\ndetails = true ; yahoo");
        Assert.assertEquals(sectionEntry2.toString(), "[user]\nname = thecarisma");
        Assert.assertEquals(sectionEntry3.toString(), "[user]\ngender = Unknown");
        Assert.assertEquals(sectionEntry4.toString(), "[social]\ntwitter = iamthecarisma");
        Assert.assertEquals(sectionEntry5.toString(), "[social]\ntwitch = amsiraceht");
    }

    @Test
    public void testKonfigerStreamWithSectionFile() throws IOException, InvalidEntryException {
        KonfigerStream ks = KonfigerStream.builder()
                .withFile(new File("src/test/resources/payroll.conf"))
                .build();

        Assert.assertTrue(ks.hasNext()); String[] next1 = ks.next();
        Assert.assertTrue(ks.hasNext()); String[] next2 = ks.next();
        Assert.assertTrue(ks.hasNext()); String[] next3 = ks.next();
        Assert.assertTrue(ks.hasNext()); String[] next4 = ks.next();
        Assert.assertTrue(ks.hasNext()); String[] next5 = ks.next();
        Assert.assertTrue(ks.hasNext()); String[] next6 = ks.next();
        Assert.assertTrue(ks.hasNext()); String[] next7 = ks.next();
        Assert.assertTrue(ks.hasNext()); String[] next8 = ks.next();
        Assert.assertTrue(ks.hasNext()); String[] next9 = ks.next();
        Assert.assertTrue(ks.hasNext()); String[] next10 = ks.next();
        Assert.assertTrue(ks.hasNext()); String[] next11 = ks.next();
        Assert.assertTrue(ks.hasNext()); String[] next12 = ks.next();
        Assert.assertArrayEquals(next1, new String[]{"source", "https://en.wikipedia.org/wiki/INI_file", "", "", "__global__", ""});
        Assert.assertArrayEquals(next2, new String[]{"date", "Nov 20 2020", "", "", "__global__", ""});
        Assert.assertArrayEquals(next3, new String[]{"name", "John Doe", "", "", "owner",
                " Copied from https://en.wikipedia.org/wiki/INI_file last modified 1 April 2001 by John Doe"});
        Assert.assertArrayEquals(next4, new String[]{"organization", "Acme Widgets Inc.", "", "", "owner",
                " Copied from https://en.wikipedia.org/wiki/INI_file last modified 1 April 2001 by John Doe"});
        Assert.assertArrayEquals(next5, new String[]{"server", "192.0.2.62",
                " use IP address in case network name resolution is not working", "", "database", ""});
        Assert.assertArrayEquals(next6, new String[]{"port", "143", "", "", "database", ""});
        Assert.assertArrayEquals(next7, new String[]{"file", "\"payroll.dat\"", "", "", "database", ""});
        Assert.assertArrayEquals(next8, new String[]{"extension", "gd2", "", "", "database", ""});
        Assert.assertArrayEquals(next9, new String[]{"extension", "gettext", "", "", "database", ""});
        Assert.assertArrayEquals(next10, new String[]{"extension", "gmp", "", "", "database", ""});
        Assert.assertArrayEquals(next11, new String[]{"extension", "intl", "", "", "database", ""});
        Assert.assertArrayEquals(next12, new String[]{"extension", "imap", "", "", "database", ""});
    }

    @Test
    public void testStreamEntryFile() throws IOException, InvalidEntryException {
        
    }

    @Test
    public void testKonfigerSectionString() throws IOException, InvalidEntryException {
        Konfiger kon = new Konfiger(KonfigerStream.builder()
                .withString("details=true\n" +
                        "[user]\n\n" +
                        "name = thecarisma\n" +
                        "gender = Unknown\n" +
                        "\n" +
                        "[social]\r\n" +
                        "twitter = iamthecarisma\n" +
                        "twitch = amsiraceht")
                .build());

    }

}
