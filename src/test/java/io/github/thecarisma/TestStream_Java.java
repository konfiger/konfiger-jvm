package io.github.thecarisma;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestStream_Java {

    @Test(expected = FileNotFoundException.class)
    public void Should_Throw_Exceptions() throws FileNotFoundException {
        KonfigerStream ks = new KonfigerStream(new File("tryer.ini"));
    }

    @Test
    public void Should_Successfully_Initialize() throws FileNotFoundException {
        KonfigerStream ks = new KonfigerStream(new File("./README.md"));
    }

    @Test
    public void Validate_The_File_Stream_Value() throws IOException, InvalidEntryException {
        KonfigerStream ks = new KonfigerStream(new File("src/test/resources/test.config.ini"));
        while (ks.hasNext()) {
            Assert.assertNotEquals(ks.next(), null);
        }
    }

    @Test
    public void Validate_The_String_Stream_Key() throws IOException, InvalidEntryException {
        KonfigerStream ks = new KonfigerStream(" Name =Adewale Azeez,Project =konfiger, Date=April 24 2020", '=', ',');
        Assert.assertEquals(ks.next()[0], "Name");
        Assert.assertEquals(ks.next()[0], "Project");
        Assert.assertEquals(ks.next()[0], "Date");
    }

    @Test
    public void Validate_The_String_Stream_Value() throws IOException, InvalidEntryException {
        KonfigerStream ks = new KonfigerStream("Name=Adewale Azeez,Project=konfiger, Date=April 24 2020", '=', ',');
        Assert.assertEquals(ks.next()[1], "Adewale Azeez");
        Assert.assertEquals(ks.next()[1], "konfiger");
        Assert.assertEquals(ks.next()[1], "April 24 2020");
    }

    @Test
    public void Test_String_Stream_Key_Trimming() throws IOException, InvalidEntryException {
        KonfigerStream ks = new KonfigerStream(" Name =Adewale Azeez:Project =konfiger: Date=April 24 2020", '=', ':');
        Assert.assertTrue(ks.isTrimmingKey());
        ks.setTrimmingKey(false);
        Assert.assertFalse(ks.isTrimmingKey());
        Assert.assertEquals(ks.next()[0], " Name ");
        Assert.assertEquals(ks.next()[0], "Project ");
        Assert.assertEquals(ks.next()[0], " Date");
    }

    @Test
    public void Test_The_Single_Pair_Commenting_In_String_Stream() throws IOException, InvalidEntryException {
        KonfigerStream ks = new KonfigerStream("Name:Adewale Azeez,//Project:konfiger,Date:April 24 2020", ':', ',');
        while (ks.hasNext()) {
            Assert.assertNotEquals(ks.next()[0], "Project");
        }
    }

    @Test
    public void Test_The_Single_Pair_Commenting_In_File_Stream_1() throws IOException, InvalidEntryException {
        KonfigerStream ks = new KonfigerStream(new File("src/test/resources/test.comment.inf"));
        ks.setCommentPrefix("[");
        while (ks.hasNext()) {
            Assert.assertFalse(ks.next()[0].startsWith("["));
        }
    }

    @Test
    public void Test_The_Single_Pair_Commenting_In_File_Stream() throws IOException, InvalidEntryException {
        KonfigerStream ks = new KonfigerStream(new File("src/test/resources/test.txt"),':', ',');
        while (ks.hasNext()) {
            Assert.assertFalse(ks.next()[0].startsWith("//"));
        }
    }

    @Test
    public void Test_String_Stream_Value_Trimming() throws IOException, InvalidEntryException {
        KonfigerStream ks = new KonfigerStream(" Name =Adewale Azeez :Project = konfiger: Date= April 24 2020 :Language = Multiple Languages", '=', ':');
        Assert.assertNotEquals(ks.isTrimmingValue(), false);
        Assert.assertTrue(ks.isTrimmingValue());
        Assert.assertEquals(ks.next()[1], "Adewale Azeez");
        Assert.assertEquals(ks.next()[1], "konfiger");
        Assert.assertEquals(ks.next()[1], "April 24 2020");
        Assert.assertEquals(ks.next()[1], "Multiple Languages");
    }

    @Test
    public void Test_String_Stream_Key_Value_Trimming() throws IOException, InvalidEntryException {
        String entriesStr = " Name =Adewale Azeez :Project = konfiger: Date= April 24 2020 :Language = Multiple Languages";
        KonfigerStream ks = new KonfigerStream(entriesStr, '=', ':');
        KonfigerStream ks1 = new KonfigerStream(entriesStr, '=', ':');
        Assert.assertEquals(ks.next()[0], "Name");
        Assert.assertEquals(ks.next()[0], "Project");
        Assert.assertEquals(ks.next()[0], "Date");
        Assert.assertEquals(ks.next()[0], "Language");

        Assert.assertEquals(ks1.next()[1], "Adewale Azeez");
        Assert.assertEquals(ks1.next()[1], "konfiger");
        Assert.assertEquals(ks1.next()[1], "April 24 2020");
        Assert.assertEquals(ks1.next()[1], "Multiple Languages");
    }

    @Test
    public void Read_Multiline_Entry_And_Test_Continuation_Char_In_File_Stream() throws IOException, InvalidEntryException {
        KonfigerStream ks = new KonfigerStream(new File("src/test/resources/test.contd.conf"));
        while (ks.hasNext()) {
            Assert.assertFalse(ks.next()[1].contains("\n"));
        }
    }

    @Test
    public void Read_Multiline_Entry_And_Test_Continuation_Char_In_String_Stream() throws IOException, InvalidEntryException {
        KonfigerStream ks = new KonfigerStream("Description = This project is the closest thing to Android +\n" +
                "              [Shared Preference](https://developer.android.com/reference/android/content/SharedPreferences) +\n" +
                "              in other languages and off the Android platform.\n" +
                "ProjectName = konfiger\n" +
                "ProgrammingLanguages = C, C++, C#, Dart, Elixr, Erlang, Go, +\n" +
                "               Haskell, Java, Kotlin, NodeJS, Powershell, +\n" +
                "               Python, Ring, Rust, Scala, Visual Basic, +\n" +
                "               and whatever language possible in the future");
        ks.setContinuationChar('+');
        while (ks.hasNext()) {
            Assert.assertFalse(ks.next()[1].contains("\n"));
        }
    }

    @Test
    public void Test_Backward_Slash_Ending_Value() throws IOException, InvalidEntryException {
        KonfigerStream ks = new KonfigerStream("uri1 = http://uri1.thecarisma.com/core/api/v1/\r\n" +
                "uri2 = http://uri2.thecarisma.com/core/api/v2/\r\n" +
                "ussd.uri = https://ussd.thecarisma.com/");

        int count = 0;
        while(ks.hasNext()) {
            Assert.assertTrue(ks.next()[1].endsWith("/"));
            count++;
        }
        Assert.assertEquals(count, 3);
    }

    @Test
    public void Test_Escape_Slash_Ending() throws IOException, InvalidEntryException {
        KonfigerStream ks = new KonfigerStream("external-resource-location = \\\\988.43.13.9\\testing\\\\public\\sansportal\\rideon\\\\\r\n" +
                "boarding-link = https://boarding.thecarisma.com/konfiger\r\n" +
                "ussd.uri = thecarisma.com\\");

        int count = 0;
        while(ks.hasNext()) {
            Assert.assertFalse(ks.next()[1].isEmpty());
            count++;
        }
        Assert.assertEquals(count, 3);
    }

}
