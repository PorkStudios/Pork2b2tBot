/*
 * Decompiled with CFR 0_132.
 */
package org.yaml.snakeyaml.reader;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.reader.ReaderException;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.scanner.Constant;

public class StreamReader {
    private String name;
    private final Reader stream;
    private int pointer = 0;
    private boolean eof = true;
    private String buffer;
    private int index = 0;
    private int line = 0;
    private int column = 0;
    private char[] data;
    private static final int BUFFER_SIZE = 1025;

    public StreamReader(String stream) {
        this.name = "'string'";
        this.buffer = "";
        this.checkPrintable(stream);
        this.buffer = stream + "\u0000";
        this.stream = null;
        this.eof = true;
        this.data = null;
    }

    public StreamReader(Reader reader) {
        this.name = "'reader'";
        this.buffer = "";
        this.stream = reader;
        this.eof = false;
        this.data = new char[1025];
        this.update();
    }

    void checkPrintable(String data) {
        int codePoint;
        int length = data.length();
        for (int offset = 0; offset < length; offset += Character.charCount((int)codePoint)) {
            codePoint = data.codePointAt(offset);
            if (StreamReader.isPrintable(codePoint)) continue;
            throw new ReaderException(this.name, offset, codePoint, "special characters are not allowed");
        }
    }

    public static boolean isPrintable(String data) {
        int codePoint;
        int length = data.length();
        for (int offset = 0; offset < length; offset += Character.charCount((int)codePoint)) {
            codePoint = data.codePointAt(offset);
            if (StreamReader.isPrintable(codePoint)) continue;
            return false;
        }
        return true;
    }

    public static boolean isPrintable(int c) {
        return c >= 32 && c <= 126 || c == 9 || c == 10 || c == 13 || c == 133 || c >= 160 && c <= 55295 || c >= 57344 && c <= 65533 || c >= 65536 && c <= 1114111;
    }

    public Mark getMark() {
        return new Mark(this.name, this.index, this.line, this.column, this.buffer, this.pointer);
    }

    public void forward() {
        this.forward(1);
    }

    public void forward(int length) {
        for (int i = 0; i < length; ++i) {
            if (this.pointer == this.buffer.length()) {
                this.update();
            }
            if (this.pointer == this.buffer.length()) break;
            int c = this.buffer.codePointAt(this.pointer);
            this.pointer += Character.charCount(c);
            this.index += Character.charCount(c);
            if (Constant.LINEBR.has(c) || c == 13 && this.buffer.charAt(this.pointer) != '\n') {
                ++this.line;
                this.column = 0;
                continue;
            }
            if (c == 65279) continue;
            ++this.column;
        }
        if (this.pointer == this.buffer.length()) {
            this.update();
        }
    }

    public int peek() {
        if (this.pointer == this.buffer.length()) {
            this.update();
        }
        if (this.pointer == this.buffer.length()) {
            return -1;
        }
        return this.buffer.codePointAt(this.pointer);
    }

    public int peek(int index) {
        int codePoint;
        int offset = 0;
        int nextIndex = 0;
        do {
            if (this.pointer + offset == this.buffer.length()) {
                this.update();
            }
            if (this.pointer + offset == this.buffer.length()) {
                return -1;
            }
            codePoint = this.buffer.codePointAt(this.pointer + offset);
            offset += Character.charCount(codePoint);
        } while (++nextIndex <= index);
        return codePoint;
    }

    public String prefix(int length) {
        StringBuilder builder = new StringBuilder();
        int offset = 0;
        for (int resultLength = 0; resultLength < length; ++resultLength) {
            if (this.pointer + offset == this.buffer.length()) {
                this.update();
            }
            if (this.pointer + offset == this.buffer.length()) break;
            int c = this.buffer.codePointAt(this.pointer + offset);
            builder.appendCodePoint(c);
            offset += Character.charCount(c);
        }
        return builder.toString();
    }

    public String prefixForward(int length) {
        String prefix = this.prefix(length);
        this.pointer += prefix.length();
        this.index += prefix.length();
        this.column += length;
        return prefix;
    }

    private void update() {
        if (!this.eof) {
            this.buffer = this.buffer.substring(this.pointer);
            this.pointer = 0;
            try {
                boolean eofDetected = false;
                int converted = this.stream.read(this.data, 0, 1024);
                if (converted > 0) {
                    if (Character.isHighSurrogate(this.data[converted - 1])) {
                        int oneMore = this.stream.read(this.data, converted, 1);
                        if (oneMore != -1) {
                            converted += oneMore;
                        } else {
                            eofDetected = true;
                        }
                    }
                    StringBuilder builder = new StringBuilder(this.buffer.length() + converted).append(this.buffer).append(this.data, 0, converted);
                    if (eofDetected) {
                        this.eof = true;
                        builder.append('\u0000');
                    }
                    this.buffer = builder.toString();
                    this.checkPrintable(this.buffer);
                } else {
                    this.eof = true;
                    this.buffer = this.buffer + "\u0000";
                }
            }
            catch (IOException ioe) {
                throw new YAMLException(ioe);
            }
        }
    }

    public int getColumn() {
        return this.column;
    }

    public Charset getEncoding() {
        return Charset.forName(((UnicodeReader)this.stream).getEncoding());
    }

    public int getIndex() {
        return this.index;
    }

    public int getLine() {
        return this.line;
    }
}

