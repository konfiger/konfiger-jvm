package io.github.thecarisma

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class TestStream_Kotlin {
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
        val ks = KonfigerStream(" Name =Adewale Azeez,Project =konfiger, Date=April 24 2020", '=', ',')
        Assert.assertEquals(ks.next()[0], "Name")
        Assert.assertEquals(ks.next()[0], "Project")
        Assert.assertEquals(ks.next()[0], "Date")
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
        Assert.assertTrue(ks.isTrimmingKey)
        ks.isTrimmingKey = false
        Assert.assertFalse(ks.isTrimmingKey)
        Assert.assertEquals(ks.next()[0], " Name ")
        Assert.assertEquals(ks.next()[0], "Project ")
        Assert.assertEquals(ks.next()[0], " Date")
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

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Test_String_Stream_Value_Trimming() {
        val ks = KonfigerStream(" Name =Adewale Azeez :Project = konfiger: Date= April 24 2020 :Language = Multiple Languages", '=', ':')
        Assert.assertNotEquals(ks.isTrimmingValue, false)
        Assert.assertTrue(ks.isTrimmingValue)
        Assert.assertEquals(ks.next()[1], "Adewale Azeez")
        Assert.assertEquals(ks.next()[1], "konfiger")
        Assert.assertEquals(ks.next()[1], "April 24 2020")
        Assert.assertEquals(ks.next()[1], "Multiple Languages")
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Test_String_Stream_Key_Value_Trimming() {
        val entriesStr = " Name =Adewale Azeez :Project = konfiger: Date= April 24 2020 :Language = Multiple Languages"
        val ks = KonfigerStream(entriesStr, '=', ':')
        val ks1 = KonfigerStream(entriesStr, '=', ':')
        Assert.assertEquals(ks.next()[0], "Name")
        Assert.assertEquals(ks.next()[0], "Project")
        Assert.assertEquals(ks.next()[0], "Date")
        Assert.assertEquals(ks.next()[0], "Language")

        Assert.assertEquals(ks1.next()[1], "Adewale Azeez")
        Assert.assertEquals(ks1.next()[1], "konfiger")
        Assert.assertEquals(ks1.next()[1], "April 24 2020")
        Assert.assertEquals(ks1.next()[1], "Multiple Languages")
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Read_Multiline_Entry_And_Test_Continuation_Char_In_File_Stream() {
        val ks = KonfigerStream(File("src/test/resources/test.contd.conf"))
        while (ks.hasNext()) {
            Assert.assertFalse(ks.next()[1].contains("\n"))
        }
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Read_Multiline_Entry_And_Test_Continuation_Char_In_String_Stream() {
        val ks = KonfigerStream("Description = This project is the closest thing to Android +\n" +
                "              [Shared Preference](https://developer.android.com/reference/android/content/SharedPreferences) +\n" +
                "              in other languages and off the Android platform.\n" +
                "ProjectName = konfiger\n" +
                "ProgrammingLanguages = C, C++, C#, Dart, Elixr, Erlang, Go, +\n" +
                "               Haskell, Java, Kotlin, NodeJS, Powershell, +\n" +
                "               Python, Ring, Rust, Scala, Visual Basic, +\n" +
                "               and whatever language possible in the future")
        ks.continuationChar = '+'
        while (ks.hasNext()) {
            Assert.assertFalse(ks.next()[1].contains("\n"))
        }
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Test_Backward_Slash_Ending_Value() {
        val ks = KonfigerStream("uri1 = http://uri1.thecarisma.com/core/api/v1/\r\n" +
                "uri2 = http://uri2.thecarisma.com/core/api/v2/\r\n" +
                "ussd.uri = https://ussd.thecarisma.com/")
        var count = 0
        while (ks.hasNext()) {
            Assert.assertTrue(ks.next()[1].endsWith("/"))
            count++
        }
        Assert.assertEquals(count.toLong(), 3)
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Test_Escape_Slash_Ending() {
        val ks = KonfigerStream("external-resource-location = \\\\988.43.13.9\\testing\\\\public\\sansportal\\rideon\\\\\r\n" +
                "boarding-link = https://boarding.thecarisma.com/konfiger\r\n" +
                "ussd.uri = thecarisma.com\\")
        var count = 0
        while (ks.hasNext()) {
            Assert.assertFalse(ks.next()[1].isEmpty())
            count++
        }
        Assert.assertEquals(count.toLong(), 3)
    }

}