/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec;

import io.netty.handler.codec.DateFormatter;
import io.netty.handler.codec.ValueConverter;
import io.netty.util.AsciiString;
import io.netty.util.internal.PlatformDependent;
import java.text.ParseException;
import java.util.Date;

public class CharSequenceValueConverter
implements ValueConverter<CharSequence> {
    public static final CharSequenceValueConverter INSTANCE = new CharSequenceValueConverter();

    @Override
    public CharSequence convertObject(Object value) {
        if (value instanceof CharSequence) {
            return (CharSequence)value;
        }
        return value.toString();
    }

    @Override
    public CharSequence convertInt(int value) {
        return String.valueOf(value);
    }

    @Override
    public CharSequence convertLong(long value) {
        return String.valueOf(value);
    }

    @Override
    public CharSequence convertDouble(double value) {
        return String.valueOf(value);
    }

    @Override
    public CharSequence convertChar(char value) {
        return String.valueOf(value);
    }

    @Override
    public CharSequence convertBoolean(boolean value) {
        return String.valueOf(value);
    }

    @Override
    public CharSequence convertFloat(float value) {
        return String.valueOf(value);
    }

    @Override
    public boolean convertToBoolean(CharSequence value) {
        if (value instanceof AsciiString) {
            return ((AsciiString)value).parseBoolean();
        }
        return Boolean.parseBoolean(value.toString());
    }

    @Override
    public CharSequence convertByte(byte value) {
        return String.valueOf(value);
    }

    @Override
    public byte convertToByte(CharSequence value) {
        if (value instanceof AsciiString) {
            return ((AsciiString)value).byteAt(0);
        }
        return Byte.parseByte(value.toString());
    }

    @Override
    public char convertToChar(CharSequence value) {
        return value.charAt(0);
    }

    @Override
    public CharSequence convertShort(short value) {
        return String.valueOf(value);
    }

    @Override
    public short convertToShort(CharSequence value) {
        if (value instanceof AsciiString) {
            return ((AsciiString)value).parseShort();
        }
        return Short.parseShort(value.toString());
    }

    @Override
    public int convertToInt(CharSequence value) {
        if (value instanceof AsciiString) {
            return ((AsciiString)value).parseInt();
        }
        return Integer.parseInt(value.toString());
    }

    @Override
    public long convertToLong(CharSequence value) {
        if (value instanceof AsciiString) {
            return ((AsciiString)value).parseLong();
        }
        return Long.parseLong(value.toString());
    }

    @Override
    public CharSequence convertTimeMillis(long value) {
        return String.valueOf(value);
    }

    @Override
    public long convertToTimeMillis(CharSequence value) {
        Date date = DateFormatter.parseHttpDate(value);
        if (date == null) {
            PlatformDependent.throwException(new ParseException("header can't be parsed into a Date: " + value, 0));
            return 0L;
        }
        return date.getTime();
    }

    @Override
    public float convertToFloat(CharSequence value) {
        if (value instanceof AsciiString) {
            return ((AsciiString)value).parseFloat();
        }
        return Float.parseFloat(value.toString());
    }

    @Override
    public double convertToDouble(CharSequence value) {
        if (value instanceof AsciiString) {
            return ((AsciiString)value).parseDouble();
        }
        return Double.parseDouble(value.toString());
    }
}

