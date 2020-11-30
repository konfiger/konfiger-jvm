package io.github.thecarisma;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 */
public class KonfigerStream {

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
    private int sectionMatcher = 0;
    String filePath = "";
    private String patchkey = "";
    private List<Entry.Comment> sectionComments = new ArrayList<>();
    private List<Entry.Comment> entryComments = new ArrayList<>();
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
                .withDelimiters(new char[]{delimiter})
                .withSeparators(new char[]{separator});
        this.builder.errTolerance = errTolerance;
    }

    public KonfigerStream(File file, char delimiter, char separator, boolean errTolerance) {
        this(builder()
                .withFile(file)
                .withDelimiters(new char[]{delimiter})
                .withSeparators(new char[]{separator}));
        this.builder.errTolerance = errTolerance;
    }

    public KonfigerStream(Builder builder) {
        this.builder = builder;
        if (this.builder.filePath != null) {
            this.isFile = true;
            this.filePath = this.builder.filePath;
            try {
                in = new FileInputStream(new File(this.filePath));
            } catch (java.io.FileNotFoundException e) {
                throw new FileNotFoundException(e);
            }
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

    int multilineCommentPrefixMatchIndex(char c, int subCount) {
        for (int index = 0; index < builder.multilineCommentPrefixes.length; ++index) {
            if (builder.multilineCommentPrefixes[index].charAt(subCount) == c) {
                return index;
            }
        }
        return -1;
    }

    boolean matchesAnyDelimiter(char[] delimiters, char c) {
        for (char delimiter : delimiters) {
            if (delimiter == c) {
                return true;
            }
        }
        return false;
    }

    boolean matchesAnySeparator(char[] separators, char c) {
        for (char separator : separators) {
            if (separator == c) {
                return true;
            }
        }
        return false;
    }

    public boolean hasNext() {
        int subCount = 0;
        int commentPrefixIndex = -1;
        String comment = "", patchComment = "";
        boolean terminateMultilineComment = false;
        patchkey = "";
        try {
            if (!doneReading_) {
                if (isFile) {
                    hasNext_ = ((i = in.read()) != -1);
                    while (("" + (char) i).trim().isEmpty()) {
                        hasNext_ = ((i = in.read()) != -1);
                        if (i == '\n') {
                            ++line;
                            column = 0;
                        }
                    }
                    if ((commentPrefixIndex = multilineCommentPrefixMatchIndex((char) i, subCount)) > -1) {
                        do {
                            patchkey += (char) i;
                            subCount++;
                            if (builder.multilineCommentPrefixesSizes[commentPrefixIndex] == subCount) {
                                break;
                            }
                        } while ((i = in.read()) != -1 &&
                                ((char) i == builder.multilineCommentPrefixes[commentPrefixIndex].charAt(subCount - 1)));
                        if (patchkey.equals(builder.multilineCommentPrefixes[commentPrefixIndex])) {
                            while ((i = in.read()) != -1) {
                                if ((commentPrefixIndex = multilineCommentPrefixMatchIndex((char) i, 0)) < 0) {
                                    comment += (char) i;
                                } else {
                                    subCount = 0;
                                    patchComment = "";
                                    do {
                                        patchComment += (char) i;
                                        subCount++;
                                        if (builder.multilineCommentPrefixesSizes[commentPrefixIndex] == subCount) {
                                            if (patchComment.equals(builder.multilineCommentPrefixes[commentPrefixIndex])) {
                                                terminateMultilineComment = true;
                                            }
                                            break;
                                        }
                                    } while ((i = in.read()) != -1);
                                    if (terminateMultilineComment) {
                                        break;
                                    } else {
                                        comment += patchComment;
                                    }
                                }
                            }
                            Entry.Comment comment1 = new Entry.Comment();
                            comment1.setCommentKeyword(builder.multilineCommentPrefixes[commentPrefixIndex]);
                            comment1.setValue(comment);
                            comment1.setMultiline(true);
                            entryComments.add(comment1);
                            return hasNext();
                        }
                    }
                    subCount = 0;
                    if ((commentPrefixIndex = commentPrefixMatchIndex((char) i, subCount)) > -1) {
                        do {
                            patchkey += (char) i;
                            subCount++;
                            if (builder.commentPrefixSizes[commentPrefixIndex] == subCount) {
                                break;
                            }
                        } while ((i = in.read()) != -1 &&
                                ((char) i == builder.commentPrefixes[commentPrefixIndex].charAt(subCount - 1)));
                        if (patchkey.equals(builder.commentPrefixes[commentPrefixIndex])) {
                            while ((i = in.read()) != -1 && !matchesAnySeparator(builder.separators, (char) i)) {
                                comment += (char) i;
                            }
                            Entry.Comment comment1 = new Entry.Comment();
                            comment1.setCommentKeyword(builder.commentPrefixes[commentPrefixIndex]);
                            comment1.setValue(comment);
                            entryComments.add(comment1);
                            return hasNext();
                        }
                    }

                    if (!hasNext_) {
                        doneReading();
                    }

                    if (hasNext_) {
                        if (patchkey.isEmpty() && (char) i == builder.beginSectionChar) {
                            ++sectionMatcher;
                            String internalSection = "";
                            boolean hasSubSection = false;
                            while ((i = in.read()) != -1) {
                                if (i == builder.beginSectionChar) {
                                    ++sectionMatcher;
                                    hasSubSection = true;
                                    continue;
                                } else if (i == builder.endSectionChar) {
                                    --sectionMatcher;
                                    if (sectionMatcher == 0) {
                                        hasNext_ = ((i = in.read()) != -1);
                                        sectionComments = entryComments;
                                        entryComments = new ArrayList<>();
                                        if (hasSubSection) {
                                            section += builder.subSectionDelimiter + internalSection;
                                        } else {
                                            section = internalSection;
                                        }
                                        return hasNext();
                                    }
                                    continue;
                                }
                                internalSection += (char) i;
                            }
                        }
                    }
                    if (sectionMatcher > 0) {
                        throw new NestedSectionException("There is mismatch in the start and end delimiter of the section" +
                                " or subsection in file '" + builder.filePath + "'");
                    }
                    return hasNext_;
                } else {
                    long length = builder.string.length();
                    while (readPosition < length) {
                        if (!("" + builder.string.charAt(readPosition)).trim().isEmpty()) {
                            if ((commentPrefixIndex = multilineCommentPrefixMatchIndex(builder.string.charAt(readPosition),
                                    subCount)) > -1) {
                                while (builder.string.charAt(readPosition) ==
                                        builder.multilineCommentPrefixes[commentPrefixIndex].charAt(subCount)) {
                                    patchkey += builder.string.charAt(readPosition);
                                    ++subCount;
                                    ++readPosition;
                                    if (builder.multilineCommentPrefixesSizes[commentPrefixIndex] == subCount) {
                                        break;
                                    }
                                }
                                if (patchkey.equals(builder.multilineCommentPrefixes[commentPrefixIndex])) {
                                    while (readPosition < length) {
                                        if ((commentPrefixIndex = multilineCommentPrefixMatchIndex(
                                                builder.string.charAt(readPosition),
                                                0)) < 0) {
                                            comment += builder.string.charAt(readPosition);
                                        } else {
                                            subCount = 0;
                                            patchComment = "";
                                            do {
                                                patchComment += builder.string.charAt(readPosition);
                                                subCount++;
                                                if (builder.multilineCommentPrefixesSizes[commentPrefixIndex] == subCount) {
                                                    if (patchComment.equals(builder.multilineCommentPrefixes[commentPrefixIndex])) {
                                                        terminateMultilineComment = true;
                                                    }
                                                    break;
                                                }
                                            } while ((++readPosition) < length);
                                            if (terminateMultilineComment) {
                                                break;
                                            } else {
                                                comment += patchComment;
                                            }
                                        }
                                        ++readPosition;
                                    }
                                    ++readPosition;
                                    Entry.Comment comment1 = new Entry.Comment();
                                    comment1.setCommentKeyword(builder.multilineCommentPrefixes[commentPrefixIndex]);
                                    comment1.setValue(comment);
                                    comment1.setMultiline(true);
                                    entryComments.add(comment1);
                                    return hasNext();
                                }
                            }
                            subCount = 0;
                            if ((commentPrefixIndex = commentPrefixMatchIndex(builder.string.charAt(readPosition),
                                    subCount)) > -1) {
                                while (builder.string.charAt(readPosition + subCount) ==
                                        builder.commentPrefixes[commentPrefixIndex].charAt(subCount)) {
                                    ++subCount;
                                    if (builder.commentPrefixSizes[commentPrefixIndex] == subCount) {
                                        break;
                                    }
                                }
                                if (builder.commentPrefixSizes[commentPrefixIndex] == subCount) {
                                    ++readPosition;
                                    while (readPosition < length &&
                                            !matchesAnySeparator(builder.separators, builder.string.charAt(readPosition))) {
                                        comment += builder.string.charAt(readPosition);
                                        ++readPosition;
                                    }
                                    ++readPosition;
                                    Entry.Comment comment1 = new Entry.Comment();
                                    comment1.setCommentKeyword(builder.commentPrefixes[commentPrefixIndex]);
                                    comment1.setValue(comment);
                                    entryComments.add(comment1);
                                    return hasNext();
                                }
                            }

                            hasNext_ = true;
                            if (builder.string.charAt(readPosition) == builder.beginSectionChar) {
                                ++sectionMatcher;
                                ++readPosition;
                                String internalSection = "";
                                boolean hasSubSection = false;
                                do {
                                    if (builder.string.charAt(readPosition) == builder.beginSectionChar) {
                                        ++sectionMatcher;
                                        ++readPosition;
                                        hasSubSection = true;
                                        continue;
                                    } else if (builder.string.charAt(readPosition) == builder.endSectionChar) {
                                        --sectionMatcher;
                                        ++readPosition;
                                        if (sectionMatcher == 0) {
                                            sectionComments = entryComments;
                                            entryComments = new ArrayList<>();
                                            if (hasSubSection) {
                                                section += builder.subSectionDelimiter + internalSection;
                                            } else {
                                                section = internalSection;
                                            }
                                            return hasNext();
                                        }
                                        continue;
                                    }
                                    internalSection += builder.string.charAt(readPosition);
                                    ++readPosition;
                                } while (readPosition < length);
                            }
                            if (sectionMatcher > 0) {
                                throw new NestedSectionException("There is mismatch in the start and end delimiter of the section" +
                                        " or subsection in \n<" + this.builder.string + ">");
                            }
                            return true;
                        }
                        ++readPosition;
                    }
                    hasNext_ = false;
                    return false;
                }
            }
        } catch (IOException ex) {
            throw new InvalidFileException(ex);
        }
        return hasNext_;
    }

    public SectionEntry next() {
        SectionEntry entry = new SectionEntry();
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
            try {
                do {
                    char c = (char) i;
                    ++column;
                    if (c == '\n') {
                        ++line;
                        column = 0;
                        if (!parseKey && prevChar == this.builder.continuationChar && prevPrevChar != '\\') {
                            String tmpValue = value.toString();
                            value = new StringBuilder();
                            if (tmpValue.charAt(tmpValue.length() - 1) == '\r') {
                                tmpValue = tmpValue.substring(0, tmpValue.length() - 2);
                            } else {
                                tmpValue = tmpValue.substring(0, tmpValue.length() - 1);
                            }
                            value.append(tmpValue);
                            do {
                                c = (char) i;
                            } while ((i = in.read()) != -1 && ("" + c).trim().isEmpty());
                            isMultiline = true;
                        }
                    }
                    if (!parseKey) {
                        if ((commentPrefixIndex = commentPrefixMatchIndex(c, subCount)) > -1 && !this.builder.ignoreInlineComment) {
                            boolean hasInlineComment = false;
                            do {
                                subCount++;
                                if (builder.commentPrefixSizes[commentPrefixIndex] == subCount) {
                                    while ((i = in.read()) != -1 && !matchesAnySeparator(builder.separators, (char) i)) {
                                        inlineComment += (char) i;
                                    }
                                    hasInlineComment = true;
                                    break;
                                }
                            } while ((i = in.read()) != -1 &&
                                    ((char) i == builder.commentPrefixes[commentPrefixIndex].charAt(subCount)));
                            if (hasInlineComment) {
                                Entry.Comment comment = new Entry.Comment();
                                comment.setCommentKeyword(builder.commentPrefixes[commentPrefixIndex]);
                                comment.setValue(inlineComment);
                                entry.getInlineComments().add(comment);
                                break;
                            }
                            patchValue += ((char) i);
                            subCount = 0;

                        }
                    }
                    if (matchesAnySeparator(this.builder.separators, c) && prevChar != '^') {
                        if ((key.length() == 0) && (value.length() == 0)) continue;
                        break;
                    }
                    if (matchesAnyDelimiter(this.builder.delimiters, c) && parseKey) {
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
                            value.append((char) i);
                            isMultiline = false;
                        }
                        value.append(patchValue);
                        patchValue = "";
                    }
                    prevPrevChar = (c == '\r' ? prevPrevChar : prevChar);
                    prevChar = (c == '\r' ? (prevChar != '\\' ? '\0' : '\\') : c);
                } while ((i = in.read()) != -1);
            } catch (IOException ex) {
                throw new InvalidFileException(ex);
            }
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
                                        !matchesAnySeparator(builder.separators, builder.string.charAt(readPosition+1))) {
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
                            Entry.Comment comment = new Entry.Comment();
                            comment.setCommentKeyword(builder.commentPrefixes[commentPrefixIndex]);
                            comment.setValue(inlineComment);
                            entry.getInlineComments().add(comment);
                            break;
                        }
                        patchValue += (builder.string.charAt(readPosition));
                        subCount = 0;

                    }
                }
                if (matchesAnySeparator(this.builder.separators, c) && prevChar != '^' ) {
                    if ((key.length() == 0) && (value.length() == 0)) continue;
                    if (parseKey && !this.builder.errTolerance) {
                        throw new InvalidEntryException("Invalid entry detected in \n<" +
                                this.builder.string + ">\nnear", line, column);
                    }
                    break;
                }
                if (matchesAnyDelimiter(this.builder.delimiters, c) && parseKey) {
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
        entry.setKey((trimmingKey ? (patchkey+key.toString()).trim() : (patchkey+key.toString())));
        if (!value.toString().isEmpty() || !parseKey) {
            // TODO send list of separator also
            entry.addValue((trimmingValue ? KonfigerUtil.unEscapeString(value.toString(), this.builder.separators[0]).trim() :
                    KonfigerUtil.unEscapeString(value.toString(), this.builder.separators[0])));
        }
        entry.setComments(entryComments);
        if (!section.isEmpty()) {
            entry.setSection((trimmingSection ? section.trim() : section));
        }
        entry.setSectionComment(sectionComments);
        entryComments = new ArrayList<>();
        return entry;
    }

    public SectionEntry nextEntry() {
        return next();
    }

    private void doneReading() {
        if (this.isFile) {
            try {
                in.close();
            } catch (IOException ex) {
                throw new InvalidFileException(ex);
            }
        }
        this.doneReading_ = true;
        this.hasNext_ = false;
    }

}
