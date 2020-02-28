package io.github.thecarisma;

import java.io.File;
import java.io.FileNotFoundException;

public class KonfigerStream {

    private char delimeter;
    private char seperator;
    private boolean errTolerance;
    private boolean isFile = false;
    private String strStream = "" ;
    private File file;
    private int readPosition = 0;
    private boolean hasNext_ = false;
    private boolean doneReading_ = false;

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
        this.file = file;
        this.delimeter = delimeter;
        this.seperator = seperator;
        this.errTolerance = errTolerance;
        this.isFile = true;
    }

    public boolean hasNext() {
        if (!this.doneReading_) {
            if (this.isFile) {

            } else {
                while (this.readPosition < this.strStream.length()) {
                    if (!(""+this.strStream.charAt(this.readPosition)).trim().isEmpty()) {
                        this.hasNext_ = true;
                        return this.hasNext_;
                    }
                    ++this.readPosition;
                }
                this.hasNext_ = false;
                return this.hasNext_;
            }
        }
        return this.hasNext_;
    }

    public String[] next() throws InvalidEntryException {
        String[] ret = new String[2];
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        boolean parseKey = true;
        char prevChar = '\0';
        int line = 1;
        int column = 0;

        if (this.isFile) {

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
                char char_ = this.strStream.charAt(this.readPosition);
                ++column;
                if (char_ == '\n') {
                    ++line;
                    column = 0;
                }
                if (char_ == this.seperator && this.strStream.charAt(this.readPosition-1) != '\\') {
                    if ((key.length() == 0) && (value.length() == 0)) continue;
                    if (parseKey && !this.errTolerance) {
                        throw new InvalidEntryException("Invalid entry detected near", line, column);
                    }
                    break;
                }
                if (char_ == this.delimeter && parseKey) {
                    if ((value.length() > 0) && !this.errTolerance) {
                        throw new InvalidEntryException("The input is imporperly sepreated near", line, column);
                    }
                    parseKey = false;
                    continue;
                }
                if (parseKey) {
                    key.append(char_);
                } else {
                    value.append(char_);
                }
            }
        }
        ret[0] = key.toString();
        ret[1] = value.toString(); //escape it
        return ret;
    }

    private void doneReading() {
        this.doneReading_ = true;
        this.hasNext_ = false;
    }

}
