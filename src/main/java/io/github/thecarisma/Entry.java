package io.github.thecarisma;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 22-Nov-20 07:17 AM
 */
public class Entry {
    int indentLevel;
    String key;
    char delimiter = '=';
    char continuationChar = '+';
    boolean indentAsContinuation;
    List<String> values = new ArrayList<>();
    List<Comment> comments = new ArrayList<>();
    List<Comment> inlineComments = new ArrayList<>();

    public int getIndentLevel() {
        return indentLevel;
    }

    public void setIndentLevel(int indentLevel) {
        this.indentLevel = indentLevel;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public char getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
    }

    public char getContinuationChar() {
        return continuationChar;
    }

    public void setContinuationChar(char continuationChar) {
        this.continuationChar = continuationChar;
    }

    public boolean getIndentAsContinuation() {
        return indentAsContinuation;
    }

    public void setIndentAsContinuation(boolean indentAsContinuation) {
        this.indentAsContinuation = indentAsContinuation;
    }

    public String getValue() {
        return values.size() > 0 ? values.get(0) : "";
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
        return toString(true, delimiter);
    }

    public String toString(Builder builder) {
        return toString(builder.addSpaceBeforeDelimiter,
                builder.addSpaceAfterDelimiter,
                builder.delimiters[0],
                builder.indentAsContinuation,
                builder.continuationChar,
                builder.indentation,
                builder.addSpaceBeforeCommentKeyword);
    }

    public String toString(boolean addAssignmentSpacing, char delimiter) {
        return toString(addAssignmentSpacing, addAssignmentSpacing, delimiter,
                indentAsContinuation, continuationChar, "    ", true);
    }
    public String toString(boolean addAssignmentSpacing, char delimiter, boolean addSpaceBeforeCommentKeyword) {
        return toString(addAssignmentSpacing, addAssignmentSpacing, delimiter,
                indentAsContinuation, continuationChar, "    ", addSpaceBeforeCommentKeyword);
    }

    public String toString(boolean addSpaceBeforeDelimiter,
                           boolean addSpaceAfterDelimiter,
                           char delimiter,
                           boolean indentAsContinuation,
                           char continuationChar,
                           String indentation,
                           boolean addSpaceBeforeCommentKeyword) {

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
        } else {
            String value = "";
            int size = values.size();
            for (int i = 0; i < size; ++i) {
                String value_ = values.get(i);
                value += value_;
                if (indentAsContinuation) {
                    if (i < size-1) {
                        value += "\n" + indentation;
                    }
                } else {
                    if (i < size-1) {
                        value += " " + continuationChar + "\n";
                    }
                }
            }
            strValue += String.format("%s%s%s%s%s%s%s%s",
                    !hasComment ? "" : comment_ + (hasKey || hasValue ? "\n" : ""),
                    hasKey ? key : "",
                    (hasKey && hasValue && addSpaceBeforeDelimiter) ? " " : "",
                    (hasKey && hasValue) ? delimiter : "",
                    (hasKey && hasValue && addSpaceAfterDelimiter) ? " " : "",
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
