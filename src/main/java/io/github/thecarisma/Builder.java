package io.github.thecarisma;

import java.io.File;
import java.net.URL;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 30-Nov-20 10:25 AM
 */
public class Builder {
    char[] delimiters;
    char[] separators;
    char beginSectionChar;
    char endSectionChar;
    boolean errTolerance;
    boolean ignoreInlineComment;
    boolean newlineAndIndentationForMultilineDelimiter;
    boolean enableIndentedSection;
    boolean enableNestedSections;
    boolean addAssignmentSpacing;
    boolean commentsAsMultiline;
    boolean addSpaceBeforeCommentKeyword;
    String filePath;
    String string;
    String[] commentPrefixes;
    String[] multilineCommentPrefixes;
    int[] commentPrefixSizes;
    int[] multilineCommentPrefixesSizes;
    String subSectionDelimiter;
    char continuationChar;

    public Builder() {
        string = "";
        delimiters = new char[]{'='};
        separators = new char[]{'\n'};
        this.beginSectionChar = '[';
        this.endSectionChar = ']';
        subSectionDelimiter = "~~>";
        continuationChar = '\\';

        commentPrefixes = new String[] {";"};
        commentPrefixSizes = new int[] {1};

        multilineCommentPrefixes = new String[] {"```", "'''", "\"\"\""};
        multilineCommentPrefixesSizes = new int[] {3,3,3};

    }

    public Builder withDelimiters(char[] delimiters) {
        if (delimiters.length < 1) {
            throw new InvalidArgumentException("Invalid length of argument for delimiters, the delimiters must" +
                    " contain at least one value");
        }
        this.delimiters = delimiters;
        return this;
    }

    public Builder withSeparators(char[] separators) {
        if (separators.length < 1) {
            throw new InvalidArgumentException("Invalid length of argument for separators, the separators must" +
                    " contain at least one value");
        }
        this.separators = separators;
        return this;
    }

    public Builder withBeginSectionChar(char beginSectionChar) {
        this.beginSectionChar = beginSectionChar;
        return this;
    }

    public Builder withEndSectionChar(char endSectionChar) {
        this.endSectionChar = endSectionChar;
        return this;
    }

    public Builder withErrTolerance() {
        this.errTolerance = true;
        return this;
    }

    public Builder ignoreInlineComment() {
        this.ignoreInlineComment = true;
        return this;
    }

    public Builder useNewlineAndIndentationForMultilineDelimiter() {
        this.newlineAndIndentationForMultilineDelimiter = true;
        return this;
    }

    public Builder enableIndentedSection() {
        this.enableIndentedSection = true;
        return this;
    }

    public Builder enableNestedSections() {
        this.enableNestedSections = true;
        return this;
    }

    public Builder withAssignmentSpacing() {
        this.addAssignmentSpacing = true;
        return this;
    }

    public Builder useCommentsAsMultiline() {
        this.commentsAsMultiline = true;
        return this;
    }

    public Builder withSpaceBeforeCommentKeyword() {
        this.addSpaceBeforeCommentKeyword = true;
        return this;
    }

    public Builder withURL(URL url) {
        if (!this.string.isEmpty() && this.filePath != null) {
            throw new IllegalStateException("The stream can only have one source which is either" +
                    " string, file or URL. Build using any of withString(...), withFile(...) and withURL(...) but" +
                    " not combination of the methods.");
        }
        File file = new File(url.getPath());
        if (!file.exists()) {
            throw new FileNotFoundException("The file does not exist " + url.getPath());
        }
        this.filePath = file.getAbsolutePath();
        return this;
    }

    public Builder withFile(File file) throws FileNotFoundException {
        if (!this.string.isEmpty()) {
            throw new IllegalStateException("The stream can only have one source which is either" +
                    " string, file or URL. Build using any of withString(...), withFile(...) and withURL(...) but" +
                    " not combination of the methods.");
        }
        if (!file.exists()) {
            throw new FileNotFoundException("The file does not exist " + file.getAbsolutePath());
        }
        this.filePath = file.getAbsolutePath();
        return this;
    }

    public Builder withString(String string) {
        if (this.filePath != null) {
            throw new IllegalStateException("The stream can only have one source which is either" +
                    " string, file or URL. Build using any of withString(...), withFile(...) and withURL(...) but" +
                    " not combination of the methods.");
        }
        this.string = string;
        return this;
    }

    public Builder withCommentPrefixes(String... commentPrefixes) {
        this.commentPrefixes = commentPrefixes;
        this.commentPrefixSizes = new int[this.commentPrefixes.length];
        for (int index = 0; index < commentPrefixSizes.length; ++index) {
            this.commentPrefixSizes[index] = this.commentPrefixes[index].length();
        }
        return this;
    }

    public Builder withMultilineCommentPrefixes(String... multilineCommentPrefixes) {
        this.multilineCommentPrefixes = commentPrefixes;
        this.multilineCommentPrefixesSizes = new int[this.multilineCommentPrefixes.length];
        for (int index = 0; index < multilineCommentPrefixesSizes.length; ++index) {
            this.multilineCommentPrefixesSizes[index] = this.multilineCommentPrefixes[index].length();
        }
        return this;
    }

    public Builder withContinuationChar(char continuationChar) {
        this.continuationChar = continuationChar;
        return this;
    }

    public Builder withSubSectionDelimiter(String subSectionDelimiter) {
        this.subSectionDelimiter = subSectionDelimiter;
        return this;
    }

    public KonfigerStream build() throws FileNotFoundException {
        return new KonfigerStream(this);
    }
}
