package io.github.thecarisma;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 06-Dec-20 12:33 PM
 */
public class Section {
    int indentLevel;
    String title = "";
    List<Entry.Comment> comments = new ArrayList<>();
    List<Entry> entries = new ArrayList<>();
    List<Section> subSections = new ArrayList<>();
    private String toStringCache = ""; // internal use only. cache
    private boolean changeOccur = true; // internal use only. cache

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

    public List<Entry> getEntries() {
        return entries;
    }

    public List<Section> getSubSections() {
        return subSections;
    }

    public void putSection(Section subSection) {
        if (entries.contains(subSection)) {
            throw new InvalidArgumentException("This title already contains a subsection with name '" +
                    subSection.title + "'");
        }
        subSections.add(subSection);
        changeOccur = true;
    }

    public void put(Entry entry) {
        entries.add(entry);
        changeOccur = true;
    }

    public void putString(String key, String value) {
        Entry entry = new Entry();
        entry.setKey(key);
        entry.addValue(value);
        entries.add(entry);
        changeOccur = true;
    }
    
    @Override
    public String toString() {
        if (!changeOccur) {
            return toStringCache;
        }
        return toString(new Builder());
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
                    sectionComment_.append(comments.get(index).toString());
                    if (index <= comments.size()-1) {
                        sectionComment_.append("\n");
                    }
                }
            }
            toStringCache += String.format("%s%s[%s]%s",
                    indentation,
                    sectionComment_.toString(),
                    title,
                    builder.separators[0]);
        }

        long entriesSize = entries.size();
        for (int index = 0; index < entriesSize; ++index) {
            Entry entry = entries.get(index);
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
            if (index < entriesSize-1) {
                toStringCache += builder.separators[0];
            }
            entry.setIndentLevel(actualIndentLevel);
        }

        long subSectionsSize = subSections.size();
        if (entriesSize > 0) {
            toStringCache += builder.separators[0];
        }
        for (Section subSection : subSections) {
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
                subSection.setTitle(builder.beginSectionChar + title + builder.endSectionChar);
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
}
