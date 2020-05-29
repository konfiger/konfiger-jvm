package io.github.thecarisma;

import io.github.thecarisma.InvalidEntryException;
import io.github.thecarisma.KonfigerStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

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

}
