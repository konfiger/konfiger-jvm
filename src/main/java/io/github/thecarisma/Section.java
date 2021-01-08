package io.github.thecarisma;

import java.util.*;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 06-Dec-20 12:33 PM
 */
public class Section {
    int indentLevel;
    String title = Konfiger.GLOBAL_SECTION_NAME;
    List<Entry.Comment> comments = new ArrayList<>();
    Map<String, List<Entry>> entries = new LinkedHashMap<>();
    Map<String, Section> subSections = new LinkedHashMap<>();
    Builder builder; // internal use only. cache
    private String toStringCache = ""; // internal use only. cache
    private boolean changeOccur = true; // internal use only. cache
    private Konfiger konfiger;

    public Section() {
    }

    public Section(Builder builder) {
        this.builder = builder;
    }

    public Section(Konfiger konfiger) {
        this.konfiger = konfiger;
        this.builder = konfiger.stream.builder;
    }

    public Builder getBuilder() {
        return builder;
    }

    public void setBuilder(Builder builder) {
        this.builder = builder;
    }

    public int getIndentLevel() {
        return indentLevel;
    }

    public void setIndentLevel(int indentLevel) {
        this.indentLevel = indentLevel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        changeOccur = true;
    }

    public List<Entry.Comment> getComments() {
        return comments;
    }

    public void setComments(List<Entry.Comment> comments) {
        this.comments = comments;
        changeOccur = true;
    }

    public void addComment(Entry.Comment comment) {
        this.comments.add(comment);
        changeOccur = true;
    }

    public void addComment(String commentValue) {
        Entry.Comment comment = new Entry.Comment();
        comment.setCommentKeyword(";");
        comment.setValue(commentValue);
        this.comments.add(comment);
        changeOccur = true;
    }

    public Map<String, Section> getSubSections() {
        return subSections;
    }
    
    /* OPS */

    public Set<Map.Entry<String, String>> stringEntries() {
        Map<String, String> entryList = new LinkedHashMap<>();
        for (String key : entries.keySet()) {
            List<Entry> entry = entries.get(key);
            String value = "";
            for (Entry ent : entry) {
                value += ent.getValue();
            }
            entryList.put(key, value);
        }

        return entryList.entrySet();
    }

    public Map<String, List<Entry>> entries() {
        return entries;
    }

    public Set<String> keys() {
        return entries.keySet();
    }

    public Collection<String> values() {
        Collection<String> values = new ArrayList<>();
        for (List<Entry> entry : entries.values()) {
            if (entry.size() > 0) {
                values.add(entry.get(0).getValue());
            }
        }
        return values;
    }

    public void clear() {
        changeOccur = true;
        comments.clear();
        entries.clear();
        subSections.clear();
    }

    public List<Entry> remove(String key) {
        changeOccur = true;
        List<Entry> result = entries.remove(key);
        reportEntryRemoved(title, result);
        return result;
    }

    public List<Entry> remove(int index) {
        int i = -1;
        for (String key : entries.keySet()) {
            ++i;
            if (i==index) {
                return remove(key);
            }
        }
        return null;
    }

    public int size() {
        int size = entries.size();
        for (Section section : subSections.values()) {
            size += section.size();
        }
        return size;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void updateAt(int index, String value) {
        if (index < size()) {
            int i = -1;
            for (String key : entries.keySet()) {
                ++i;
                if (i==index) {
                    this.changeOccur = true;
                    putString(key, value);
                    Entry entry = new Entry();
                    entry.setKey(key);
                    entry.addValue(value);
                    reportEntryChanged(title, entries.get(key), entry);
                    break;
                }
            }
        }
    }

    public void putSection(Section subSection) {
        if (subSections.containsKey(subSection.title)) {
            throw new InvalidArgumentException("This section already contains a subsection with title '" +
                    subSection.title + "'");
        }
        subSections.put(subSection.title, subSection);
        changeOccur = true;
    }

    public Section getSection(String sectionTitle) {
        return subSections.get(sectionTitle);
    }

    public Section removeSection(String sectionTitle) {
        changeOccur = true;
        return subSections.remove(sectionTitle);
    }

    /* PUTS */

    public void putInSection(String section, Entry entry) {
        // put in sub sections too
    }

    public void put(Entry entry) {
        List<Entry> entryList = new ArrayList<>();
        entryList.add(entry);
        entries.put(entry.key, entryList);
        changeOccur = true;
        reportEntryAdded(title, entry);
    }

    public void putInList(Entry entry) {
        List<Entry> entryList = new ArrayList<>();
        if (entries.containsKey(entry.key)) {
            entryList = entries.get(entry.key);
            reportEntryChanged(title, entryList, entry);
        }
        if (!entries.containsKey(entry.key)) {
            reportEntryAdded(title, entry);
        }
        entryList.add(entry);
        entries.put(entry.key, entryList);
        changeOccur = true;
    }

    public void putStringInList(String key, String value) {
        Entry entry = new Entry();
        entry.setKey(key);
        entry.addValue(value);
        putInList(entry);
    }

    public void putString(String key, String value) {
        Entry entry = new Entry();
        entry.setKey(key);
        entry.addValue(value);
        put(entry);
    }

    public void putBoolean(String key, boolean value) {
        putString(key, Boolean.toString(value));
    }

    public void putLong(String key, long value) {
        putString(key, Long.toString(value));
    }

    public void putInt(String key, int value) {
        putString(key, Integer.toString(value));
    }

    public void putFloat(String key, float value) {
        putString(key, Float.toString(value));
    }

    public void putDouble(String key, double value) {
        putString(key, Double.toString(value));
    }

    public void putList(String key, List<String> values) {
        for (String value : values) {
            putStringInList(key, value);
        }
    }

    /* END PUTS */

    public boolean contains(String key) {
        return entries.containsKey(key);
    }

    /* GETS */

    public List<Entry> getList(String key) {
        if (!builder.isCaseSensitive) {
            for (String entryKey : entries.keySet()) {
                if (entryKey.toLowerCase().equals(key)) {
                    key = entryKey;
                    break;
                }
            }
        }
        if (entries.containsKey(key)) {
            return entries.get(key);
        }
        return null;
    }

    public List<String> getListAsStrings(String key) {
        List<String> result = new ArrayList<>();
        for (Entry entry : getList(key)) {
            result.add(entry.getValue());
        }
        return result;
    }

    public Entry get(String key) {
        Entry entry = null;
        if (builder.enableEntryCache) {
            if (this.konfiger.currentCachedObject[0].equals(key) &&
                    this.konfiger.currentCachedObject[2].equals(title)) {
                return (Entry) this.konfiger.currentCachedObject[1];
            }
            if (this.konfiger.prevCachedObject[0].equals(key) &&
                    this.konfiger.prevCachedObject[2].equals(title)) {
                return (Entry) this.konfiger.prevCachedObject[1];
            }
        }
        if (!builder.isCaseSensitive) {
            for (String entryKey : entries.keySet()) {
                if (entryKey.toLowerCase().equals(key)) {
                    key = entryKey;
                    break;
                }
            }
        }
        if (entries.containsKey(key) && entries.get(key).size() > 0) {
            entry = entries.get(key).get(0);
        }
        if (builder.enableEntryCache) {
            this.konfiger.shiftCache(title, key, entry);
        }

        return entry;
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public long getLong(String key) {
        return getLong(key, 0L);
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public float getFloat(String key) {
        return getFloat(key, 0F);
    }

    public double getDouble(String key) {
        return getDouble(key, 0.0);
    }

    public Entry get(String key, Entry fallback) {
        Entry entry = get(key);
        return ( entry == null ? fallback : entry );
    }

    public String getString(String key, String fallback) {
        Entry entry = get(key);
        return (entry != null ? entry.getValue() : fallback);
    }

    public boolean getBoolean(String key, boolean fallback) {
        boolean ret = fallback;
        try {
            String v = getString(key);
            if (v != null && !v.isEmpty()) {
                ret = v.toLowerCase().equals("true");
            }
        } catch (Exception ex) {
            if (!builder.errTolerance) {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public long getLong(String key, long fallback) {
        long ret = fallback;
        try {
            ret = Long.parseLong(getString(key));
        } catch (Exception ex) {
            if (!builder.errTolerance) {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public int getInt(String key, int fallback) {
        int ret = fallback;
        try {
            ret = Integer.parseInt(getString(key));
        } catch (Exception ex) {
            if (!builder.errTolerance) {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public float getFloat(String key, float fallback) {
        float ret = fallback;
        try {
            ret = Float.parseFloat(getString(key));
        } catch (Exception ex) {
            if (!builder.errTolerance) {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public double getDouble(String key, double fallback) {
        double ret = fallback;
        try {
            if (!contains(key)) {
                return ret;
            }
            ret = Double.parseDouble(getString(key));
        } catch (Exception ex) {
            if (!builder.errTolerance) {
                ex.printStackTrace();
            }
        }
        return ret;
    }
    
    @Override
    public String toString() {
        if (!changeOccur) {
            return toStringCache;
        }
        return toString(builder != null ? builder : new Builder());
    }

    public String toString(Builder builder) {
        toStringCache = "";
        String indentation = "";
        if (indentLevel > 0) {
            for (int count = 0; count < indentLevel; ++count) {
                indentation += builder.indentation;
            }
        }
        if (builder.addSeparatorBeforeSection) {
            toStringCache += builder.separators[0];
        }
        if (title != null && !title.isEmpty() && !title.equals(Konfiger.GLOBAL_SECTION_NAME)) {
            StringBuilder sectionComment_ = new StringBuilder();
            if (comments != null && !comments.isEmpty()) {
                for (int index = 0; index < comments.size(); ++index) {
                    Entry.Comment comment = comments.get(index);
                    int actualIndentLevel = comment.getIndentLevel();
                    if (builder.indentSubSection && actualIndentLevel == 0) {
                        // TODO there is a bug here such that if the section
                        //  indent level is detected from stream this add extra
                        //  indent which should not be, but can't fix now till
                        //  indent is implements from within Stream
                        comment.setIndentLevel(actualIndentLevel + indentLevel);
                    }
                    sectionComment_.append(comment.toString(builder));
                    if (index <= comments.size()-1) {
                        sectionComment_.append("\n");
                    }
                }
            }
            toStringCache += String.format("%s%s%s%s%s%s",
                    sectionComment_.toString(),
                    indentation,
                    builder.beginSectionChar,
                    title,
                    builder.endSectionChar,
                    builder.separators[0]);
        }

        long entriesSize = entries.size();
        int index = 0;
        for (Map.Entry<String, List<Entry>> entryValue : entries.entrySet()) {
            for (Entry entry : entryValue.getValue()) {
                int actualIndentLevel = entry.getIndentLevel();
                if (builder.indentSubSection && actualIndentLevel == 0) {
                    // TODO there is a bug here such that if the section
                    //  indent level is detected from stream this add extra
                    //  indent which should not be, but can't fix now till
                    //  indent is implements from within Stream
                    entry.setIndentLevel(actualIndentLevel + indentLevel);
                }
                String value = entry.toString(builder);
                toStringCache += value;
                if (index < entriesSize - 1) {
                    toStringCache += builder.separators[0];
                }
                entry.setIndentLevel(actualIndentLevel);
            }
            ++index;
        }

        long subSectionsSize = subSections.size();
        if (entriesSize > 0) {
            toStringCache += builder.separators[0];
        }
        for (Section subSection : subSections.values()) {
            int actualIndentLevel = subSection.getIndentLevel();
            String actualTitle = subSection.getTitle();
            if (builder.indentSubSection && actualIndentLevel == 0) {
                // TODO there is a bug here such that if the section
                //  indent level is detected from stream this add extra
                //  indent which should not be, but can't fix now till
                //  indent is implements from within Stream
                subSection.setIndentLevel(actualIndentLevel + indentLevel + 1);
            }
            if (builder.subSectionTitleAsNested) {
                String preChars = "";
                String postChars = "";
                for (int count = 0; count <= indentLevel; ++count) {
                    preChars += builder.beginSectionChar;
                    postChars += builder.endSectionChar;
                }
                subSection.setTitle(preChars + actualTitle + postChars);
            } else {
                subSection.setTitle(title + builder.subSectionDelimiter + actualTitle);
            }
            toStringCache += subSection.toString(builder);
            subSection.setIndentLevel(actualIndentLevel);
            subSection.setTitle(actualTitle);
        }
        if (builder.addSeparatorAfterSection) {
            toStringCache += builder.separators[0];
        }

        changeOccur = false;
        return toStringCache;
    }

    void reportEntryAdded(String section, Entry entry) {
        if (konfiger == null) {
            return;
        }
        for (EntryListener listener : konfiger.entryListeners) {
            listener.entryAdded(section, entry);
        }
    }

    void reportEntryRemoved(String section, List<Entry> entries) {
        if (konfiger == null) {
            return;
        }
        for (EntryListener listener : konfiger.entryListeners) {
            listener.entryRemoved(section, entries);
        }
    }

    void reportEntryChanged(String section, List<Entry> entries, Entry newEntry) {
        if (konfiger == null) {
            return;
        }
        for (EntryListener listener : konfiger.entryListeners) {
            listener.entryChanged(section, entries, newEntry);
        }
    }

}
