/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.string;

import io.netty.buffer.ByteBufUtil;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.nio.charset.Charset;

public final class LineSeparator {
    public static final LineSeparator DEFAULT = new LineSeparator(StringUtil.NEWLINE);
    public static final LineSeparator UNIX = new LineSeparator("\n");
    public static final LineSeparator WINDOWS = new LineSeparator("\r\n");
    private final String value;

    public LineSeparator(String lineSeparator) {
        this.value = ObjectUtil.checkNotNull(lineSeparator, "lineSeparator");
    }

    public String value() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LineSeparator)) {
            return false;
        }
        LineSeparator that = (LineSeparator)o;
        return this.value != null ? this.value.equals(that.value) : that.value == null;
    }

    public int hashCode() {
        return this.value != null ? this.value.hashCode() : 0;
    }

    public String toString() {
        return ByteBufUtil.hexDump(this.value.getBytes(CharsetUtil.UTF_8));
    }
}

