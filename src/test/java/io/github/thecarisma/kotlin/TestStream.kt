package io.github.thecarisma.kotlin

import io.github.thecarisma.InvalidEntryException
import io.github.thecarisma.KonfigerStream
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class TestStream {
    @Test(expected = FileNotFoundException::class)
    @Throws(FileNotFoundException::class)
    fun Should_Throw_Exceptions() {
        val ks = KonfigerStream(File("tryer.ini"))
    }

    @Test
    @Throws(FileNotFoundException::class)
    fun Should_Successfully_Initialize() {
        val ks = KonfigerStream(File("./README.md"))
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Validate_The_File_Stream_Value() {
        val ks = KonfigerStream(File("src/test/resources/test.config.ini"))
        while (ks.hasNext()) {
            Assert.assertNotEquals(ks.next(), null)
        }
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Validate_The_String_Stream_Key() {
        val ks = KonfigerStream("Name=Adewale Azeez,Project=konfiger, Date=April 24 2020", '=', ',')
        Assert.assertEquals(ks.next()[0], "Name")
        Assert.assertEquals(ks.next()[0], "Project")
        Assert.assertEquals(ks.next()[0], " Date")
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Validate_The_String_Stream_Value() {
        val ks = KonfigerStream("Name=Adewale Azeez,Project=konfiger, Date=April 24 2020", '=', ',')
        Assert.assertEquals(ks.next()[1], "Adewale Azeez")
        Assert.assertEquals(ks.next()[1], "konfiger")
        Assert.assertEquals(ks.next()[1], "April 24 2020")
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Test_String_Stream_Key_Trimming() {
        val ks = KonfigerStream(" Name =Adewale Azeez:Project =konfiger: Date=April 24 2020", '=', ':')
        Assert.assertFalse(ks.isTrimming)
        ks.isTrimming = true
        Assert.assertTrue(ks.isTrimming)
        Assert.assertEquals(ks.next()[0], "Name")
        Assert.assertEquals(ks.next()[0], "Project")
        Assert.assertEquals(ks.next()[0], "Date")
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Test_The_Single_Pair_Commenting_In_String_Stream() {
        val ks = KonfigerStream("Name:Adewale Azeez,//Project:konfiger,Date:April 24 2020", ':', ',')
        while (ks.hasNext()) {
            Assert.assertNotEquals(ks.next()[0], "Project")
        }
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Test_The_Single_Pair_Commenting_In_File_Stream_1() {
        val ks = KonfigerStream(File("src/test/resources/test.comment.inf"))
        ks.commentPrefix = "["
        while (ks.hasNext()) {
            Assert.assertFalse(ks.next()[0].startsWith("["))
        }
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Test_The_Single_Pair_Commenting_In_File_Stream() {
        val ks = KonfigerStream(File("src/test/resources/test.txt"), ':', ',')
        while (ks.hasNext()) {
            Assert.assertFalse(ks.next()[0].startsWith("//"))
        }
    }
}