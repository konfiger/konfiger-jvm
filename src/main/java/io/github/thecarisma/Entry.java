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
    List<Comment> comments = new ArrayList<>();
    List<Comment> inlineComments = new ArrayList<>();

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

    public List<Comment> getComments() {
        return comments;
    }

    public void addValue(String value) {
        this.values.add(value);
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void addComment(String commentValue) {
        Comment comment = new Comment();
        comment.setCommentKeyword(";");
        comment.setValue(commentValue);
        this.comments.add(comment);
    }

    public List<Comment> getInlineComments() {
        return inlineComments;
    }

    public void setInlineComments(List<Comment> inlineComments) {
        this.inlineComments = inlineComments;
    }

    public void addInlineComment(Comment comment) {
        this.inlineComments.add(comment);
    }

    public void addInlineComment(String commentValue) {
        Comment comment = new Comment();
        comment.setCommentKeyword(";");
        comment.setValue(commentValue);
        this.inlineComments.add(comment);
    }

    @Override
    public String toString() {
        return toString(true, '=');
    }

    public String toString(KonfigerStream.Builder builder) {
        return toString(builder.addAssignmentSpacing,
                builder.delimiter,
                builder.addSpaceBeforeCommentKeyword);
    }

    public String toString(boolean addAssignmentSpacing) {
        return toString(addAssignmentSpacing, '=', true);
    }

    public String toString(boolean addAssignmentSpacing, char delimiter) {
        return toString(addAssignmentSpacing, delimiter, true);
    }

    public String toString(boolean addAssignmentSpacing, char delimiter, boolean addSpaceBeforeCommentKeyword) {
        boolean hasValue = !values.isEmpty();
        boolean hasKey = key != null;
        boolean hasComment = !comments.isEmpty();
        boolean hasInlineComment = !inlineComments.isEmpty();
        String comment_ = "";
        String inlineComment_ = "";
        if (hasComment) {
            for (int index = 0; index < comments.size(); ++index) {
                comment_ += comments.get(index).toString();
                if (index < comments.size()-1) {
                    comment_ += "\n";
                }
            }
        }
        if (hasInlineComment) {
            for (int index = 0; index < inlineComments.size(); ++index) {
                inlineComment_ += inlineComments.get(index).toString();
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
                    (hasInlineComment && addSpaceBeforeCommentKeyword) ? " " : "",
                    inlineComment_);
        }
        for (String value : values) {
            strValue += String.format("%s%s%s%s%s%s%s%s",
                    !hasComment ? "" : comment_ + (hasKey || hasValue ? "\n" : ""),
                    hasKey ? key : "",
                    (hasKey && hasValue && addAssignmentSpacing) ? " " : "",
                    (hasKey && hasValue) ? delimiter : "",
                    (hasKey && hasValue && addAssignmentSpacing) ? " " : "",
                    hasValue ? value : "",
                    (hasInlineComment && addSpaceBeforeCommentKeyword) ? " " : "",
                    inlineComment_);
        }
        return strValue;
    }

    public static class Comment {
        boolean isMultiline;
        String commentKeyword = ";";
        String value;

        public boolean isMultiline() {
            return isMultiline;
        }

        public void setMultiline(boolean multiline) {
            isMultiline = multiline;
        }

        public String getCommentKeyword() {
            return commentKeyword;
        }

        public void setCommentKeyword(String commentKeyword) {
            this.commentKeyword = commentKeyword;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("%s%s%s",
                    commentKeyword,
                    value,
                    isMultiline ? commentKeyword : "");
        }
    }
}
