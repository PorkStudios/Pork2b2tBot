/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.srp6;

import gnu.crypto.hash.IMessageDigest;
import gnu.crypto.key.IncomingMessage;
import gnu.crypto.key.KeyAgreementException;
import gnu.crypto.key.OutgoingMessage;
import gnu.crypto.key.srp6.SRP6TLSClient;
import gnu.crypto.sasl.srp.SRP;
import gnu.crypto.util.Util;
import java.math.BigInteger;

public class SRP6SaslClient
extends SRP6TLSClient {
    protected OutgoingMessage computeSharedSecret(IncomingMessage in) throws KeyAgreementException {
        OutgoingMessage result = super.computeSharedSecret(in);
        byte[] sBytes = Util.trim(this.K);
        IMessageDigest hash = this.srp.newDigest();
        hash.update(sBytes, 0, sBytes.length);
        this.K = new BigInteger(1, hash.digest());
        return result;
    }
}

