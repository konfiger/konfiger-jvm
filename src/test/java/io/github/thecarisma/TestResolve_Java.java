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

    static class Entries {
        String project = "konfiger";
        String author = "Adewale Azeez";
        String platform = "Cross Platform";
        String file = "test.comment.inf";
    }

    @Test
    public void Invalid_Argument_Type_To_Konfiger_Resolve() throws IOException, InvalidEntryException, IllegalAccessException, InvocationTargetException {
        Konfiger kon = new Konfiger(new File("src/test/resources/test.comment.inf"), true);
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
    public void Dissolve_An_Object_Into_Konfiger() throws IOException, InvalidEntryException, IllegalAccessException, InvocationTargetException {
        Konfiger kon = new Konfiger("");
        kon.dissolve(new Entries());

        Assert.assertEquals(kon.get("project"), "konfiger");
        Assert.assertEquals(kon.get("platform"), "Cross Platform");
        Assert.assertEquals(kon.get("file"), "test.comment.inf");
        Assert.assertEquals(kon.get("author"), "Adewale Azeez");
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

}
