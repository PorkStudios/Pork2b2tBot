/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce.spec;

import gnu.crypto.prng.IRandom;
import java.security.spec.AlgorithmParameterSpec;

public class TMMHParameterSpec
implements AlgorithmParameterSpec {
    protected IRandom keystream;
    protected Integer tagLength;
    protected byte[] prefix;

    public IRandom getKeystream() {
        return this.keystream;
    }

    public Integer getTagLength() {
        return this.tagLength;
    }

    public byte[] getPrefix() {
        return this.prefix;
    }

    public TMMHParameterSpec(IRandom keystream, Integer tagLength, byte[] prefix) {
        this.keystream = keystream;
        this.tagLength = tagLength;
        this.prefix = prefix;
    }

    public TMMHParameterSpec(IRandom keystream, Integer tagLength) {
        this(keystream, tagLength, null);
    }
}

