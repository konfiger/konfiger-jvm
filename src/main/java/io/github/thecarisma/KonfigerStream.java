package io.github.thecarisma;

import java.io.*;
import java.net.URL;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 */
public class KonfigerStream {

    public static class Builder {
        char delimiter;
        char separator;
        char beginSectionChar;
        char endSectionChar;
        boolean errTolerance;
        boolean ignoreInlineComment;
        String filePath;
        String string = "";
        String[] commentPrefixes = new String[] {";"};
        int[] commentPrefixSizes = new int[] {1};
        char continuationChar = '\\';

        public Builder() {
            this.delimiter = '=';
            this.separator = '\n';
            this.beginSectionChar = '[';
            this.endSectionChar = ']';
        }

        public Builder withDelimiter(char delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        public Builder withSeparator(char separator) {
            this.separator = separator;
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

        public Builder withURL(URL url) throws FileNotFoundException {
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
                        " string, file or URL. Build using withFile(...) or withString(...).");
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
                        " string, file or URL. Build using withFile(...) or withString(...).");
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

        public Builder withContinuationChar(char continuationChar) {
            this.continuationChar = continuationChar;
            return this;
        }

        public KonfigerStream build() throws FileNotFoundException {
            return new KonfigerStream(this);
        }

    }

    private InputStream in;
    private int readPosition = 0;
    private boolean isFile;
    private boolean hasNext_ = false;
    private boolean trimmingSection = true;
    private boolean trimmingKey = true;
    private boolean trimmingValue = true;
    private boolean doneReading_ = false;
    private String section = "__global__";
    private int i = -1;
    String filePath = "";
    private String patchkey = "";
    private String sectionComment = "";
    private String entryComment = "";
    int line = 1;
    int column = 0;
    final Builder builder;

    public KonfigerStream() {
        this("");
    }

    public KonfigerStream(String rawString) {
        this(rawString, '=', '\n', false);
    }

    public KonfigerStream(File file) throws FileNotFoundException {
        this(file, '=', '\n', false);
    }

    public KonfigerStream(String rawString, char delimiter, char separator) {
        this(rawString, delimiter, separator, false);
    }

    public KonfigerStream(File file, char delimiter, char separator) throws FileNotFoundException {
        this(file, delimiter, separator, false);
    }

    public KonfigerStream(String rawString, char delimiter, char separator, boolean errTolerance) {
        this.builder = builder()
                .withString(rawString)
                .withDelimiter(delimiter)
                .withSeparator(separator);
        this.builder.errTolerance = errTolerance;
    }

    public KonfigerStream(File file, char delimiter, char separator, boolean errTolerance) throws FileNotFoundException {
        this(builder()
                .withFile(file)
                .withDelimiter(delimiter)
                .withSeparator(separator));
        this.builder.errTolerance = errTolerance;
    }

    public KonfigerStream(Builder builder) throws FileNotFoundException {
        this.builder = builder;
        if (this.builder.filePath != null) {
            this.isFile = true;
            this.filePath = this.builder.filePath;
            in = new FileInputStream(new File(this.filePath));
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isTrimmingSection() {
        return trimmingSection;
    }

    public void setTrimmingSection(boolean trimmingSection) {
        this.trimmingSection = trimmingSection;
    }

    public boolean isTrimmingKey() {
        return trimmingKey;
    }

    public void setTrimmingKey(boolean trimming) {
        this.trimmingKey = trimming;
    }

    public boolean isTrimmingValue() {
        return trimmingValue;
    }

    public void setTrimmingValue(boolean trimming) {
        this.trimmingValue = trimming;
    }

    /**
     * @deprecated use {@link KonfigerStream#getCommentPrefixes} instead
     */
    @Deprecated
    public String getCommentPrefix() {
        if (builder.commentPrefixes.length > 0) {
            return builder.commentPrefixes[0];
        }
        return ";";
    }

    /**
     * @deprecated use {@link KonfigerStream#setCommentPrefixes} instead
     */
    @Deprecated
    public void setCommentPrefix(String commentPrefix) {
        setCommentPrefixes(commentPrefix);
    }

    public String[] getCommentPrefixes() {
        return builder.commentPrefixes;
    }

    public void setCommentPrefixes(String... commentPrefixes) {
        Builder builder = new Builder().withCommentPrefixes(commentPrefixes);
        this.builder.commentPrefixes = builder.commentPrefixes;
        this.builder.commentPrefixSizes = builder.commentPrefixSizes;
    }

    public char getContinuationChar() {
        return builder.continuationChar;
    }

    public void setContinuationChar(char continuationChar) {
        this.builder.continuationChar = continuationChar;
    }

    public void errorTolerance(boolean errTolerance) {
        this.builder.errTolerance = errTolerance;
    }

    public boolean isErrorTolerant() {
        return this.builder.errTolerance;
    }

    int commentPrefixMatchIndex(char c, int subCount) {
        for (int index = 0; index < builder.commentPrefixes.length; ++index) {
            if (builder.commentPrefixes[index].charAt(subCount) == c) {
                return index;
            }
        }
        return -1;
    }

    public boolean hasNext() throws IOException {
        int subCount = 0;
        int commentPrefixIndex = -1;
        String comment = "";
        patchkey = "";
        if (!doneReading_) {
            if (isFile) {
                hasNext_ = ((i = in.read()) != -1);
                while ((""+(char) i).trim().isEmpty()) {
                    hasNext_ = ((i = in.read()) != -1);
                    if (i == '\n') {
                        ++line;
                        column = 0;
                    }
                }
                if ((commentPrefixIndex = commentPrefixMatchIndex((char)i, subCount)) > -1) {
                    do {
                        patchkey += (char)i;
                        subCount++;
                        if (builder.commentPrefixSizes[commentPrefixIndex] == subCount) {
                            break;
                        }
                    } while ((i = in.read()) != -1 &&
                            ((char)i == builder.commentPrefixes[commentPrefixIndex].charAt(subCount-1)));
                    if (patchkey.equals(builder.commentPrefixes[commentPrefixIndex])) {
                        while ((i = in.read()) != -1 && (char)i != builder.separator) {
                            comment += (char)i;
                        }
                        entryComment = comment;
                        return hasNext();
                    }
                }

                if (!hasNext_) {
                    doneReading();
                }

                if (hasNext_) {
                    if (patchkey.isEmpty() && (char)i == builder.beginSectionChar) {
                        section = "";
                        while ((i = in.read()) != -1) {
                            if (i == builder.endSectionChar) {
                                hasNext_ = ((i = in.read()) != -1);
                                sectionComment = entryComment;
                                entryComment = "";
                                return hasNext();
                            }
                            section += (char)i;
                        }
                    }
                }
                return hasNext_;
            } else {
                long length = builder.string.length();
                while (readPosition < length) {
                    if (!(""+builder.string.charAt(readPosition)).trim().isEmpty()) {
                        if ((commentPrefixIndex = commentPrefixMatchIndex(builder.string.charAt(readPosition),
                                subCount)) > -1) {
                            while (builder.string.charAt(readPosition+subCount) ==
                                    builder.commentPrefixes[commentPrefixIndex].charAt(subCount)) {
                                ++subCount;
                                if (builder.commentPrefixSizes[commentPrefixIndex] == subCount) {
                                    break;
                                }
                            }
                            if (builder.commentPrefixSizes[commentPrefixIndex] == subCount) {
                                ++readPosition;
                                while (readPosition < length &&
                                        builder.string.charAt(readPosition) != builder.separator) {
                                    comment += builder.string.charAt(readPosition);
                                    ++readPosition;
                                }
                                ++readPosition;
                                entryComment = comment;
                                return hasNext();
                            }
                        }

                        hasNext_ = true;
                        if (builder.string.charAt(readPosition) == builder.beginSectionChar) {
                            section = "";
                            ++readPosition;
                            while (readPosition < length) {
                                if (builder.string.charAt(readPosition) == builder.endSectionChar) {
                                    ++readPosition;
                                    sectionComment = entryComment;
                                    entryComment = "";
                                    return hasNext();
                                }
                                section += builder.string.charAt(readPosition);
                                ++readPosition;
                            }
                        }
                        return true;
                    }
                    ++readPosition;
                }
                hasNext_ = false;
                return false;
            }
        }
        return hasNext_;
    }

    // indexes
    // 0 -> key
    // 1 -> value
    // 2 -> comment
    // 3 -> inline comment
    // 4 -> section
    // 5 -> section comment
    public String[] next() throws InvalidEntryException, IOException {
        String[] ret = new String[6];
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        String inlineComment = "";
        boolean parseKey = true;
        boolean isMultiline = false;
        char prevChar = '\0';
        char prevPrevChar = '\0';
        int subCount = 0;
        int commentPrefixIndex = -1;
        String patchValue = "";

        if (this.isFile) {
            do {
                char c = (char)i;
                ++column;
                if (c == '\n') {
                    ++line;
                    column = 0;
                    if (!parseKey && prevChar == this.builder.continuationChar && prevPrevChar != '\\') {
                        String tmpValue = value.toString();
                        value = new StringBuilder();
                        if (tmpValue.charAt(tmpValue.length()-1) == '\r') {
                            tmpValue = tmpValue.substring(0, tmpValue.length() - 2);
                        } else {
                            tmpValue = tmpValue.substring(0, tmpValue.length() - 1);
                        }
                        value.append(tmpValue);
                        do {
                            c = (char)i;
                        } while ((i = in.read()) != -1 && (""+c).trim().isEmpty());
                        isMultiline = true;
                    }
                }
                if (!parseKey) {
                    if ((commentPrefixIndex = commentPrefixMatchIndex(c, subCount)) > -1 && !this.builder.ignoreInlineComment) {
                        boolean hasInlineComment = false;
                        do {
                            subCount++;
                            if (builder.commentPrefixSizes[commentPrefixIndex] == subCount) {
                                while ((i = in.read()) != -1 && (char)i != builder.separator) {
                                    inlineComment += (char)i;
                                }
                                hasInlineComment = true;
                                break;
                            }
                        } while ((i = in.read()) != -1 &&
                                ((char)i == builder.commentPrefixes[commentPrefixIndex].charAt(subCount)));
                        if (hasInlineComment) {
                            break;
                        }
                        patchValue += ((char) i);
                        subCount = 0;

                    }
                }
                if (c == this.builder.separator && prevChar != '^') {
                    if ((key.length() == 0) && (value.length() == 0)) continue;
                    if (parseKey && !this.builder.errTolerance) {
                        throw new InvalidEntryException("Invalid entry detected in file '" +
                                this.filePath + "' near", line, column);
                    }
                    break;
                }
                if (c == this.builder.delimiter && parseKey) {
                    if ((value.length() > 0) && !this.builder.errTolerance) {
                        throw new InvalidEntryException("The input is improperly separated in file '" +
                                this.filePath + "' near", line, column);
                    }
                    parseKey = false;
                    continue;
                }
                if (parseKey) {
                    key.append(c);
                } else {
                    value.append(c);
                    if (isMultiline) {
                        value.append((char)i);
                        isMultiline = false;
                    }
                    value.append(patchValue);
                    patchValue = "";
                }
                prevPrevChar = (c == '\r' ? prevPrevChar : prevChar);
                prevChar = (c == '\r' ? (prevChar != '\\' ? '\0' : '\\') : c);
            } while ((i = in.read()) != -1);
        } else {
            long length = this.builder.string.length();
            for (;this.readPosition <= length; ++this.readPosition) {
                if (this.readPosition == this.builder.string.length()) {
                    if (key.length() > 0) {
                        if (parseKey && !this.builder.errTolerance) {
                            throw new InvalidEntryException("Invalid entry detected in \n<" +
                                    this.builder.string + ">\nnear", line, column);
                        }
                    }
                    this.doneReading();
                    break;
                }
                char c = this.builder.string.charAt(this.readPosition);
                ++column;
                if (c == '\n') {
                    ++line;
                    column = 0;
                    if (!parseKey && prevChar == this.builder.continuationChar && prevPrevChar != '\\') {
                        String tmpValue = value.toString();
                        value = new StringBuilder();
                        if (tmpValue.charAt(tmpValue.length()-1) == '\r') {
                            tmpValue = tmpValue.substring(0, tmpValue.length() - 2);
                        } else {
                            tmpValue = tmpValue.substring(0, tmpValue.length() - 1);
                        }
                        value.append(tmpValue);
                        do {
                            ++this.readPosition;
                            c = this.builder.string.charAt(this.readPosition);
                        } while((""+c).trim().isEmpty());
                    }
                }
                if (!parseKey) {
                    if ((commentPrefixIndex = commentPrefixMatchIndex(c, subCount)) > -1 && !this.builder.ignoreInlineComment) {
                        boolean hasInlineComment = false;
                        do {
                            subCount++;
                            if (builder.commentPrefixSizes[commentPrefixIndex] == subCount) {
                                while (readPosition+1 < length &&
                                        builder.string.charAt(readPosition+1) != builder.separator) {
                                    ++readPosition;
                                    inlineComment += builder.string.charAt(readPosition);
                                }
                                ++readPosition;
                                hasInlineComment = true;
                                break;
                            }
                            ++readPosition;
                        } while (builder.string.charAt(readPosition) ==
                                builder.commentPrefixes[commentPrefixIndex].charAt(subCount));
                        if (hasInlineComment) {
                            break;
                        }
                        patchValue += (builder.string.charAt(readPosition));
                        subCount = 0;

                    }
                }
                if (c == this.builder.separator && prevChar != '^' ) {
                    if ((key.length() == 0) && (value.length() == 0)) continue;
                    if (parseKey && !this.builder.errTolerance) {
                        throw new InvalidEntryException("Invalid entry detected in \n<" +
                                this.builder.string + ">\nnear", line, column);
                    }
                    break;
                }
                if (c == this.builder.delimiter && parseKey) {
                    if ((value.length() > 0) && !this.builder.errTolerance) {
                        throw new InvalidEntryException("The input is improperly separated in \n<" +
                                this.builder.string + ">\nnear", line, column);
                    }
                    parseKey = false;
                    continue;
                }
                if (parseKey) {
                    key.append(c);
                } else {
                    value.append(c).append(patchValue);
                    patchValue = "";
                }
                prevPrevChar = (c == '\r' ? prevPrevChar : prevChar);
                prevChar = (c == '\r' ? (prevChar != '\\' ? '\0' : '\\') : c);
            }
            ++readPosition;
        }
        ret[0] = (trimmingKey ? (patchkey+key.toString()).trim() : (patchkey+key.toString()));
        ret[1] = (trimmingValue ? KonfigerUtil.unEscapeString(value.toString(), this.builder.separator).trim() :
                KonfigerUtil.unEscapeString(value.toString(), this.builder.separator));
        ret[2] = entryComment;
        ret[3] = inlineComment;
        ret[4] = (trimmingSection ? section.trim() : section);
        ret[5] = sectionComment;
        entryComment = "";
        return ret;
    }

    public Entry nextEntry() throws IOException, InvalidEntryException {
        String[] entry = next();
        Entry sectionEntry = new Entry();
        sectionEntry.setKey(entry[0]);
        sectionEntry.setValue(entry[1]);
        sectionEntry.setComment(entry[2]);
        sectionEntry.setInlineComment(entry[3]);
        sectionEntry.setSection(entry[4]);
        sectionEntry.setSectionComment(entry[5]);
        return sectionEntry;
    }

    private void doneReading() throws IOException {
        if (this.isFile) {
            in.close();
        }
        this.doneReading_ = true;
        this.hasNext_ = false;
    }

}
