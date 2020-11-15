package io.github.thecarisma;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 18-Nov-20 11:51 PM
 */
public interface EntryListener {
    boolean entryAdded(String section, String key, String value);
    boolean entryRemoved(String section, String key, String value);
    boolean entryChanged(String section, String key, String value, String newValue);
}
