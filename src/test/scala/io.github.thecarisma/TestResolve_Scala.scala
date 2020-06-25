package io.github.thecarisma

import java.io.{File, IOException}
import java.lang.reflect.InvocationTargetException

import org.junit.{Assert, Test}

class TestResolve_Scala {

  class TextsFlat {
    val project: String = null
    val author: String = null
    val Platform: String = null
    val File: String = null
  }

  class Texts {
    val project = ""
    val author = ""
    val Platform = ""
    val file = ""

    def matchGetKey(key: String): String = {
      key match {
        case "project" => "Project"
        case "author" => "Author"
        case "file" => "File"
        case _ => ""
      }
    }

    def matchPutKey(key: String): String = {
      key match {
        case "Project" => "project"
        case "Author" => "author"
        case "File" => "file"
        case _ => ""
      }
    }
  }

  class TextsAnnotated {
    @KonfigerValue("Project") val project: String = ""
    @KonfigerValue("Author")  val author: String = ""
    val Platform: String = ""
    @KonfigerValue("File") val file: String = ""
  }

  class Entries {
    val project = "konfiger"
    val author = "Adewale Azeez"
    val platform = "Cross Platform"
    val file = "test.comment.inf"
  }

  class EntriesAnnotated {
    @KonfigerValue("Project") val project = "konfiger"
    @KonfigerValue("Author") val author = "Adewale Azeez"
    private[thecarisma] val Platform = "Cross Platform"
    @KonfigerValue("File") val file = "test.comment.inf"
  }

  @Test
  @throws[IOException]
  @throws[InvalidEntryException]
  @throws[IllegalAccessException]
  @throws[InvocationTargetException]
  def Invalid_Argument_Type_To_Konfiger_Resolve(): Unit = {
    val kon = new Konfiger(new File("src/test/resources/test.comment.inf"), true)
    kon.resolve(123)
  }

  @Test
  @throws[IOException]
  @throws[InvalidEntryException]
  @throws[IllegalAccessException]
  @throws[InvocationTargetException]
  def Resolve_Without_matchGetKey_Function(): Unit = {
    val textsFlat = new TextsFlat
    val kStream = new KonfigerStream(new File("src/test/resources/test.comment.inf"))
    kStream.setCommentPrefix("[")
    val kon = new Konfiger(kStream)
    kon.resolve(textsFlat)
    Assert.assertNull(textsFlat.project)
    Assert.assertEquals(textsFlat.Platform, "Cross Platform")
    Assert.assertEquals(textsFlat.File, "test.comment.inf")
    Assert.assertNull(textsFlat.author)
  }

  @Test
  @throws[IOException]
  @throws[InvalidEntryException]
  @throws[IllegalAccessException]
  @throws[InvocationTargetException]
  def Resolve_With_matchGetKey_Function(): Unit = {
    val texts = new Texts
    val kStream = new KonfigerStream(new File("src/test/resources/test.comment.inf"))
    kStream.setCommentPrefix("[")
    val kon = new Konfiger(kStream)
    kon.resolve(texts)
    Assert.assertEquals(texts.project, "konfiger")
    Assert.assertEquals(texts.Platform, "Cross Platform")
    Assert.assertEquals(texts.file, "test.comment.inf")
    Assert.assertEquals(texts.author, "Adewale Azeez")
  }

  @Test
  @throws[IOException]
  @throws[InvalidEntryException]
  @throws[IllegalAccessException]
  @throws[InvocationTargetException]
  def Resolve_With_Changing_Values_And_Map_Key_With_matchPutKey(): Unit = {
    val texts = new Texts
    val kStream = new KonfigerStream(new File("src/test/resources/test.comment.inf"))
    kStream.setCommentPrefix("[")
    val kon = new Konfiger(kStream)
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
  @throws[IOException]
  @throws[InvalidEntryException]
  @throws[IllegalAccessException]
  @throws[InvocationTargetException]
  def Resolve_With_Changing_Values_And_Map_Key_With_matchPutKey_Using_Annotation(): Unit = {
    val texts = new TextsAnnotated
    val kStream = new KonfigerStream(new File("src/test/resources/test.comment.inf"))
    kStream.setCommentPrefix("[")
    val kon = new Konfiger(kStream)
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
  @throws[IOException]
  @throws[InvalidEntryException]
  @throws[IllegalAccessException]
  @throws[InvocationTargetException]
  def Dissolve_An_Object_Into_Konfiger(): Unit = {
    val kon = new Konfiger("")
    kon.dissolve(new Entries)
    Assert.assertEquals(kon.get("project"), "konfiger")
    Assert.assertEquals(kon.get("platform"), "Cross Platform")
    Assert.assertEquals(kon.get("file"), "test.comment.inf")
    Assert.assertEquals(kon.get("author"), "Adewale Azeez")
  }

  @Test
  @throws[IOException]
  @throws[InvalidEntryException]
  @throws[IllegalAccessException]
  @throws[InvocationTargetException]
  def Dissolve_An_Object_Into_Konfiger_Using_Annotation(): Unit = {
    val kon = new Konfiger("")
    kon.dissolve(new EntriesAnnotated)
    Assert.assertEquals(kon.get("Project"), "konfiger")
    Assert.assertEquals(kon.get("Platform"), "Cross Platform")
    Assert.assertEquals(kon.get("File"), "test.comment.inf")
    Assert.assertEquals(kon.get("Author"), "Adewale Azeez")
  }

  @Test
  @throws[IOException]
  @throws[InvalidEntryException]
  @throws[IllegalAccessException]
  @throws[InvocationTargetException]
  def Detach_An_Object_From_Konfiger(): Unit = {
    val texts = new Texts
    val kStream = new KonfigerStream(new File("src/test/resources/test.comment.inf"))
    kStream.setCommentPrefix("[")
    val kon = new Konfiger(kStream)
    kon.resolve(texts)
    Assert.assertEquals(texts.project, "konfiger")
    Assert.assertEquals(texts.Platform, "Cross Platform")
    Assert.assertEquals(texts.file, "test.comment.inf")
    Assert.assertEquals(texts.author, "Adewale Azeez")
    Assert.assertEquals(texts, kon.detach)
    kon.put("Project", "konfiger-nodejs")
    kon.put("Platform", "Windows, Linux, Mac, Raspberry")
    kon.put("author", "Thecarisma")
    Assert.assertNotEquals(texts.project, "konfiger-nodejs")
    Assert.assertFalse(texts.Platform.contains("Windows"))
    Assert.assertFalse(texts.Platform.contains("Linux"))
    Assert.assertFalse(texts.Platform.contains("Mac"))
    Assert.assertFalse(texts.Platform.contains("Raspberry"))
    Assert.assertNotEquals(texts.author, "Thecarisma")
    kon.put("author", "Adewale")
    Assert.assertNotEquals(texts.author, "Adewale")
  }

}
