package io.github.thecarisma;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 29-Nov-20 08:15 AM
 */
public class SectionEntry extends Entry {
    String section;
    String sectionComment;

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSectionComment() {
        return sectionComment;
    }

    public void setSectionComment(String sectionComment) {
        this.sectionComment = sectionComment;
    }

    @Override
    public String toString() {
        return toString('=', true);
    }

    public String toString(char delimiter, boolean addSpacer) {
        return toString(delimiter, ';', addSpacer, false);
    }

    public String toString(char delimiter, char commentChar, boolean addSpacer) {
        return toString(delimiter, commentChar, addSpacer, false);
    }

    public String toString(char delimiter, char commentChar, boolean addSpacing, boolean commentsAsMultiline) {
        return String.format("%s[%s]\n%s",
                sectionComment == null || sectionComment.isEmpty() ? "" : ";" + (addSpacing ? " " : "") + sectionComment + "\n",
                section,
                super.toString(delimiter, commentChar, addSpacing, commentsAsMultiline));
    }
}
