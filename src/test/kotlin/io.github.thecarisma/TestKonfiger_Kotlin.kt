package io.github.thecarisma

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.IOException

class TestKonfiger_Kotlin {
    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Validate_Konfiger_String_Stream_Entries() {
        val konfiger = Konfiger("\n" +
                "String=This is a string\n" +
                "Number=215415245\n" +
                "Float=56556.436746\n" +
                "Boolean=true\n", false)
        Assert.assertEquals(konfiger["String"], "This is a string")
        Assert.assertEquals(konfiger["Number"], "215415245")
        Assert.assertEquals(konfiger["Float"], "56556.436746")
        Assert.assertNotEquals(konfiger["Number"], "true")
        Assert.assertEquals(konfiger["Boolean"], "true")
        konfiger.put("String", "This is an updated string")
        Assert.assertEquals(konfiger["String"], "This is an updated string")
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Validate_Konfiger_Entries_Get_Method() {
        val konfiger = Konfiger(File("src/test/resources/test.config.ini"))
        konfiger.put("One", konfiger)
        konfiger.put("Two", "\"hello\", \"world\"")
        konfiger.put("Three", 3)
        konfiger.putInt("Four", 4)
        konfiger.putBoolean("Five", true)
        konfiger.put("Six", false)
        konfiger.put("Seven", "121251656.1367367263726")
        konfiger.putFloat("Eight", 0.21f)
        Assert.assertNotEquals(konfiger["One"], konfiger.toString())
        Assert.assertEquals(konfiger["Two"], "\"hello\", \"world\"")
        Assert.assertEquals(konfiger["Three"], "3")
        Assert.assertEquals(konfiger["Four"], "4")
        Assert.assertEquals(konfiger["Five"], "true")
        Assert.assertEquals(konfiger["Six"], "false")
        Assert.assertEquals(konfiger["Seven"], "121251656.1367367263726")
        Assert.assertEquals(konfiger["Eight"], "0.21")
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Validate_LazyLoad_Konfiger_Entries_Get_With_Fallback() {
        val konfiger = Konfiger(File("src/test/resources/test.config.ini"), true)
        Assert.assertEquals(konfiger["Occupation", "Pen Tester"], "Software Engineer")
        Assert.assertEquals(konfiger["Hobby", "Worm Creation"], "i don't know")
        Assert.assertNull(konfiger["Fav OS"])
        Assert.assertNotNull(konfiger["Fav OS", "Whatever get work done"])
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Validate_Konfiger_Entries_Get_Returned_Types() {
        val konfiger = Konfiger("")
        konfiger.put("One", konfiger)
        konfiger.putLong("Two", 123456789)
        konfiger.putBoolean("Bool", true)
        konfiger.putFloat("Float", 123.56f)
        konfiger.putString("Dummy", "Noooooo 1")
        konfiger.putString("Dummy2", "Noooooo 2")
        Assert.assertEquals(konfiger["Two"], "123456789")
        Assert.assertEquals(konfiger.getLong("Two"), 123456789)
        Assert.assertNotEquals(konfiger.getLong("Two"), "123456789")
        Assert.assertEquals(konfiger["Bool"], "true")
        Assert.assertFalse(konfiger.getBoolean("Two"))
        Assert.assertNotEquals(konfiger.getBoolean("Two"), true)
        Assert.assertNotEquals(konfiger.getBoolean("Two"), "true")
        Assert.assertEquals(konfiger["Float"], "123.56")
        Assert.assertEquals(konfiger.getFloat("Float"), 123.56f, 0.00f)
        Assert.assertNotEquals(konfiger.getFloat("Float"), "123.56")
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Validate_Konfiger_Default_Value_For_Non_Existing_Key() {
        val konfiger = Konfiger("")
        Assert.assertNull(konfiger["Name"])
        Assert.assertNotEquals(konfiger.getString("Name"), null)
        Assert.assertEquals(konfiger.getString("Name"), "")
        Assert.assertNotEquals(konfiger["Name", "Adewale Azeez"], null)
        Assert.assertEquals(konfiger["Name", "Adewale Azeez"], "Adewale Azeez")
        Assert.assertFalse(konfiger.getBoolean("CleanupOnClose"))
        Assert.assertNotEquals(konfiger.getBoolean("CleanupOnClose", true), false)
        Assert.assertEquals(konfiger.getLong("TheNumber"), 0)
        Assert.assertEquals(konfiger.getLong("TheNumber", 123), 123)
        Assert.assertEquals(konfiger.getFloat("TheNumber").toDouble(), 0.0, 0.0)
        Assert.assertNotEquals(konfiger.getFloat("TheNumber"), 0.1)
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Remove_Entry_And_Validate_Size() {
        val konfiger = Konfiger("One=111,Two=222,Three=333", false, '=', ',')
        Assert.assertEquals(konfiger.size().toLong(), 3)
        Assert.assertNotEquals(konfiger["Two"], null)
        Assert.assertEquals(konfiger.remove("Two"), "222")
        Assert.assertNull(konfiger["Two"])
        Assert.assertEquals(konfiger.size().toLong(), 2)
        Assert.assertEquals(konfiger.remove(0), "111")
        Assert.assertEquals(konfiger.size().toLong(), 1)
        Assert.assertEquals(konfiger["Three"], "333")
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Set_Get_Delimeter_And_Seperator() {
        val konfiger = Konfiger(File("src/test/resources/test.config.ini"), true)
        Assert.assertEquals(konfiger.seperator.toLong(), '\n'.toLong())
        Assert.assertEquals(konfiger.delimeter.toLong(), '='.toLong())
        Assert.assertTrue(konfiger.toString().split("\n".toRegex()).toTypedArray().size > 2)
        konfiger.seperator = '-'
        konfiger.delimeter = '+'
        Assert.assertEquals(konfiger.seperator.toLong(), '-'.toLong())
        Assert.assertEquals(konfiger.delimeter.toLong(), '+'.toLong())
        Assert.assertEquals(konfiger.toString().split("\n".toRegex()).toTypedArray().size.toLong(), 1)
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Escaping_And_Unescaping_Entries_And_Save() {
        val ks = KonfigerStream(File("src/test/resources/test.config.ini"))
        val ks1 = KonfigerStream(File("src/test/resources/test.txt"), ':', ',')
        val konfiger = Konfiger(ks, true)
        val konfiger1 = Konfiger(ks1, true)
        Assert.assertEquals(konfiger["Hobby"], "i don't know")
        Assert.assertEquals(konfiger1["Hobby"], konfiger["Hobby"])
        Assert.assertEquals(konfiger1["Hobby"], "i don't know")
        konfiger.save("src/test/resources/test.config.ini")
        val newKs = KonfigerStream(File("src/test/resources/test.config.ini"))
        val newKonfiger = Konfiger(newKs, true)
        val newKonfiger1 = Konfiger(File("src/test/resources/test.txt"), true, ':', ',')
        Assert.assertEquals(konfiger.toString(), newKonfiger.toString())
        Assert.assertEquals(konfiger1.toString(), newKonfiger1.toString())
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Test_Complex_And_Confusing_Seperator() {
        val konfiger = Konfiger("Occupation=Software En^gineergLocation=Ni^geriagState=La^gos", false, '=', 'g')
        Assert.assertEquals(konfiger.size().toLong(), 3)
        Assert.assertTrue(konfiger.toString().contains("^g"))
        for (entry in konfiger.entries()) {
            Assert.assertFalse(entry.value.contains("^g"))
        }
        konfiger.seperator = 'f'
        Assert.assertEquals(konfiger["Occupation"], "Software Engineer")
        konfiger.seperator = '\n'
        Assert.assertFalse(konfiger.toString().contains("^g"))
        Assert.assertEquals(konfiger.size().toLong(), 3)
        for (entry in konfiger.entries()) {
            Assert.assertFalse(entry.value.contains("\\g"))
        }
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Append_New_Unparsed_Entries_From_String_And_File() {
        val konfiger = Konfiger("")
        Assert.assertEquals(konfiger.size().toLong(), 0)
        konfiger.appendString("Language=English")
        Assert.assertEquals(konfiger.size().toLong(), 1)
        Assert.assertNull(konfiger["Name"])
        Assert.assertNotEquals(konfiger["Name"], "Adewale Azeez")
        Assert.assertEquals(konfiger["Language"], "English")
        konfiger.appendFile(File("src/test/resources/test.config.ini"))
        Assert.assertNotEquals(konfiger["Name"], null)
        Assert.assertEquals(konfiger["Name"], "Adewale Azeez")
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Test_Prev_And_Current_Cache() {
        val konfiger = Konfiger("")
        konfiger.put("Name", "Adewale")
        konfiger.put("Project", "konfiger")
        konfiger.putInt("Year", 2020)
        Assert.assertEquals(konfiger.getInt("Year").toLong(), 2020)
        Assert.assertEquals(konfiger["Project"], "konfiger")
        Assert.assertEquals(konfiger["Name"], "Adewale")
        Assert.assertEquals(konfiger.getInt("Year").toLong(), 2020)
        Assert.assertEquals(konfiger.currentCachedObject[0], "Name")
        Assert.assertEquals(konfiger.prevCachedObject[0], "Year")
        Assert.assertEquals(konfiger.currentCachedObject[1], "Adewale")
        Assert.assertEquals(konfiger.prevCachedObject[1], "2020")
        Assert.assertEquals(konfiger["Name"], "Adewale")
        Assert.assertEquals(konfiger["Name"], "Adewale")
        Assert.assertEquals(konfiger["Project"], "konfiger")
        Assert.assertEquals(konfiger["Name"], "Adewale")
        Assert.assertEquals(konfiger["Name"], "Adewale")
        Assert.assertEquals(konfiger["Name"], "Adewale")
        Assert.assertEquals(konfiger.currentCachedObject[0], "Project")
        Assert.assertEquals(konfiger.prevCachedObject[0], "Name")
        Assert.assertEquals(konfiger.currentCachedObject[1], "konfiger")
        Assert.assertEquals(konfiger.prevCachedObject[1], "Adewale")
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Test_The_Single_Pair_Commenting_In_String_Stream_Konfiger() {
        val ks = KonfigerStream("Name:Adewale Azeez,//Project:konfiger,Date:April 24 2020", ':', ',')
        val kon = Konfiger(ks)
        for (key in kon.keys()) {
            Assert.assertNotEquals(kon.getString(key), "Project")
        }
        Assert.assertEquals(kon.size().toLong(), 2)
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Test_Contains_With_Lazy_Load() {
        val ks = KonfigerStream(File("src/test/resources/test.comment.inf"))
        ks.commentPrefix = "["
        val kon = Konfiger(ks, true)
        Assert.assertTrue(kon.contains("File"))
        Assert.assertTrue(kon.contains("Project"))
        Assert.assertTrue(kon.contains("Author"))
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Read_Multiline_Entry_From_File_Stream() {
        val ks = KonfigerStream(File("src/test/resources/test.contd.conf"))
        val kon = Konfiger(ks, true)
        Assert.assertTrue(kon.getString("ProgrammingLanguages").indexOf("Kotlin, NodeJS, Powershell, Python, Ring, Rust") > 0)
        Assert.assertEquals(kon["ProjectName"], "konfiger")
        Assert.assertTrue(kon.getString("Description").endsWith(" in other languages and off the Android platform."))
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Check_Size_In_LazyLoad_And_No_LazyLoad() {
        val ks = KonfigerStream(File("src/test/resources/test.contd.conf"))
        val kon = Konfiger(ks, false)
        val ks1 = KonfigerStream(File("src/test/resources/test.contd.conf"))
        val kon1 = Konfiger(ks1, true)
        Assert.assertTrue(kon.size() > 0)
        Assert.assertTrue(kon1.size() > 0)
        Assert.assertFalse(kon.isEmpty)
        Assert.assertFalse(kon1.isEmpty)
        Assert.assertEquals(kon1.size().toLong(), kon1.size().toLong())
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class)
    fun Check_putComment_In_The_Konfiger_Object() {
        val kon = Konfiger("Name:Adewale Azeez,//Project:konfiger,Date:April 24 2020", false, ':', ',')
        kon.putComment("A comment at the end")
        Assert.assertTrue(kon.toString().contains("//:A comment"))
    }
}