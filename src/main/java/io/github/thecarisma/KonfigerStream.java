package io.github.thecarisma;

import java.io.*;

public class KonfigerStream {

    private char delimeter;
    private char seperator;
    boolean errTolerance;
    private boolean isFile = false;
    private String strStream = "" ;
    private InputStream in;
    String filePath = "";
    private int readPosition = 0;
    private boolean hasNext_ = false;
    private boolean doneReading_ = false;
    private boolean escapingEntry = true;
    private int i = -1;

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

    public boolean isEscapingEntry() {
        return escapingEntry;
    }

    public void setEscapingEntry(boolean escapingEntry) {
        this.escapingEntry = escapingEntry;
    }

    public boolean hasNext() throws IOException {
        if (!this.doneReading_) {
            if (this.isFile) {
                this.hasNext_ = ((i = in.read()) != -1);
                if (!this.hasNext_) {
                    this.doneReading();
                }
                return this.hasNext_;
            } else {
                while (this.readPosition < this.strStream.length()) {
                    if (!(""+this.strStream.charAt(this.readPosition)).trim().isEmpty()) {
                        this.hasNext_ = true;
                        return true;
                    }
                    ++this.readPosition;
                }
                this.hasNext_ = false;
                return false;
            }
        }
        return this.hasNext_;
    }

    public String[] next() throws InvalidEntryException, IOException {
        String[] ret = new String[2];
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        boolean parseKey = true;
        char prevChar = '\0';
        int line = 1;
        int column = 0;

        if (this.isFile) {
            do {
                char c = (char)i;
                ++column;
                if (c == '\n') {
                    ++line;
                    column = 0;
                }
                if (c == this.seperator /*&& this.strStream.charAt(this.readPosition-1) != '\\'*/) {
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
                }
                if (c == this.seperator && this.strStream.charAt(this.readPosition-1) != '\\') {
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
            }
        }
        ret[0] = key.toString();
        ret[1] = (escapingEntry ? KonfigerUtil.unEscapeString(value.toString(), this.seperator) : value.toString());
        return ret;
    }

    private void doneReading() {
        this.doneReading_ = true;
        this.hasNext_ = false;
    }

}
