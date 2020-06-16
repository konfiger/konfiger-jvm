package io.github.thecarisma

import java.io.{File, IOException}
import java.lang.reflect.InvocationTargetException

import org.junit.{Assert, Test}

class TestResolve_Scala {

  private class TextsFlat {
    private val project = null
    private val author = null
    private val Platform = null
    private val File = null
  }

  private class Texts {
    private val project = null
    private val author = null
    private val Platform = null
    private val file = null

    private def matchGetKey(key: String): String = {
      key match {
        case "project" =>
          return "Project"
        case "author" =>
          return "Author"
        case "file" =>
          return "File"
      }
      ""
    }

    private def matchPutKey(key: String): String = {
      key match {
        case "Project" =>
          return "project"
        case "Author" =>
          return "author"
        case "File" =>
          return "file"
      }
      ""
    }
  }

  private class Entries {
    private val project = "konfiger"
    private val author = "Adewale Azeez"
    private val platform = "Cross Platform"
    private val file = "test.comment.inf"
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
    val textsFlat = new TestResolve_Java.TextsFlat
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
    val texts = new TestResolve_Java.Texts
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
    val texts = new TestResolve_Java.Texts
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
    kon.dissolve(new TestResolve_Java.Entries)
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
  def Detach_An_Object_From_Konfiger(): Unit = {
    val texts = new TestResolve_Java.Texts
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
