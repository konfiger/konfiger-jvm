/*
 * The MIT License
 *
 * Copyright 2019 Adewale Azeez <azeezadewale98@gmail.com>.
 *
 */
package io.github.thecarisma;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 */
public class KeyValueDB implements Iterable<KeyValueObject> {
    
    /**
     * 
     */
    private char delimeter ;
    
    /**
     * 
     */
    private char seperator ;

    /**
     *
     */
    private boolean dbChanged = true ;

    /**
     *
     */
    private boolean isCaseSensitive = true ;
    
    /**
     * 
     */
    private String stringValue = "" ;
    
    /**
     * 
     */
    private ArrayList<KeyValueObject> keyValueObjects = new ArrayList<>();
    
    /**
     * 
     */
    public KeyValueDB() {
        parse("", true, '=', '\n', false);
    }

    /**
     * 
     * @param keyValueDB 
     */
    public KeyValueDB(String keyValueDB) {
        parse(keyValueDB, true, '=', '\n', false);
    }
    
    /**
     * 
     * @param keyValueDB
     * @param caseSensitive 
     */
    public KeyValueDB(String keyValueDB, boolean caseSensitive) {
        parse(keyValueDB, caseSensitive, '=', '\n', false);
    }
    
    /**
     * 
     * @param keyValueDB
     * @param caseSensitive
     * @param delimeter 
     */
    public KeyValueDB(String keyValueDB, boolean caseSensitive, char delimeter){
        parse(keyValueDB, caseSensitive, delimeter, '\n', false);
    }
    
    /**
     * 
     * @param keyValueDB
     * @param caseSensitive
     * @param delimeter
     * @param seperator 
     */
    public KeyValueDB(String keyValueDB, boolean caseSensitive, char delimeter, char seperator){
        parse(keyValueDB, caseSensitive, delimeter, seperator, false);
    }
    
    /**
     * 
     * @param keyValueDB
     * @param caseSensitive
     * @param delimeter
     * @param seperator
     * @param errTolerance 
     */
    public KeyValueDB(String keyValueDB, boolean caseSensitive, char delimeter, char seperator, boolean errTolerance){
        parse(keyValueDB, caseSensitive, delimeter, seperator, errTolerance);
    }

    /**
     * 
     * @return 
     */
    @Override
    public Iterator<KeyValueObject> iterator() {
        return new Iterator<KeyValueObject>() {
            private int index = 0 ;

            @Override
            public boolean hasNext() {
                return index < keyValueObjects.size();
            }

            @Override
            public KeyValueObject next() {
                if (!hasNext()) throw new UnsupportedOperationException("The KeyValue database is empty");
                return keyValueObjects.get(index++);
            }

            @Override
            public void remove() {

            }
        };
    }
    
    /**
     * 
     * @param index
     * @return 
     */
    public KeyValueObject getKeyValueObject(int index) {
        return keyValueObjects.get(index);
    }
    
    /**
     * 
     * @param key
     * @return 
     */
    public KeyValueObject getKeyValueObject(String key) {
        key = ((isCaseSensitive) ? key : key.toLowerCase()) ;
        for (KeyValueObject keyValueObject: keyValueObjects) {
            if (keyValueObject.getKey().equals(key)) {
                return keyValueObject ;
            }
        }
        return new KeyValueObject("","");
    }

    /**
     *
     * @param key
     * @return
     */
    public KeyValueObject getLikeKeyValueObject(String key) {
        key = ((isCaseSensitive) ? key : key.toLowerCase()) ;
        for (KeyValueObject keyValueObject: keyValueObjects) {
            if (keyValueObject.getKey().contains(key)) {
                return keyValueObject ;
            }
        }
        return new KeyValueObject("","");
    }
    
    /**
     * 
     * @param index
     * @return 
     */
    public String get(int index) {
        return keyValueObjects.get(index).getValue();
    }
    
    /**
     * 
     * @param key
     * @return 
     */
    public String get(String key) {
        key = ((isCaseSensitive) ? key : key.toLowerCase()) ;
        for (KeyValueObject keyValueObject: keyValueObjects) {
            if (keyValueObject.getKey().equals(key)) {
                return keyValueObject.getValue() ;
            }
        }
        return "";
    }

    /**
     *
     * @param key
     * @return
     */
    public String getLike(String key) {
        key = ((isCaseSensitive) ? key : key.toLowerCase()) ;
        for (KeyValueObject keyValueObject: keyValueObjects) {
            if (keyValueObject.getKey().contains(key)) {
                return keyValueObject.getValue() ;
            }
        }
        return "";
    }
    
    /**
     * 
     * @param key
     * @param defaultKeyValueObject
     * @return 
     */
    public KeyValueObject getKeyValueObject(String key, @NonNull KeyValueObject defaultKeyValueObject) {
        key = ((isCaseSensitive) ? key : key.toLowerCase()) ;
        for (KeyValueObject keyValueObject: keyValueObjects) {
            if (keyValueObject.getKey().equals(key)) {
                return keyValueObject;
            }
        }
        return defaultKeyValueObject;
    }

    /**
     *
     * @param key
     * @param defaultKeyValueObject
     * @return
     */
    public KeyValueObject getLikeKeyValueObject(String key, @NonNull KeyValueObject defaultKeyValueObject) {
        key = ((isCaseSensitive) ? key : key.toLowerCase()) ;
        for (KeyValueObject keyValueObject: keyValueObjects) {
            if (keyValueObject.getKey().contains(key)) {
                return keyValueObject;
            }
        }
        return defaultKeyValueObject;
    }
    
    /**
     * 
     * @param key
     * @param defaultKeyValueObject
     * @return 
     */
    public String get(String key, @NonNull KeyValueObject defaultKeyValueObject) {
        key = ((isCaseSensitive) ? key : key.toLowerCase()) ;
        for (KeyValueObject keyValueObject: keyValueObjects) {
            if (keyValueObject.getKey().equals(key)) {
                return keyValueObject.getValue() ;
            }
        }
        return defaultKeyValueObject.getValue();
    }

    /**
     *
     * @param key
     * @param defaultKeyValueObject
     * @return
     */
    public String getLike(String key, @NonNull KeyValueObject defaultKeyValueObject) {
        key = ((isCaseSensitive) ? key : key.toLowerCase()) ;
        for (KeyValueObject keyValueObject: keyValueObjects) {
            if (keyValueObject.getKey().contains(key)) {
                return keyValueObject.getValue() ;
            }
        }
        return defaultKeyValueObject.getValue();
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String get(String key, @NonNull String defaultValue) {
        key = ((isCaseSensitive) ? key : key.toLowerCase()) ;
        for (KeyValueObject keyValueObject_: keyValueObjects) {
            if (keyValueObject_.getKey().equals(key)) {
                return keyValueObject_.getValue() ;
            }
        }
        return defaultValue;
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String getLike(String key, @NonNull String defaultValue) {
        key = ((isCaseSensitive) ? key : key.toLowerCase()) ;
        for (KeyValueObject keyValueObject_: keyValueObjects) {
            if (keyValueObject_.getKey().contains(key)) {
                return keyValueObject_.getValue() ;
            }
        }
        return defaultValue;
    }
    
    /**
     * 
     * @param index
     * @param value 
     */
    public void set(int index, String value) {
        dbChanged = true ;
        keyValueObjects.get(index).setValue(value);
    }
    
    /**
     * 
     * @param key
     * @param value 
     */
    public void set(String key, String value) {
        key = ((isCaseSensitive) ? key : key.toLowerCase()) ;
        for (KeyValueObject keyValueObject: keyValueObjects) {
            if (keyValueObject.getKey().equals(key)) {
                keyValueObject.setValue(value);
                dbChanged = true ;
                return;
            }
        }
        add(key, value);
    }
    
    /**
     * 
     * @param index
     * @param keyValueObject 
     */
    public void setKeyValueObject(int index, KeyValueObject keyValueObject) {
        dbChanged = true ;
        keyValueObjects.set(index, keyValueObject);
    }
    
    /**
     * 
     * @param key
     * @param keyValueObject 
     */
    public void setKeyValueObject(String key, KeyValueObject keyValueObject) {
        key = ((isCaseSensitive) ? key : key.toLowerCase()) ;
        for (int i = 0; i <= keyValueObjects.size(); i++) {
            if (keyValueObjects.get(i).getKey().equals(key)) {
                keyValueObjects.set(i, keyValueObject);
                dbChanged = true ;
                return;
            }
        }
        add(keyValueObject);
    }
    
    /**
     * 
     * @param keyValueObject 
     */
    public void add(KeyValueObject keyValueObject) {
        if (!get(((isCaseSensitive) ? keyValueObject.getKey() : keyValueObject.getKey().toLowerCase())).equals("")) {
            setKeyValueObject(((isCaseSensitive) ? keyValueObject.getKey() : keyValueObject.getKey().toLowerCase()), keyValueObject);
            return;
        }
        keyValueObjects.add(keyValueObject);
        dbChanged = true ;
    }
    
    /**
     * 
     * @param key
     * @param value 
     */
    public void add(String key, String value) {
        key = ((isCaseSensitive) ? key : key.toLowerCase()) ;
        if (!get(key).equals("")) {
            set(key, value);
            return;
        }
        keyValueObjects.add(new KeyValueObject(key, value));
        dbChanged = true ;
    }

    /**
     *
     * @param index
     * @return
     */
    public KeyValueObject remove(int index) {
        KeyValueObject keyValueObject = keyValueObjects.get(index);
        if (keyValueObjects.remove(keyValueObject)) {
			dbChanged = true;
            return keyValueObject;
        }
        return new KeyValueObject("", "");
    }

    /**
     *
     * @param key
     * @return
     */
    public KeyValueObject remove(String key) {
        KeyValueObject keyValueObject = new KeyValueObject("", "");
        if (!isCaseSensitive) key = key.toLowerCase();
        for (int i = 0; i <= keyValueObjects.size(); i++) {
            if (keyValueObjects.get(i).getKey().equals(key)) {
                keyValueObject = keyValueObjects.get(i);
                if (keyValueObjects.remove(keyValueObject)) {
                    dbChanged = true;
                    return keyValueObject;
                }
                break;
            }
        }
        return keyValueObject;
    }
    
    /**
     * 
     * @param keyValueDB
     * @param caseSensitive
     * @param delimeter
     * @param seperator
     * @param errTolerance 
     */
    private void parse(String keyValueDB, boolean caseSensitive, char delimeter, char seperator, boolean errTolerance) {
        this.delimeter = delimeter;
        this.seperator = seperator;
        this.isCaseSensitive = caseSensitive ;
        char characters[] = keyValueDB.replaceAll("\r", "").toCharArray();
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        boolean parseKey = true;
        int line = 1, column = 0;
        for (int i = 0; i <= characters.length ; i++ ) {
            if (i == characters.length) {
                if (!key.toString().equals("")) {
                    if (key.toString().equals("") && value.toString().equals("")) continue;
                    if (parseKey && !errTolerance) throw new UnsupportedOperationException("Invalid entry detected near Line " + line + ":" + column);
                    keyValueObjects.add(new KeyValueObject(key.toString(), value.toString()));
                }
                break;
            }
            char character = characters[i];
            column++;
            if (character == '\n') {
                line++;
                column = 0 ;
            }
            if (character == seperator) {
                if (key.toString().equals("") && value.toString().equals("")) continue;
                if (parseKey && !errTolerance) throw new UnsupportedOperationException("Invalid entry detected near Line " + line + ":" + column);
                keyValueObjects.add(new KeyValueObject(key.toString(), value.toString()));
                parseKey = true ;
                key = new StringBuilder();
                value = new StringBuilder();
                continue;
            }
            if (character == delimeter) {
                if (!value.toString().equals("") && !errTolerance)  throw new UnsupportedOperationException("The input is imporperly sepreated near Line " + line + ":" + column+". Check the seperator");
                parseKey = false ;
                continue;
            }
            if (parseKey) {
                key.append(((caseSensitive)) ? character : ("" + character).toLowerCase());
            } else {
                value.append(character);
            }
        }
    }
    
    @Override
    public String toString() {
        if (dbChanged) {
            stringValue = "" ;
            for (int i = 0; i < keyValueObjects.size(); i++) {
                stringValue += keyValueObjects.get(i).getKey() + delimeter + keyValueObjects.get(i).getValue() ;
                if (i != (keyValueObjects.size() - 1)) stringValue += seperator;
            }
            dbChanged = false ;
        }
        return stringValue;
    }

    /**
     *
     */
    public void clear() {
        keyValueObjects = new ArrayList<>();
    }

    /**
     *
     * @return
     */
    public boolean isEmpty() {
        return keyValueObjects.size() == 0;
    }

    /**
     *
     * @return
     */
    public int size() {
        return keyValueObjects.size();
    }
    
}
