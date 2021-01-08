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
    char continuationChar;
    int sizeOfSpaceForTab;
    boolean errTolerance;
    boolean ignoreInlineComment;
    boolean indentAsContinuation;
    boolean enableIndentedSection;
    boolean indentSubSection;
    boolean enableNestedSections;
    boolean subSectionTitleAsNested;
    boolean addSeparatorBeforeSection;
    boolean addSeparatorAfterSection;
    boolean addSpaceBeforeDelimiter;
    boolean addSpaceAfterDelimiter;
    boolean commentsAsMultiline;
    boolean trimmingKey;
    boolean trimmingValue;
    boolean trimmingSection;
    boolean addSpaceBeforeCommentKeyword;
    boolean addSpaceAfterCommentKeyword;
    boolean wrapMultilineValue;
    boolean isCaseSensitive;
    boolean enableEntryCache;
    String filePath;
    String string;
    String subSectionDelimiter;
    String indentation = Konfiger.DEFAULT_TAB;
    String[] commentPrefixes;
    String[] multilineCommentPrefixes;
    int[] commentPrefixSizes;
    int[] multilineCommentPrefixesSizes;

    public Builder() {
        string = "";
        delimiters = new char[]{'='};
        separators = new char[]{'\n'};
        this.beginSectionChar = '[';
        this.endSectionChar = ']';
        subSectionDelimiter = "\\";
        continuationChar = '\\';

        sizeOfSpaceForTab = 4;
        trimmingKey = true;
        trimmingValue = true;
        trimmingSection = true;

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

    public Builder withSizeOfSpaceForTab(int sizeOfSpaceForTab) {
        this.sizeOfSpaceForTab = sizeOfSpaceForTab;
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

    public Builder withTrimmingSection(boolean trimmingSection) {
        this.trimmingSection = trimmingSection;
        return this;
    }

    public Builder withTrimmingKey(boolean trimmingKey) {
        this.trimmingKey = trimmingKey;
        return this;
    }

    public Builder withTrimmingValue(boolean trimmingValue) {
        this.trimmingValue = trimmingValue;
        return this;
    }

    public Builder wrapMultilineValue() {
        this.wrapMultilineValue = true;
        return this;
    }

    public Builder withCaseSensitivity(boolean isCaseSensitive) {
        this.isCaseSensitive = isCaseSensitive;
        return this;
    }

    public Builder enableEntryCache(boolean enableEntryCache) {
        this.enableEntryCache = enableEntryCache;
        return this;
    }

    public Builder ignoreInlineComment() {
        this.ignoreInlineComment = true;
        return this;
    }

    public Builder useIndentationAsContinuation() {
        this.indentAsContinuation = true;
        return this;
    }

    public Builder enableIndentedSection() {
        this.enableIndentedSection = true;
        return this;
    }

    public Builder indentSubSection() {
        this.indentSubSection = true;
        return this;
    }

    public Builder enableNestedSections() {
        this.enableNestedSections = true;
        return this;
    }

    public Builder writeSubSectionTitleAsNested() {
        this.subSectionTitleAsNested = true;
        return this;
    }

    public Builder withAssignmentSpacing() {
        this.addSpaceBeforeDelimiter = true;
        this.addSpaceAfterDelimiter = true;
        return this;
    }

    public Builder withSpaceBeforeDelimiter() {
        this.addSpaceBeforeDelimiter = true;
        return this;
    }

    public Builder withSpaceAfterDelimiter() {
        this.addSpaceAfterDelimiter = true;
        return this;
    }

    public Builder addSeparatorBeforeSection() {
        this.addSeparatorBeforeSection = true;
        return this;
    }

    public Builder addSeparatorAfterSection() {
        this.addSeparatorAfterSection = true;
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

    public Builder withSpaceAfterCommentKeyword() {
        this.addSpaceAfterCommentKeyword = true;
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
        this.multilineCommentPrefixes = multilineCommentPrefixes;
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

    public Builder withIndentation(String indentation) {
        this.indentation = indentation;
        return this;
    }

    public KonfigerStream build() {
        return new KonfigerStream(this);
    }

    public Konfiger konfiger() {
        return new Konfiger(this);
    }

    public Konfiger konfiger(boolean lazyLoad) {
        return new Konfiger(this, lazyLoad);
    }

    // getters

    public char[] getDelimiters() {
        return delimiters;
    }

    public char[] getSeparators() {
        return separators;
    }

    public char getBeginSectionChar() {
        return beginSectionChar;
    }

    public char getEndSectionChar() {
        return endSectionChar;
    }

    public char getContinuationChar() {
        return continuationChar;
    }

    public int getSizeOfSpaceForTab() {
        return sizeOfSpaceForTab;
    }

    public boolean isErrTolerance() {
        return errTolerance;
    }

    public boolean isIgnoreInlineComment() {
        return ignoreInlineComment;
    }

    public boolean isIndentAsContinuation() {
        return indentAsContinuation;
    }

    public boolean isEnableIndentedSection() {
        return enableIndentedSection;
    }

    public boolean isIndentSubSection() {
        return indentSubSection;
    }

    public boolean isEnableNestedSections() {
        return enableNestedSections;
    }

    public boolean isSubSectionTitleAsNested() {
        return subSectionTitleAsNested;
    }

    public boolean isAddSeparatorBeforeSection() {
        return addSeparatorBeforeSection;
    }

    public boolean isAddSeparatorAfterSection() {
        return addSeparatorAfterSection;
    }

    public boolean isAddSpaceBeforeDelimiter() {
        return addSpaceBeforeDelimiter;
    }

    public boolean isAddSpaceAfterDelimiter() {
        return addSpaceAfterDelimiter;
    }

    public boolean isCommentsAsMultiline() {
        return commentsAsMultiline;
    }

    public boolean isTrimmingKey() {
        return trimmingKey;
    }

    public boolean isTrimmingValue() {
        return trimmingValue;
    }

    public boolean isTrimmingSection() {
        return trimmingSection;
    }

    public boolean isAddSpaceBeforeCommentKeyword() {
        return addSpaceBeforeCommentKeyword;
    }

    public boolean isAddSpaceAfterCommentKeyword() {
        return addSpaceAfterCommentKeyword;
    }

    public boolean isWrapMultilineValue() {
        return wrapMultilineValue;
    }

    public boolean isCaseSensitive() {
        return isCaseSensitive;
    }

    public boolean isEnableEntryCache() {
        return enableEntryCache;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getString() {
        return string;
    }

    public String getSubSectionDelimiter() {
        return subSectionDelimiter;
    }

    public String getIndentation() {
        return indentation;
    }

    public String[] getCommentPrefixes() {
        return commentPrefixes;
    }

    public String[] getMultilineCommentPrefixes() {
        return multilineCommentPrefixes;
    }

    public int[] getCommentPrefixSizes() {
        return commentPrefixSizes;
    }

    public int[] getMultilineCommentPrefixesSizes() {
        return multilineCommentPrefixesSizes;
    }
}
