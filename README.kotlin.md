# <p style="text-align: center;" align="center"><img src="https://github.com/konfiger/konfiger.github.io/raw/master/icons/konfiger-kotlin.png" alt="konfiger-kotlin" style="width:180px;height:160px;" width="180" height="160" /><br /> konfiger-kotlin</p>

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

Download the jar file from the [releases](https://github.com/konfiger/konfiger-jvm/releases) and add the downloaded konfiger-$.jar to your kotlin or android project class path or library folder.

### Maven

Add the following repository and dependency detail to your pom.xml

Using mvn:

```xml
<dependencies>
    <dependency>
        <groupId>io.github.thecarisma</groupId>
        <artifactId>konfiger</artifactId>
        <version>1.2.4</version>
    </dependency>
</dependencies>
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

```kotlin
import io.github.thecarisma.*
import java.io.*

object Test_Kotlin {
    @Throws(IOException::class, InvalidEntryException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val konfiger = Konfiger(File("test/test.config.ini"), true)

        //add a string
        konfiger.putString("Greet", "Hello World")

        //get an object
        println(konfiger["Greet"])

        //remove an object
        konfiger.remove("Greet")

        //add an String
        konfiger.putString("What", "i don't know what to write here")

        for (entry in konfiger.entries()) {
            println(entry)
        }
    }
}
```

### Write to disk

Initialize an empty konfiger object and populate it with random data, then save it to a file

```kotlin
import io.github.thecarisma.*
import java.io.*

object Test_Kotlin {
    @Throws(IOException::class, InvalidEntryException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val randomValues = arrayOf("One", "Two", "Three", "Four", "Five")
        val konfiger = Konfiger("", false)
        for (i in 0..199) {
            val random = Math.floor(Math.random() * (randomValues.size - 1) + 0)
            konfiger.putString("" + i, randomValues[random.toInt()])
        }
        konfiger.save("test/konfiger.conf")
    }
}
```

### Get Types

Load the entries as string and get them as a true type.

```kotlin
import io.github.thecarisma.*
import java.io.*

object Test_Kotlin {
    @Throws(IOException::class, InvalidEntryException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val konfiger = Konfiger(
            "\n" +
                    "String=This is a string\n" +
                    "Number=215415245\n" +
                    "Float=56556.436746\n" +
                    "Boolean=true\n", false
        )

        val str = konfiger.getString("String")
        val num = konfiger.getLong("Number")
        val flo = konfiger.getFloat("Float")
        val bool = konfiger.getBoolean("Boolean")

        println(str is String)
        println(num is Long)
        println(flo is Float)
        println(bool is Boolean)
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

```kotlin
import io.github.thecarisma.*
import java.io.*

object Test_Kotlin {
    @Throws(IOException::class, InvalidEntryException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val konfiger = Konfiger(
            File("test/konfiger.conf"),  //the file path
            true //lazyLoad true
        )
        //at this point nothing is read from the file

        //the size of konfiger is 0 even if the file contains over 1000 entries
        println(konfiger.size())

        //the key 'Twos' is at the second line in the file, therefore two entry has
        //been read to get the value.
        println(konfiger["Twos"])

        //the size becomes 2,
        println(konfiger.size())

        //to read all the entries simply call the toString() method
        println(konfiger.toString())

        //now the size is equal to the entry
        println(konfiger.size())
    }
}
```

### Seperator and delimeter

Initailize a konfiger object with default seperator and delimeter then change the seperator and selimeter at runtime

```kotlin
import io.github.thecarisma.*
import java.io.*

object Test_Kotlin {
    @Throws(IOException::class, InvalidEntryException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val konfiger = Konfiger(File("test/konfiger.conf"), false)
        konfiger.delimeter = '?'
        konfiger.seperator = ','

        println(konfiger.toString())
    }
}
```

### Read file with Stream

Read a key value file using the progressive [KonfigerStream](https://github.com/konfiger/konfiger-nodejs/blob/master/src/io/github/thecarisma/KonfigerStream.js), each scan returns the current key value array `[ 'key', 'value']`

```kotlin
import io.github.thecarisma.*
import java.io.*
import java.util.*

object Test_Kotlin {
    @Throws(IOException::class, InvalidEntryException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val kStream = KonfigerStream(File("test/konfiger.conf"))
        while (kStream.hasNext()) {
            val entry = kStream.next()
            println(Arrays.toString(entry))
        }
    }
}
```

### Read String with Stream

Read a key value string using the progressive [KonfigerStream](https://github.com/konfiger/konfiger-nodejs/blob/master/src/io/github/thecarisma/KonfigerStream.js), each scan returns the current key value array `[ 'key', 'value']`

```kotlin
import io.github.thecarisma.*
import java.io.*
import java.util.*

object Test_Kotlin {
    @Throws(IOException::class, InvalidEntryException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val kStream = KonfigerStream(
            "\n" +
                    "String=This is a string\n" +
                    "Number=215415245\n" +
                    "Float=56556.436746\n" +
                    "Boolean=true\n"
        )
        
        while (kStream.hasNext()) {
            val entry = kStream.next()
            println(Arrays.toString(entry))
        }
    }
}
```

### Skip Comment entries

Read all the key value entry using the stream and skipping all commented entries. The default comment prefix is `//` but in the example below the commented entries starts with `#` so the prefix is changed. The same thing happen if the key value entry is loaded from file. 

```kotlin
import io.github.thecarisma.*
import java.io.*
import java.util.*

object Test_Kotlin {
    @Throws(IOException::class, InvalidEntryException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val kStream = KonfigerStream(
            "\n" +
                    "String=This is a string\n" +
                    "#Number=215415245\n" +
                    "Float=56556.436746\n" +
                    "#Boolean=true\n"
        )
        kStream.commentPrefix = "#"

        while (kStream.hasNext()) {
            val entry = kStream.next()
            println(Arrays.toString(entry))
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
```

```kotlin
import io.github.thecarisma.*
import java.io.File
import java.io.IOException
import java.lang.reflect.InvocationTargetException

object Test_Kotlin {
    @Throws(
        IOException::class,
        InvalidEntryException::class,
        InvocationTargetException::class,
        IllegalAccessException::class
    )
    @JvmStatic
    fun main(args: Array<String>) {
        val properties = Properties()
        val konfiger = Konfiger(File("test/properties.conf"), true)
        konfiger.resolve(properties)
        println(properties.project) // konfiger
        println(properties.author) // Adewale Azeez
        konfiger.put("project", "konfiger-nodejs")
        println(properties.project) // konfiger-nodejs
    }

    internal class Properties {
        var project: String? = null
        var author: String? = null
    }
}
```

### Dissolve Object

The following snippet reads the value of a javascript object into the konfiger object, the object is not attached to konfiger unlike resolve function.

```kotlin
import io.github.thecarisma.InvalidEntryException
import io.github.thecarisma.Konfiger
import java.io.IOException
import java.lang.reflect.InvocationTargetException

object Test_Kotlin {
    internal class Properties {
        var project = "konfiger"
        var author = "Adewale"
    }

    @Throws(
        IOException::class,
        InvalidEntryException::class,
        InvocationTargetException::class,
        IllegalAccessException::class
    )
    @JvmStatic
    fun main(args: Array<String>) {
        val properties = Properties()
        val konfiger = Konfiger("")
        konfiger.dissolve(properties)
        println(konfiger["project"]) // konfiger
        println(konfiger["author"]) // Adewale Azeez
    }
}
```

### Multiline value

Konfiger stream allow multiline value. If the line ends with `\` the next line will be parse as the continuation and the leading spaces will be trimmed. The continuation character chan be changed like the example below the continuation character is changed from `\` to `+`.

for the file test.contd.conf

```kotlin
Description = This project is the closest thing to Android +
              Shared Preference in other languages +
              and off the Android platform.
ProjectName = konfiger
```

```kotlin
import io.github.thecarisma.InvalidEntryException
import io.github.thecarisma.KonfigerStream
import java.io.File
import java.io.IOException

object Test_Kotlin {
    @Throws(IOException::class, InvalidEntryException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val ks = KonfigerStream(File("test/test.contd.conf"))
        ks.continuationChar = '+'
        println(ks.next()[1])
        println(ks.next()[1])
    }
}
```

## Native Object Attachement

This feature of the project allow seamless integration with the konfiger entries by eliminating the need for writing `Konfiger.get*("")` everytime to read a value into a variable or writing lot of `Konfiger.put*()` to add an entry. 

The two function `resolve` is used to attach an object. resolve function integrate the object such that the entries in konfiger will be assigned to the matching key in the object. See the [resolve](#object-attachement-get) and [dissolve](#object-attachement-put) examples above.

In a case where the object keys are different from the entries keys in the konfiger object the function `matchGetKey` can be declared in the object to match the key when setting the object entries values, and the function `matchPutKey` is called when setting the konfiger entries from the object.

For the file English.lang

```kotlin
LoginTitle = Login Page
AgeInstruction = You must me 18 years or above to register
NewsletterOptin = Signup for our weekly news letter
```

For an object which as the same key as the konfiger entries above there is no need to declare the matchGetKey or matchPutKey in the object. Resolve example

```kotlin
import io.github.thecarisma.InvalidEntryException
import io.github.thecarisma.Konfiger
import java.io.File
import java.io.IOException
import java.lang.reflect.InvocationTargetException

object Test_Kotlin {
    @Throws(
        IOException::class,
        InvalidEntryException::class,
        InvocationTargetException::class, IllegalAccessException::class
    )
    @JvmStatic
    fun main(args: Array<String>) {
        val pageProps = PageProps()
        val kon = Konfiger(File("test/English.lang"))
        kon.resolve(pageProps)
        println(pageProps.toString())
    }

    internal class PageProps {
        var LoginTitle: String? = null
        var AgeInstruction: String? = null
        var NewsletterOptin: String? = null
        override fun toString(): String {
            return "LoginTitle=" + LoginTitle + ",AgeInstruction=" + AgeInstruction +
                    ",NewsletterOptin=" + NewsletterOptin
        }
    }
}
```

Dissolve example

```kotlin
import io.github.thecarisma.InvalidEntryException
import io.github.thecarisma.Konfiger
import java.io.IOException
import java.lang.reflect.InvocationTargetException

object Test_Kotlin {
    @Throws(
        IOException::class,
        InvalidEntryException::class,
        InvocationTargetException::class, IllegalAccessException::class
    )
    @JvmStatic
    fun main(args: Array<String>) {
        val pageProps = PageProps()
        val kon = Konfiger("")
        kon.dissolve(pageProps)
        println(kon)
    }

    internal class PageProps {
        var LoginTitle = "Login Page"
        var AgeInstruction = "You must me 18 years or above to register"
        var NewsletterOptin = "Signup for our weekly news letter"
    }
}
```

### matchGetKey

If the identifier in the object keys does not match the above entries key the object will not be resolved. For example loginTitle does not match LoginTitle, the matchGetKey can be used to map the variable key to the konfiger entry key. The following example map the object key to konfiger entries key.

```kotlin
import io.github.thecarisma.InvalidEntryException
import io.github.thecarisma.Konfiger
import java.io.File
import java.io.IOException
import java.lang.reflect.InvocationTargetException

object Test_Kotlin {
    @Throws(
        IOException::class,
        InvalidEntryException::class,
        InvocationTargetException::class, IllegalAccessException::class
    )
    @JvmStatic
    fun main(args: Array<String>) {
        val pageProps = PageProps()
        val kon = Konfiger(File("test/English.lang"))
        kon.resolve(pageProps)
        println(pageProps.toString())
    }

    internal class PageProps {
        var loginTitle: String? = null
        var ageInstruct: String? = null
        var NewsletterOptin: String? = null
        fun matchGetKey(key: String): String {
            if ("loginTitle" == key) {
                return "LoginTitle"
            } else if ("ageInstruct" == key) {
                return "AgeInstruction"
            }
            return ""
        }

        override fun toString(): String {
            return "loginTitle=" + loginTitle + ",ageInstruct=" + ageInstruct +
                    ",NewsletterOptin=" + NewsletterOptin
        }
    }
}
```

The way the above code snippet works is that when iterating the object keys if check if the function matchGetKey is present in the object if it is present the key is sent as parameter to the matchGetKey and the returned value is used to get the value from konfiger, if the matchGetKey does not return anything the object key is used to get the value from konfiger (as in the case of NewsletterOptin).

> During the resolve or dissolve process if the entry value is function it is skipped even if it key matches

For dissolving an object the method matchGetKey is invoked to find the actual key to use to add the entry in konfiger, if the object does not declare the matchGetKey function the entries will be added to konfiger as it is declared. The following example similar to the one above but dissolves an object into konfiger.

```kotlin
import io.github.thecarisma.InvalidEntryException
import io.github.thecarisma.Konfiger
import java.io.IOException
import java.lang.reflect.InvocationTargetException

object Test_Kotlin {
    @Throws(
        IOException::class,
        InvalidEntryException::class,
        InvocationTargetException::class, IllegalAccessException::class
    )
    @JvmStatic
    fun main(args: Array<String>) {
        val pageProps = PageProps()
        val kon = Konfiger("")
        kon.dissolve(pageProps)
        println(kon)
    }

    internal class PageProps {
        var loginTitle = "Login Page"
        var ageInstruct = "You must me 18 years or above to register"
        var NewsletterOptin = "Signup for our weekly news letter"
        fun matchGetKey(key: String): String {
            if ("loginTitle" == key) {
                return "LoginTitle"
            } else if ("ageInstruct" == key) {
                return "AgeInstruction"
            }
            return ""
        }
    }
}
```

### matchPutKey

The matchPutKey is invoked when an entry value is changed or when a new entry is added to konfiger. The matchPutKey is invoked with the new entry key and checked in the object matchPutKey (if decalred), the returned value is what is set in the object. E.g. if an entry `[Name, Thecarisma]` is added to konfiger the object matchPutKey is invoked with the parameter `Name` the returned value is used to set the corresponding object entry. 

```kotlin
import io.github.thecarisma.InvalidEntryException
import io.github.thecarisma.Konfiger
import java.io.IOException
import java.lang.reflect.InvocationTargetException

object Test_Kotlin {
    @Throws(
        IOException::class,
        InvalidEntryException::class,
        InvocationTargetException::class, IllegalAccessException::class
    )
    @JvmStatic
    fun main(args: Array<String>) {
        val pageProps = PageProps()
        val kon = Konfiger("")
        kon.resolve(pageProps)

        kon.put("LoginTitle", "Login Page")
        kon.put("AgeInstruction", "You must me 18 years or above to register")
        kon.put("NewsletterOptin", "Signup for our weekly news letter")
        println(pageProps.loginTitle)
        println(pageProps.ageInstruct)
        println(pageProps.NewsletterOptin)
    }

    internal class PageProps {
        var loginTitle: String? = null
        var ageInstruct: String? = null
        var NewsletterOptin: String? = null
        fun matchPutKey(key: String): String {
            if ("LoginTitle" == key) {
                return "loginTitle"
            } else if ("AgeInstruction" == key) {
                return "ageInstruct"
            }
            return ""
        }
    }
}
```

### Annotation

A more cleaner way to map an entry key to a field in an object is to use the KonfigerValue anotation. The annotation accept the key value to map to the field in konfiger.

The annotation is used for lookup when resolving or disolving an object and when changing the value of the field by updating it in konfiger. In short the anotation value is used to lookup the field instead of the verbose `matchGetKey` and `matchPutKey` methods.

```kotlin
import io.github.thecarisma.InvalidEntryException
import io.github.thecarisma.Konfiger
import io.github.thecarisma.KonfigerValue
import java.io.IOException
import java.lang.reflect.InvocationTargetException

object Test_Kotlin {
    @Throws(
        IOException::class,
        InvalidEntryException::class,
        InvocationTargetException::class, IllegalAccessException::class
    )
    @JvmStatic
    fun main(args: Array<String>) {
        val pageProps = PageProps()
        val kon = Konfiger("")
        kon.resolve(pageProps)
        kon.put("LoginTitle", "Login Page")
        kon.put("AgeInstruction", "You must me 18 years or above to register")
        kon.put("NewsletterOptin", "Signup for our weekly news letter")
        println(pageProps.loginTitle)
        println(pageProps.ageInstruct)
        println(pageProps.NewsletterOptin)
    }

    internal class PageProps {
        @KonfigerValue("LoginTitle") var loginTitle: String? = null
        @KonfigerValue("AgeInstruction") var ageInstruct: String? = null
        var NewsletterOptin: String? = null
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

```kotlin
val konfiger = Konfiger(File("test/konfiger.conf"), true)
```

The following initializer read all the entries from file at once

```kotlin
val konfiger = Konfiger(File("test/konfiger.conf"), false)
```

The following initializer read all the entries from string when needed

```kotlin
val konfiger = Konfiger(
            "\n" +
                    "Ones=11111111111\n" +
                    "Twos=2222222222222\n", true
        )
```

The following initializer read all the entries from String at once

```kotlin
val konfiger = Konfiger(
            "\n" +
                    "Ones=11111111111\n" +
                    "Twos=2222222222222\n", false
        )
```

Initialize a string which have custom delimeter and seperator

```kotlin
val konfiger = Konfiger(
            "Ones:11111111111,Twos:2222222222222",
            true,
            ':',
            ','
        )
```

### Inserting

The following types can be added into the object, int, float, long, boolean, object and string.

To add any object into the entry use the `put` method as it check the value type and properly get it string value

```kotlin
konfiger.putString("String", "This is a string")
konfiger.putLong("Long", 143431423)
konfiger.putBoolean("Boolean", true)
konfiger.putFloat("Float", 12.345f)
```

The `put` method do a type check on the value and calls the appropriate put method e.g `konfiger.put("Boolean", true)` will result in a call to `konfiger.putBoolean("Boolean", true)`. The following method are avaliable to directly add the value according to the type, `putString`, `putBoolean`, `putLong` and `putInt`. The previous example can be re-written as:

```kotlin
konfiger.putString("String", "This is a string")
konfiger.putLong("Long", 143431423)
konfiger.putBoolean("Boolean", true)
konfiger.putFloat("Float", 12.345)
```

### Finding

There are various ways to get the value from the konfiger object, the main `get` method and `getString` method both returns a string type, the other get methods returns specific types

```kotlin
konfiger.get("String")
```

To get specific type from the object use the following methods, `getString`, `getBoolean`, `getLong`, `getFloat` and `getInt`. 

```kotlin
konfiger.getString("String")
konfiger.getLong("Long")
konfiger.getBoolean("Boolean")
konfiger.getFloat("Float")
```

If the requested entrr does not exist a null/undefined value is returned, to prevent that a fallback value should be sent as second parameter incase the key is not found the second parameter will be returned.

```kotlin
konfiger.get("String", "Default Value")
konfiger.getBoolean("Boolean", false)
```

If the konfiger is initialized with lazy loading enabled if the get method is called the stream will start reading until the key is found and the stream is paused again, if the key is not found the stream will read to end. 

### Updating

The `put` method can be used to add new entry or to update an already existing entry in the object. The `updateAt` method is usefull for updating a value using it index instead of key

```kotlin
konfiger.updateAt(0, "This is an updated string")
```

### Removing

The `remove` method removes a key value entry from the konfiger, it returns true if an entry is removed and false if no entry is removed. The `remove` method accept either key(string) or index(int) of the entry.

```kotlin
konfiger.remove("String")
konfiger.remove(0)
```

### Saving to disk

Every operation on the konfiger object is done in memory to save the updated entries in a file call the `save` method with the file path to save the entry. If the konfiger is initiated from file then there is no need to add the file path to the `save` method, the entries will be saved to the file path used during initialization.

```kotlin
konfiger.save("test/test.config.ini")
```

in case of load from file, the save will write the entries to *test/test.config.ini*.

```kotlin
//...
val konfiger = Konfiger(File("test/test.config.ini"), true)
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

Before you begin contribution please read the contribution guide at [CONTRIBUTING GUIDE](https://github.com/konfiger/konfiger.github.io/blob/master/CONTRIBUTING.MD)

You can open issue or file a request that only address problems in this implementation on this repo, if the issue address the concepts of the package then create an issue or rfc [here](https://github.com/konfiger/konfiger.github.io/)

## Support

You can support some of this community as they make big impact in the traing of individual to get started with software engineering and open source contribution.

- [https://www.patreon.com/devcareer](https://www.patreon.com/devcareer)

## License

MIT License Copyright (c) 2020 [Adewale Azeez](https://twitter.com/iamthecarisma) - konfiger

