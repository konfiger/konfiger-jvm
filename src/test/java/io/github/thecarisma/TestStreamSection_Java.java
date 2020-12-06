package io.github.thecarisma;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestStreamSection_Java {

    @Test
    public void testStreamEntryString() {
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

        Assert.assertTrue(ks.hasNext()); Entry next1 = ks.next();
        Assert.assertTrue(ks.hasNext()); Entry next2 = ks.next();
        Assert.assertTrue(ks.hasNext()); Entry next3 = ks.next();
        Assert.assertTrue(ks.hasNext()); Entry next4 = ks.next();
        Assert.assertTrue(ks.hasNext()); Entry next5 = ks.next();
        /*Assert.assertArrayEquals(next1, new Entry{"details", "true", "comment for details", " yahoo", Konfiger.GLOBAL_SECTION_NAME, ""});
        Assert.assertArrayEquals(next2, new Entry{"name", "thecarisma", "", "", "user", ""});
        Assert.assertArrayEquals(next3, new Entry{"gender", "Unknown", "", "", "user", ""});
        Assert.assertArrayEquals(next4, new Entry{"twitter", "iamthecarisma", "", "", "social", ""});
        Assert.assertArrayEquals(next5, new Entry{"twitch", "amsiraceht", "", "", "social", ""});*/
    }

    @Test
    public void testKonfigerStreamWithSectionString() {
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

        Assert.assertTrue(ks.hasNext()); Entry sectionEntry1 = ks.next();
        Assert.assertTrue(ks.hasNext()); Entry sectionEntry2 = ks.next();
        Assert.assertTrue(ks.hasNext()); Entry sectionEntry3 = ks.next();
        Assert.assertTrue(ks.hasNext()); Entry sectionEntry4 = ks.next();
        Assert.assertTrue(ks.hasNext()); Entry sectionEntry5 = ks.next();
        Assert.assertEquals(sectionEntry1.toString(), ";comment for details\ndetails = true ; yahoo");
        Assert.assertEquals(sectionEntry2.toString(), "name = thecarisma");
        Assert.assertEquals(sectionEntry3.toString(), "gender = Unknown");
        Assert.assertEquals(sectionEntry4.toString(), "twitter = iamthecarisma");
        Assert.assertEquals(sectionEntry5.toString(), "twitch = amsiraceht");
    }

    @Test
    public void testKonfigerStreamWithSectionFile() {
        KonfigerStream ks = KonfigerStream.builder()
                .withFile(new File("src/test/resources/payroll.conf"))
                .build();

        Assert.assertTrue(ks.hasNext()); Entry next1 = ks.next();
        Assert.assertTrue(ks.hasNext()); Entry next2 = ks.next();
        Assert.assertTrue(ks.hasNext()); Entry next3 = ks.next();
        Assert.assertTrue(ks.hasNext()); Entry next4 = ks.next();
        Assert.assertTrue(ks.hasNext()); Entry next5 = ks.next();
        Assert.assertTrue(ks.hasNext()); Entry next6 = ks.next();
        Assert.assertTrue(ks.hasNext()); Entry next7 = ks.next();
        Assert.assertTrue(ks.hasNext()); Entry next8 = ks.next();
        Assert.assertTrue(ks.hasNext()); Entry next9 = ks.next();
        Assert.assertTrue(ks.hasNext()); Entry next10 = ks.next();
        Assert.assertTrue(ks.hasNext()); Entry next11 = ks.next();
        Assert.assertTrue(ks.hasNext()); Entry next12 = ks.next();
        /*Assert.assertArrayEquals(next1, new Entry{"source", "https://en.wikipedia.org/wiki/INI_file", "", "", Konfiger.GLOBAL_SECTION_NAME, ""});
        Assert.assertArrayEquals(next2, new Entry{"date", "Nov 20 2020", "", "", Konfiger.GLOBAL_SECTION_NAME, ""});
        Assert.assertArrayEquals(next3, new Entry{"name", "John Doe", "", "", "owner",
                " Copied from https://en.wikipedia.org/wiki/INI_file last modified 1 April 2001 by John Doe"});
        Assert.assertArrayEquals(next4, new Entry{"organization", "Acme Widgets Inc.", "", "", "owner",
                " Copied from https://en.wikipedia.org/wiki/INI_file last modified 1 April 2001 by John Doe"});
        Assert.assertArrayEquals(next5, new Entry{"server", "192.0.2.62",
                " use IP address in case network name resolution is not working", "", "database", ""});
        Assert.assertArrayEquals(next6, new Entry{"port", "143", "", "", "database", ""});
        Assert.assertArrayEquals(next7, new Entry{"file", "\"payroll.dat\"", "", "", "database", ""});
        Assert.assertArrayEquals(next8, new Entry{"extension", "gd2", "", "", "database", ""});
        Assert.assertArrayEquals(next9, new Entry{"extension", "gettext", "", "", "database", ""});
        Assert.assertArrayEquals(next10, new Entry{"extension", "gmp", "", "", "database", ""});
        Assert.assertArrayEquals(next11, new Entry{"extension", "intl", "", "", "database", ""});
        Assert.assertArrayEquals(next12, new Entry{"extension", "imap", "", "", "database", ""});*/
    }

    @Test
    public void testMultilineCommentFile() {
        KonfigerStream ks = KonfigerStream.builder()
                .withFile(new File("src/test/resources/multiline_comment.ini"))
                .build();

        Assert.assertTrue(ks.hasNext()); Entry next1 = ks.next();
        Assert.assertTrue(ks.hasNext()); Entry next2 = ks.next();
        Assert.assertEquals(next1.getComments().size(), 1);
        Assert.assertEquals(next2.getComments().size(), 3);
    }

    @Test
    public void testMultilineCommentString() {
        KonfigerStream ks = KonfigerStream.builder()
                .withString("[section with comments only]\n" +
                        "''' first comment\n" +
                        "second's comment\n" +
                        "third comment'''\n" +
                        "project = konfiger\n" +
                        "\n" +
                        "; first comment\n" +
                        "; second comment\n" +
                        "; third comment\n" +
                        "author = thecarisma")
                .build();

        Assert.assertTrue(ks.hasNext()); Entry next1 = ks.next();
        Assert.assertTrue(ks.hasNext()); Entry next2 = ks.next();
        Assert.assertEquals(next1.getComments().size(), 1);
        Assert.assertEquals(next2.getComments().size(), 3);
    }

    @Test
    public void testMultilineComment1() {
        KonfigerStream ks = KonfigerStream.builder()
                .withString("[section with comments only]\n" +
                        "; first comment\n" +
                        "; second comment\n" +
                        "; third comment\n" +
                        "project = konfiger")
                .build();

        Assert.assertTrue(ks.hasNext()); Entry entry = ks.next();
        String multilineComment = "";
        int index = 0;
        for (Entry.Comment comment : entry.getComments()) {
            index++;
            multilineComment += comment.getValue();
            if (index < entry.getComments().size()) {
                multilineComment += "\n";
            }
        }
        Entry.Comment comment = new Entry.Comment();
        comment.setCommentKeyword("'''");
        comment.setMultiline(true);
        comment.setValue(multilineComment);
        entry.setComments(new ArrayList<Entry.Comment>());
        entry.addComment(comment);
        Assert.assertEquals(entry.toString(true, '='),
                "''' first comment\n" +
                        " second comment\n" +
                        " third comment'''\n" +
                        "project = konfiger");
    }

    @Test
    public void testMultilineComment2() {
        KonfigerStream ks = KonfigerStream.builder()
                .withString("''' first comment\n" +
                        " second comment\n" +
                        " third comment'''\n" +
                        "project = konfiger")
                .build();

        Assert.assertTrue(ks.hasNext()); Entry entry = ks.next();
        List<Entry.Comment> commentList = new ArrayList<>();
        for (Entry.Comment comment : entry.getComments()) {
            String[] separated = comment.getValue().split("\n");
            for (String separated_ : separated) {
                Entry.Comment comment1 = new Entry.Comment();
                comment1.setCommentKeyword(";");
                comment1.setValue(separated_);
                commentList.add(comment1);
            }
        }
        entry.setComments(commentList);
        Assert.assertEquals(entry.toString(true, '='),
                "; first comment\n" +
                        "; second comment\n" +
                        "; third comment\n" +
                        "project = konfiger");
    }

    @Test
    public void testNestedAndIndentedSectionFile() {
        KonfigerStream ks = KonfigerStream.builder()
                .withFile(new File("src/test/resources/nested_section.conf"))
                .enableIndentedSection()
                .enableNestedSections()
                .build();

        while (ks.hasNext()) {
            Entry entry = ks.next();
            Assert.assertNotNull(entry);
        }
    }

    @Test
    public void testNestedAndIndentedSectionString() {
        KonfigerStream ks = KonfigerStream.builder()
                .withString("[contact]\n" +
                        "phone = +235012345678\n" +
                        "email = ?\n" +
                        "\n" +
                        "    [contact.social-accounts]\n" +
                        "    twitter = iamthecarisma\n" +
                        "    twitch = amsiraceht\n" +
                        "    github = thecarisma")
                .enableIndentedSection()
                .enableNestedSections()
                .build();

        while (ks.hasNext()) {
            Entry entry = ks.next();
            Assert.assertNotNull(entry);
            System.out.println(entry);
        }
    }

    @Test
    public void testWriteAsReadCommentFile() {
        KonfigerStream ks = KonfigerStream.builder()
                .withFile(new File("src/test/resources/comment_prefixes.conf"))
                .withCommentPrefixes(";", "#", "//")
                .enableIndentedSection()
                .enableNestedSections()
                .build();

        while (ks.hasNext()) {
            Entry entry = ks.next();
            Assert.assertNotNull(entry);
            System.out.println(entry);
        }
    }

    @Test
    public void testPythonConfigParserStructure() {
        KonfigerStream ks = KonfigerStream.builder()
                .withFile(new File("src/test/resources/configparser.python.conf"))
                .withCommentPrefixes(";", "#")
                .withDelimiters(new char[] {'=', ':'})
                .withTrimmingValue(false)
                .build();

        while (ks.hasNext()) {
            Entry entry = ks.next();
            System.out.println(entry.getKey() + "->" + entry.getValue());
        }
    }

}
