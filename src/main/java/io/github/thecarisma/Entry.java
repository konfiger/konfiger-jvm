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
    List<String> comments = new ArrayList<>();
    String inlineComment;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public void addComment(String comment) {
        this.comments.add(comment);
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
        boolean hasValue = value != null;
        boolean hasKey = key != null;
        boolean hasComment = !comments.isEmpty();
        boolean hasInlineComment = inlineComment != null && !inlineComment.isEmpty();
        String comment_ = "";
        if (hasComment) {
            if (commentsAsMultiline) {
                comment_ = "'''" + (addSpacing ? " " : "");
                for (int index = 0; index < comments.size(); ++index) {
                    comment_ += comments.get(index).trim();
                    if (index < comments.size()-1) {
                        comment_ += "\n";
                    }
                }
                comment_ += "'''";
            } else {
                for (int index = 0; index < comments.size(); ++index) {
                    comment_ += commentChar + (addSpacing ? " " : "") + comments.get(index).trim();
                    if (index < comments.size()-1) {
                        comment_ += "\n";
                    }
                }
            }
        }
        return String.format("%s%s%s%s%s%s%s%s",
                !hasComment ? "" : comment_ + (hasKey || hasValue ? "\n" : ""),
                hasKey ? key : "",
                (hasKey && hasValue && addSpacing) ? " " : "",
                (hasKey && hasValue) ? delimiter : "",
                (hasKey && hasValue && addSpacing) ? " " : "",
                hasValue ? value : "",
                (hasInlineComment && addSpacing) ? " " : "",
                !hasInlineComment ? "" : ";" + (addSpacing ? " " : "") + inlineComment.trim());
    }
}
