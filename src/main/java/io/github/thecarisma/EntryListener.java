package io.github.thecarisma;

import java.util.List;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 18-Nov-20 11:51 PM
 */
public interface EntryListener {
    void entryAdded(String section, Entry entry);
    void entryRemoved(String section, List<Entry> entries);
    void entryChanged(String section, List<Entry> entries, Entry newEntry);
}
