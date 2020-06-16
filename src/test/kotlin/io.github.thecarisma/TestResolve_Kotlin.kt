package io.github.thecarisma;

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.IOException
import java.lang.reflect.InvocationTargetException

class TextsFlat {
    var project: String? = null
    var author: String? = null
    var Platform: String? = null
    var File: String? = null
}

class Texts {
    var project: String? = null
    var author: String? = null
    var Platform: String? = null
    var file: String? = null
    fun matchGetKey(key: String?): String {
        when (key) {
            "project" -> return "Project"
            "author" -> return "Author"
            "file" -> return "File"
        }
        return ""
    }

    fun matchPutKey(key: String?): String {
        when (key) {
            "Project" -> return "project"
            "Author" -> return "author"
            "File" -> return "file"
        }
        return ""
    }
}

class Entries {
    var project = "konfiger"
    var author = "Adewale Azeez"
    var platform = "Cross Platform"
    var file = "test.comment.inf"
}

class TestResolve_Kotlin {

    @Test
    @Throws(IOException::class, InvalidEntryException::class, IllegalAccessException::class, InvocationTargetException::class)
    fun Invalid_Argument_Type_To_Konfiger_Resolve() {
        val kon = Konfiger(File("src/test/resources/test.comment.inf"), true)
        kon.resolve(123)
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class, IllegalAccessException::class, InvocationTargetException::class)
    fun Resolve_Without_matchGetKey_Function() {
        val textsFlat = TextsFlat()
        val kStream = KonfigerStream(File("src/test/resources/test.comment.inf"))
        kStream.commentPrefix = "["
        val kon = Konfiger(kStream)
        kon.resolve(textsFlat)
        Assert.assertNull(textsFlat.project)
        Assert.assertEquals(textsFlat.Platform, "Cross Platform")
        Assert.assertEquals(textsFlat.File, "test.comment.inf")
        Assert.assertNull(textsFlat.author)
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class, IllegalAccessException::class, InvocationTargetException::class)
    fun Resolve_With_matchGetKey_Function() {
        val texts = Texts()
        val kStream = KonfigerStream(File("src/test/resources/test.comment.inf"))
        kStream.commentPrefix = "["
        val kon = Konfiger(kStream)
        kon.resolve(texts)
        Assert.assertEquals(texts.project, "konfiger")
        Assert.assertEquals(texts.Platform, "Cross Platform")
        Assert.assertEquals(texts.file, "test.comment.inf")
        Assert.assertEquals(texts.author, "Adewale Azeez")
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class, IllegalAccessException::class, InvocationTargetException::class)
    fun Resolve_With_Changing_Values_And_Map_Key_With_matchPutKey() {
        val texts = Texts()
        val kStream = KonfigerStream(File("src/test/resources/test.comment.inf"))
        kStream.commentPrefix = "["
        val kon = Konfiger(kStream)
        kon.resolve(texts)
        Assert.assertEquals(texts.project, "konfiger")
        Assert.assertEquals(texts.Platform, "Cross Platform")
        Assert.assertEquals(texts.file, "test.comment.inf")
        Assert.assertEquals(texts.author, "Adewale Azeez")
        kon.put("Project", "konfiger-nodejs")
        kon.put("Platform", "Windows, Linux, Mac, Raspberry")
        kon.put("author", "Thecarisma")
        Assert.assertEquals(texts.project, "konfiger-nodejs")
        Assert.assertTrue(texts.Platform!!.contains("Windows"))
        Assert.assertTrue(texts.Platform!!.contains("Linux"))
        Assert.assertTrue(texts.Platform!!.contains("Mac"))
        Assert.assertTrue(texts.Platform!!.contains("Raspberry"))
        Assert.assertEquals(texts.author, "Thecarisma")
        kon.put("author", "Adewale")
        Assert.assertEquals(texts.author, "Adewale")
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class, IllegalAccessException::class, InvocationTargetException::class)
    fun Dissolve_An_Object_Into_Konfiger() {
        val kon = Konfiger("")
        kon.dissolve(Entries())
        Assert.assertEquals(kon["project"], "konfiger")
        Assert.assertEquals(kon["platform"], "Cross Platform")
        Assert.assertEquals(kon["file"], "test.comment.inf")
        Assert.assertEquals(kon["author"], "Adewale Azeez")
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class, IllegalAccessException::class, InvocationTargetException::class)
    fun Detach_An_Object_From_Konfiger() {
        val texts = Texts()
        val kStream = KonfigerStream(File("src/test/resources/test.comment.inf"))
        kStream.commentPrefix = "["
        val kon = Konfiger(kStream)
        kon.resolve(texts)
        Assert.assertEquals(texts.project, "konfiger")
        Assert.assertEquals(texts.Platform, "Cross Platform")
        Assert.assertEquals(texts.file, "test.comment.inf")
        Assert.assertEquals(texts.author, "Adewale Azeez")
        Assert.assertEquals(texts, kon.detach())
        kon.put("Project", "konfiger-nodejs")
        kon.put("Platform", "Windows, Linux, Mac, Raspberry")
        kon.put("author", "Thecarisma")
        Assert.assertNotEquals(texts.project, "konfiger-nodejs")
        Assert.assertFalse(texts.Platform!!.contains("Windows"))
        Assert.assertFalse(texts.Platform!!.contains("Linux"))
        Assert.assertFalse(texts.Platform!!.contains("Mac"))
        Assert.assertFalse(texts.Platform!!.contains("Raspberry"))
        Assert.assertNotEquals(texts.author, "Thecarisma")
        kon.put("author", "Adewale")
        Assert.assertNotEquals(texts.author, "Adewale")
    }

}
