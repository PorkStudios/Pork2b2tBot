/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl.srp;

import gnu.crypto.assembly.Direction;
import gnu.crypto.cipher.CipherFactory;
import gnu.crypto.cipher.IBlockCipher;
import gnu.crypto.key.IKeyAgreementParty;
import gnu.crypto.key.IncomingMessage;
import gnu.crypto.key.KeyAgreementException;
import gnu.crypto.key.KeyAgreementFactory;
import gnu.crypto.key.OutgoingMessage;
import gnu.crypto.sasl.IAuthInfoProvider;
import gnu.crypto.sasl.IllegalMechanismStateException;
import gnu.crypto.sasl.InputBuffer;
import gnu.crypto.sasl.IntegrityException;
import gnu.crypto.sasl.OutputBuffer;
import gnu.crypto.sasl.ServerMechanism;
import gnu.crypto.sasl.srp.CALG;
import gnu.crypto.sasl.srp.IALG;
import gnu.crypto.sasl.srp.KDF;
import gnu.crypto.sasl.srp.SRP;
import gnu.crypto.sasl.srp.SRPRegistry;
import gnu.crypto.sasl.srp.SecurityContext;
import gnu.crypto.sasl.srp.ServerStore;
import gnu.crypto.util.PRNG;
import gnu.crypto.util.Util;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import javax.security.sasl.AuthenticationException;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public class SRPServer
extends ServerMechanism
implements SaslServer {
    private static final String NAME = "SRPServer";
    private static final String WARN = " WARN";
    private static final String INFO = " INFO";
    private static final String TRACE = "DEBUG";
    private static final boolean DEBUG = true;
    private static final int debuglevel = 3;
    private static final PrintWriter err = new PrintWriter(System.out, true);
    private String U;
    private BigInteger N;
    private BigInteger g;
    private BigInteger A;
    private BigInteger B;
    private byte[] s;
    private byte[] cIV;
    private byte[] sIV;
    private byte[] cn;
    private byte[] sn;
    private SRP srp;
    private byte[] sid;
    private int ttl;
    private byte[] cCB;
    private String mandatory;
    private String L;
    private String o;
    private String chosenIntegrityAlgorithm;
    private String chosenConfidentialityAlgorithm;
    private int rawSendSize;
    private byte[] K;
    private boolean replayDetection;
    private int inCounter;
    private int outCounter;
    private IALG inMac;
    private IALG outMac;
    private CALG inCipher;
    private CALG outCipher;
    private IKeyAgreementParty serverHandler;

    private static final void debug(String level, Object obj) {
        err.println("[" + level + "] SRPServer: " + String.valueOf(obj));
    }

    protected void initMechanism() throws SaslException {
        String mda = (String)this.properties.get("gnu.crypto.sasl.srp.hash");
        this.srp = SRP.instance(mda == null ? SRPRegistry.SRP_DEFAULT_DIGEST_NAME : mda);
    }

    protected void resetMechanism() throws SaslException {
        this.s = null;
        this.B = null;
        this.A = null;
        this.K = null;
        this.outMac = null;
        this.inMac = null;
        this.outCipher = null;
        this.inCipher = null;
        this.sid = null;
    }

    public byte[] evaluateResponse(byte[] response) throws SaslException {
        switch (this.state) {
            case 0: {
                if (response == null) {
                    return null;
                }
                ++this.state;
                return this.sendProtocolElements(response);
            }
            case 1: {
                if (this.complete) break;
                ++this.state;
                return this.sendEvidence(response);
            }
        }
        throw new IllegalMechanismStateException("evaluateResponse()");
    }

    protected byte[] engineUnwrap(byte[] incoming, int offset, int len) throws SaslException {
        byte[] result;
        if (this.inMac == null && this.inCipher == null) {
            throw new IllegalStateException("connection is not protected");
        }
        try {
            if (this.inMac != null) {
                byte[] computed_mac;
                int macBytesCount = this.inMac.length();
                int payloadLength = len - macBytesCount;
                byte[] received_mac = new byte[macBytesCount];
                System.arraycopy(incoming, offset + payloadLength, received_mac, 0, macBytesCount);
                this.inMac.update(incoming, offset, payloadLength);
                if (this.replayDetection) {
                    ++this.inCounter;
                    this.inMac.update(new byte[]{(byte)(this.inCounter >>> 24), (byte)(this.inCounter >>> 16), (byte)(this.inCounter >>> 8), (byte)this.inCounter});
                }
                if (!Arrays.equals(received_mac, computed_mac = this.inMac.doFinal())) {
                    throw new IntegrityException("engineUnwrap()");
                }
                if (this.inCipher != null) {
                    result = this.inCipher.doFinal(incoming, offset, payloadLength);
                } else {
                    result = new byte[payloadLength];
                    System.arraycopy(incoming, offset, result, 0, result.length);
                }
            } else {
                result = this.inCipher.doFinal(incoming, offset, len);
            }
        }
        catch (IOException x) {
            if (x instanceof SaslException) {
                throw (SaslException)x;
            }
            throw new SaslException("engineUnwrap()", x);
        }
        return result;
    }

    protected byte[] engineWrap(byte[] outgoing, int offset, int len) throws SaslException {
        byte[] result;
        if (this.outMac == null && this.outCipher == null) {
            throw new IllegalStateException("connection is not protected");
        }
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            if (this.outCipher != null) {
                result = this.outCipher.doFinal(outgoing, offset, len);
                out.write(result);
                if (this.outMac != null) {
                    this.outMac.update(result);
                    if (this.replayDetection) {
                        ++this.outCounter;
                        this.outMac.update(new byte[]{(byte)(this.outCounter >>> 24), (byte)(this.outCounter >>> 16), (byte)(this.outCounter >>> 8), (byte)this.outCounter});
                    }
                    byte[] C = this.outMac.doFinal();
                    out.write(C);
                }
            } else {
                out.write(outgoing, offset, len);
                this.outMac.update(outgoing, offset, len);
                if (this.replayDetection) {
                    ++this.outCounter;
                    this.outMac.update(new byte[]{(byte)(this.outCounter >>> 24), (byte)(this.outCounter >>> 16), (byte)(this.outCounter >>> 8), (byte)this.outCounter});
                }
                byte[] C = this.outMac.doFinal();
                out.write(C);
            }
            result = out.toByteArray();
        }
        catch (IOException x) {
            if (x instanceof SaslException) {
                throw (SaslException)x;
            }
            throw new SaslException("engineWrap()", x);
        }
        return result;
    }

    protected String getNegotiatedQOP() {
        if (this.inMac != null) {
            if (this.inCipher != null) {
                return "auth-conf";
            }
            return "auth-int";
        }
        return "auth";
    }

    protected String getNegotiatedStrength() {
        if (this.inMac != null) {
            if (this.inCipher != null) {
                return "high";
            }
            return "medium";
        }
        return "low";
    }

    protected String getNegotiatedRawSendSize() {
        return String.valueOf(this.rawSendSize);
    }

    protected String getReuse() {
        return "true";
    }

    private final byte[] sendProtocolElements(byte[] input) throws SaslException {
        InputBuffer frameIn = new InputBuffer(input);
        try {
            this.U = frameIn.getText();
            this.authorizationID = frameIn.getText();
            this.sid = frameIn.getEOS();
            this.cn = frameIn.getOS();
            this.cCB = frameIn.getEOS();
        }
        catch (IOException x) {
            if (x instanceof SaslException) {
                throw (SaslException)x;
            }
            throw new AuthenticationException("sendProtocolElements()", x);
        }
        if (ServerStore.instance().isAlive(this.sid)) {
            SecurityContext ctx = ServerStore.instance().restoreSession(this.sid);
            this.srp = SRP.instance(ctx.getMdName());
            this.K = ctx.getK();
            this.cIV = ctx.getClientIV();
            this.sIV = ctx.getServerIV();
            this.replayDetection = ctx.hasReplayDetection();
            this.inCounter = ctx.getInCounter();
            this.outCounter = ctx.getOutCounter();
            this.inMac = ctx.getInMac();
            this.outMac = ctx.getOutMac();
            this.inCipher = ctx.getInCipher();
            this.outCipher = ctx.getOutCipher();
            if (this.sn == null || this.sn.length != 16) {
                this.sn = new byte[16];
            }
            PRNG.nextBytes(this.sn);
            this.setupSecurityServices(false);
            OutputBuffer frameOut = new OutputBuffer();
            try {
                frameOut.setScalar(1, 255);
                frameOut.setOS(this.sn);
                frameOut.setEOS(this.channelBinding);
            }
            catch (IOException x) {
                if (x instanceof SaslException) {
                    throw (SaslException)x;
                }
                throw new AuthenticationException("sendProtocolElements()", x);
            }
            byte[] result = frameOut.encode();
            SRPServer.debug(INFO, "Old session...");
            SRPServer.debug(INFO, "S: " + Util.dumpString(result));
            SRPServer.debug(INFO, "  sn = " + Util.dumpString(this.sn));
            SRPServer.debug(INFO, " sCB = " + Util.dumpString(this.channelBinding));
            return result;
        }
        this.authenticator.activate(this.properties);
        HashMap<String, Object> mapB = new HashMap<String, Object>();
        mapB.put("gnu.crypto.srp6.ka.H", this.srp.getAlgorithm());
        mapB.put("gnu.crypto.srp6.ka.password.db", this.authenticator);
        try {
            this.serverHandler.init(mapB);
            OutgoingMessage out = new OutgoingMessage();
            out.writeString(this.U);
            IncomingMessage in = new IncomingMessage(out.toByteArray());
            out = this.serverHandler.processMessage(in);
            in = new IncomingMessage(out.toByteArray());
            this.N = in.readMPI();
            this.g = in.readMPI();
            this.s = in.readMPI().toByteArray();
            this.B = in.readMPI();
        }
        catch (KeyAgreementException x) {
            throw new SaslException("sendProtocolElements()", x);
        }
        this.L = this.createL();
        OutputBuffer frameOut = new OutputBuffer();
        try {
            frameOut.setScalar(1, 0);
            frameOut.setMPI(this.N);
            frameOut.setMPI(this.g);
            frameOut.setOS(this.s);
            frameOut.setMPI(this.B);
            frameOut.setText(this.L);
        }
        catch (IOException x) {
            if (x instanceof SaslException) {
                throw (SaslException)x;
            }
            throw new AuthenticationException("sendProtocolElements()", x);
        }
        byte[] result = frameOut.encode();
        SRPServer.debug(INFO, "New session...");
        SRPServer.debug(INFO, "S: " + Util.dumpString(result));
        SRPServer.debug(INFO, "   N = 0x" + this.N.toString(16));
        SRPServer.debug(INFO, "   g = 0x" + this.g.toString(16));
        SRPServer.debug(INFO, "   s = " + Util.dumpString(this.s));
        SRPServer.debug(INFO, "   B = 0x" + this.B.toString(16));
        SRPServer.debug(INFO, "   L = " + this.L);
        return result;
    }

    private final byte[] sendEvidence(byte[] input) throws SaslException {
        byte[] M2;
        byte[] expected;
        byte[] M1;
        InputBuffer frameIn = new InputBuffer(input);
        try {
            this.A = frameIn.getMPI();
            M1 = frameIn.getOS();
            this.o = frameIn.getText();
            this.cIV = frameIn.getOS();
        }
        catch (IOException x) {
            if (x instanceof SaslException) {
                throw (SaslException)x;
            }
            throw new AuthenticationException("sendEvidence()", x);
        }
        this.parseO(this.o);
        try {
            OutgoingMessage out = new OutgoingMessage();
            out.writeMPI(this.A);
            IncomingMessage in = new IncomingMessage(out.toByteArray());
            this.serverHandler.processMessage(in);
            this.K = this.serverHandler.getSharedSecret();
        }
        catch (KeyAgreementException x) {
            throw new SaslException("sendEvidence()", x);
        }
        try {
            expected = this.srp.generateM1(this.N, this.g, this.U, this.s, this.A, this.B, this.K, this.authorizationID, this.L, this.cn, this.cCB);
        }
        catch (UnsupportedEncodingException x) {
            throw new AuthenticationException("sendEvidence()", x);
        }
        if (!Arrays.equals(M1, expected)) {
            throw new AuthenticationException("M1 mismatch");
        }
        this.setupSecurityServices(true);
        try {
            M2 = this.srp.generateM2(this.A, M1, this.K, this.U, this.authorizationID, this.o, this.sid, this.ttl, this.cIV, this.sIV, this.channelBinding);
        }
        catch (UnsupportedEncodingException x) {
            throw new AuthenticationException("sendEvidence()", x);
        }
        OutputBuffer frameOut = new OutputBuffer();
        try {
            frameOut.setOS(M2);
            frameOut.setOS(this.sIV);
            frameOut.setEOS(this.sid);
            frameOut.setScalar(4, this.ttl);
            frameOut.setEOS(this.channelBinding);
        }
        catch (IOException x) {
            if (x instanceof SaslException) {
                throw (SaslException)x;
            }
            throw new AuthenticationException("sendEvidence()", x);
        }
        byte[] result = frameOut.encode();
        SRPServer.debug(INFO, "S: " + Util.dumpString(result));
        SRPServer.debug(INFO, "  M2 = " + Util.dumpString(M2));
        SRPServer.debug(INFO, " sIV = " + Util.dumpString(this.sIV));
        SRPServer.debug(INFO, " sid = " + new String(this.sid));
        SRPServer.debug(INFO, " ttl = " + this.ttl);
        SRPServer.debug(INFO, " sCB = " + Util.dumpString(this.channelBinding));
        return result;
    }

    private final String createL() {
        int i;
        String s = (String)this.properties.get("gnu.crypto.sasl.srp.mandatory");
        if (s == null) {
            s = "replay_detection";
        }
        if (!("none".equals(s) || "replay_detection".equals(s) || "integrity".equals(s) || "confidentiality".equals(s))) {
            s = "replay_detection";
        }
        this.mandatory = s;
        s = (String)this.properties.get("gnu.crypto.sasl.srp.confidentiality");
        boolean bl = false;
        if (s != null) {
            bl = Boolean.valueOf(s);
        }
        boolean confidentiality = bl;
        s = (String)this.properties.get("gnu.crypto.sasl.srp.integrity");
        boolean bl2 = true;
        if (s != null) {
            bl2 = Boolean.valueOf(s);
        }
        boolean integrity = bl2;
        s = (String)this.properties.get("gnu.crypto.sasl.srp.replay.detection");
        boolean bl3 = true;
        if (s != null) {
            bl3 = Boolean.valueOf(s);
        }
        boolean replayDetection = bl3;
        StringBuffer sb = new StringBuffer();
        sb.append("mda").append("=").append(this.srp.getAlgorithm()).append(",");
        if (!"none".equals(this.mandatory)) {
            sb.append("mandatory").append("=").append(this.mandatory).append(",");
        }
        if (replayDetection) {
            sb.append("replay_detection").append(",");
            integrity = true;
        }
        if (integrity) {
            i = 0;
            while (i < SRPRegistry.INTEGRITY_ALGORITHMS.length) {
                sb.append("integrity").append("=").append(SRPRegistry.INTEGRITY_ALGORITHMS[i]).append(",");
                ++i;
            }
        }
        if (confidentiality) {
            i = 0;
            while (i < SRPRegistry.CONFIDENTIALITY_ALGORITHMS.length) {
                IBlockCipher cipher = CipherFactory.getInstance(SRPRegistry.CONFIDENTIALITY_ALGORITHMS[i]);
                if (cipher != null) {
                    sb.append("confidentiality").append("=").append(SRPRegistry.CONFIDENTIALITY_ALGORITHMS[i]).append(",");
                }
                ++i;
            }
        }
        String result = sb.append("maxbuffersize").append("=").append(2147483643).toString();
        return result;
    }

    private final void parseO(String o) throws AuthenticationException {
        this.replayDetection = false;
        boolean integrity = false;
        boolean confidentiality = false;
        StringTokenizer st = new StringTokenizer(o.toLowerCase(), ",");
        while (st.hasMoreTokens()) {
            int i;
            String option = st.nextToken();
            if (option.equals("replay_detection")) {
                this.replayDetection = true;
                continue;
            }
            if (option.startsWith("integrity=")) {
                if (integrity) {
                    throw new AuthenticationException("Only one integrity algorithm may be chosen");
                }
                option = option.substring(option.indexOf(61) + 1);
                i = 0;
                while (i < SRPRegistry.INTEGRITY_ALGORITHMS.length) {
                    if (SRPRegistry.INTEGRITY_ALGORITHMS[i].equals(option)) {
                        this.chosenIntegrityAlgorithm = option;
                        integrity = true;
                        break;
                    }
                    ++i;
                }
                if (integrity) continue;
                throw new AuthenticationException("Unknown integrity algorithm: " + option);
            }
            if (option.startsWith("confidentiality=")) {
                if (confidentiality) {
                    throw new AuthenticationException("Only one confidentiality algorithm may be chosen");
                }
                option = option.substring(option.indexOf(61) + 1);
                i = 0;
                while (i < SRPRegistry.CONFIDENTIALITY_ALGORITHMS.length) {
                    if (SRPRegistry.CONFIDENTIALITY_ALGORITHMS[i].equals(option)) {
                        this.chosenConfidentialityAlgorithm = option;
                        confidentiality = true;
                        break;
                    }
                    ++i;
                }
                if (confidentiality) continue;
                throw new AuthenticationException("Unknown confidentiality algorithm: " + option);
            }
            if (!option.startsWith("maxbuffersize=")) continue;
            String maxBufferSize = option.substring(option.indexOf(61) + 1);
            try {
                this.rawSendSize = Integer.parseInt(maxBufferSize);
                if (this.rawSendSize <= 2147483643 && this.rawSendSize >= 1) continue;
                throw new AuthenticationException("Illegal value for 'maxbuffersize' option");
            }
            catch (NumberFormatException x) {
                throw new AuthenticationException("maxbuffersize=" + String.valueOf(maxBufferSize), x);
            }
        }
        if (this.replayDetection && !integrity) {
            throw new AuthenticationException("Missing integrity protection algorithm but replay detection is chosen");
        }
        if (this.mandatory.equals("replay_detection") && !this.replayDetection) {
            throw new AuthenticationException("Replay detection is mandatory but was not chosen");
        }
        if (this.mandatory.equals("integrity") && !integrity) {
            throw new AuthenticationException("Integrity protection is mandatory but was not chosen");
        }
        if (this.mandatory.equals("confidentiality") && !confidentiality) {
            throw new AuthenticationException("Confidentiality is mandatory but was not chosen");
        }
        int blockSize = 0;
        if (this.chosenConfidentialityAlgorithm != null) {
            IBlockCipher cipher = CipherFactory.getInstance(this.chosenConfidentialityAlgorithm);
            if (cipher != null) {
                blockSize = cipher.defaultBlockSize();
            } else {
                throw new AuthenticationException("Confidentiality algorithm (" + this.chosenConfidentialityAlgorithm + ") not available");
            }
        }
        this.sIV = new byte[blockSize];
        if (blockSize > 0) {
            PRNG.nextBytes(this.sIV);
        }
    }

    private final void setupSecurityServices(boolean newSession) throws SaslException {
        this.complete = true;
        if (newSession) {
            this.inCounter = 0;
            this.outCounter = 0;
            if (this.chosenConfidentialityAlgorithm != null) {
                SRPServer.debug(INFO, "Activating confidentiality protection filter");
                this.inCipher = CALG.getInstance(this.chosenConfidentialityAlgorithm);
                this.outCipher = CALG.getInstance(this.chosenConfidentialityAlgorithm);
            }
            if (this.chosenIntegrityAlgorithm != null) {
                SRPServer.debug(INFO, "Activating integrity protection filter");
                this.inMac = IALG.getInstance(this.chosenIntegrityAlgorithm);
                this.outMac = IALG.getInstance(this.chosenIntegrityAlgorithm);
            }
            this.sid = this.inMac != null ? ServerStore.getNewSessionID() : new byte[]{};
        } else {
            this.K = this.srp.generateKn(this.K, this.cn, this.sn);
        }
        KDF kdf = KDF.getInstance(this.K);
        if (this.inCipher != null) {
            this.outCipher.init(kdf, this.sIV, Direction.FORWARD);
            this.inCipher.init(kdf, this.cIV, Direction.REVERSED);
        }
        if (this.inMac != null) {
            this.outMac.init(kdf);
            this.inMac.init(kdf);
        }
        if (this.sid != null && this.sid.length != 0) {
            SRPServer.debug(INFO, "Updating security context for sid = " + new String(this.sid));
            ServerStore.instance().cacheSession(this.ttl, new SecurityContext(this.srp.getAlgorithm(), this.sid, this.K, this.cIV, this.sIV, this.replayDetection, this.inCounter, this.outCounter, this.inMac, this.outMac, this.inCipher, this.outCipher));
        }
    }

    private final /* synthetic */ void this() {
        this.U = null;
        this.ttl = 360;
        this.L = null;
        this.rawSendSize = 2147483643;
        this.replayDetection = true;
        this.inCounter = 0;
        this.outCounter = 0;
        this.serverHandler = KeyAgreementFactory.getPartyBInstance("srp-sasl");
    }

    public SRPServer() {
        super("SRP");
        this.this();
    }
}

