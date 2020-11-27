package io.github.thecarisma;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class TestStream_Java {

    @Test(expected = FileNotFoundException.class)
    public void Should_Throw_Exceptions() {
        KonfigerStream ks = new KonfigerStream(new File("tryer.ini"));
    }

    @Test
    public void Should_Successfully_Initialize() {
        KonfigerStream ks = new KonfigerStream(new File("./README.md"));
    }

    @Test
    public void Validate_The_File_Stream_Value() {
        KonfigerStream ks = new KonfigerStream(new File("src/test/resources/test.config.ini"));
        while (ks.hasNext()) {
            Assert.assertNotEquals(ks.next(), null);
        }
    }

    @Test
    public void Validate_The_String_Stream_Key() {
        KonfigerStream ks = new KonfigerStream(" Name =Adewale Azeez,Project =konfiger, Date=April 24 2020", '=', ',');
        Assert.assertEquals(ks.next().getKey(), "Name");
        Assert.assertEquals(ks.next().getKey(), "Project");
        Assert.assertEquals(ks.next().getKey(), "Date");
    }

    @Test
    public void validate_The_String_Stream_Value() {
        KonfigerStream ks = new KonfigerStream("Name= Adewale Azeez,Project= konfiger , Date=April 24 2020", '=', ',');
        Assert.assertEquals(ks.next().getValues().get(0), "Adewale Azeez");
        Assert.assertEquals(ks.next().getValues().get(0), "konfiger");
        Assert.assertEquals(ks.next().getValues().get(0), "April 24 2020");
    }

    @Test
    public void test_String_Stream_Key_Trimming() {
        KonfigerStream ks = new KonfigerStream(" Name =Adewale Azeez:Project =konfiger: Date=April 24 2020", '=', ':');
        Assert.assertTrue(ks.isTrimmingKey());
        ks.setTrimmingKey(false);
        Assert.assertFalse(ks.isTrimmingKey());
        Assert.assertEquals(ks.next().getKey(), " Name ");
        Assert.assertEquals(ks.next().getKey(), "Project ");
        Assert.assertEquals(ks.next().getKey(), " Date");
    }

    @Test
    public void Test_The_Single_Pair_Commenting_In_String_Stream() {
        KonfigerStream ks = new KonfigerStream("Name:Adewale Azeez,;Project:konfiger,Date:April 24 2020", ':', ',');
        while (ks.hasNext()) {
            Assert.assertNotEquals(ks.next().getKey(), "Project");
        }
    }

    @Test(expected = InvalidEntryException.class)
    public void Test_The_Single_Pair_Commenting_In_File_Stream_1() {
        KonfigerStream ks = new KonfigerStream(new File("src/test/resources/test.comment.inf"));
        ks.setCommentPrefix("[");
        while (ks.hasNext()) {
            Assert.assertFalse(ks.next().getKey().startsWith("["));
        }
    }

    @Test
    public void testMultipleCommentPrefixFile() {
        KonfigerStream ks = new KonfigerStream(new File("src/test/resources/test.comment.inf"));
        ks.setCommentPrefixes("[", ";", "#", "@", "<>");
        while (ks.hasNext()) {
            SectionEntry entry = ks.next();
            Assert.assertFalse(entry.getKey().startsWith("["));
        }
    }

    @Test
    public void testMultipleCommentPrefixString() {
        KonfigerStream ks = new KonfigerStream("; The second part\n" +
                "[Second Part]\n" +
                "# This is also a comment\n" +
                "Version=2.1.3 / 2.1.5\n" +
                "Date=April 2020 ; Inline comment\n" +
                "Platform=Cross Platform");
        ks.setCommentPrefixes("[", ";", "#", "@", "<>");
        while (ks.hasNext()) {
            SectionEntry entry = ks.next();
            Assert.assertFalse(entry.getKey().startsWith("["));
        }
    }

    @Test
    public void testEntriesCommentFile() {
        KonfigerStream ks = new KonfigerStream(new File("src/test/resources/test.comment.inf"));
        ks.setCommentPrefixes(";", "#", "@", "<>");
        while (ks.hasNext()) {
            SectionEntry entry = ks.next();
            Assert.assertFalse(entry.getKey().startsWith(";"));
            Assert.assertFalse(entry.getKey().startsWith("#"));
            Assert.assertFalse(entry.getKey().startsWith("@"));
            Assert.assertFalse(entry.getKey().startsWith("<>"));
        }
    }

    @Test
    public void testTheSinglePairCommentingInFileStream() {
        KonfigerStream ks = new KonfigerStream(new File("src/test/resources/test.txt"),':', ',');
        ks.setCommentPrefixes("//");
        while (ks.hasNext()) {
            Assert.assertFalse(ks.next().getKey().startsWith("//"));
        }
    }

    @Test
    public void Test_String_Stream_Value_Trimming() {
        KonfigerStream ks = new KonfigerStream(" Name =Adewale Azeez :Project = konfiger: Date= April 24 2020 :Language = Multiple Languages", '=', ':');
        Assert.assertNotEquals(ks.isTrimmingValue(), false);
        Assert.assertTrue(ks.isTrimmingValue());
        Assert.assertEquals(ks.next().getValues().get(0), "Adewale Azeez");
        Assert.assertEquals(ks.next().getValues().get(0), "konfiger");
        Assert.assertEquals(ks.next().getValues().get(0), "April 24 2020");
        Assert.assertEquals(ks.next().getValues().get(0), "Multiple Languages");
    }

    @Test
    public void Test_String_Stream_Key_Value_Trimming() {
        String entriesStr = " Name =Adewale Azeez :Project = konfiger: Date= April 24 2020 :Language = Multiple Languages";
        KonfigerStream ks = new KonfigerStream(entriesStr, '=', ':');
        KonfigerStream ks1 = new KonfigerStream(entriesStr, '=', ':');
        Assert.assertEquals(ks.next().getKey(), "Name");
        Assert.assertEquals(ks.next().getKey(), "Project");
        Assert.assertEquals(ks.next().getKey(), "Date");
        Assert.assertEquals(ks.next().getKey(), "Language");

        Assert.assertEquals(ks1.next().getValues().get(0), "Adewale Azeez");
        Assert.assertEquals(ks1.next().getValues().get(0), "konfiger");
        Assert.assertEquals(ks1.next().getValues().get(0), "April 24 2020");
        Assert.assertEquals(ks1.next().getValues().get(0), "Multiple Languages");
    }

    @Test
    public void Read_Multiline_Entry_And_Test_Continuation_Char_In_File_Stream() {
        KonfigerStream ks = KonfigerStream.builder()
                .withFile(new File("src/test/resources/test.contd.conf"))
                .ignoreInlineComment()
                .build();
        while (ks.hasNext()) {
            Assert.assertFalse(ks.next().getValues().get(0).endsWith("\\"));
        }
    }

    @Test
    public void Read_Multiline_Entry_And_Test_Continuation_Char_In_String_Stream() {
        KonfigerStream ks = KonfigerStream.builder()
                .withString("Description = This project is the closest thing to Android +\n" +
                "              [Shared Preference](https://developer.android.com/reference/android/content/SharedPreferences) +\n" +
                "              in other languages and off the Android platform.\n" +
                "ProjectName = konfiger\n" +
                "ProgrammingLanguages = C, C++, C#, Dart, Elixr, Erlang, Go, +\n" +
                "               Haskell, Java, Kotlin, NodeJS, Powershell, +\n" +
                "               Python, Ring, Rust, Scala, Visual Basic, +\n" +
                "               and whatever language possible in the future")
                .ignoreInlineComment()
                .build();
        ks.setContinuationChar('+');
        while (ks.hasNext()) {
            Assert.assertFalse(ks.next().getValues().get(0).endsWith("\\"));
        }
    }

    @Test
    public void Test_Backward_Slash_Ending_Value() {
        KonfigerStream ks = KonfigerStream.builder()
                .withString("uri1 = http://uri1.thecarisma.com/core/api/v1/\r\n" +
                "uri2 = http://uri2.thecarisma.com/core/api/v2/\r\n" +
                "ussd.uri = https://ussd.thecarisma.com/")
                .ignoreInlineComment()
                .build();

        int count = 0;
        while(ks.hasNext()) {
            Assert.assertTrue(ks.next().getValues().get(0).endsWith("/"));
            count++;
        }
        Assert.assertEquals(count, 3);
    }

    @Test
    public void Test_Escape_Slash_Ending() {
        KonfigerStream ks = new KonfigerStream("external-resource-location = \\\\988.43.13.9\\testing\\\\public\\sansportal\\rideon\\\\\r\n" +
                "boarding-link = https://boarding.thecarisma.com/konfiger\r\n" +
                "ussd.uri = thecarisma.com\\");

        int count = 0;
        while(ks.hasNext()) {
            Assert.assertFalse(ks.next().getValues().get(0).isEmpty());
            count++;
        }
        Assert.assertEquals(count, 3);
    }

    @Test
    public void Test_Error_Tolerancy_In_String_Stream() {
        KonfigerStream ks = new KonfigerStream("Firt=1st", '-', '$', true);

        Assert.assertTrue(ks.isErrorTolerant());
        while(ks.hasNext()) {
            Assert.assertTrue(ks.next().getValues().get(0).isEmpty());
        }
    }

    @Test
    public void Test_Error_Tolerancy_In_File_Stream() {
        KonfigerStream ks = new KonfigerStream(new File("src/test/resources/test.comment.inf"));

        Assert.assertFalse(ks.isErrorTolerant());
        while(ks.hasNext()) {
            try {
                Assert.assertFalse(ks.next().getValues().get(0).isEmpty());
            } catch (InvalidEntryException ex) {
                break;
            }
        }
        ks.errorTolerance(true);
        Assert.assertTrue(ks.isErrorTolerant());
        while(ks.hasNext()) {
            Assert.assertNotEquals(ks.next(), null);
        }
    }



    @Test
    public void testBuilder() {
        KonfigerStream.Builder builder = KonfigerStream.builder()
                .withCommentPrefixes("[", ";", "@")
                .withErrTolerance()
                .withDelimiter(':');
        Assert.assertEquals(builder.commentPrefixes.length, 3);
        Assert.assertTrue(builder.errTolerance);
        Assert.assertEquals(builder.delimiter, ':');
    }

    @Test(expected = IllegalStateException.class)
    public void testBuilderSingleStreamSource() {
        KonfigerStream.builder()
                .withString("Name=thecarisma")
                .withFile(new File("src/test/resources/test.comment.inf"));
    }

    @Test
    public void testConstructorWithBuilder() {
        KonfigerStream ks = new KonfigerStream(KonfigerStream.builder()
                .withString("Description = This project is the closest thing to Android +\n" +
                        "              [Shared Preference](https://developer.android.com/reference/android/content/SharedPreferences) +\n" +
                        "              in other languages and off the Android platform.\n" +
                        "~ProjectName = konfiger\n" +
                        "## This is a comment\n" +
                        "~ This is another comment\n" +
                        "ProgrammingLanguages = C, C++, C#, Dart, Elixr, Erlang, Go, +\n" +
                        "               Haskell, Java, Kotlin, NodeJS, Powershell, +\n" +
                        "               Python, Ring, Rust, Scala, Visual Basic, +\n" +
                        "               and whatever language possible in the future")
                .withContinuationChar('+')
                .withCommentPrefixes("##", "~")
        );
        while (ks.hasNext()) {
            Assert.assertFalse(ks.next().getValues().get(0).endsWith("\\"));
        }
    }

}
