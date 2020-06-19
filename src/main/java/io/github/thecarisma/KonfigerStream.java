package io.github.thecarisma;

import java.io.*;

public class KonfigerStream {

    char delimeter;
    char seperator;
    boolean errTolerance;
    private boolean isFile = false;
    private String strStream = "" ;
    private InputStream in;
    String filePath = "";
    private int readPosition = 0;
    private boolean hasNext_ = false;
    private boolean trimmingKey = true;
    private boolean trimmingValue = true;
    private boolean doneReading_ = false;
    private int i = -1;
    private String commentPrefix = "//";
    private char continuationChar = '\\';
    private String patchkey = "";
    int line = 0;
    int column = 0;

    public KonfigerStream(String rawString) {
        this(rawString, '=', '\n', false);
    }

    public KonfigerStream(File file) throws FileNotFoundException {
        this(file, '=', '\n', false);
    }

    public KonfigerStream(String rawString, char delimeter, char seperator) {
        this(rawString, delimeter, seperator, false);
    }

    public KonfigerStream(File file, char delimeter, char seperator) throws FileNotFoundException {
        this(file, delimeter, seperator, false);
    }

    public KonfigerStream(String rawString, char delimeter, char seperator, boolean errTolerance) {
        this.strStream = rawString;
        this.delimeter = delimeter;
        this.seperator = seperator;
        this.errTolerance = errTolerance;
        this.isFile = false;
    }

    public KonfigerStream(File file, char delimeter, char seperator, boolean errTolerance) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException("The file does not exist " + file.getAbsolutePath());
        }
        this.filePath = file.getAbsolutePath();
        in = new FileInputStream(file);
        this.delimeter = delimeter;
        this.seperator = seperator;
        this.errTolerance = errTolerance;
        this.isFile = true;
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

    public String getCommentPrefix() {
        return commentPrefix;
    }

    public void setCommentPrefix(String commentPrefix) {
        this.commentPrefix = commentPrefix;
    }

    public char getContinuationChar() {
        return continuationChar;
    }

    public void setContinuationChar(char continuationChar) {
        this.continuationChar = continuationChar;
    }

    public boolean hasNext() throws IOException {
        int subCount = 0;
        int commetPrefixSize = commentPrefix.length();
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
                if ((char)i == commentPrefix.charAt(subCount)) {
                    do {
                        patchkey += (char)i;
                        subCount++;
                        if (commetPrefixSize == subCount) {
                            break;
                        }
                    } while ((i = in.read()) != -1 && (char)i == commentPrefix.charAt(subCount));
                    if (patchkey.equals(commentPrefix)) {
                        while ((i = in.read()) != -1 && (char)i != seperator) {}
                        return hasNext();
                    }
                }

                if (!hasNext_) {
                    doneReading();
                }
                return hasNext_;
            } else {
                while (readPosition < strStream.length()) {
                    if (!(""+strStream.charAt(readPosition)).trim().isEmpty()) {
                        if (strStream.charAt(readPosition) == commentPrefix.charAt(subCount)) {
                            while (strStream.charAt(readPosition+subCount) == commentPrefix.charAt(subCount)) {
                                ++subCount;
                                if (commetPrefixSize == subCount) {
                                    break;
                                }
                            }
                            if (commetPrefixSize == subCount) {
                                ++readPosition;
                                while (readPosition < strStream.length() && strStream.charAt(readPosition) != seperator) {
                                    ++readPosition;
                                }
                                ++readPosition;
                                return hasNext();
                            }
                        }
                        hasNext_ = true;
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

    public String[] next() throws InvalidEntryException, IOException {
        String[] ret = new String[2];
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        boolean parseKey = true;
        boolean isMultiline = false;
        char prevChar = '\0';
        char prevPrevChar = '\0';
        line |= 1;

        if (this.isFile) {
            do {
                char c = (char)i;
                ++column;
                if (c == '\n') {
                    ++line;
                    column = 0;
                    if (!parseKey && prevChar == this.continuationChar && prevPrevChar != '\\') {
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
                if (c == this.seperator && prevChar != '/') {
                    if ((key.length() == 0) && (value.length() == 0)) continue;
                    if (parseKey && !this.errTolerance) {
                        throw new InvalidEntryException("Invalid entry detected near", line, column);
                    }
                    break;
                }
                if (c == this.delimeter && parseKey) {
                    if ((value.length() > 0) && !this.errTolerance) {
                        throw new InvalidEntryException("The input is improperly separated near", line, column);
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
                }
                prevPrevChar = (c == '\r' ? (prevPrevChar == '\\' ? '\0' : prevPrevChar) : prevChar);
                prevChar = (c == '\r' ? (prevChar != '\\' ? '\0' : '\\') : c);
            } while ((i = in.read()) != -1);
        } else {
            for (;this.readPosition <= this.strStream.length(); ++this.readPosition) {
                if (this.readPosition == this.strStream.length()) {
                    if (key.length() > 0) {
                        if (parseKey && !this.errTolerance) {
                            throw new InvalidEntryException("Invalid entry detected near", line, column);
                        }
                    }
                    this.doneReading();
                    break;
                }
                char c = this.strStream.charAt(this.readPosition);
                ++column;
                if (c == '\n') {
                    ++line;
                    column = 0;
                    if (!parseKey && prevChar == this.continuationChar && prevPrevChar != '\\') {
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
                            c = this.strStream.charAt(this.readPosition);
                        } while((""+c).trim().isEmpty());
                    }
                }
                if (c == this.seperator && prevChar != '/') {
                    if ((key.length() == 0) && (value.length() == 0)) continue;
                    if (parseKey && !this.errTolerance) {
                        throw new InvalidEntryException("Invalid entry detected near", line, column);
                    }
                    break;
                }
                if (c == this.delimeter && parseKey) {
                    if ((value.length() > 0) && !this.errTolerance) {
                        throw new InvalidEntryException("The input is improperly separated near", line, column);
                    }
                    parseKey = false;
                    continue;
                }
                if (parseKey) {
                    key.append(c);
                } else {
                    value.append(c);
                }
                // whatever is happening here, well
                prevPrevChar = prevChar;
                prevChar = (c == '\r' ? (prevChar != '\\' ? '\0' : '\\') : c);
            }
            ++readPosition;
        }
        ret[0] = (trimmingKey ? (patchkey+key.toString()).trim() : (patchkey+key.toString()));
        ret[1] = (trimmingValue ? KonfigerUtil.unEscapeString(value.toString(), this.seperator).trim() : KonfigerUtil.unEscapeString(value.toString(), this.seperator));
        return ret;
    }

    private void doneReading() throws IOException {
        if (this.isFile) {
            in.close();
        }
        this.doneReading_ = true;
        this.hasNext_ = false;
    }

}
