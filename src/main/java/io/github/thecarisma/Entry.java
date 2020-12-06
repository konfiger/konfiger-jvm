package io.github.thecarisma;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 22-Nov-20 07:17 AM
 */
// TODO cache to string
public class Entry {
    String key;
    int indentLevel = 0;
    char delimiter = '=';
    char continuationChar = '\\';
    boolean indentAsContinuation;
    List<String> values = new ArrayList<>();
    List<Comment> comments = new ArrayList<>();
    List<Comment> inlineComments = new ArrayList<>();
    String section = Konfiger.GLOBAL_SECTION_NAME; // internal use only
    List<Comment> sectionComment = new ArrayList<>(); // internal use only
    private String toStringCache = ""; // internal use only. cache
    private boolean changeOccur = true; // internal use only. cache

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
        changeOccur = true;
    }

    public int getIndentLevel() {
        return indentLevel;
    }

    public void setIndentLevel(int indentLevel) {
        this.indentLevel = indentLevel;
        changeOccur = true;
    }

    public char getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
        changeOccur = true;
    }

    public char getContinuationChar() {
        return continuationChar;
    }

    public void setContinuationChar(char continuationChar) {
        this.continuationChar = continuationChar;
        changeOccur = true;
    }

    public boolean getIndentAsContinuation() {
        return indentAsContinuation;
    }

    public void setIndentAsContinuation(boolean indentAsContinuation) {
        this.indentAsContinuation = indentAsContinuation;
        changeOccur = true;
    }

    public String getFirstValue() {
        return values.size() > 0 ? values.get(0) : "";
    }

    public String getValue(Object... params) {
        String indentation = params.length > 0 ? (String)params[0] : "    ";
        boolean indentAsContinuation = params.length > 1 ? (boolean)params[1] : this.indentAsContinuation;
        char continuationChar = params.length > 2 ? (char)params[2] : this.continuationChar;
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
                    value += (continuationChar != '\0' ? continuationChar : "") + "\n";
                }
            }
        }
        return value;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
        changeOccur = true;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void addValue(String value) {
        this.values.add(value);
        changeOccur = true;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        changeOccur = true;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        changeOccur = true;
    }

    public void addComment(String commentValue) {
        Comment comment = new Comment();
        comment.setCommentKeyword(";");
        comment.setValue(commentValue);
        this.comments.add(comment);
        changeOccur = true;
    }

    public List<Comment> getInlineComments() {
        return inlineComments;
    }

    public void setInlineComments(List<Comment> inlineComments) {
        this.inlineComments = inlineComments;
        changeOccur = true;
    }

    public void addInlineComment(Comment comment) {
        this.inlineComments.add(comment);
        changeOccur = true;
    }

    public void addInlineComment(String commentValue) {
        Comment comment = new Comment();
        comment.setCommentKeyword(";");
        comment.setValue(commentValue);
        this.inlineComments.add(comment);
        changeOccur = true;
    }

    @Override
    public String toString() {
        if (!changeOccur) {
            return toStringCache;
        }
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
        String preIndentation = "";
        if (indentLevel > 0) {
            for (int count = 0; count < indentLevel; ++count) {
                preIndentation += indentation;
            }
        }
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
        toStringCache = "";
        if (!hasValue) {
            toStringCache = String.format("%s%s%s%s%s%s%s%s%s",
                    preIndentation,
                    !hasComment ? "" : comment_ + (hasKey ? "\n" : ""),
                    hasKey ? key : "",
                    "",
                    "",
                    "",
                    "",
                    (hasInlineComment && addSpaceBeforeCommentKeyword) ? " " : "",
                    inlineComment_);
        } else {
            toStringCache += String.format("%s%s%s%s%s%s%s%s%s",
                    preIndentation,
                    !hasComment ? "" : comment_ + "\n",
                    hasKey ? key : "",
                    (hasKey && addSpaceBeforeDelimiter) ? " " : "",
                    (hasKey) ? delimiter : "",
                    (hasKey && addSpaceAfterDelimiter) ? " " : "",
                    getValue(indentation, indentAsContinuation, continuationChar),
                    (hasInlineComment && addSpaceBeforeCommentKeyword) ? " " : "",
                    inlineComment_);
        }

        changeOccur = false;
        return toStringCache;
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
            if (isMultiline && commentKeyword.length() < 3) {
                commentKeyword = "\"\"\"";
            }
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
