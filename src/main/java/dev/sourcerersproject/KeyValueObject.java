/*
 * The MIT License
 *
 * Copyright 2019 Azeez Adewale <azeezadewale98@gmail.com>.
 *
 */
package dev.sourcerersproject;

/**
 *
 * @author Azeez Adewale <azeezadewale98@gmail.com>
 */
public class KeyValueObject {
    
    /**
     * 
     */
    private String key ;
    
    /**
     * 
     */
    private String value ;
    
    /**
     * 
     * @param key
     * @param value 
     */
    public KeyValueObject(String key, String value) {
        this.key = key.trim();
        this.value = value;
    }
    
    /**
     * 
     * @return 
     */
    public String getKey() {
        return key;
    }
    
    /**
     * 
     * @return 
     */
    public String getValue() {
        return value;
    }
    
    /**
     * 
     * @param key 
     */
    public void setKey(String key) {
        this.key = key.trim();
    }
    
    /**
     * 
     * @param value 
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode()) + ":Key=" + key + ",Value=" + value;
    }
    
}
