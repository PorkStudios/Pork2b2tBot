/*
 * Decompiled with CFR 0_132.
 */
package org.apache.commons.codec.binary;

import java.io.InputStream;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.BaseNCodec;
import org.apache.commons.codec.binary.BaseNCodecInputStream;

public class Base32InputStream
extends BaseNCodecInputStream {
    public Base32InputStream(InputStream in) {
        this(in, false);
    }

    public Base32InputStream(InputStream in, boolean doEncode) {
        super(in, new Base32(false), doEncode);
    }

    public Base32InputStream(InputStream in, boolean doEncode, int lineLength, byte[] lineSeparator) {
        super(in, new Base32(lineLength, lineSeparator), doEncode);
    }
}

