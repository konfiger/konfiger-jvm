package io.github.thecarisma

import java.io.{File, FileNotFoundException, IOException}

import org.junit.{Assert, Test}

class TestStream_Scala {
  @Test(expected = classOf[FileNotFoundException])
  @throws[FileNotFoundException]
  def Should_Throw_Exceptions(): Unit = {
    val ks = new KonfigerStream(new File("tryer.ini"))
  }

  @Test
  @throws[FileNotFoundException]
  def Should_Successfully_Initialize(): Unit = {
    val ks = new KonfigerStream(new File("./README.md"))
  }

  @Test
  @throws[IOException]
  @throws[InvalidEntryException]
  def Validate_The_File_Stream_Value(): Unit = {
    val ks = new KonfigerStream(new File("src/test/resources/test.config.ini"))
    while ( {
      ks.hasNext
    }) Assert.assertNotEquals(ks.next, null)
  }

  @Test
  @throws[IOException]
  @throws[InvalidEntryException]
  def Validate_The_String_Stream_Key(): Unit = {
    val ks = new KonfigerStream("Name=Adewale Azeez,Project=konfiger, Date=April 24 2020", '=', ',')
    Assert.assertEquals(ks.next()(0), "Name")
    Assert.assertEquals(ks.next()(0), "Project")
    Assert.assertEquals(ks.next()(0), " Date")
  }

  @Test
  @throws[IOException]
  @throws[InvalidEntryException]
  def Validate_The_String_Stream_Value(): Unit = {
    val ks = new KonfigerStream("Name=Adewale Azeez,Project=konfiger, Date=April 24 2020", '=', ',')
    Assert.assertEquals(ks.next()(1), "Adewale Azeez")
    Assert.assertEquals(ks.next()(1), "konfiger")
    Assert.assertEquals(ks.next()(1), "April 24 2020")
  }

  @Test
  @throws[IOException]
  @throws[InvalidEntryException]
  def Test_String_Stream_Key_Trimming(): Unit = {
    val ks = new KonfigerStream(" Name =Adewale Azeez:Project =konfiger: Date=April 24 2020", '=', ':')
    Assert.assertFalse(ks.isTrimming)
    ks.setTrimming(true)
    Assert.assertTrue(ks.isTrimming)
    Assert.assertEquals(ks.next()(0), "Name")
    Assert.assertEquals(ks.next()(0), "Project")
    Assert.assertEquals(ks.next()(0), "Date")
  }

  @Test
  @throws[IOException]
  @throws[InvalidEntryException]
  def Test_The_Single_Pair_Commenting_In_String_Stream(): Unit = {
    val ks = new KonfigerStream("Name:Adewale Azeez,//Project:konfiger,Date:April 24 2020", ':', ',')
    while ( {
      ks.hasNext
    }) Assert.assertNotEquals(ks.next()(0), "Project")
  }

  @Test
  @throws[IOException]
  @throws[InvalidEntryException]
  def Test_The_Single_Pair_Commenting_In_File_Stream_1(): Unit = {
    val ks = new KonfigerStream(new File("src/test/resources/test.comment.inf"))
    ks.setCommentPrefix("[")
    while ( {
      ks.hasNext
    }) Assert.assertFalse(ks.next()(0).startsWith("["))
  }

  @Test
  @throws[IOException]
  @throws[InvalidEntryException]
  def Test_The_Single_Pair_Commenting_In_File_Stream(): Unit = {
    val ks = new KonfigerStream(new File("src/test/resources/test.txt"), ':', ',')
    while ( {
      ks.hasNext
    }) Assert.assertFalse(ks.next()(0).startsWith("//"))
  }
}
