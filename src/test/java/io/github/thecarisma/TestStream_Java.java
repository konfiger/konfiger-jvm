package io.github.thecarisma;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class TestStream_Java {

    @Test(expected = FileNotFoundException.class)
    public void Should_Throw_Exceptions() {
        KonfigerStream ks = KonfigerStream.builder()
                .withFile(new File("tryer.ini")).build();
    }

    @Test
    public void Should_Successfully_Initialize() {
        KonfigerStream ks = KonfigerStream.builder()
                .withFile(new File("./README.md")).build();
    }

    @Test
    public void Validate_The_File_Stream_Value() {
        KonfigerStream ks = KonfigerStream.builder()
                .withFile(new File("src/test/resources/test.config.ini")).build();
        while (ks.hasNext()) {
            Assert.assertNotEquals(ks.next(), null);
        }
    }

    @Test
    public void Validate_The_String_Stream_Key() {
        KonfigerStream ks = KonfigerStream.builder()
                .withString(" Name =Adewale Azeez,Project =konfiger, Date=April 24 2020")
                .withDelimiters(new char[]{'='})
                .withSeparators(new char[]{','})
                .build();
        Assert.assertEquals(ks.next().getKey(), "Name");
        Assert.assertEquals(ks.next().getKey(), "Project");
        Assert.assertEquals(ks.next().getKey(), "Date");
    }

    @Test
    public void validate_The_String_Stream_Value() {
        KonfigerStream ks = KonfigerStream.builder()
                .withString("Name= Adewale Azeez,Project= konfiger , Date=April 24 2020")
                .withDelimiters(new char[]{'='})
                .withSeparators(new char[]{','})
                .build();
        Assert.assertEquals(ks.next().getValues().get(0), "Adewale Azeez");
        Assert.assertEquals(ks.next().getValues().get(0), "konfiger");
        Assert.assertEquals(ks.next().getValues().get(0), "April 24 2020");
    }

    @Test
    public void test_String_Stream_Key_Trimming() {
        KonfigerStream ks = KonfigerStream.builder()
                .withString(" Name =Adewale Azeez:Project =konfiger: Date=April 24 2020")
                .withDelimiters(new char[]{'='})
                .withSeparators(new char[]{':'})
                .withTrimmingKey(false)
                .build();
        Assert.assertFalse(ks.getBuilder().isTrimmingKey());
        Assert.assertEquals(ks.next().getKey(), " Name ");
        Assert.assertEquals(ks.next().getKey(), "Project ");
        Assert.assertEquals(ks.next().getKey(), " Date");
    }

    @Test
    public void Test_The_Single_Pair_Commenting_In_String_Stream() {
        KonfigerStream ks = KonfigerStream.builder()
                .withString("Name:Adewale Azeez,;Project:konfiger,Date:April 24 2020")
                .withDelimiters(new char[]{':'})
                .withSeparators(new char[]{','})
                .build();
        while (ks.hasNext()) {
            Assert.assertNotEquals(ks.next().getKey(), "Project");
        }
    }

    @Test
    public void Test_The_Single_Pair_Commenting_In_File_Stream_1() {
        KonfigerStream ks = KonfigerStream.builder()
                .withFile(new File("src/test/resources/test.comment.inf"))
                .withCommentPrefixes("[")
                .build();
        while (ks.hasNext()) {
            Assert.assertFalse(ks.next().getKey().startsWith("["));
        }
    }

    @Test
    public void testMultipleCommentPrefixFile() {
        KonfigerStream ks = KonfigerStream.builder()
                .withFile(new File("src/test/resources/test.comment.inf"))
                .withCommentPrefixes("[", ";", "#", "@", "<>")
                .build();
        while (ks.hasNext()) {
            Entry entry = ks.next();
            Assert.assertFalse(entry.getKey().startsWith("["));
        }
    }

    @Test
    public void testMultipleCommentPrefixString() {
        KonfigerStream ks = KonfigerStream.builder()
                .withString("; The second part\n" +
                        "[Second Part]\n" +
                        "# This is also a comment\n" +
                        "Version=2.1.3 / 2.1.5\n" +
                        "Date=April 2020 ; Inline comment\n" +
                        "Platform=Cross Platform")
                .withCommentPrefixes("[", ";", "#", "@", "<>")
                .build();
        while (ks.hasNext()) {
            Entry entry = ks.next();
            Assert.assertFalse(entry.getKey().startsWith("["));
        }
    }

    @Test
    public void testEntriesCommentFile() {
        KonfigerStream ks = KonfigerStream.builder()
                .withFile(new File("src/test/resources/test.comment.inf"))
                .withCommentPrefixes(";", "#", "@", "<>")
                .build();
        while (ks.hasNext()) {
            Entry entry = ks.next();
            Assert.assertFalse(entry.getKey().startsWith(";"));
            Assert.assertFalse(entry.getKey().startsWith("#"));
            Assert.assertFalse(entry.getKey().startsWith("@"));
            Assert.assertFalse(entry.getKey().startsWith("<>"));
        }
    }

    @Test
    public void testTheSinglePairCommentingInFileStream() {
        KonfigerStream ks = KonfigerStream.builder()
                .withFile(new File("src/test/resources/test.txt"))
                .withDelimiters(new char[]{':'})
                .withSeparators(new char[]{','})
                .withCommentPrefixes("//")
                .build();
        while (ks.hasNext()) {
            Assert.assertFalse(ks.next().getKey().startsWith("//"));
        }
    }

    @Test
    public void Test_String_Stream_Value_Trimming() {
        KonfigerStream ks = KonfigerStream.builder()
                .withString(" Name =Adewale Azeez :Project = konfiger: Date= April 24 2020 :Language = Multiple Languages")
                .withDelimiters(new char[]{'='})
                .withSeparators(new char[]{':'})
                .build();
        Assert.assertNotEquals(ks.getBuilder().isTrimmingValue(), false);
        Assert.assertTrue(ks.getBuilder().isTrimmingValue());
        Assert.assertEquals(ks.next().getValues().get(0), "Adewale Azeez");
        Assert.assertEquals(ks.next().getValues().get(0), "konfiger");
        Assert.assertEquals(ks.next().getValues().get(0), "April 24 2020");
        Assert.assertEquals(ks.next().getValues().get(0), "Multiple Languages");
    }

    @Test
    public void Test_String_Stream_Key_Value_Trimming() {
        String entriesStr = " Name =Adewale Azeez :Project = konfiger: Date= April 24 2020 :Language = Multiple Languages";
        Builder builder = KonfigerStream.builder()
                .withString(entriesStr)
                .withDelimiters(new char[]{'='})
                .withSeparators(new char[]{':'});
        KonfigerStream ks = builder.build();
        KonfigerStream ks1 = builder.build();
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
                .wrapMultilineValue()
                .build();
        while (ks.hasNext()) {
            System.out.println(ks.next().getKey());
            //Assert.assertFalse(ks.next().getValues().get(0).endsWith("\\"));
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
                .withTrimmingValue(false)
                .withContinuationChar('+')
                .build();
        while (ks.hasNext()) {
            Assert.assertFalse(ks.next().getValue().endsWith("\\"));
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
        KonfigerStream ks = KonfigerStream.builder()
                .withString("external-resource-location = \\\\988.43.13.9\\testing\\\\public\\sansportal\\rideon\\\\\r\n" +
                        "boarding-link = https://boarding.thecarisma.com/konfiger\r\n" +
                        "ussd.uri = thecarisma.com\\")
                .ignoreInlineComment()
                .build();

        int count = 0;
        while(ks.hasNext()) {
            Assert.assertFalse(ks.next().getValues().get(0).isEmpty());
            count++;
        }
        Assert.assertEquals(count, 3);
    }

    @Test
    public void Test_Error_Tolerancy_In_String_Stream() {
        KonfigerStream ks = KonfigerStream.builder()
                .withString("Firt=1st")
                .withDelimiters(new char[]{'-'})
                .withSeparators(new char[]{'$'})
                .withErrTolerance()
                .ignoreInlineComment()
                .build();

        Assert.assertTrue(ks.getBuilder().isErrTolerance());
        while(ks.hasNext()) {
            Assert.assertTrue(ks.next().getValues().isEmpty());
        }
    }

    //@Test
    public void Test_Error_Tolerancy_In_File_Stream() {
        /*KonfigerStream ks = new KonfigerStream(new File("src/test/resources/test.comment.inf"));

        Assert.assertFalse(ks.isErrorTolerant());
        while(ks.hasNext()) {
            try {
                System.out.println(ks.next().getValues());
            } catch (InvalidEntryException ex) {
                break;
            }
        }
        ks.errorTolerance(true);
        Assert.assertTrue(ks.isErrorTolerant());
        while(ks.hasNext()) {
            Assert.assertNotEquals(ks.next(), null);
        }*/
    }

    @Test
    public void testBuilder() {
        Builder builder = KonfigerStream.builder()
                .withCommentPrefixes("[", ";", "@")
                .withErrTolerance()
                .withDelimiters(new char[]{':'});
        Assert.assertEquals(builder.commentPrefixes.length, 3);
        Assert.assertTrue(builder.errTolerance);
        Assert.assertEquals(builder.delimiters[0], ':');
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

    @Test
    public void testMultipleDelimiterFile() {
        KonfigerStream ks = KonfigerStream.builder()
                .withFile(new File("src/test/resources/multiple_delimiter.conf"))
                .enableNestedSections()
                .withDelimiters(new char[]{':', '=', '-', '~'})
                .build();

        while (ks.hasNext()) {
            Entry entry = ks.next();
            Assert.assertFalse(entry.getKey().contains(":"));
            Assert.assertFalse(entry.getKey().contains("="));
            Assert.assertFalse(entry.getKey().contains("-"));
            Assert.assertFalse(entry.getKey().contains("~"));
        }
    }

    @Test
    public void testMultipleDelimiterString() {
        KonfigerStream ks = KonfigerStream.builder()
                .withString("key: value\n" +
                        "another_delimiter = equal to as a delimiter\n" +
                        "dashed - using dash as a delimiter\n" +
                        "creepy_delimiter ~ mixing delimiter can cause parse errors and incorrect values")
                .enableIndentedSection()
                .enableNestedSections()
                .withDelimiters(new char[]{':', '=', '-', '~'})
                .build();

        while (ks.hasNext()) {
            Entry entry = ks.next();
            Assert.assertFalse(entry.getKey().contains(":"));
            Assert.assertFalse(entry.getKey().contains("="));
            Assert.assertFalse(entry.getKey().contains("-"));
            Assert.assertFalse(entry.getKey().contains("~"));
        }
    }

    @Test
    public void testMultipleSeparatorFile() {
        KonfigerStream ks = KonfigerStream.builder()
                .withFile(new File("src/test/resources/multiple_separator.conf"))
                .enableIndentedSection()
                .enableNestedSections()
                .withSeparators(new char[]{'\n', '&', '|'})
                .build();

        while (ks.hasNext()) {
            Entry entry = ks.next();
            Assert.assertFalse(entry.getFirstValue().contains("\n"));
            Assert.assertFalse(entry.getFirstValue().contains("&"));
            Assert.assertFalse(entry.getFirstValue().contains("|"));
        }
    }

    @Test
    public void testMultipleSeparatorString() {
        KonfigerStream ks = KonfigerStream.builder()
                .withString("key = value|another_delimiter = equal to as a delimiter&dashed = using dash as a delimiter\n" +
                        "creepy_delimiter = mixing delimiter can cause parse errors and incorrect values")
                .enableIndentedSection()
                .enableNestedSections()
                .withSeparators(new char[]{'\n', '&', '|'})
                .build();

        while (ks.hasNext()) {
            Entry entry = ks.next();
            Assert.assertFalse(entry.getFirstValue().contains("\n"));
            Assert.assertFalse(entry.getFirstValue().contains("&"));
            Assert.assertFalse(entry.getFirstValue().contains("|"));
        }
    }

    @Test
    public void testAOCPassportFileFile() {
        KonfigerStream ks = KonfigerStream.builder()
                .withFile(new File("src/test/resources/passport_file.txt"))
                .withMultilineCommentPrefixes("---")
                .withDelimiters(new char[]{':'})
                .withSeparators(new char[]{' ', '\n'})
                .build();

        while (ks.hasNext()) {
            Entry entry = ks.next();
            Assert.assertFalse(entry.getKey().contains(":"));
            Assert.assertFalse(entry.getFirstValue().contains(" "));
        }
    }

    @Test
    public void testMultilineValueFile() {
        KonfigerStream ks = KonfigerStream.builder()
                .withFile(new File("src/test/resources/multiline_value.conf"))
                .withContinuationChar('\\')
                .withTrimmingValue(false)
                .build();

        while (ks.hasNext()) {
            Entry entry = ks.next();
            Assert.assertFalse(entry.getKey().contains("\\"));
            Assert.assertFalse(entry.getFirstValue().startsWith(Konfiger.DEFAULT_TAB));
            System.out.println(entry.getKey() + " :" + entry.getValue());
        }
    }

    @Test
    public void testMultilineValueString() {
        KonfigerStream ks = KonfigerStream.builder()
                .withString("artist = Travis Scott\n" +
                        "song = Astronimical\n" +
                        "verse1 = She tut it was an ocean \\\n" +
                        "it just a pool \\\n" +
                        "now I got an opium \\\n" +
                        "it just a goo\n" +
                        "\n" +
                        "verse2 = This right here is astronomical\n" +
                        "    I see you picked up on my ways\n" +
                        "    I feel responsible\n" +
                        "\"\"\"year = 19XX\"\"\"" +
                        "\"\"\"year = 201X\"\"\"")
                .withContinuationChar('\\')
                .withTrimmingValue(false)
                .build();

        while (ks.hasNext()) {
            Entry entry = ks.next();
            Assert.assertFalse(entry.getKey().contains("\\"));
            Assert.assertFalse(entry.getFirstValue().startsWith(Konfiger.DEFAULT_TAB));
        }
    }

}
