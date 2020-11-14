package io.github.thecarisma;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class TestResolve_Java {

    static class TextsFlat {
        String project;
        String author;
        String Platform;
        String File;
    }

    static class Texts {
        String project;
        String author;
        String Platform;
        String file;

        String matchGetKey(String key) {
            switch (key) {
                case "project":
                    return "Project";
                case "author":
                    return "Author";
                case "file":
                    return "File";
            }
            return "";
        }

        String matchPutKey(String key) {
            switch (key) {
                case "Project":
                    return "project";
                case "Author":
                    return "author";
                case "File":
                    return "file";
            }
            return "";
        }
    }

    static class TextsAnnotated {
        @KonfigerKey("Project") String project;
        @KonfigerKey("Author") String author;
        String Platform;
        @KonfigerKey("File") String file;
    }

    static class Entries {
        String project = "konfiger";
        String author = "Adewale Azeez";
        String platform = "Cross Platform";
        String file = "test.comment.inf";
    }

    static class EntriesAnnotated {
        @KonfigerKey("Project") String project = "konfiger";
        @KonfigerKey("Author") String author = "Adewale Azeez";
        String Platform = "Cross Platform";
        @KonfigerKey("File") String file = "test.comment.inf";
    }

    static class MixedTypes {
        String project;
        boolean weAllCake;
        long ageOfEarth;
        int lengthOfRiverNile;
        float pi;
        double pie;
        @KonfigerKey("AnnotatedEntry") boolean annotatedEntry;
    }

    static class MixedTypesEntries {
        String project = "konfiger";
        boolean weAllCake = true;
        long ageOfEarth = 121526156252322L;
        int lengthOfRiverNile = 45454545;
        float pi = 3.14F;
        double pie = 1.1121;
    }

    @Test
    public void Invalid_Argument_Type_To_Konfiger_Resolve() throws IOException, InvalidEntryException, IllegalAccessException, InvocationTargetException {
        Konfiger kon = new Konfiger(new File("src/test/resources/test.config.ini"), true);
        kon.resolve(123);
    }

    @Test
    public void Resolve_Without_matchGetKey_Function() throws IOException, InvalidEntryException, IllegalAccessException, InvocationTargetException {
        TextsFlat textsFlat = new TextsFlat();
        KonfigerStream kStream = new KonfigerStream(new File("src/test/resources/test.comment.inf"));
        kStream.setCommentPrefix("[");
        Konfiger kon = new Konfiger(kStream);
        kon.resolve(textsFlat);

        Assert.assertNull(textsFlat.project);
        Assert.assertEquals(textsFlat.Platform, "Cross Platform");
        Assert.assertEquals(textsFlat.File, "test.comment.inf");
        Assert.assertNull(textsFlat.author);
    }

    @Test
    public void Resolve_With_matchGetKey_Function() throws IOException, InvalidEntryException, IllegalAccessException, InvocationTargetException {
        Texts texts = new Texts();
        KonfigerStream kStream = new KonfigerStream(new File("src/test/resources/test.comment.inf"));
        kStream.setCommentPrefix("[");
        Konfiger kon = new Konfiger(kStream);
        kon.resolve(texts);

        Assert.assertEquals(texts.project, "konfiger");
        Assert.assertEquals(texts.Platform, "Cross Platform");
        Assert.assertEquals(texts.file, "test.comment.inf");
        Assert.assertEquals(texts.author, "Adewale Azeez");
    }

    @Test
    public void Resolve_With_Changing_Values_And_Map_Key_With_matchPutKey() throws IOException, InvalidEntryException, IllegalAccessException, InvocationTargetException {
        Texts texts = new Texts();
        KonfigerStream kStream = new KonfigerStream(new File("src/test/resources/test.comment.inf"));
        kStream.setCommentPrefix("[");
        Konfiger kon = new Konfiger(kStream);
        kon.resolve(texts);

        Assert.assertEquals(texts.project, "konfiger");
        Assert.assertEquals(texts.Platform, "Cross Platform");
        Assert.assertEquals(texts.file, "test.comment.inf");
        Assert.assertEquals(texts.author, "Adewale Azeez");

        kon.put("Project", "konfiger-nodejs");
        kon.put("Platform", "Windows, Linux, Mac, Raspberry");
        kon.put("author", "Thecarisma");

        Assert.assertEquals(texts.project, "konfiger-nodejs");
        Assert.assertTrue(texts.Platform.contains("Windows"));
        Assert.assertTrue(texts.Platform.contains("Linux"));
        Assert.assertTrue(texts.Platform.contains("Mac"));
        Assert.assertTrue(texts.Platform.contains("Raspberry"));
        Assert.assertEquals(texts.author, "Thecarisma");

        kon.put("author", "Adewale");
        Assert.assertEquals(texts.author, "Adewale");
    }

    @Test
    public void Resolve_With_Changing_Values_And_Map_Key_With_matchPutKey_Using_Annotation() throws IOException, InvalidEntryException, IllegalAccessException, InvocationTargetException {
        TextsAnnotated texts = new TextsAnnotated();
        KonfigerStream kStream = new KonfigerStream(new File("src/test/resources/test.comment.inf"));
        kStream.setCommentPrefix("[");
        Konfiger kon = new Konfiger(kStream);
        kon.resolve(texts);

        Assert.assertEquals(texts.project, "konfiger");
        Assert.assertEquals(texts.Platform, "Cross Platform");
        Assert.assertEquals(texts.file, "test.comment.inf");
        Assert.assertEquals(texts.author, "Adewale Azeez");

        kon.put("Project", "konfiger-nodejs");
        kon.put("Platform", "Windows, Linux, Mac, Raspberry");
        kon.put("author", "Thecarisma");

        Assert.assertEquals(texts.project, "konfiger-nodejs");
        Assert.assertTrue(texts.Platform.contains("Windows"));
        Assert.assertTrue(texts.Platform.contains("Linux"));
        Assert.assertTrue(texts.Platform.contains("Mac"));
        Assert.assertTrue(texts.Platform.contains("Raspberry"));
        Assert.assertEquals(texts.author, "Thecarisma");

        kon.put("author", "Adewale");
        Assert.assertEquals(texts.author, "Adewale");
    }

    @Test
    public void Dissolve_An_Object_Into_Konfiger() throws IOException, InvalidEntryException, IllegalAccessException, InvocationTargetException {
        Konfiger kon = new Konfiger("");
        kon.dissolve(new Entries());

        Assert.assertEquals(kon.get("project"), "konfiger");
        Assert.assertEquals(kon.get("platform"), "Cross Platform");
        Assert.assertEquals(kon.get("file"), "test.comment.inf");
        Assert.assertEquals(kon.get("author"), "Adewale Azeez");
    }

    @Test
    public void Dissolve_An_Object_Into_Konfiger_Using_Annotation() throws IOException, InvalidEntryException, IllegalAccessException, InvocationTargetException {
        Konfiger kon = new Konfiger("");
        kon.dissolve(new EntriesAnnotated());

        Assert.assertEquals(kon.get("Project"), "konfiger");
        Assert.assertEquals(kon.get("Platform"), "Cross Platform");
        Assert.assertEquals(kon.get("File"), "test.comment.inf");
        Assert.assertEquals(kon.get("Author"), "Adewale Azeez");
    }

    @Test
    public void Detach_An_Object_From_Konfiger() throws IOException, InvalidEntryException, IllegalAccessException, InvocationTargetException {
        Texts texts = new Texts();
        KonfigerStream kStream = new KonfigerStream(new File("src/test/resources/test.comment.inf"));
        kStream.setCommentPrefix("[");
        Konfiger kon = new Konfiger(kStream);
        kon.resolve(texts);

        Assert.assertEquals(texts.project, "konfiger");
        Assert.assertEquals(texts.Platform, "Cross Platform");
        Assert.assertEquals(texts.file, "test.comment.inf");
        Assert.assertEquals(texts.author, "Adewale Azeez");
        Assert.assertEquals(texts, kon.detach());

        kon.put("Project", "konfiger-nodejs");
        kon.put("Platform", "Windows, Linux, Mac, Raspberry");
        kon.put("author", "Thecarisma");

        Assert.assertNotEquals(texts.project, "konfiger-nodejs");
        Assert.assertFalse(texts.Platform.contains("Windows"));
        Assert.assertFalse(texts.Platform.contains("Linux"));
        Assert.assertFalse(texts.Platform.contains("Mac"));
        Assert.assertFalse(texts.Platform.contains("Raspberry"));
        Assert.assertNotEquals(texts.author, "Thecarisma");

        kon.put("author", "Adewale");
        Assert.assertNotEquals(texts.author, "Adewale");
    }

    @Test
    public void Resolve_With_matchGetKey_Function_MixedTypes() throws IOException, InvalidEntryException, IllegalAccessException, InvocationTargetException {
        MixedTypes entries = new MixedTypes();
        Konfiger kon = new Konfiger(new File("src/test/resources/mixed.types"));
        kon.resolve(entries);

        Assert.assertEquals(entries.project, "konfiger");
        Assert.assertNotEquals(entries.weAllCake, "true");
        Assert.assertTrue(entries.weAllCake);
        Assert.assertTrue(entries.annotatedEntry);
        Assert.assertNotEquals(entries.ageOfEarth, "121526156252322");
        Assert.assertEquals(entries.ageOfEarth, 121526156252322L);
        Assert.assertNotEquals(entries.lengthOfRiverNile, "45454545");
        Assert.assertEquals(entries.lengthOfRiverNile, 45454545);
        Assert.assertNotEquals(entries.pi, "3.14");
        Assert.assertEquals(entries.pi, 3.14, 1);
        Assert.assertNotEquals(entries.pie, "1.1121");
        Assert.assertEquals(entries.pie, 1.1121, 1);
    }

    @Test
    public void Dissolve_An_MixedTypes_Object_Into_Konfiger() throws IOException, InvalidEntryException, IllegalAccessException, InvocationTargetException {
        Konfiger kon = new Konfiger("");
        kon.dissolve(new MixedTypesEntries());

        Assert.assertEquals(kon.get("project"), "konfiger");
        Assert.assertEquals(kon.get("weAllCake"), "true");
        Assert.assertTrue(kon.getBoolean("weAllCake"));
        Assert.assertEquals(kon.get("ageOfEarth"), "121526156252322");
        Assert.assertEquals(kon.getLong("ageOfEarth"), 121526156252322L);
        Assert.assertEquals(kon.get("lengthOfRiverNile"), "45454545");
        Assert.assertEquals(kon.getInt("lengthOfRiverNile"), 45454545);
        Assert.assertEquals(kon.get("pi"), "3.14");
        Assert.assertEquals(kon.getFloat("pi"), 3.14, 1);
        Assert.assertEquals(kon.get("pie"), "1.1121");
        Assert.assertEquals(kon.getDouble("pie"), 1.1121, 1);
    }

    @Test
    public void Resolve_With_Changing_Values_For_MixedTypes() throws IOException, InvalidEntryException, IllegalAccessException, InvocationTargetException {
        MixedTypes texts = new MixedTypes();
        Konfiger kon = new Konfiger(new File("src/test/resources/mixed.types"));
        kon.resolve(texts);

        Assert.assertEquals(texts.project, "konfiger");
        Assert.assertTrue(texts.weAllCake);
        Assert.assertEquals(texts.ageOfEarth, 121526156252322L);
        Assert.assertEquals(texts.lengthOfRiverNile, 45454545);
        Assert.assertEquals(texts.pi, 3.14F, 1);
        Assert.assertEquals(texts.pie, 1.1121, 1);
        Assert.assertTrue(texts.annotatedEntry);

        kon.put("project", "konfiger-nodejs");
        kon.put("AnnotatedEntry", false);
        kon.put("ageOfEarth", 121323L);
        kon.put("pie", 2.1212);

        Assert.assertEquals(texts.project, "konfiger-nodejs");
        Assert.assertFalse(texts.annotatedEntry);
        Assert.assertEquals(texts.ageOfEarth, 121323L);
        Assert.assertEquals(texts.pie, 2.1212, 1);

        kon.put("AnnotatedEntry", true);
        Assert.assertTrue(texts.annotatedEntry);
    }

    @Test
    public void Resolve_With_Changing_Values_And_Map_Key_With_attach() throws IOException, InvalidEntryException, IllegalAccessException, InvocationTargetException {
        Texts texts = new Texts();
        KonfigerStream kStream = new KonfigerStream(new File("src/test/resources/test.comment.inf"));
        kStream.setCommentPrefix("[");
        Konfiger kon = new Konfiger(kStream);
        kon.attach(texts);

        Assert.assertNotEquals(texts.project, "konfiger");
        Assert.assertNotEquals(texts.Platform, "Cross Platform");
        Assert.assertNotEquals(texts.author, "Adewale Azeez");

        kon.put("Project", "konfiger-nodejs");
        kon.put("Platform", "Windows, Linux, Mac, Raspberry");
        kon.put("author", "Thecarisma");

        Assert.assertEquals(texts.project, "konfiger-nodejs");
        Assert.assertTrue(texts.Platform.contains("Windows"));
        Assert.assertTrue(texts.Platform.contains("Linux"));
        Assert.assertTrue(texts.Platform.contains("Mac"));
        Assert.assertTrue(texts.Platform.contains("Raspberry"));
        Assert.assertEquals(texts.author, "Thecarisma");

        kon.put("author", "Adewale");
        Assert.assertEquals(texts.author, "Adewale");
    }

}
