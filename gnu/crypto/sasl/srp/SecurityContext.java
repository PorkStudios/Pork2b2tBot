/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl.srp;

import gnu.crypto.sasl.srp.CALG;
import gnu.crypto.sasl.srp.IALG;

class SecurityContext {
    private String mdName;
    private byte[] sid;
    private byte[] K;
    private byte[] cIV;
    private byte[] sIV;
    private boolean replayDetection;
    private int inCounter;
    private int outCounter;
    private IALG inMac;
    private IALG outMac;
    private CALG inCipher;
    private CALG outCipher;

    String getMdName() {
        return this.mdName;
    }

    byte[] getSID() {
        return this.sid;
    }

    byte[] getK() {
        return this.K;
    }

    byte[] getClientIV() {
        return this.cIV;
    }

    byte[] getServerIV() {
        return this.sIV;
    }

    boolean hasReplayDetection() {
        return this.replayDetection;
    }

    int getInCounter() {
        return this.inCounter;
    }

    int getOutCounter() {
        return this.outCounter;
    }

    IALG getInMac() {
        return this.inMac;
    }

    IALG getOutMac() {
        return this.outMac;
    }

    CALG getInCipher() {
        return this.inCipher;
    }

    CALG getOutCipher() {
        return this.outCipher;
    }

    SecurityContext(String mdName, byte[] sid, byte[] K, byte[] cIV, byte[] sIV, boolean replayDetection, int inCounter, int outCounter, IALG inMac, IALG outMac, CALG inCipher, CALG outCipher) {
        this.mdName = mdName;
        this.sid = sid;
        this.K = K;
        this.cIV = cIV;
        this.sIV = sIV;
        this.replayDetection = replayDetection;
        this.inCounter = inCounter;
        this.outCounter = outCounter;
        this.inMac = inMac;
        this.outMac = outMac;
        this.inCipher = inCipher;
        this.outCipher = outCipher;
    }
}

