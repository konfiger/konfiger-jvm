package io.github.thecarisma;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 22-Nov-20 07:17 AM
 */
public class Entry {
    String key;
    List<String> values = new ArrayList<>();
    List<String> comments = new ArrayList<>();
    String inlineComment;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public List<String> getComments() {
        return comments;
    }

    public void addValue(String value) {
        this.values.add(value);
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public void addComment(String comment) {
        this.comments.add(comment);
    }

    public String getInlineComment() {
        return inlineComment;
    }

    public void setInlineComment(String inlineComment) {
        this.inlineComment = inlineComment;
    }

    @Override
    public String toString() {
        return toString(true, '=');
    }

    public String toString(KonfigerStream.Builder builder) {
        return toString(builder.addAssignmentSpacing,
                builder.commentsAsMultiline,
                builder.delimiter,
                builder.commentPrefixes[0],
                builder.multilineCommentPrefixes[0],
                builder.addSpacePrePostCommentKeyword);
    }

    public String toString(boolean addAssignmentSpacing) {
        return toString(addAssignmentSpacing,
                false, '=', ";");
    }

    public String toString(boolean addAssignmentSpacing, char delimiter) {
        return toString(addAssignmentSpacing, false, delimiter, ";");
    }

    public String toString(boolean addAssignmentSpacing, char delimiter, String commentChar) {
        return toString(addAssignmentSpacing, false, delimiter, commentChar);
    }

    public String toString(boolean addAssignmentSpacing, boolean commentsAsMultiline, char delimiter, String commentChar) {
        return toString(addAssignmentSpacing, commentsAsMultiline, delimiter, commentChar, "'''");
    }

    public String toString(boolean addAssignmentSpacing, boolean commentsAsMultiline, char delimiter, String commentChar,
                           String multilineCommentKeyword) {
        return toString(addAssignmentSpacing, commentsAsMultiline, delimiter, commentChar,
                multilineCommentKeyword, false);
    }

    public String toString(boolean addAssignmentSpacing, boolean commentsAsMultiline, char delimiter, String commentChar,
                           String multilineCommentKeyword, boolean addSpacePrePostCommentKeyword) {
        boolean hasValue = !values.isEmpty();
        boolean hasKey = key != null;
        boolean hasComment = !comments.isEmpty();
        boolean hasInlineComment = inlineComment != null && !inlineComment.isEmpty();
        String comment_ = "";
        if (hasComment) {
            if (commentsAsMultiline) {
                comment_ = multilineCommentKeyword + (addSpacePrePostCommentKeyword ? " " : "");
                for (int index = 0; index < comments.size(); ++index) {
                    comment_ += comments.get(index);
                    if (index < comments.size()-1) {
                        comment_ += "\n";
                    }
                }
                comment_ += multilineCommentKeyword;
            } else {
                for (int index = 0; index < comments.size(); ++index) {
                    comment_ += commentChar + (addSpacePrePostCommentKeyword ? " " : "") + comments.get(index);
                    if (index < comments.size()-1) {
                        comment_ += "\n";
                    }
                }
            }
        }
        String strValue = "";
        if (!hasValue) {
            strValue = String.format("%s%s%s%s%s%s%s%s",
                    !hasComment ? "" : comment_ + (hasKey ? "\n" : ""),
                    hasKey ? key : "",
                    "",
                    "",
                    "",
                    "",
                    (hasInlineComment && addSpacePrePostCommentKeyword) ? " " : "",
                    !hasInlineComment ? "" : commentChar + (addSpacePrePostCommentKeyword ? " " : "") + inlineComment.trim());
        }
        for (String value : values) {
            strValue += String.format("%s%s%s%s%s%s%s%s",
                    !hasComment ? "" : comment_ + (hasKey || hasValue ? "\n" : ""),
                    hasKey ? key : "",
                    (hasKey && hasValue && addAssignmentSpacing) ? " " : "",
                    (hasKey && hasValue) ? delimiter : "",
                    (hasKey && hasValue && addAssignmentSpacing) ? " " : "",
                    hasValue ? value : "",
                    (hasInlineComment && addSpacePrePostCommentKeyword) ? " " : "",
                    !hasInlineComment ? "" : commentChar + (addSpacePrePostCommentKeyword ? " " : "") + inlineComment);
        }
        return strValue;
    }
}
