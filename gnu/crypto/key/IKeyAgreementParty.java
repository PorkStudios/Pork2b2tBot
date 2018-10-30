/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key;

import gnu.crypto.key.IncomingMessage;
import gnu.crypto.key.KeyAgreementException;
import gnu.crypto.key.OutgoingMessage;
import java.util.Map;

public interface IKeyAgreementParty {
    public String name();

    public void init(Map var1) throws KeyAgreementException;

    public OutgoingMessage processMessage(IncomingMessage var1) throws KeyAgreementException;

    public boolean isComplete();

    public byte[] getSharedSecret() throws KeyAgreementException;

    public void reset();
}

