package io.github.thecarisma;

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.IOException
import java.lang.reflect.InvocationTargetException

class TestResolve_Kotlin {

    internal class TextsFlat {
        var project: String? = null
        var author: String? = null
        var Platform: String? = null
        var File: String? = null
    }

    internal class Texts {
        var project: String = ""
        var author: String = ""
        var Platform: String = ""
        var file: String = ""
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

    internal class TextsAnnotated {
        @KonfigerKey("Project") var project: String = ""
        @KonfigerKey("Author") var author: String = ""
        var Platform: String = ""
        @KonfigerKey("File") var file: String = ""
    }

    internal class Entries {
        var project = "konfiger"
        var author = "Adewale Azeez"
        var platform = "Cross Platform"
        var file = "test.comment.inf"
    }

    internal class EntriesAnnotated {
        @KonfigerKey("Project") var project = "konfiger"
        @KonfigerKey("Author") var author = "Adewale Azeez"
        var Platform = "Cross Platform"
        @KonfigerKey("File") var file = "test.comment.inf"
    }

    internal class MixedTypes {
        var project: String? = null
        var weAllCake = false
        var ageOfEarth: Long = 0
        var lengthOfRiverNile = 0
        var pi = 0f
        var pie = 0.0

        @KonfigerKey("AnnotatedEntry")
        var annotatedEntry = false
    }

    internal class MixedTypesEntries {
        var project = "konfiger"
        var weAllCake = true
        var ageOfEarth = 121526156252322L
        var lengthOfRiverNile = 45454545
        var pi = 3.14f
        var pie = 1.1121
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class, IllegalAccessException::class, InvocationTargetException::class)
    fun Invalid_Argument_Type_To_Konfiger_Resolve() {
        val kon = Konfiger(File("src/test/resources/test.config.ini"), true)
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
    fun Resolve_With_Changing_Values_And_Map_Key_With_matchPutKey_Using_Annotation() {
        val texts = TextsAnnotated()
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
        Assert.assertTrue(texts.Platform.contains("Windows"))
        Assert.assertTrue(texts.Platform.contains("Linux"))
        Assert.assertTrue(texts.Platform.contains("Mac"))
        Assert.assertTrue(texts.Platform.contains("Raspberry"))
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
    fun Dissolve_An_Object_Into_Konfiger_Using_Annotation() {
        val kon = Konfiger("")
        kon.dissolve(TestResolve_Java.EntriesAnnotated())
        Assert.assertEquals(kon["Project"], "konfiger")
        Assert.assertEquals(kon["Platform"], "Cross Platform")
        Assert.assertEquals(kon["File"], "test.comment.inf")
        Assert.assertEquals(kon["Author"], "Adewale Azeez")
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

    @Test
    @Throws(IOException::class, InvalidEntryException::class, IllegalAccessException::class, InvocationTargetException::class)
    fun Resolve_With_matchGetKey_Function_MixedTypes() {
        val entries = MixedTypes()
        val kon = Konfiger(java.io.File("src/test/resources/mixed.types"))
        kon.resolve(entries)
        Assert.assertEquals(entries.project, "konfiger")
        Assert.assertNotEquals(entries.weAllCake, "true")
        Assert.assertTrue(entries.weAllCake)
        Assert.assertTrue(entries.annotatedEntry)
        Assert.assertNotEquals(entries.ageOfEarth, "121526156252322")
        Assert.assertEquals(entries.ageOfEarth, 121526156252322L)
        Assert.assertNotEquals(entries.lengthOfRiverNile, "45454545")
        Assert.assertEquals(entries.lengthOfRiverNile.toLong(), 45454545)
        Assert.assertNotEquals(entries.pi, "3.14")
        Assert.assertEquals(entries.pi.toDouble(), 3.14, 1.0)
        Assert.assertNotEquals(entries.pie, "1.1121")
        Assert.assertEquals(entries.pie, 1.1121, 1.0)
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class, IllegalAccessException::class, InvocationTargetException::class)
    fun Dissolve_An_MixedTypes_Object_Into_Konfiger() {
        val kon = Konfiger("")
        kon.dissolve(MixedTypesEntries())
        Assert.assertEquals(kon.get("project"), "konfiger")
        Assert.assertEquals(kon.get("weAllCake"), "true")
        Assert.assertTrue(kon.getBoolean("weAllCake"))
        Assert.assertEquals(kon.get("ageOfEarth"), "121526156252322")
        Assert.assertEquals(kon.getLong("ageOfEarth"), 121526156252322L)
        Assert.assertEquals(kon.get("lengthOfRiverNile"), "45454545")
        Assert.assertEquals(kon.getInt("lengthOfRiverNile").toLong(), 45454545)
        Assert.assertEquals(kon.get("pi"), "3.14")
        Assert.assertEquals(kon.getFloat("pi").toDouble(), 3.14, 1.0)
        Assert.assertEquals(kon.get("pie"), "1.1121")
        Assert.assertEquals(kon.getDouble("pie"), 1.1121, 1.0)
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class, IllegalAccessException::class, InvocationTargetException::class)
    fun Resolve_With_Changing_Values_For_MixedTypes() {
        val texts = MixedTypes()
        val kon = Konfiger(java.io.File("src/test/resources/mixed.types"))
        kon.resolve(texts)
        Assert.assertEquals(texts.project, "konfiger")
        Assert.assertTrue(texts.weAllCake)
        Assert.assertEquals(texts.ageOfEarth, 121526156252322L)
        Assert.assertEquals(texts.lengthOfRiverNile.toLong(), 45454545)
        Assert.assertEquals(texts.pi, 3.14f, 1f)
        Assert.assertEquals(texts.pie, 1.1121, 1.0)
        Assert.assertTrue(texts.annotatedEntry)
        kon.put("project", "konfiger-nodejs")
        kon.put("AnnotatedEntry", false)
        kon.put("ageOfEarth", 121323L)
        kon.put("pie", 2.1212)
        Assert.assertEquals(texts.project, "konfiger-nodejs")
        Assert.assertFalse(texts.annotatedEntry)
        Assert.assertEquals(texts.ageOfEarth, 121323L)
        Assert.assertEquals(texts.pie, 2.1212, 1.0)
        kon.put("AnnotatedEntry", true)
        Assert.assertTrue(texts.annotatedEntry)
    }

    @Test
    @Throws(IOException::class, InvalidEntryException::class, IllegalAccessException::class, InvocationTargetException::class)
    fun Resolve_With_Changing_Values_And_Map_Key_With_attach() {
        val texts = TestResolve_Java.Texts()
        val kStream = KonfigerStream(File("src/test/resources/test.comment.inf"))
        kStream.commentPrefix = "["
        val kon = Konfiger(kStream)
        kon.attach(texts)
        Assert.assertNotEquals(texts.project, "konfiger")
        Assert.assertNotEquals(texts.Platform, "Cross Platform")
        Assert.assertNotEquals(texts.author, "Adewale Azeez")
        kon.put("Project", "konfiger-nodejs")
        kon.put("Platform", "Windows, Linux, Mac, Raspberry")
        kon.put("author", "Thecarisma")
        Assert.assertEquals(texts.project, "konfiger-nodejs")
        Assert.assertTrue(texts.Platform.contains("Windows"))
        Assert.assertTrue(texts.Platform.contains("Linux"))
        Assert.assertTrue(texts.Platform.contains("Mac"))
        Assert.assertTrue(texts.Platform.contains("Raspberry"))
        Assert.assertEquals(texts.author, "Thecarisma")
        kon.put("author", "Adewale")
        Assert.assertEquals(texts.author, "Adewale")
    }

}
