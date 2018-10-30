/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec;

import io.netty.util.AsciiString;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.ObjectUtil;
import java.util.BitSet;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public final class DateFormatter {
    private static final BitSet DELIMITERS;
    private static final String[] DAY_OF_WEEK_TO_SHORT_NAME;
    private static final String[] CALENDAR_MONTH_TO_SHORT_NAME;
    private static final FastThreadLocal<DateFormatter> INSTANCES;
    private final GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    private final StringBuilder sb = new StringBuilder(29);
    private boolean timeFound;
    private int hours;
    private int minutes;
    private int seconds;
    private boolean dayOfMonthFound;
    private int dayOfMonth;
    private boolean monthFound;
    private int month;
    private boolean yearFound;
    private int year;

    public static Date parseHttpDate(CharSequence txt) {
        return DateFormatter.parseHttpDate(txt, 0, txt.length());
    }

    public static Date parseHttpDate(CharSequence txt, int start, int end) {
        int length = end - start;
        if (length == 0) {
            return null;
        }
        if (length < 0) {
            throw new IllegalArgumentException("Can't have end < start");
        }
        if (length > 64) {
            throw new IllegalArgumentException("Can't parse more than 64 chars,looks like a user error or a malformed header");
        }
        return DateFormatter.formatter().parse0(ObjectUtil.checkNotNull(txt, "txt"), start, end);
    }

    public static String format(Date date) {
        return DateFormatter.formatter().format0(ObjectUtil.checkNotNull(date, "date"));
    }

    public static StringBuilder append(Date date, StringBuilder sb) {
        return DateFormatter.formatter().append0(ObjectUtil.checkNotNull(date, "date"), ObjectUtil.checkNotNull(sb, "sb"));
    }

    private static DateFormatter formatter() {
        DateFormatter formatter = INSTANCES.get();
        formatter.reset();
        return formatter;
    }

    private static boolean isDelim(char c) {
        return DELIMITERS.get(c);
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static int getNumericalValue(char c) {
        return c - 48;
    }

    private DateFormatter() {
        this.reset();
    }

    public void reset() {
        this.timeFound = false;
        this.hours = -1;
        this.minutes = -1;
        this.seconds = -1;
        this.dayOfMonthFound = false;
        this.dayOfMonth = -1;
        this.monthFound = false;
        this.month = -1;
        this.yearFound = false;
        this.year = -1;
        this.cal.clear();
        this.sb.setLength(0);
    }

    private boolean tryParseTime(CharSequence txt, int tokenStart, int tokenEnd) {
        int len = tokenEnd - tokenStart;
        if (len < 5 || len > 8) {
            return false;
        }
        int localHours = -1;
        int localMinutes = -1;
        int localSeconds = -1;
        int currentPartNumber = 0;
        int currentPartValue = 0;
        int numDigits = 0;
        for (int i = tokenStart; i < tokenEnd; ++i) {
            char c = txt.charAt(i);
            if (DateFormatter.isDigit(c)) {
                currentPartValue = currentPartValue * 10 + DateFormatter.getNumericalValue(c);
                if (++numDigits <= 2) continue;
                return false;
            }
            if (c == ':') {
                if (numDigits == 0) {
                    return false;
                }
                switch (currentPartNumber) {
                    case 0: {
                        localHours = currentPartValue;
                        break;
                    }
                    case 1: {
                        localMinutes = currentPartValue;
                        break;
                    }
                    default: {
                        return false;
                    }
                }
                currentPartValue = 0;
                ++currentPartNumber;
                numDigits = 0;
                continue;
            }
            return false;
        }
        if (numDigits > 0) {
            localSeconds = currentPartValue;
        }
        if (localHours >= 0 && localMinutes >= 0 && localSeconds >= 0) {
            this.hours = localHours;
            this.minutes = localMinutes;
            this.seconds = localSeconds;
            return true;
        }
        return false;
    }

    private boolean tryParseDayOfMonth(CharSequence txt, int tokenStart, int tokenEnd) {
        int len = tokenEnd - tokenStart;
        if (len == 1) {
            char c0 = txt.charAt(tokenStart);
            if (DateFormatter.isDigit(c0)) {
                this.dayOfMonth = DateFormatter.getNumericalValue(c0);
                return true;
            }
        } else if (len == 2) {
            char c0 = txt.charAt(tokenStart);
            char c1 = txt.charAt(tokenStart + 1);
            if (DateFormatter.isDigit(c0) && DateFormatter.isDigit(c1)) {
                this.dayOfMonth = DateFormatter.getNumericalValue(c0) * 10 + DateFormatter.getNumericalValue(c1);
                return true;
            }
        }
        return false;
    }

    private static boolean matchMonth(String month, CharSequence txt, int tokenStart) {
        return AsciiString.regionMatchesAscii(month, true, 0, txt, tokenStart, 3);
    }

    private boolean tryParseMonth(CharSequence txt, int tokenStart, int tokenEnd) {
        int len = tokenEnd - tokenStart;
        if (len != 3) {
            return false;
        }
        if (DateFormatter.matchMonth("Jan", txt, tokenStart)) {
            this.month = 0;
        } else if (DateFormatter.matchMonth("Feb", txt, tokenStart)) {
            this.month = 1;
        } else if (DateFormatter.matchMonth("Mar", txt, tokenStart)) {
            this.month = 2;
        } else if (DateFormatter.matchMonth("Apr", txt, tokenStart)) {
            this.month = 3;
        } else if (DateFormatter.matchMonth("May", txt, tokenStart)) {
            this.month = 4;
        } else if (DateFormatter.matchMonth("Jun", txt, tokenStart)) {
            this.month = 5;
        } else if (DateFormatter.matchMonth("Jul", txt, tokenStart)) {
            this.month = 6;
        } else if (DateFormatter.matchMonth("Aug", txt, tokenStart)) {
            this.month = 7;
        } else if (DateFormatter.matchMonth("Sep", txt, tokenStart)) {
            this.month = 8;
        } else if (DateFormatter.matchMonth("Oct", txt, tokenStart)) {
            this.month = 9;
        } else if (DateFormatter.matchMonth("Nov", txt, tokenStart)) {
            this.month = 10;
        } else if (DateFormatter.matchMonth("Dec", txt, tokenStart)) {
            this.month = 11;
        } else {
            return false;
        }
        return true;
    }

    private boolean tryParseYear(CharSequence txt, int tokenStart, int tokenEnd) {
        int len = tokenEnd - tokenStart;
        if (len == 2) {
            char c0 = txt.charAt(tokenStart);
            char c1 = txt.charAt(tokenStart + 1);
            if (DateFormatter.isDigit(c0) && DateFormatter.isDigit(c1)) {
                this.year = DateFormatter.getNumericalValue(c0) * 10 + DateFormatter.getNumericalValue(c1);
                return true;
            }
        } else if (len == 4) {
            char c0 = txt.charAt(tokenStart);
            char c1 = txt.charAt(tokenStart + 1);
            char c2 = txt.charAt(tokenStart + 2);
            char c3 = txt.charAt(tokenStart + 3);
            if (DateFormatter.isDigit(c0) && DateFormatter.isDigit(c1) && DateFormatter.isDigit(c2) && DateFormatter.isDigit(c3)) {
                this.year = DateFormatter.getNumericalValue(c0) * 1000 + DateFormatter.getNumericalValue(c1) * 100 + DateFormatter.getNumericalValue(c2) * 10 + DateFormatter.getNumericalValue(c3);
                return true;
            }
        }
        return false;
    }

    private boolean parseToken(CharSequence txt, int tokenStart, int tokenEnd) {
        if (!this.timeFound) {
            this.timeFound = this.tryParseTime(txt, tokenStart, tokenEnd);
            if (this.timeFound) {
                return this.dayOfMonthFound && this.monthFound && this.yearFound;
            }
        }
        if (!this.dayOfMonthFound) {
            this.dayOfMonthFound = this.tryParseDayOfMonth(txt, tokenStart, tokenEnd);
            if (this.dayOfMonthFound) {
                return this.timeFound && this.monthFound && this.yearFound;
            }
        }
        if (!this.monthFound) {
            this.monthFound = this.tryParseMonth(txt, tokenStart, tokenEnd);
            if (this.monthFound) {
                return this.timeFound && this.dayOfMonthFound && this.yearFound;
            }
        }
        if (!this.yearFound) {
            this.yearFound = this.tryParseYear(txt, tokenStart, tokenEnd);
        }
        return this.timeFound && this.dayOfMonthFound && this.monthFound && this.yearFound;
    }

    private Date parse0(CharSequence txt, int start, int end) {
        boolean allPartsFound = this.parse1(txt, start, end);
        return allPartsFound && this.normalizeAndValidate() ? this.computeDate() : null;
    }

    private boolean parse1(CharSequence txt, int start, int end) {
        int tokenStart = -1;
        for (int i = start; i < end; ++i) {
            char c = txt.charAt(i);
            if (DateFormatter.isDelim(c)) {
                if (tokenStart == -1) continue;
                if (this.parseToken(txt, tokenStart, i)) {
                    return true;
                }
                tokenStart = -1;
                continue;
            }
            if (tokenStart != -1) continue;
            tokenStart = i;
        }
        return tokenStart != -1 && this.parseToken(txt, tokenStart, txt.length());
    }

    private boolean normalizeAndValidate() {
        if (this.dayOfMonth < 1 || this.dayOfMonth > 31 || this.hours > 23 || this.minutes > 59 || this.seconds > 59) {
            return false;
        }
        if (this.year >= 70 && this.year <= 99) {
            this.year += 1900;
        } else if (this.year >= 0 && this.year < 70) {
            this.year += 2000;
        } else if (this.year < 1601) {
            return false;
        }
        return true;
    }

    private Date computeDate() {
        this.cal.set(5, this.dayOfMonth);
        this.cal.set(2, this.month);
        this.cal.set(1, this.year);
        this.cal.set(11, this.hours);
        this.cal.set(12, this.minutes);
        this.cal.set(13, this.seconds);
        return this.cal.getTime();
    }

    private String format0(Date date) {
        this.append0(date, this.sb);
        return this.sb.toString();
    }

    private StringBuilder append0(Date date, StringBuilder sb) {
        this.cal.setTime(date);
        sb.append(DAY_OF_WEEK_TO_SHORT_NAME[this.cal.get(7) - 1]).append(", ");
        sb.append(this.cal.get(5)).append(' ');
        sb.append(CALENDAR_MONTH_TO_SHORT_NAME[this.cal.get(2)]).append(' ');
        sb.append(this.cal.get(1)).append(' ');
        DateFormatter.appendZeroLeftPadded(this.cal.get(11), sb).append(':');
        DateFormatter.appendZeroLeftPadded(this.cal.get(12), sb).append(':');
        return DateFormatter.appendZeroLeftPadded(this.cal.get(13), sb).append(" GMT");
    }

    private static StringBuilder appendZeroLeftPadded(int value, StringBuilder sb) {
        if (value < 10) {
            sb.append('0');
        }
        return sb.append(value);
    }

    static {
        int c;
        DELIMITERS = new BitSet();
        DELIMITERS.set(9);
        for (c = 32; c <= 47; c = (int)((char)(c + 1))) {
            DELIMITERS.set(c);
        }
        for (c = 59; c <= 64; c = (int)((char)(c + 1))) {
            DELIMITERS.set(c);
        }
        for (c = 91; c <= 96; c = (int)((char)(c + 1))) {
            DELIMITERS.set(c);
        }
        for (c = 123; c <= 126; c = (int)((char)(c + 1))) {
            DELIMITERS.set(c);
        }
        DAY_OF_WEEK_TO_SHORT_NAME = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        CALENDAR_MONTH_TO_SHORT_NAME = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        INSTANCES = new FastThreadLocal<DateFormatter>(){

            @Override
            protected DateFormatter initialValue() {
                return new DateFormatter();
            }
        };
    }

}

