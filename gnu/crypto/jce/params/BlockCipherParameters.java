/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce.params;

import gnu.crypto.jce.params.DERReader;
import gnu.crypto.jce.params.DERWriter;
import gnu.crypto.jce.spec.BlockCipherParameterSpec;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.AlgorithmParametersSpi;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;

public class BlockCipherParameters
extends AlgorithmParametersSpi {
    private static final String DEFAULT_FORMAT = "ASN.1";
    protected BlockCipherParameterSpec cipherSpec;

    protected byte[] engineGetEncoded() throws IOException {
        return this.engineGetEncoded(DEFAULT_FORMAT);
    }

    protected byte[] engineGetEncoded(String format) throws IOException {
        if (!format.equalsIgnoreCase(DEFAULT_FORMAT) && !format.equalsIgnoreCase("asn1")) {
            throw new IOException("unknown format \"" + format + '\"');
        }
        DERWriter writer = new DERWriter();
        return writer.joinarrays(writer.writeBigInteger(BigInteger.valueOf(this.cipherSpec.getBlockSize())), writer.writeBigInteger(BigInteger.valueOf(this.cipherSpec.getKeySize())), this.cipherSpec.getIV() != null ? writer.writeBigInteger(new BigInteger(this.cipherSpec.getIV())) : new byte[]{});
    }

    protected void engineInit(AlgorithmParameterSpec spec) throws InvalidParameterSpecException {
        if (!(spec instanceof BlockCipherParameterSpec)) {
            throw new InvalidParameterSpecException();
        }
        this.cipherSpec = (BlockCipherParameterSpec)spec;
    }

    protected void engineInit(byte[] encoded, String format) throws IOException {
        if (!format.equalsIgnoreCase(DEFAULT_FORMAT) && !format.equalsIgnoreCase("ASN1")) {
            throw new IOException("invalid format: only accepts ASN.1");
        }
        this.engineInit(encoded);
    }

    protected void engineInit(byte[] encoded) throws IOException {
        DERReader reader = new DERReader(encoded);
        int bs = reader.getBigInteger().intValue();
        int ks = reader.getBigInteger().intValue();
        byte[] iv = null;
        if (reader.hasMorePrimitives()) {
            iv = reader.getBigInteger().toByteArray();
        }
        this.cipherSpec = new BlockCipherParameterSpec(iv, bs, ks);
        System.out.println(this.cipherSpec);
    }

    protected AlgorithmParameterSpec engineGetParameterSpec(Class c) throws InvalidParameterSpecException {
        if (c.isInstance(this.cipherSpec)) {
            return this.cipherSpec;
        }
        throw new InvalidParameterSpecException();
    }

    protected String engineToString() {
        return this.cipherSpec.toString();
    }
}

