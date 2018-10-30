/*
 * Decompiled with CFR 0_132.
 */
package com.google.gson.stream;

import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;

public class JsonReader
implements Closeable {
    private static final char[] NON_EXECUTE_PREFIX = ")]}'\n".toCharArray();
    private static final long MIN_INCOMPLETE_INTEGER = -922337203685477580L;
    private static final int PEEKED_NONE = 0;
    private static final int PEEKED_BEGIN_OBJECT = 1;
    private static final int PEEKED_END_OBJECT = 2;
    private static final int PEEKED_BEGIN_ARRAY = 3;
    private static final int PEEKED_END_ARRAY = 4;
    private static final int PEEKED_TRUE = 5;
    private static final int PEEKED_FALSE = 6;
    private static final int PEEKED_NULL = 7;
    private static final int PEEKED_SINGLE_QUOTED = 8;
    private static final int PEEKED_DOUBLE_QUOTED = 9;
    private static final int PEEKED_UNQUOTED = 10;
    private static final int PEEKED_BUFFERED = 11;
    private static final int PEEKED_SINGLE_QUOTED_NAME = 12;
    private static final int PEEKED_DOUBLE_QUOTED_NAME = 13;
    private static final int PEEKED_UNQUOTED_NAME = 14;
    private static final int PEEKED_LONG = 15;
    private static final int PEEKED_NUMBER = 16;
    private static final int PEEKED_EOF = 17;
    private static final int NUMBER_CHAR_NONE = 0;
    private static final int NUMBER_CHAR_SIGN = 1;
    private static final int NUMBER_CHAR_DIGIT = 2;
    private static final int NUMBER_CHAR_DECIMAL = 3;
    private static final int NUMBER_CHAR_FRACTION_DIGIT = 4;
    private static final int NUMBER_CHAR_EXP_E = 5;
    private static final int NUMBER_CHAR_EXP_SIGN = 6;
    private static final int NUMBER_CHAR_EXP_DIGIT = 7;
    private final Reader in;
    private boolean lenient = false;
    private final char[] buffer = new char[1024];
    private int pos = 0;
    private int limit = 0;
    private int lineNumber = 0;
    private int lineStart = 0;
    private int peeked = 0;
    private long peekedLong;
    private int peekedNumberLength;
    private String peekedString;
    private int[] stack = new int[32];
    private int stackSize = 0;
    private String[] pathNames;
    private int[] pathIndices;

    public JsonReader(Reader in) {
        this.stack[this.stackSize++] = 6;
        this.pathNames = new String[32];
        this.pathIndices = new int[32];
        if (in == null) {
            throw new NullPointerException("in == null");
        }
        this.in = in;
    }

    public final void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    public final boolean isLenient() {
        return this.lenient;
    }

    public void beginArray() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = this.doPeek();
        }
        if (p != 3) {
            throw new IllegalStateException("Expected BEGIN_ARRAY but was " + (Object)((Object)this.peek()) + " at line " + this.getLineNumber() + " column " + this.getColumnNumber() + " path " + this.getPath());
        }
        this.push(1);
        this.pathIndices[this.stackSize - 1] = 0;
        this.peeked = 0;
    }

    public void endArray() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = this.doPeek();
        }
        if (p == 4) {
            --this.stackSize;
        } else {
            throw new IllegalStateException("Expected END_ARRAY but was " + (Object)((Object)this.peek()) + " at line " + this.getLineNumber() + " column " + this.getColumnNumber() + " path " + this.getPath());
        }
        int[] arrn = this.pathIndices;
        int n = this.stackSize - 1;
        arrn[n] = arrn[n] + 1;
        this.peeked = 0;
    }

    public void beginObject() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = this.doPeek();
        }
        if (p != 1) {
            throw new IllegalStateException("Expected BEGIN_OBJECT but was " + (Object)((Object)this.peek()) + " at line " + this.getLineNumber() + " column " + this.getColumnNumber() + " path " + this.getPath());
        }
        this.push(3);
        this.peeked = 0;
    }

    public void endObject() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = this.doPeek();
        }
        if (p == 2) {
            --this.stackSize;
        } else {
            throw new IllegalStateException("Expected END_OBJECT but was " + (Object)((Object)this.peek()) + " at line " + this.getLineNumber() + " column " + this.getColumnNumber() + " path " + this.getPath());
        }
        this.pathNames[this.stackSize] = null;
        int[] arrn = this.pathIndices;
        int n = this.stackSize - 1;
        arrn[n] = arrn[n] + 1;
        this.peeked = 0;
    }

    public boolean hasNext() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = this.doPeek();
        }
        return p != 2 && p != 4;
    }

    public JsonToken peek() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = this.doPeek();
        }
        switch (p) {
            case 1: {
                return JsonToken.BEGIN_OBJECT;
            }
            case 2: {
                return JsonToken.END_OBJECT;
            }
            case 3: {
                return JsonToken.BEGIN_ARRAY;
            }
            case 4: {
                return JsonToken.END_ARRAY;
            }
            case 12: 
            case 13: 
            case 14: {
                return JsonToken.NAME;
            }
            case 5: 
            case 6: {
                return JsonToken.BOOLEAN;
            }
            case 7: {
                return JsonToken.NULL;
            }
            case 8: 
            case 9: 
            case 10: 
            case 11: {
                return JsonToken.STRING;
            }
            case 15: 
            case 16: {
                return JsonToken.NUMBER;
            }
            case 17: {
                return JsonToken.END_DOCUMENT;
            }
        }
        throw new AssertionError();
    }

    private int doPeek() throws IOException {
        int c;
        int result;
        int peekStack;
        block47 : {
            block49 : {
                block48 : {
                    block46 : {
                        peekStack = this.stack[this.stackSize - 1];
                        if (peekStack != 1) break block46;
                        this.stack[this.stackSize - 1] = 2;
                        break block47;
                    }
                    if (peekStack != 2) break block48;
                    c = this.nextNonWhitespace(true);
                    switch (c) {
                        case 93: {
                            this.peeked = 4;
                            return 4;
                        }
                        case 59: {
                            this.checkLenient();
                        }
                        case 44: {
                            break;
                        }
                        default: {
                            throw this.syntaxError("Unterminated array");
                        }
                    }
                    break block47;
                }
                if (peekStack == 3 || peekStack == 5) {
                    int c2;
                    this.stack[this.stackSize - 1] = 4;
                    if (peekStack == 5) {
                        c2 = this.nextNonWhitespace(true);
                        switch (c2) {
                            case 125: {
                                this.peeked = 2;
                                return 2;
                            }
                            case 59: {
                                this.checkLenient();
                            }
                            case 44: {
                                break;
                            }
                            default: {
                                throw this.syntaxError("Unterminated object");
                            }
                        }
                    }
                    c2 = this.nextNonWhitespace(true);
                    switch (c2) {
                        case 34: {
                            this.peeked = 13;
                            return 13;
                        }
                        case 39: {
                            this.checkLenient();
                            this.peeked = 12;
                            return 12;
                        }
                        case 125: {
                            if (peekStack != 5) {
                                this.peeked = 2;
                                return 2;
                            }
                            throw this.syntaxError("Expected name");
                        }
                    }
                    this.checkLenient();
                    --this.pos;
                    if (this.isLiteral((char)c2)) {
                        this.peeked = 14;
                        return 14;
                    }
                    throw this.syntaxError("Expected name");
                }
                if (peekStack != 4) break block49;
                this.stack[this.stackSize - 1] = 5;
                c = this.nextNonWhitespace(true);
                switch (c) {
                    case 58: {
                        break;
                    }
                    case 61: {
                        this.checkLenient();
                        if ((this.pos < this.limit || this.fillBuffer(1)) && this.buffer[this.pos] == '>') {
                            ++this.pos;
                            break;
                        }
                        break block47;
                    }
                    default: {
                        throw this.syntaxError("Expected ':'");
                    }
                }
                break block47;
            }
            if (peekStack == 6) {
                if (this.lenient) {
                    this.consumeNonExecutePrefix();
                }
                this.stack[this.stackSize - 1] = 7;
            } else if (peekStack == 7) {
                c = this.nextNonWhitespace(false);
                if (c == -1) {
                    this.peeked = 17;
                    return 17;
                }
                this.checkLenient();
                --this.pos;
            } else if (peekStack == 8) {
                throw new IllegalStateException("JsonReader is closed");
            }
        }
        c = this.nextNonWhitespace(true);
        switch (c) {
            case 93: {
                if (peekStack == 1) {
                    this.peeked = 4;
                    return 4;
                }
            }
            case 44: 
            case 59: {
                if (peekStack == 1 || peekStack == 2) {
                    this.checkLenient();
                    --this.pos;
                    this.peeked = 7;
                    return 7;
                }
                throw this.syntaxError("Unexpected value");
            }
            case 39: {
                this.checkLenient();
                this.peeked = 8;
                return 8;
            }
            case 34: {
                if (this.stackSize == 1) {
                    this.checkLenient();
                }
                this.peeked = 9;
                return 9;
            }
            case 91: {
                this.peeked = 3;
                return 3;
            }
            case 123: {
                this.peeked = 1;
                return 1;
            }
        }
        --this.pos;
        if (this.stackSize == 1) {
            this.checkLenient();
        }
        if ((result = this.peekKeyword()) != 0) {
            return result;
        }
        result = this.peekNumber();
        if (result != 0) {
            return result;
        }
        if (!this.isLiteral(this.buffer[this.pos])) {
            throw this.syntaxError("Expected value");
        }
        this.checkLenient();
        this.peeked = 10;
        return 10;
    }

    private int peekKeyword() throws IOException {
        int peeking;
        String keywordUpper;
        String keyword;
        char c = this.buffer[this.pos];
        if (c == 't' || c == 'T') {
            keyword = "true";
            keywordUpper = "TRUE";
            peeking = 5;
        } else if (c == 'f' || c == 'F') {
            keyword = "false";
            keywordUpper = "FALSE";
            peeking = 6;
        } else if (c == 'n' || c == 'N') {
            keyword = "null";
            keywordUpper = "NULL";
            peeking = 7;
        } else {
            return 0;
        }
        int length = keyword.length();
        for (int i = 1; i < length; ++i) {
            if (this.pos + i >= this.limit && !this.fillBuffer(i + 1)) {
                return 0;
            }
            c = this.buffer[this.pos + i];
            if (c == keyword.charAt(i) || c == keywordUpper.charAt(i)) continue;
            return 0;
        }
        if ((this.pos + length < this.limit || this.fillBuffer(length + 1)) && this.isLiteral(this.buffer[this.pos + length])) {
            return 0;
        }
        this.pos += length;
        this.peeked = peeking;
        return this.peeked;
    }

    private int peekNumber() throws IOException {
        char[] buffer = this.buffer;
        int p = this.pos;
        int l = this.limit;
        long value = 0L;
        boolean negative = false;
        boolean fitsInLong = true;
        int last = 0;
        int i = 0;
        block6 : do {
            if (p + i == l) {
                if (i == buffer.length) {
                    return 0;
                }
                if (!this.fillBuffer(i + 1)) break;
                p = this.pos;
                l = this.limit;
            }
            char c = buffer[p + i];
            switch (c) {
                case '-': {
                    if (last == 0) {
                        negative = true;
                        last = 1;
                        break;
                    }
                    if (last == 5) {
                        last = 6;
                        break;
                    }
                    return 0;
                }
                case '+': {
                    if (last == 5) {
                        last = 6;
                        break;
                    }
                    return 0;
                }
                case 'E': 
                case 'e': {
                    if (last == 2 || last == 4) {
                        last = 5;
                        break;
                    }
                    return 0;
                }
                case '.': {
                    if (last == 2) {
                        last = 3;
                        break;
                    }
                    return 0;
                }
                default: {
                    if (c < '0' || c > '9') {
                        if (!this.isLiteral(c)) break block6;
                        return 0;
                    }
                    if (last == 1 || last == 0) {
                        value = - c - 48;
                        last = 2;
                        break;
                    }
                    if (last == 2) {
                        if (value == 0L) {
                            return 0;
                        }
                        long newValue = value * 10L - (long)(c - 48);
                        fitsInLong &= value > -922337203685477580L || value == -922337203685477580L && newValue < value;
                        value = newValue;
                        break;
                    }
                    if (last == 3) {
                        last = 4;
                        break;
                    }
                    if (last != 5 && last != 6) break;
                    last = 7;
                }
            }
            ++i;
        } while (true);
        if (last == 2 && fitsInLong && (value != Long.MIN_VALUE || negative)) {
            this.peekedLong = negative ? value : - value;
            this.pos += i;
            this.peeked = 15;
            return 15;
        }
        if (last == 2 || last == 4 || last == 7) {
            this.peekedNumberLength = i;
            this.peeked = 16;
            return 16;
        }
        return 0;
    }

    private boolean isLiteral(char c) throws IOException {
        switch (c) {
            case '#': 
            case '/': 
            case ';': 
            case '=': 
            case '\\': {
                this.checkLenient();
            }
            case '\t': 
            case '\n': 
            case '\f': 
            case '\r': 
            case ' ': 
            case ',': 
            case ':': 
            case '[': 
            case ']': 
            case '{': 
            case '}': {
                return false;
            }
        }
        return true;
    }

    public String nextName() throws IOException {
        String result;
        int p = this.peeked;
        if (p == 0) {
            p = this.doPeek();
        }
        if (p == 14) {
            result = this.nextUnquotedValue();
        } else if (p == 12) {
            result = this.nextQuotedValue('\'');
        } else if (p == 13) {
            result = this.nextQuotedValue('\"');
        } else {
            throw new IllegalStateException("Expected a name but was " + (Object)((Object)this.peek()) + " at line " + this.getLineNumber() + " column " + this.getColumnNumber() + " path " + this.getPath());
        }
        this.peeked = 0;
        this.pathNames[this.stackSize - 1] = result;
        return result;
    }

    public String nextString() throws IOException {
        String result;
        int p = this.peeked;
        if (p == 0) {
            p = this.doPeek();
        }
        if (p == 10) {
            result = this.nextUnquotedValue();
        } else if (p == 8) {
            result = this.nextQuotedValue('\'');
        } else if (p == 9) {
            result = this.nextQuotedValue('\"');
        } else if (p == 11) {
            result = this.peekedString;
            this.peekedString = null;
        } else if (p == 15) {
            result = Long.toString(this.peekedLong);
        } else if (p == 16) {
            result = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        } else {
            throw new IllegalStateException("Expected a string but was " + (Object)((Object)this.peek()) + " at line " + this.getLineNumber() + " column " + this.getColumnNumber() + " path " + this.getPath());
        }
        this.peeked = 0;
        int[] arrn = this.pathIndices;
        int n = this.stackSize - 1;
        arrn[n] = arrn[n] + 1;
        return result;
    }

    public boolean nextBoolean() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = this.doPeek();
        }
        if (p == 5) {
            this.peeked = 0;
            int[] arrn = this.pathIndices;
            int n = this.stackSize - 1;
            arrn[n] = arrn[n] + 1;
            return true;
        }
        if (p == 6) {
            this.peeked = 0;
            int[] arrn = this.pathIndices;
            int n = this.stackSize - 1;
            arrn[n] = arrn[n] + 1;
            return false;
        }
        throw new IllegalStateException("Expected a boolean but was " + (Object)((Object)this.peek()) + " at line " + this.getLineNumber() + " column " + this.getColumnNumber() + " path " + this.getPath());
    }

    public void nextNull() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = this.doPeek();
        }
        if (p != 7) {
            throw new IllegalStateException("Expected null but was " + (Object)((Object)this.peek()) + " at line " + this.getLineNumber() + " column " + this.getColumnNumber() + " path " + this.getPath());
        }
        this.peeked = 0;
        int[] arrn = this.pathIndices;
        int n = this.stackSize - 1;
        arrn[n] = arrn[n] + 1;
    }

    public double nextDouble() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = this.doPeek();
        }
        if (p == 15) {
            this.peeked = 0;
            int[] arrn = this.pathIndices;
            int n = this.stackSize - 1;
            arrn[n] = arrn[n] + 1;
            return this.peekedLong;
        }
        if (p == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        } else if (p == 8 || p == 9) {
            this.peekedString = this.nextQuotedValue(p == 8 ? (char)'\'' : '\"');
        } else if (p == 10) {
            this.peekedString = this.nextUnquotedValue();
        } else if (p != 11) {
            throw new IllegalStateException("Expected a double but was " + (Object)((Object)this.peek()) + " at line " + this.getLineNumber() + " column " + this.getColumnNumber() + " path " + this.getPath());
        }
        this.peeked = 11;
        double result = Double.parseDouble(this.peekedString);
        if (!this.lenient && (Double.isNaN(result) || Double.isInfinite(result))) {
            throw new MalformedJsonException("JSON forbids NaN and infinities: " + result + " at line " + this.getLineNumber() + " column " + this.getColumnNumber() + " path " + this.getPath());
        }
        this.peekedString = null;
        this.peeked = 0;
        int[] arrn = this.pathIndices;
        int n = this.stackSize - 1;
        arrn[n] = arrn[n] + 1;
        return result;
    }

    public long nextLong() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = this.doPeek();
        }
        if (p == 15) {
            this.peeked = 0;
            int[] arrn = this.pathIndices;
            int n = this.stackSize - 1;
            arrn[n] = arrn[n] + 1;
            return this.peekedLong;
        }
        if (p == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        } else if (p == 8 || p == 9) {
            this.peekedString = this.nextQuotedValue(p == 8 ? (char)'\'' : '\"');
            try {
                long result = Long.parseLong(this.peekedString);
                this.peeked = 0;
                int[] arrn = this.pathIndices;
                int n = this.stackSize - 1;
                arrn[n] = arrn[n] + 1;
                return result;
            }
            catch (NumberFormatException ignored) {}
        } else {
            throw new IllegalStateException("Expected a long but was " + (Object)((Object)this.peek()) + " at line " + this.getLineNumber() + " column " + this.getColumnNumber() + " path " + this.getPath());
        }
        this.peeked = 11;
        double asDouble = Double.parseDouble(this.peekedString);
        long result = (long)asDouble;
        if ((double)result != asDouble) {
            throw new NumberFormatException("Expected a long but was " + this.peekedString + " at line " + this.getLineNumber() + " column " + this.getColumnNumber() + " path " + this.getPath());
        }
        this.peekedString = null;
        this.peeked = 0;
        int[] arrn = this.pathIndices;
        int n = this.stackSize - 1;
        arrn[n] = arrn[n] + 1;
        return result;
    }

    private String nextQuotedValue(char quote) throws IOException {
        char[] buffer = this.buffer;
        StringBuilder builder = new StringBuilder();
        do {
            int p = this.pos;
            int l = this.limit;
            int start = p;
            while (p < l) {
                char c;
                if ((c = buffer[p++]) == quote) {
                    this.pos = p;
                    builder.append(buffer, start, p - start - 1);
                    return builder.toString();
                }
                if (c == '\\') {
                    this.pos = p;
                    builder.append(buffer, start, p - start - 1);
                    builder.append(this.readEscapeCharacter());
                    p = this.pos;
                    l = this.limit;
                    start = p;
                    continue;
                }
                if (c != '\n') continue;
                ++this.lineNumber;
                this.lineStart = p;
            }
            builder.append(buffer, start, p - start);
            this.pos = p;
        } while (this.fillBuffer(1));
        throw this.syntaxError("Unterminated string");
    }

    private String nextUnquotedValue() throws IOException {
        String result;
        StringBuilder builder = null;
        int i = 0;
        block4 : do {
            if (this.pos + i < this.limit) {
                switch (this.buffer[this.pos + i]) {
                    case '#': 
                    case '/': 
                    case ';': 
                    case '=': 
                    case '\\': {
                        this.checkLenient();
                    }
                    case '\t': 
                    case '\n': 
                    case '\f': 
                    case '\r': 
                    case ' ': 
                    case ',': 
                    case ':': 
                    case '[': 
                    case ']': 
                    case '{': 
                    case '}': {
                        break block4;
                    }
                    default: {
                        ++i;
                        break;
                    }
                }
                continue;
            }
            if (i < this.buffer.length) {
                if (!this.fillBuffer(i + 1)) break;
                continue;
            }
            if (builder == null) {
                builder = new StringBuilder();
            }
            builder.append(this.buffer, this.pos, i);
            this.pos += i;
            i = 0;
            if (!this.fillBuffer(1)) break;
        } while (true);
        if (builder == null) {
            result = new String(this.buffer, this.pos, i);
        } else {
            builder.append(this.buffer, this.pos, i);
            result = builder.toString();
        }
        this.pos += i;
        return result;
    }

    private void skipQuotedValue(char quote) throws IOException {
        char[] buffer = this.buffer;
        do {
            int p = this.pos;
            int l = this.limit;
            while (p < l) {
                char c;
                if ((c = buffer[p++]) == quote) {
                    this.pos = p;
                    return;
                }
                if (c == '\\') {
                    this.pos = p;
                    this.readEscapeCharacter();
                    p = this.pos;
                    l = this.limit;
                    continue;
                }
                if (c != '\n') continue;
                ++this.lineNumber;
                this.lineStart = p;
            }
            this.pos = p;
        } while (this.fillBuffer(1));
        throw this.syntaxError("Unterminated string");
    }

    private void skipUnquotedValue() throws IOException {
        do {
            int i = 0;
            while (this.pos + i < this.limit) {
                switch (this.buffer[this.pos + i]) {
                    case '#': 
                    case '/': 
                    case ';': 
                    case '=': 
                    case '\\': {
                        this.checkLenient();
                    }
                    case '\t': 
                    case '\n': 
                    case '\f': 
                    case '\r': 
                    case ' ': 
                    case ',': 
                    case ':': 
                    case '[': 
                    case ']': 
                    case '{': 
                    case '}': {
                        this.pos += i;
                        return;
                    }
                }
                ++i;
            }
            this.pos += i;
        } while (this.fillBuffer(1));
    }

    public int nextInt() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = this.doPeek();
        }
        if (p == 15) {
            int result = (int)this.peekedLong;
            if (this.peekedLong != (long)result) {
                throw new NumberFormatException("Expected an int but was " + this.peekedLong + " at line " + this.getLineNumber() + " column " + this.getColumnNumber() + " path " + this.getPath());
            }
            this.peeked = 0;
            int[] arrn = this.pathIndices;
            int n = this.stackSize - 1;
            arrn[n] = arrn[n] + 1;
            return result;
        }
        if (p == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        } else if (p == 8 || p == 9) {
            this.peekedString = this.nextQuotedValue(p == 8 ? (char)'\'' : '\"');
            try {
                int result = Integer.parseInt(this.peekedString);
                this.peeked = 0;
                int[] arrn = this.pathIndices;
                int n = this.stackSize - 1;
                arrn[n] = arrn[n] + 1;
                return result;
            }
            catch (NumberFormatException ignored) {}
        } else {
            throw new IllegalStateException("Expected an int but was " + (Object)((Object)this.peek()) + " at line " + this.getLineNumber() + " column " + this.getColumnNumber() + " path " + this.getPath());
        }
        this.peeked = 11;
        double asDouble = Double.parseDouble(this.peekedString);
        int result = (int)asDouble;
        if ((double)result != asDouble) {
            throw new NumberFormatException("Expected an int but was " + this.peekedString + " at line " + this.getLineNumber() + " column " + this.getColumnNumber() + " path " + this.getPath());
        }
        this.peekedString = null;
        this.peeked = 0;
        int[] arrn = this.pathIndices;
        int n = this.stackSize - 1;
        arrn[n] = arrn[n] + 1;
        return result;
    }

    public void close() throws IOException {
        this.peeked = 0;
        this.stack[0] = 8;
        this.stackSize = 1;
        this.in.close();
    }

    public void skipValue() throws IOException {
        int count = 0;
        do {
            int p;
            if ((p = this.peeked) == 0) {
                p = this.doPeek();
            }
            if (p == 3) {
                this.push(1);
                ++count;
            } else if (p == 1) {
                this.push(3);
                ++count;
            } else if (p == 4) {
                --this.stackSize;
                --count;
            } else if (p == 2) {
                --this.stackSize;
                --count;
            } else if (p == 14 || p == 10) {
                this.skipUnquotedValue();
            } else if (p == 8 || p == 12) {
                this.skipQuotedValue('\'');
            } else if (p == 9 || p == 13) {
                this.skipQuotedValue('\"');
            } else if (p == 16) {
                this.pos += this.peekedNumberLength;
            }
            this.peeked = 0;
        } while (count != 0);
        int[] arrn = this.pathIndices;
        int n = this.stackSize - 1;
        arrn[n] = arrn[n] + 1;
        this.pathNames[this.stackSize - 1] = "null";
    }

    private void push(int newTop) {
        if (this.stackSize == this.stack.length) {
            int[] newStack = new int[this.stackSize * 2];
            int[] newPathIndices = new int[this.stackSize * 2];
            String[] newPathNames = new String[this.stackSize * 2];
            System.arraycopy(this.stack, 0, newStack, 0, this.stackSize);
            System.arraycopy(this.pathIndices, 0, newPathIndices, 0, this.stackSize);
            System.arraycopy(this.pathNames, 0, newPathNames, 0, this.stackSize);
            this.stack = newStack;
            this.pathIndices = newPathIndices;
            this.pathNames = newPathNames;
        }
        this.stack[this.stackSize++] = newTop;
    }

    private boolean fillBuffer(int minimum) throws IOException {
        int total;
        char[] buffer = this.buffer;
        this.lineStart -= this.pos;
        if (this.limit != this.pos) {
            this.limit -= this.pos;
            System.arraycopy(buffer, this.pos, buffer, 0, this.limit);
        } else {
            this.limit = 0;
        }
        this.pos = 0;
        while ((total = this.in.read(buffer, this.limit, buffer.length - this.limit)) != -1) {
            this.limit += total;
            if (this.lineNumber == 0 && this.lineStart == 0 && this.limit > 0 && buffer[0] == '\ufeff') {
                ++this.pos;
                ++this.lineStart;
                ++minimum;
            }
            if (this.limit < minimum) continue;
            return true;
        }
        return false;
    }

    private int getLineNumber() {
        return this.lineNumber + 1;
    }

    private int getColumnNumber() {
        return this.pos - this.lineStart + 1;
    }

    private int nextNonWhitespace(boolean throwOnEof) throws IOException {
        block12 : {
            char c;
            char[] buffer = this.buffer;
            int p = this.pos;
            int l = this.limit;
            block4 : do {
                if (p == l) {
                    this.pos = p;
                    if (!this.fillBuffer(1)) break block12;
                    p = this.pos;
                    l = this.limit;
                }
                if ((c = buffer[p++]) == '\n') {
                    ++this.lineNumber;
                    this.lineStart = p;
                    continue;
                }
                if (c == ' ' || c == '\r' || c == '\t') continue;
                if (c == '/') {
                    this.pos = p;
                    if (p == l) {
                        --this.pos;
                        boolean charsLoaded = this.fillBuffer(2);
                        ++this.pos;
                        if (!charsLoaded) {
                            return c;
                        }
                    }
                    this.checkLenient();
                    char peek = buffer[this.pos];
                    switch (peek) {
                        case '*': {
                            ++this.pos;
                            if (!this.skipTo("*/")) {
                                throw this.syntaxError("Unterminated comment");
                            }
                            p = this.pos + 2;
                            l = this.limit;
                            continue block4;
                        }
                        case '/': {
                            ++this.pos;
                            this.skipToEndOfLine();
                            p = this.pos;
                            l = this.limit;
                            continue block4;
                        }
                    }
                    return c;
                }
                if (c != '#') break;
                this.pos = p;
                this.checkLenient();
                this.skipToEndOfLine();
                p = this.pos;
                l = this.limit;
            } while (true);
            this.pos = p;
            return c;
        }
        if (throwOnEof) {
            throw new EOFException("End of input at line " + this.getLineNumber() + " column " + this.getColumnNumber());
        }
        return -1;
    }

    private void checkLenient() throws IOException {
        if (!this.lenient) {
            throw this.syntaxError("Use JsonReader.setLenient(true) to accept malformed JSON");
        }
    }

    private void skipToEndOfLine() throws IOException {
        while (this.pos < this.limit || this.fillBuffer(1)) {
            char c;
            if ((c = this.buffer[this.pos++]) == '\n') {
                ++this.lineNumber;
                this.lineStart = this.pos;
                break;
            }
            if (c != '\r') continue;
            break;
        }
    }

    private boolean skipTo(String toFind) throws IOException {
        while (this.pos + toFind.length() <= this.limit || this.fillBuffer(toFind.length())) {
            block5 : {
                if (this.buffer[this.pos] == '\n') {
                    ++this.lineNumber;
                    this.lineStart = this.pos + 1;
                } else {
                    for (int c = 0; c < toFind.length(); ++c) {
                        if (this.buffer[this.pos + c] == toFind.charAt(c)) {
                            continue;
                        }
                        break block5;
                    }
                    return true;
                }
            }
            ++this.pos;
        }
        return false;
    }

    public String toString() {
        return this.getClass().getSimpleName() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber();
    }

    public String getPath() {
        StringBuilder result = new StringBuilder().append('$');
        int size = this.stackSize;
        block4 : for (int i = 0; i < size; ++i) {
            switch (this.stack[i]) {
                case 1: 
                case 2: {
                    result.append('[').append(this.pathIndices[i]).append(']');
                    continue block4;
                }
                case 3: 
                case 4: 
                case 5: {
                    result.append('.');
                    if (this.pathNames[i] == null) continue block4;
                    result.append(this.pathNames[i]);
                    break;
                }
            }
        }
        return result.toString();
    }

    private char readEscapeCharacter() throws IOException {
        if (this.pos == this.limit && !this.fillBuffer(1)) {
            throw this.syntaxError("Unterminated escape sequence");
        }
        char escaped = this.buffer[this.pos++];
        switch (escaped) {
            case 'u': {
                int i;
                if (this.pos + 4 > this.limit && !this.fillBuffer(4)) {
                    throw this.syntaxError("Unterminated escape sequence");
                }
                char result = '\u0000';
                int end = i + 4;
                for (i = this.pos; i < end; ++i) {
                    char c = this.buffer[i];
                    result = (char)(result << 4);
                    if (c >= '0' && c <= '9') {
                        result = (char)(result + (c - 48));
                        continue;
                    }
                    if (c >= 'a' && c <= 'f') {
                        result = (char)(result + (c - 97 + 10));
                        continue;
                    }
                    if (c >= 'A' && c <= 'F') {
                        result = (char)(result + (c - 65 + 10));
                        continue;
                    }
                    throw new NumberFormatException("\\u" + new String(this.buffer, this.pos, 4));
                }
                this.pos += 4;
                return result;
            }
            case 't': {
                return '\t';
            }
            case 'b': {
                return '\b';
            }
            case 'n': {
                return '\n';
            }
            case 'r': {
                return '\r';
            }
            case 'f': {
                return '\f';
            }
            case '\n': {
                ++this.lineNumber;
                this.lineStart = this.pos;
            }
        }
        return escaped;
    }

    private IOException syntaxError(String message) throws IOException {
        throw new MalformedJsonException(message + " at line " + this.getLineNumber() + " column " + this.getColumnNumber() + " path " + this.getPath());
    }

    private void consumeNonExecutePrefix() throws IOException {
        this.nextNonWhitespace(true);
        --this.pos;
        if (this.pos + NON_EXECUTE_PREFIX.length > this.limit && !this.fillBuffer(NON_EXECUTE_PREFIX.length)) {
            return;
        }
        for (int i = 0; i < NON_EXECUTE_PREFIX.length; ++i) {
            if (this.buffer[this.pos + i] == NON_EXECUTE_PREFIX[i]) continue;
            return;
        }
        this.pos += NON_EXECUTE_PREFIX.length;
    }

    static {
        JsonReaderInternalAccess.INSTANCE = new JsonReaderInternalAccess(){

            public void promoteNameToValue(JsonReader reader) throws IOException {
                if (reader instanceof JsonTreeReader) {
                    ((JsonTreeReader)reader).promoteNameToValue();
                    return;
                }
                int p = reader.peeked;
                if (p == 0) {
                    p = reader.doPeek();
                }
                if (p == 13) {
                    reader.peeked = 9;
                } else if (p == 12) {
                    reader.peeked = 8;
                } else if (p == 14) {
                    reader.peeked = 10;
                } else {
                    throw new IllegalStateException("Expected a name but was " + (Object)((Object)reader.peek()) + " " + " at line " + reader.getLineNumber() + " column " + reader.getColumnNumber() + " path " + reader.getPath());
                }
            }
        };
    }

}

