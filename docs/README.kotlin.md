# {::nomarkdown}<p style="text-align: center;" align="center"><img src="https://github.com/konfiger/konfiger.github.io/raw/master/icons/konfiger-kotlin.png" alt="konfiger-kotlin" style="width:180px;height:160px;" width="180" height="160" /><br /> konfiger-kotlin</p>{:/}

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
- [Usage](#usage)
	- [Initialization](#initialization)
	- [Inserting](#inserting)
	- [Finding](#finding)
	- [Updating](#updating)
	- [Removing](#removing)
    - [Saving to disk](#saving-to-disk)
- [API Documentations](#api-documentations)
- [How it works](#how-it-works)
- [Contributing](#contributing)
- [Support](#support)
- [License](#license)

## Installation

Download the jar file from the [releases](https://github.com/konfiger/konfiger-jvm/releases) and add the downloaded konfiger-$.jar to your kotlin or android project class path or library folder.

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

