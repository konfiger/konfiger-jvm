package io.github.thecarisma;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 22-Nov-20 07:17 AM
 */
public class Entry {
    String key;
    String value;
    String comment;
    String section;
    String inlineComment;
    String sectionComment;

    public Entry() {
        key = value = section = comment = inlineComment = sectionComment = "";
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getInlineComment() {
        return inlineComment;
    }

    public void setInlineComment(String inlineComment) {
        this.inlineComment = inlineComment;
    }

    public String getSectionComment() {
        return sectionComment;
    }

    public void setSectionComment(String sectionComment) {
        this.sectionComment = sectionComment;
    }

    @Override
    public String toString() {
        if (section.isEmpty() || section.equals("__global__")) {
            return String.format("%s%s = %s%s",
                    comment.isEmpty() ? "" : ";" + comment + "\n",
                    key, value,
                    inlineComment.isEmpty() ? "" : " ;" + inlineComment);
        }
        return String.format("%s[%s]\n%s%s = %s%s",
                sectionComment.isEmpty() ? "" : ";" + sectionComment + "\n",
                section,
                comment.isEmpty() ? "" : ";" + comment + "\n",
                key, value,
                inlineComment.isEmpty() ? "" : " ;" + inlineComment);
    }
}
