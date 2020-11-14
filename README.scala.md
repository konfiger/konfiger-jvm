# <p style="text-align: center;" align="center"><img src="https://github.com/konfiger/konfiger.github.io/raw/main/icons/konfiger-scala.png" alt="konfiger-scala" style="width:180px;height:160px;" width="180" height="160" /><br /> konfiger-scala</p>

<p style="text-align: center;" align="center">Light weight package to manage key value based configuration and data files.</p>

---

The notable use cases of this package is loading configuration file, language file, preference setting in an application. 

___

## Table of content
- [Installation](#installation)
    - [Maven](#maven)
    - [Gradle](#gradle)
- [Examples](#examples)
    - [Basic](#basic)
    - [Write to disk](#write-to-disk)
    - [Get Types](#get-types)
    - [Lazy Loading](#lazy-loading)
    - [Seperator and delimeter](#seperator-and-delimeter)
    - [Read file with Stream](#read-file-with-stream)
    - [Read String with Stream](#read-string-with-stream)
    - [Skip Comment entries](#Skip-comment-entries)
    - [Resolve Object](#resolve-object)
    - [Dissolve Object](#dissolve-object)
    - [Multiline value](#multiline-value)
- [Native Object Attachement](#native-object-attachement)
    - [matchGetKey](#matchgetkey)
    - [matchPutKey](#matchputkey)
    - [Annotation](#annotation)
- [API Documentations](#api-documentations)
- [Usage](#usage)
    - [Initialization](#initialization)
    - [Inserting](#inserting)
    - [Finding](#finding)
    - [Updating](#updating)
    - [Removing](#removing)
    - [Saving to disk](#saving-to-disk)
- [How it works](#how-it-works)
- [Contributing](#contributing)
- [Support](#support)
- [License](#license)

## Installation

Download the jar file from the [releases](https://github.com/konfiger/konfiger-jvm/releases) and add the downloaded konfiger-$.jar to your scala or android project class path or library folder.

### Maven

Add the following repository and dependency detail to your pom.xml

Using mvn-repo:

```xml
<dependencies>
    <dependency>
        <groupId>io.github.thecarisma</groupId>
        <artifactId>konfiger</artifactId>
        <version>1.2.4</version>
    </dependency>
</dependencies>

<repositories>
    <repository>
        <id>konfiger</id>
        <url>https://raw.github.com/konfiger/konfiger-jvm/mvn-repo/</url>
    </repository>
</repositories>
```

Using jitpack.io:

```xml
<dependencies>
    <dependency>
        <groupId>com.github.konfiger</groupId>
        <artifactId>konfiger-jvm</artifactId>
        <version>1.2.4</version>
    </dependency>
</dependencies>

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

### Gradle

Add it in your root build.gradle at the end of repositories:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency:

```gradle
dependencies {
        implementation 'com.github.konfiger:konfiger-jvm:1.2.4'
}
```

## Examples

### Basic

The following example load from file, add an entry, remove an entry and iterate all the key value entries

```scala
import io.github.thecarisma._
import java.io._
import scala.jdk.CollectionConverters._

object Test_Scala {
  @throws[IOException]
  @throws[InvalidEntryException]
  def main(args: Array[String]): Unit = {
    val konfiger = new Konfiger(new File("test/test.config.ini"), true)

    //add a string
    konfiger.putString("Greet", "Hello World")

    //get an object
    println(konfiger.get("Greet"))

    //remove an object
    konfiger.remove("Greet")

    //add an String
    konfiger.putString("What", "i don't know what to write here")

    for (entry <- konfiger.entries.asScala) {
      println(entry)
    }
  }
}
```

### Write to disk

Initialize an empty konfiger object and populate it with random data, then save it to a file

```scala
import io.github.thecarisma._
import java.io._

object Test_Scala {
  @throws[IOException]
  @throws[InvalidEntryException]
  def main(args: Array[String]): Unit = {
    val randomValues = Array("One", "Two", "Three", "Four", "Five")
    val konfiger = new Konfiger("", false)
    for (i <- 0 until 200) {
      val random = Math.floor(Math.random * (randomValues.length - 1) + 0)
      konfiger.putString("" + i, randomValues(random.toInt))
    }
    konfiger.save("test/konfiger.conf")
  }
}
```

### Get Types

Load the entries as string and get them as a true type.

```scala
import io.github.thecarisma._
import java.io._
import scala.jdk.CollectionConverters._

object Test_Scala {
  @throws[IOException]
  @throws[InvalidEntryException]
  def main(args: Array[String]): Unit = {
    val konfiger = new Konfiger("\n" + "String=This is a string\n" +
      "Number=215415245\n" +
      "Float=56556.436746\n" +
      "Boolean=true\n", false)

    val str = konfiger.getString("String")
    val num = konfiger.getLong("Number")
    val flo = konfiger.getFloat("Float")
    val bool = konfiger.getBoolean("Boolean")

    println(str.isInstanceOf[String])
    println(num.isInstanceOf[Long])
    println(flo.isInstanceOf[Float])
    println(bool.isInstanceOf[Boolean])
  }
}
```

### Lazy Loading

The lazyLoad parameter is useful for progressively read entries from a large file. The next example shows initializing from a file with so much key value entry with lazy loading:

The content of `test/konfiger.conf` is 

```
Ones=11111111111
Twos=2222222222222
Threes=3333333333333
Fours=444444444444
Fives=5555555555555
```

```scala
import io.github.thecarisma._
import java.io._
import scala.jdk.CollectionConverters._

object Test_Scala {
  @throws[IOException]
  @throws[InvalidEntryException]
  def main(args: Array[String]): Unit = {
    val konfiger = new Konfiger(new File("test/konfiger.conf"), //the file path
      true) //lazyLoad true
        //at this point nothing is read from the file

    //the size of konfiger is 0 even if the file contains over 1000 entries

    //the key 'Twos' is at the second line in the file, therefore two entry has
    //been read to get the value.
    println(konfiger.get("Twos"))

    //the size becomes 2,

    //to read all the entries simply call the toString() method
    println(konfiger.toString)

    //now the size is equal to the entry
    println(konfiger.size)
  }
}
```

### Seperator and delimeter

Initailize a konfiger object with default seperator and delimeter then change the seperator and selimeter at runtime

```scala
import io.github.thecarisma._
import java.io._
import scala.jdk.CollectionConverters._

object Test_Scala {
  @throws[IOException]
  @throws[InvalidEntryException]
  def main(args: Array[String]): Unit = {
    val konfiger = new Konfiger(new File("test/konfiger.conf"), false)
    konfiger.setDelimeter('?')
    konfiger.setSeperator(',')

    println(konfiger.toString)
  }
}
```

### Read file with Stream

Read a key value file using the progressive [KonfigerStream](https://github.com/konfiger/konfiger-nodejs/blob/main/src/io/github/thecarisma/KonfigerStream.js), each scan returns the current key value array `[ 'key', 'value']`

```scala
import io.github.thecarisma._
import java.io._
import scala.jdk.CollectionConverters._

object Test_Scala {
  @throws[IOException]
  @throws[InvalidEntryException]
  def main(args: Array[String]): Unit = {
    val kStream: KonfigerStream = new KonfigerStream(new File("test/konfiger.conf"))
    while ( {
      kStream.hasNext
    }) {
      val entry: Array[String] = kStream.next
      println("[" + entry(0) + "," + " " + entry(1) + "]")
    }
  }
}
```

### Read String with Stream

Read a key value string using the progressive [KonfigerStream](https://github.com/konfiger/konfiger-nodejs/blob/main/src/io/github/thecarisma/KonfigerStream.js), each scan returns the current key value array `[ 'key', 'value']`

```scala
import io.github.thecarisma._
import java.io._
import scala.jdk.CollectionConverters._

object Test_Scala {
  @throws[IOException]
  @throws[InvalidEntryException]
  def main(args: Array[String]): Unit = {
    val kStream: KonfigerStream = new KonfigerStream("\n" +
      "String=This is a string\n" +
      "Number=215415245\n" +
      "Float=56556.436746\n" +
      "Boolean=true\n")

    while ( {
      kStream.hasNext
    }) {
      val entry: Array[String] = kStream.next
      println("[" + entry(0) + "," + " " + entry(1) + "]")
    }
  }
}
```

### Skip Comment entries

Read all the key value entry using the stream and skipping all commented entries. The default comment prefix is `//` but in the example below the commented entries starts with `#` so the prefix is changed. The same thing happen if the key value entry is loaded from file. 

```scala
import io.github.thecarisma._
import java.io._
import scala.jdk.CollectionConverters._

object Test_Scala {
  @throws[IOException]
  @throws[InvalidEntryException]
  def main(args: Array[String]): Unit = {
    val kStream = new KonfigerStream("\n" +
      "String=This is a string\n" +
      "#Number=215415245\n" +
      "Float=56556.436746\n" +
      "#Boolean=true\n")
    kStream.setCommentPrefix("#")

    while ( {
      kStream.hasNext
    }) {
      val entry: Array[String] = kStream.next
      println("[" + entry(0) + "," + " " + entry(1) + "]")
    }
  }
}
```

### Resolve Object

The example below attach a javascript object to a konfiger object, whenever the value of the konfiger object changes the attached object entries is also updated.

For the file properties.conf

```
project = konfiger
author = Adewale Azeez
islibrary = true
```

```scala
import io.github.thecarisma._
import java.io._
import java.lang.reflect.InvocationTargetException

object Test_Scala {

  class Properties {
    val project: String = null
    val author: String = null
    val islibrary: Boolean = false
  }

  @throws[IOException]
  @throws[InvalidEntryException]
  @throws[InvocationTargetException]
  @throws[IllegalAccessException]
  def main(args: Array[String]): Unit = {
    val properties = new Test_Scala.Properties
    val konfiger = new Konfiger(new File("test/properties.conf"), true)
    konfiger.resolve(properties)

    System.out.println(properties.project) // konfiger
    System.out.println(properties.author) // Adewale Azeez
    System.out.println(properties.islibrary) // true
    konfiger.put("project", "konfiger-nodejs")
    System.out.println(properties.project) // konfiger-nodejs
  }
}
```

### Dissolve Object

The following snippet reads the value of a javascript object into the konfiger object, the object is not attached to konfiger unlike resolve function.

```scala
import io.github.thecarisma._
import java.io._
import java.lang.reflect.InvocationTargetException

object Test_Scala {

  class Properties {
    val project = "konfiger"
    val author = "Adewale"
    val islibrary: Boolean = true
  }

  @throws[IOException]
  @throws[InvalidEntryException]
  @throws[InvocationTargetException]
  @throws[IllegalAccessException]
  def main(args: Array[String]): Unit = {
    val properties = new Test_Scala.Properties
    val konfiger = new Konfiger("")
    konfiger.dissolve(properties)
    
    System.out.println(konfiger.get("project")) // konfiger
    System.out.println(konfiger.get("author")) // Adewale Azeez
    System.out.println(konfiger.getBoolean("islibrary")) // true

  }
}
```

### Multiline value

Konfiger stream allow multiline value. If the line ends with `\` the next line will be parse as the continuation and the leading spaces will be trimmed. The continuation character chan be changed like the example below the continuation character is changed from `\` to `+`.

for the file test.contd.conf

```
Description = This project is the closest thing to Android +
              Shared Preference in other languages +
              and off the Android platform.
ProjectName = konfiger
```

```scala
import io.github.thecarisma._
import java.io._

object Test_Scala {
  @throws[IOException]
  @throws[InvalidEntryException]
  def main(args: Array[String]): Unit = {
    val ks = new KonfigerStream(new File("test/test.contd.conf"))
    ks.setContinuationChar('+')
    System.out.println(ks.next()(1))
    System.out.println(ks.next()(1))
  }
}
```

## Native Object Attachement

This feature of the project allow seamless integration with the konfiger entries by eliminating the need for writing `Konfiger.get*("")` everytime to read a value into a variable or writing lot of `Konfiger.put*()` to add an entry. 

The two function `resolve` is used to attach an object. resolve function integrate the object such that the entries in konfiger will be assigned to the matching key in the object. See the [resolve](#resolve-object) and [dissolve](#dissolve-object) examples above.

In a case where the object keys are different from the entries keys in the konfiger object the function `matchGetKey` can be declared in the object to match the key when setting the object entries values, and the function `matchPutKey` is called when setting the konfiger entries from the object.

Konfiger is aware of the type of an object field, if the type of a field is boolean the entry value will be parsed as boolean and assigned to the field. 

For the file English.lang

```
LoginTitle = Login Page
AgeInstruction = You must me 18 years or above to register
NewsletterOptin = Signup for our weekly news letter
ShouldUpdate = true
```

For an object which as the same key as the konfiger entries above there is no need to declare the matchGetKey or matchPutKey in the object. Resolve example

```scala
import io.github.thecarisma._
import java.io._
import java.lang.reflect.InvocationTargetException


object Test_Scala {
  @throws[IOException]
  @throws[InvalidEntryException]
  @throws[InvocationTargetException]
  @throws[IllegalAccessException]
  def main(args: Array[String]): Unit = {
    val pageProps = new Test_Scala.PageProps
    val kon = new Konfiger(new File("test/English.lang"))
    kon.resolve(pageProps)
    System.out.println(pageProps.toString)
  }

  class PageProps {
    val LoginTitle: String = null
    val AgeInstruction: String = null
    val NewsletterOptin: String = null
    val ShouldUpdate: Boolean = false

    override def toString: String = "LoginTitle=" + LoginTitle + ",AgeInstruction=" + AgeInstruction + ",NewsletterOptin=" + NewsletterOptin + ",ShouldUpdate=" + ShouldUpdate
  }

}
```

Dissolve example

```scala
import io.github.thecarisma._
import java.io._
import java.lang.reflect.InvocationTargetException


object Test_Scala {
  @throws[IOException]
  @throws[InvalidEntryException]
  @throws[InvocationTargetException]
  @throws[IllegalAccessException]
  def main(args: Array[String]): Unit = {
    val pageProps = new Test_Scala.PageProps
    val kon = new Konfiger("")
    kon.dissolve(pageProps)
    System.out.println(kon)
  }

  class PageProps {
    val LoginTitle = "Login Page"
    val AgeInstruction = "You must me 18 years or above to register"
    val NewsletterOptin = "Signup for our weekly news letter"
    val ShouldUpdate = false
  }

}
```

### matchGetKey

If the identifier in the object keys does not match the above entries key the object will not be resolved. For example loginTitle does not match LoginTitle, the matchGetKey can be used to map the variable key to the konfiger entry key. The following example map the object key to konfiger entries key.

```scala
import io.github.thecarisma._
import java.io._
import java.lang.reflect.InvocationTargetException

object Test_Scala {
  @throws[IOException]
  @throws[InvalidEntryException]
  @throws[InvocationTargetException]
  @throws[IllegalAccessException]
  def main(args: Array[String]): Unit = {
    val pageProps = new Test_Scala.PageProps
    val kon = new Konfiger(new File("test/English.lang"))
    kon.resolve(pageProps)
    System.out.println(pageProps.toString)
  }

  class PageProps {
    val loginTitle: String = null
    val ageInstruct: String = null
    val NewsletterOptin: String = null

    def matchGetKey(key: String): String = {
      if ("loginTitle" == key) return "LoginTitle"
      else if ("ageInstruct" == key) return "AgeInstruction"
      ""
    }

    override def toString: String = "loginTitle=" + loginTitle + ",ageInstruct=" + ageInstruct + ",NewsletterOptin=" + NewsletterOptin
  }

}
```

The way the above code snippet works is that when iterating the object keys if check if the function matchGetKey is present in the object if it is present the key is sent as parameter to the matchGetKey and the returned value is used to get the value from konfiger, if the matchGetKey does not return anything the object key is used to get the value from konfiger (as in the case of NewsletterOptin).

> During the resolve or dissolve process if the entry value is function it is skipped even if it key matches

For dissolving an object the method matchGetKey is invoked to find the actual key to use to add the entry in konfiger, if the object does not declare the matchGetKey function the entries will be added to konfiger as it is declared. The following example similar to the one above but dissolves an object into konfiger.

```scala
import io.github.thecarisma._
import java.io._
import java.lang.reflect.InvocationTargetException

object Test_Scala {
  @throws[IOException]
  @throws[InvalidEntryException]
  @throws[InvocationTargetException]
  @throws[IllegalAccessException]
  def main(args: Array[String]): Unit = {
    val pageProps = new Test_Scala.PageProps
    val kon = new Konfiger("")
    kon.dissolve(pageProps)
    System.out.println(kon)
  }

  class PageProps {
    val loginTitle = "Login Page"
    val ageInstruct = "You must me 18 years or above to register"
    val NewsletterOptin = "Signup for our weekly news letter"

    def matchGetKey(key: String): String = {
      if ("loginTitle" == key) return "LoginTitle"
      else if ("ageInstruct" == key) return "AgeInstruction"
      ""
    }
  }

}
```

### matchPutKey

The matchPutKey is invoked when an entry value is changed or when a new entry is added to konfiger. The matchPutKey is invoked with the new entry key and checked in the object matchPutKey (if decalred), the returned value is what is set in the object. E.g. if an entry `[Name, Thecarisma]` is added to konfiger the object matchPutKey is invoked with the parameter `Name` the returned value is used to set the corresponding object entry. 

```scala
import io.github.thecarisma._
import java.io._
import java.lang.reflect.InvocationTargetException

object Test_Scala {
  @throws[IOException]
  @throws[InvalidEntryException]
  @throws[InvocationTargetException]
  @throws[IllegalAccessException]
  def main(args: Array[String]): Unit = {
    val pageProps = new Test_Scala.PageProps
    val kon = new Konfiger("")
    kon.resolve(pageProps)

    kon.put("LoginTitle", "Login Page")
    kon.put("AgeInstruction", "You must me 18 years or above to register")
    kon.put("NewsletterOptin", "Signup for our weekly news letter")
    System.out.println(pageProps.loginTitle)
    System.out.println(pageProps.ageInstruct)
    System.out.println(pageProps.NewsletterOptin)
  }

  class PageProps {
    val loginTitle: String = null
    val ageInstruct: String = null
    val NewsletterOptin: String = null

    def matchPutKey(key: String): String = {
      if ("LoginTitle" == key) return "loginTitle"
      else if ("AgeInstruction" == key) return "ageInstruct"
      ""
    }
  }

}
```

### Annotation

A more cleaner way to map an entry key to a field in an object is to use the KonfigerKey anotation. The annotation accept the key value to map to the field in konfiger.

The annotation is used for lookup when resolving or disolving an object and when changing the value of the field by updating it in konfiger. In short the anotation value is used to lookup the field instead of the verbose `matchGetKey` and `matchPutKey` methods.

```scala
import io.github.thecarisma._
import java.io._
import java.lang.reflect.InvocationTargetException

object Test_Scala {
  @throws[IOException]
  @throws[InvalidEntryException]
  @throws[InvocationTargetException]
  @throws[IllegalAccessException]
  def main(args: Array[String]): Unit = {
    val pageProps = new Test_Java.PageProps
    val kon = new Konfiger("")
    kon.resolve(pageProps)
    kon.put("LoginTitle", "Login Page")
    kon.put("AgeInstruction", "You must me 18 years or above to register")
    kon.put("NewsletterOptin", "Signup for our weekly news letter")
    System.out.println(pageProps.loginTitle)
    System.out.println(pageProps.ageInstruct)
    System.out.println(pageProps.NewsletterOptin)
  }

  class PageProps {
    @KonfigerKey("LoginTitle") val loginTitle: String = null
    @KonfigerKey("AgeInstruction") val ageInstruct: String = null
    val NewsletterOptin: String = null
  }

}
```

Konfiger does not create new entry in an object it just set existing values. Konfiger only change the value in an object if the key is defined

> Warning!!!
The values resolved is not typed so if the entry initial value is an integer the resolved value will be a string. All resolved value is string, you will need to do the type conversion by your self.

If your entry keys is the same as the object keys you don't need to declare the matchGetKey or matchPutKey function in the object.

## Usage

### Initialization

The main Konfiger contructor is not exported from the package, the two functions are exported for initialization, `fromString` and `fromFile`. The fromString function creates a Konfiger object from a string with valid key value entry or from empty string, the fromFile function creates the Konfiger object from a file, the two functions accept a cumpulsory second parameter `lazyLoad` which indicates whether to read all the entry from the file or string suring initialization. The lazyLoad parameter is useful for progressively read entries from a large file. The two initializing functions also take 2 extra optional parameters `delimeter` and `seperator`. If the third and fourth parameter is not specified the default is used, delimeter = `=`, seperator = `\n`. If the file or string has different delimeter and seperator always send the third and fourth parameter.

The following initializer progressively read the file when needed

```scala
val konfiger = new Konfiger(new File("test/konfiger.conf"), true)
```

The following initializer read all the entries from file at once

```scala
val konfiger = new Konfiger(new File("test/konfiger.conf"), false)
```

The following initializer read all the entries from string when needed

```scala
val konfiger = new Konfiger("\n" +
      "Ones=11111111111\n" +
      "Twos=2222222222222\n", true);
```

The following initializer read all the entries from String at once

```scala
val konfiger = new Konfiger("\n" +
      "Ones=11111111111\n" +
      "Twos=2222222222222\n", false);
```

Initialize a string which have custom delimeter and seperator

```scala
val konfiger = new Konfiger("Ones:11111111111,Twos:2222222222222", 
      true, 
      ':', 
      ',')
```

### Inserting

The following types can be added into the object, int, float, long, boolean, object and string.

To add any object into the entry use the `put` method as it check the value type and properly get it string value

```scala
konfiger.put("String", "This is a string")
konfiger.put("Long", 143431423)
konfiger.put("Boolean", true)
konfiger.put("Float", 12.345)
```

The `put` method do a type check on the value and calls the appropriate put method e.g `konfiger.put("Boolean", true)` will result in a call to `konfiger.putBoolean("Boolean", true)`. The following method are avaliable to directly add the value according to the type, `putString`, `putBoolean`, `putLong` and `putInt`. The previous example can be re-written as:

```scala
konfiger.putString("String", "This is a string")
konfiger.putLong("Long", 143431423)
konfiger.putBoolean("Boolean", true)
konfiger.putFloat("Float", 12.345)
```

### Finding

There are various ways to get the value from the konfiger object, the main `get` method and `getString` method both returns a string type, the other get methods returns specific types

```scala
konfiger.get("String")
```

To get specific type from the object use the following methods, `getString`, `getBoolean`, `getLong`, `getFloat` and `getInt`. 

```scala
konfiger.getString("String")
konfiger.getLong("Long")
konfiger.getBoolean("Boolean")
konfiger.getFloat("Float")
```

If the requested entrr does not exist a null/undefined value is returned, to prevent that a fallback value should be sent as second parameter incase the key is not found the second parameter will be returned.

```scala
konfiger.get("String", "Default Value")
konfiger.getBoolean("Boolean", false)
```

If the konfiger is initialized with lazy loading enabled if the get method is called the stream will start reading until the key is found and the stream is paused again, if the key is not found the stream will read to end. 

### Updating

The `put` method can be used to add new entry or to update an already existing entry in the object. The `updateAt` method is usefull for updating a value using it index instead of key

```scala
konfiger.updateAt(0, "This is an updated string")
```

### Removing

The `remove` method removes a key value entry from the konfiger, it returns true if an entry is removed and false if no entry is removed. The `remove` method accept either key(string) or index(int) of the entry.

```scala
konfiger.remove("String")
konfiger.remove(0)
```

### Saving to disk

Every operation on the konfiger object is done in memory to save the updated entries in a file call the `save` method with the file path to save the entry. If the konfiger is initiated from file then there is no need to add the file path to the `save` method, the entries will be saved to the file path used during initialization.

```scala
konfiger.save("test/test.config.ini")
```

in case of load from file, the save will write the entries to *test/test.config.ini*.

```scala
//...
val konfiger = new Konfiger(new File("test/test.config.ini"), true)
//...
konfiger.save()
```

## API Documentations

See [https://konfiger.github.io/konfiger-jvm/](https://konfiger.github.io/konfiger-jvm/#api-documentations) for API Documentation.

## How it works

Konfiger stream progressively load the key value entry from a file or string when needed, it uses two method `hasNext` which check if there is still an entry in the stream and `next` for the current key value entry in the stream. 
 
In Konfiger the key value pair is stored in a `map`, all search updating and removal is done on the `konfigerObjects` in the class. The string sent as first parameter if parsed into valid key value using the separator and delimiter fields and if loaded from file it content is parsed into valid key value pair. The `toString` method also parse the `konfigerObjects` content into a valid string with regards to the 
separator and delimeter. The value is properly escaped and unescaped.

The `save` function write the current `Konfiger` to the file, if the file does not exist it is created if it can. Everything is written in memory and is disposed on app exit hence it important to call the `save` function when nessasary.

## Contributing

Before you begin contribution please read the contribution guide at [CONTRIBUTING GUIDE](https://github.com/konfiger/konfiger.github.io/blob/main/CONTRIBUTING.MD)

You can open issue or file a request that only address problems in this implementation on this repo, if the issue address the concepts of the package then create an issue or rfc [here](https://github.com/konfiger/konfiger.github.io/)

## Support

You can support some of this community as they make big impact in the training of individual to get started with software engineering and open source contribution.

- [https://www.patreon.com/devcareer](https://www.patreon.com/devcareer)

## License

MIT License Copyright (c) 2020 [Adewale Azeez](https://twitter.com/iamthecarisma) - konfiger

