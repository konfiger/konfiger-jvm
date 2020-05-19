package io.github.thecarisma.scala;

import io.github.thecarisma.InvalidEntryException;
import io.github.thecarisma.Konfiger;
import io.github.thecarisma.KonfigerFieldsExposer;
import io.github.thecarisma.KonfigerStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class TestKonfiger {

    @Test
    public void Validate_Konfiger_String_Stream_Entries() throws IOException, InvalidEntryException {
        Konfiger konfiger = new Konfiger("\n" +
                "String=This is a string\n" +
                "Number=215415245\n" +
                "Float=56556.436746\n" +
                "Boolean=true\n", false);

        Assert.assertEquals(konfiger.get("String"), "This is a string");
        Assert.assertEquals(konfiger.get("Number"), "215415245");
        Assert.assertEquals(konfiger.get("Float"), "56556.436746");
        Assert.assertNotEquals(konfiger.get("Number"), "true");
        Assert.assertEquals(konfiger.get("Boolean"), "true");
        konfiger.put("String", "This is an updated string");
        Assert.assertEquals(konfiger.get("String"), "This is an updated string");
    }

    @Test
    public void Validate_Konfiger_Entries_Get_Method() throws IOException, InvalidEntryException {
        Konfiger konfiger = new Konfiger(new File("src/test/resources/test.config.ini"));
        konfiger.put("One", konfiger);
        konfiger.put("Two", "\"hello\", \"world\"");
        konfiger.put("Three", 3);
        konfiger.putInt("Four", 4);
        konfiger.putBoolean("Five", true);
        konfiger.put("Six", false);
        konfiger.put("Seven", "121251656.1367367263726");
        konfiger.putFloat("Eight", 0.21f);

        Assert.assertNotEquals(konfiger.get("One"), konfiger.toString());
        Assert.assertEquals(konfiger.get("Two"), "\"hello\", \"world\"");
        Assert.assertEquals(konfiger.get("Three"), "3");
        Assert.assertEquals(konfiger.get("Four"), "4");
        Assert.assertEquals(konfiger.get("Five"), "true");
        Assert.assertEquals(konfiger.get("Six"), "false");
        Assert.assertEquals(konfiger.get("Seven"), "121251656.1367367263726");
        Assert.assertEquals(konfiger.get("Eight"), "0.21");
    }

    @Test
    public void Validate_LazyLoad_Konfiger_Entries_Get_With_Fallback() throws IOException, InvalidEntryException {
        Konfiger konfiger = new Konfiger(new File("src/test/resources/test.config.ini"), true);

        Assert.assertEquals(konfiger.get("Occupation", "Pen Tester"), "Software Engineer");
        Assert.assertEquals(konfiger.get("Hobby", "Worm Creation"), "i don't know");
        Assert.assertNull(konfiger.get("Fav OS"));
        Assert.assertNotNull(konfiger.get("Fav OS", "Whatever get work done"));
    }

    @Test
    public void Validate_Konfiger_Entries_Get_Returned_Types() throws IOException, InvalidEntryException {
        Konfiger konfiger = new Konfiger("");
        konfiger.put("One", konfiger);
        konfiger.putLong("Two", 123456789);
        konfiger.putBoolean("Bool", true);
        konfiger.putFloat("Float", 123.56F);
        konfiger.putString("Dummy", "Noooooo 1");
        konfiger.putString("Dummy2", "Noooooo 2");

        Assert.assertEquals(konfiger.get("Two"), "123456789");
        Assert.assertEquals(konfiger.getLong("Two"), 123456789);
        Assert.assertNotEquals(konfiger.getLong("Two"), "123456789");

        Assert.assertEquals(konfiger.get("Bool"), "true");
        Assert.assertFalse(konfiger.getBoolean("Two"));
        Assert.assertNotEquals(konfiger.getBoolean("Two"), true);
        Assert.assertNotEquals(konfiger.getBoolean("Two"), "true");

        Assert.assertEquals(konfiger.get("Float"), "123.56");
        Assert.assertEquals(konfiger.getFloat("Float"), 123.56F, 0.00f);
        Assert.assertNotEquals(konfiger.getFloat("Float"), "123.56");
    }

    @Test
    public void Validate_Konfiger_Default_Value_For_Non_Existing_Key() throws IOException, InvalidEntryException {
        Konfiger konfiger = new Konfiger("");
        konfiger.errorTolerance(true);

        Assert.assertNull(konfiger.get("Name"));
        Assert.assertNotEquals(konfiger.getString("Name"), null);
        Assert.assertEquals(konfiger.getString("Name"), "");
        Assert.assertNotEquals(konfiger.get("Name", "Adewale Azeez"), null);
        Assert.assertEquals(konfiger.get("Name", "Adewale Azeez"), "Adewale Azeez");
        Assert.assertFalse(konfiger.getBoolean("CleanupOnClose"));
        Assert.assertNotEquals(konfiger.getBoolean("CleanupOnClose", true), false);
        Assert.assertEquals(konfiger.getLong("TheNumber"), 0);
        Assert.assertEquals(konfiger.getLong("TheNumber", 123), 123);
        Assert.assertEquals(konfiger.getFloat("TheNumber"), 0.0, 0.0F);
        Assert.assertNotEquals(konfiger.getFloat("TheNumber"), 0.1);
    }

    @Test
    public void Remove_Entry_And_Validate_Size() throws IOException, InvalidEntryException {
        Konfiger konfiger = new Konfiger("One=111,Two=222,Three=333", false, '=', ',');
        konfiger.errorTolerance(true);

        Assert.assertEquals(konfiger.size(), 3);
        Assert.assertNotEquals(konfiger.get("Two"), null);
        Assert.assertEquals(konfiger.remove("Two"), "222");
        Assert.assertNull(konfiger.get("Two"));
        Assert.assertEquals(konfiger.size(), 2);
        Assert.assertEquals(konfiger.remove(0), "111");
        Assert.assertEquals(konfiger.size(), 1);
        Assert.assertEquals(konfiger.get("Three"), "333");
    }

    @Test
    public void Set_Get_Delimeter_And_Seperator() throws IOException, InvalidEntryException {
        Konfiger konfiger = new Konfiger(new File("src/test/resources/test.config.ini"), true);

        Assert.assertEquals(konfiger.getSeperator(), '\n');
        Assert.assertEquals(konfiger.getDelimeter(), '=');
        Assert.assertTrue(konfiger.toString().split("\n").length > 2);
        konfiger.setSeperator('-');
        konfiger.setDelimeter('+');
        Assert.assertEquals(konfiger.getSeperator(), '-');
        Assert.assertEquals(konfiger.getDelimeter(), '+');
        Assert.assertEquals(konfiger.toString().split("\n").length, 1);
    }

    @Test
    public void Escaping_And_Unescaping_Entries_And_Save() throws IOException, InvalidEntryException {
        KonfigerStream ks = new KonfigerStream(new File("src/test/resources/test.config.ini"));
        KonfigerStream ks1 = new KonfigerStream(new File("src/test/resources/test.txt"), ':',  ',');
        Konfiger konfiger = new Konfiger(ks, true);
        Konfiger konfiger1 = new Konfiger(ks1, true);

        Assert.assertEquals(konfiger.get("Hobby"), "i don't know");
        Assert.assertEquals(konfiger1.get("Hobby"), konfiger.get("Hobby"));
        Assert.assertEquals(konfiger1.get("Hobby"), "i don't know");
        konfiger.save("src/test/resources/test.config.ini");

        KonfigerStream newKs = new KonfigerStream(new File("src/test/resources/test.config.ini"));
        Konfiger newKonfiger = new Konfiger(newKs, true);
        Konfiger newKonfiger1 = new Konfiger(new File("src/test/resources/test.txt"), true, ':',  ',');
        Assert.assertEquals(konfiger.toString(), newKonfiger.toString());
        Assert.assertEquals(konfiger1.toString(), newKonfiger1.toString());
    }

    @Test
    public void Test_Complex_And_Confusing_Seperator() throws IOException, InvalidEntryException {
        Konfiger konfiger = new Konfiger("Occupation=Software En/gineergLocation=Ni/geriagState=La/gos", false, '=', 'g');

        Assert.assertEquals(konfiger.size(), 3);
        Assert.assertTrue(konfiger.toString().contains("/g"));
        for (String key : konfiger.entries().keySet()) {
            Assert.assertFalse(konfiger.getString(key).contains("/g"));
        }
        konfiger.setSeperator('f');
        Assert.assertEquals(konfiger.get("Occupation"), "Software Engineer");
        konfiger.setSeperator('\n');
        Assert.assertFalse(konfiger.toString().contains("/g"));
        Assert.assertEquals(konfiger.size(), 3);
        for (String key : konfiger.entries().keySet()) {
            Assert.assertFalse(konfiger.getString(key).contains("\\g"));
        }
    }

    @Test
    public void Append_New_Unparsed_Entries_From_String_And_File() throws IOException, InvalidEntryException {
        Konfiger konfiger = new Konfiger("");

        Assert.assertEquals(konfiger.size(), 0);
        konfiger.appendString("Language=English");
        Assert.assertEquals(konfiger.size(), 1);
        Assert.assertNull(konfiger.get("Name"));
        Assert.assertNotEquals(konfiger.get("Name"), "Adewale Azeez");
        Assert.assertEquals(konfiger.get("Language"), "English");

        konfiger.appendFile(new File("src/test/resources/test.config.ini"));
        Assert.assertNotEquals(konfiger.get("Name"), null);
        Assert.assertEquals(konfiger.get("Name"), "Adewale Azeez");
    }

    @Test
    public void Test_Prev_And_Current_Cache() throws IOException, InvalidEntryException {
        Konfiger konfiger = new Konfiger("");

        konfiger.put("Name", "Adewale");
        konfiger.put("Project", "konfiger");
        konfiger.putInt("Year", 2020);

        Assert.assertEquals(konfiger.getInt("Year"), 2020);
        Assert.assertEquals(konfiger.get("Project"), "konfiger");
        Assert.assertEquals(konfiger.get("Name"), "Adewale");
        Assert.assertEquals(konfiger.getInt("Year"), 2020);
        Assert.assertEquals(KonfigerFieldsExposer.getCurrentCachedObject(konfiger)[0], "Name");
        Assert.assertEquals(KonfigerFieldsExposer.getPrevCachedObject(konfiger)[0], "Year");
        Assert.assertEquals(KonfigerFieldsExposer.getCurrentCachedObject(konfiger)[1], "Adewale");
        Assert.assertEquals(KonfigerFieldsExposer.getPrevCachedObject(konfiger)[1], "2020");
        Assert.assertEquals(konfiger.get("Name"), "Adewale");
        Assert.assertEquals(konfiger.get("Name"), "Adewale");
        Assert.assertEquals(konfiger.get("Project"), "konfiger");
        Assert.assertEquals(konfiger.get("Name"), "Adewale");
        Assert.assertEquals(konfiger.get("Name"), "Adewale");
        Assert.assertEquals(konfiger.get("Name"), "Adewale");
        Assert.assertEquals(KonfigerFieldsExposer.getCurrentCachedObject(konfiger)[0], "Project");
        Assert.assertEquals(KonfigerFieldsExposer.getPrevCachedObject(konfiger)[0], "Name");
        Assert.assertEquals(KonfigerFieldsExposer.getCurrentCachedObject(konfiger)[1], "konfiger");
        Assert.assertEquals(KonfigerFieldsExposer.getPrevCachedObject(konfiger)[1], "Adewale");
    }

    @Test
    public void Test_The_Single_Pair_Commenting_In_String_Stream_Konfiger() throws IOException, InvalidEntryException {
        KonfigerStream ks = new KonfigerStream("Name:Adewale Azeez,//Project:konfiger,Date:April 24 2020", ':', ',');
        Konfiger kon = new Konfiger(ks);
        for (String key : kon.keys()) {
            Assert.assertNotEquals(kon.getString(key), "Project");
        }
        Assert.assertEquals(kon.size(), 2);
    }

}
