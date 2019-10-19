/*
 * The MIT License
 *
 * Copyright 2019 Azeez Adewale <azeezadewale98@gmail.com>.
 *
 */
package dev.sourcerersproject;

import dev.sourcerersproject.KeyValueDB;

/**
 *
 * @author Azeez Adewale <azeezadewale98@gmail.com>
 */
public class KeyValueTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        KeyValueDB keyValueDB = new KeyValueDB("One=Adewale\nThrees=3333", true, '=', '\n', false);
        for (KeyValueObject kvo : keyValueDB) {
            System.out.println(kvo);
        }
        System.out.println();
        
        System.out.println(keyValueDB.get("Greeting"));
        keyValueDB.set("Greeting", "Hello from Azeez Adewale");
        keyValueDB.add("One", "Added another one element");
		keyValueDB.add("Null", "Remove this");
        System.out.println(keyValueDB.getLike("Three"));
        
        System.out.println();
        for (KeyValueObject kvo : keyValueDB) {
            System.out.println(kvo);
        }
        System.out.println();
		System.out.println("Removed: " + keyValueDB.remove("Null"));
        
        System.out.println(keyValueDB);
        System.out.println();
        keyValueDB.add("Two", "Added another two element");
        System.out.println(keyValueDB);
        System.out.println();
        keyValueDB.add("Three", "Added another three element");
        System.out.println(keyValueDB);
        System.out.println();
    }
    
}
