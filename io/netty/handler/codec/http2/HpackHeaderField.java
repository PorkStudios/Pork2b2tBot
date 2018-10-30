/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.HpackUtil;
import io.netty.util.internal.ObjectUtil;

class HpackHeaderField {
    static final int HEADER_ENTRY_OVERHEAD = 32;
    final CharSequence name;
    final CharSequence value;

    static long sizeOf(CharSequence name, CharSequence value) {
        return name.length() + value.length() + 32;
    }

    HpackHeaderField(CharSequence name, CharSequence value) {
        this.name = ObjectUtil.checkNotNull(name, "name");
        this.value = ObjectUtil.checkNotNull(value, "value");
    }

    final int size() {
        return this.name.length() + this.value.length() + 32;
    }

    public final int hashCode() {
        return super.hashCode();
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof HpackHeaderField)) {
            return false;
        }
        HpackHeaderField other = (HpackHeaderField)obj;
        return (HpackUtil.equalsConstantTime(this.name, other.name) & HpackUtil.equalsConstantTime(this.value, other.value)) != 0;
    }

    public String toString() {
        return this.name + ": " + this.value;
    }
}

