/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl.srp;

import gnu.crypto.assembly.Direction;
import gnu.crypto.cipher.CipherFactory;
import gnu.crypto.cipher.IBlockCipher;
import gnu.crypto.hash.MD5;
import gnu.crypto.key.IKeyAgreementParty;
import gnu.crypto.key.IncomingMessage;
import gnu.crypto.key.KeyAgreementException;
import gnu.crypto.key.KeyAgreementFactory;
import gnu.crypto.key.OutgoingMessage;
import gnu.crypto.sasl.ClientMechanism;
import gnu.crypto.sasl.IllegalMechanismStateException;
import gnu.crypto.sasl.InputBuffer;
import gnu.crypto.sasl.IntegrityException;
import gnu.crypto.sasl.OutputBuffer;
import gnu.crypto.sasl.srp.CALG;
import gnu.crypto.sasl.srp.ClientStore;
import gnu.crypto.sasl.srp.IALG;
import gnu.crypto.sasl.srp.KDF;
import gnu.crypto.sasl.srp.SRP;
import gnu.crypto.sasl.srp.SRPRegistry;
import gnu.crypto.sasl.srp.SecurityContext;
import gnu.crypto.util.PRNG;
import gnu.crypto.util.Util;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthenticationException;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public class SRPClient
extends ClientMechanism
implements SaslClient {
    private static final String NAME = "SRPClient";
    private static final String INFO = " INFO";
    private static final String TRACE = "DEBUG";
    private static final boolean DEBUG = true;
    private static final int debuglevel = 3;
    private static final PrintWriter err = new PrintWriter(System.out, true);
    private String uid;
    private String U;
    BigInteger N;
    BigInteger g;
    BigInteger A;
    BigInteger B;
    private char[] password;
    private byte[] s;
    private byte[] cIV;
    private byte[] sIV;
    private byte[] M1;
    private byte[] M2;
    private byte[] cn;
    private byte[] sn;
    private SRP srp;
    private byte[] sid;
    private int ttl;
    private byte[] sCB;
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
    private IKeyAgreementParty clientHandler;

    private static final void debug(String level, Object obj) {
        err.println("[" + level + "] SRPClient: " + String.valueOf(obj));
    }

    protected void initMechanism() throws SaslException {
        MD5 md = new MD5();
        byte[] b = this.authorizationID.getBytes();
        md.update(b, 0, b.length);
        b = this.serverName.getBytes();
        md.update(b, 0, b.length);
        b = this.protocol.getBytes();
        md.update(b, 0, b.length);
        if (this.channelBinding.length > 0) {
            md.update(this.channelBinding, 0, this.channelBinding.length);
        }
        this.uid = Util.toBase64(md.digest());
        if (ClientStore.instance().isAlive(this.uid)) {
            SecurityContext ctx = ClientStore.instance().restoreSession(this.uid);
            this.srp = SRP.instance(ctx.getMdName());
            this.sid = ctx.getSID();
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
        } else {
            this.sid = new byte[0];
            this.ttl = 0;
            this.K = null;
            this.cIV = null;
            this.sIV = null;
            this.cn = null;
            this.sn = null;
        }
    }

    protected void resetMechanism() throws SaslException {
        this.password = null;
        this.M1 = null;
        this.K = null;
        this.cIV = null;
        this.sIV = null;
        this.outMac = null;
        this.inMac = null;
        this.outCipher = null;
        this.inCipher = null;
        this.sid = null;
        this.ttl = 0;
        this.cn = null;
        this.sn = null;
    }

    public boolean hasInitialResponse() {
        return true;
    }

    public byte[] evaluateChallenge(byte[] challenge) throws SaslException {
        switch (this.state) {
            case 0: {
                ++this.state;
                return this.sendIdentities();
            }
            case 1: {
                ++this.state;
                return this.sendPublicKey(challenge);
            }
            case 2: {
                if (this.complete) break;
                ++this.state;
                return this.receiveEvidence(challenge);
            }
        }
        throw new IllegalMechanismStateException("evaluateChallenge()");
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
                    result = new byte[len - macBytesCount];
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

    private final byte[] sendIdentities() throws SaslException {
        this.getUsernameAndPassword();
        if (this.sid.length != 0) {
            this.cn = new byte[16];
            PRNG.nextBytes(this.cn);
        } else {
            this.cn = new byte[0];
        }
        OutputBuffer frameOut = new OutputBuffer();
        try {
            frameOut.setText(this.U);
            frameOut.setText(this.authorizationID);
            frameOut.setEOS(this.sid);
            frameOut.setOS(this.cn);
            frameOut.setEOS(this.channelBinding);
        }
        catch (IOException x) {
            if (x instanceof SaslException) {
                throw (SaslException)x;
            }
            throw new AuthenticationException("sendIdentities()", x);
        }
        byte[] result = frameOut.encode();
        SRPClient.debug(INFO, "C: " + Util.dumpString(result));
        SRPClient.debug(INFO, "  U = " + this.U);
        SRPClient.debug(INFO, "  I = " + this.authorizationID);
        SRPClient.debug(INFO, "sid = " + new String(this.sid));
        SRPClient.debug(INFO, " cn = " + Util.dumpString(this.cn));
        SRPClient.debug(INFO, "cCB = " + Util.dumpString(this.channelBinding));
        return result;
    }

    private final byte[] sendPublicKey(byte[] input) throws SaslException {
        int ack;
        block17 : {
            InputBuffer frameIn = new InputBuffer(input);
            try {
                ack = (int)frameIn.getScalar(1);
                if (ack == 0) {
                    this.N = frameIn.getMPI();
                    this.g = frameIn.getMPI();
                    this.s = frameIn.getOS();
                    this.B = frameIn.getMPI();
                    this.L = frameIn.getText();
                    break block17;
                }
                if (ack == 255) {
                    this.sn = frameIn.getOS();
                    this.sCB = frameIn.getEOS();
                    break block17;
                }
                throw new SaslException("sendPublicKey(): Invalid scalar (" + ack + ") in server's request");
            }
            catch (IOException x) {
                if (x instanceof SaslException) {
                    throw (SaslException)x;
                }
                throw new SaslException("sendPublicKey()", x);
            }
        }
        if (ack == 0) {
            byte[] pBytes;
            this.o = this.createO(this.L.toLowerCase());
            try {
                pBytes = new String(this.password).getBytes("US-ASCII");
            }
            catch (UnsupportedEncodingException x) {
                throw new SaslException("sendPublicKey()", x);
            }
            HashMap<String, String> mapA = new HashMap<String, String>();
            mapA.put("gnu.crypto.srp6.ka.H", this.srp.getAlgorithm());
            mapA.put("gnu.crypto.srp6.ka.I", this.U);
            mapA.put("gnu.crypto.srp6.ka.p", (String)pBytes);
            try {
                this.clientHandler.init(mapA);
                this.clientHandler.processMessage(null);
            }
            catch (KeyAgreementException x) {
                throw new SaslException("sendPublicKey()", x);
            }
            try {
                OutgoingMessage out = new OutgoingMessage();
                out.writeMPI(this.N);
                out.writeMPI(this.g);
                out.writeMPI(new BigInteger(1, this.s));
                out.writeMPI(this.B);
                IncomingMessage in = new IncomingMessage(out.toByteArray());
                out = this.clientHandler.processMessage(in);
                in = new IncomingMessage(out.toByteArray());
                this.A = in.readMPI();
                this.K = this.clientHandler.getSharedSecret();
            }
            catch (KeyAgreementException x) {
                throw new SaslException("sendPublicKey()", x);
            }
            try {
                this.M1 = this.srp.generateM1(this.N, this.g, this.U, this.s, this.A, this.B, this.K, this.authorizationID, this.L, this.cn, this.channelBinding);
            }
            catch (UnsupportedEncodingException x) {
                throw new AuthenticationException("sendPublicKey()", x);
            }
            OutputBuffer frameOut = new OutputBuffer();
            try {
                frameOut.setMPI(this.A);
                frameOut.setOS(this.M1);
                frameOut.setText(this.o);
                frameOut.setOS(this.cIV);
            }
            catch (IOException x) {
                if (x instanceof SaslException) {
                    throw (SaslException)x;
                }
                throw new AuthenticationException("sendPublicKey()", x);
            }
            byte[] result = frameOut.encode();
            SRPClient.debug(INFO, "New session, or session re-use rejected...");
            SRPClient.debug(INFO, "C: " + Util.dumpString(result));
            SRPClient.debug(INFO, "  A = 0x" + this.A.toString(16));
            SRPClient.debug(INFO, " M1 = " + Util.dumpString(this.M1));
            SRPClient.debug(INFO, "  o = " + this.o);
            SRPClient.debug(INFO, "cIV = " + Util.dumpString(this.cIV));
            return result;
        }
        this.setupSecurityServices(true);
        SRPClient.debug(INFO, "Session re-use accepted...");
        return null;
    }

    private final byte[] receiveEvidence(byte[] input) throws SaslException {
        byte[] expected;
        InputBuffer frameIn = new InputBuffer(input);
        try {
            this.M2 = frameIn.getOS();
            this.sIV = frameIn.getOS();
            this.sid = frameIn.getEOS();
            this.ttl = (int)frameIn.getScalar(4);
            this.sCB = frameIn.getEOS();
        }
        catch (IOException x) {
            if (x instanceof SaslException) {
                throw (SaslException)x;
            }
            throw new AuthenticationException("receiveEvidence()", x);
        }
        try {
            expected = this.srp.generateM2(this.A, this.M1, this.K, this.U, this.authorizationID, this.o, this.sid, this.ttl, this.cIV, this.sIV, this.sCB);
        }
        catch (UnsupportedEncodingException x) {
            throw new AuthenticationException("receiveEvidence()", x);
        }
        if (!Arrays.equals(this.M2, expected)) {
            throw new AuthenticationException("M2 mismatch");
        }
        this.setupSecurityServices(false);
        return null;
    }

    private final void getUsernameAndPassword() throws AuthenticationException {
        try {
            if (!this.properties.containsKey("gnu.crypto.sasl.username") && !this.properties.containsKey("gnu.crypto.sasl.password")) {
                String defaultName = System.getProperty("user.name");
                NameCallback nameCB = defaultName == null ? new NameCallback("username: ") : new NameCallback("username: ", defaultName);
                PasswordCallback pwdCB = new PasswordCallback("password: ", false);
                this.handler.handle(new Callback[]{nameCB, pwdCB});
                this.U = nameCB.getName();
                this.password = pwdCB.getPassword();
            } else {
                if (this.properties.containsKey("gnu.crypto.sasl.username")) {
                    this.U = (String)this.properties.get("gnu.crypto.sasl.username");
                } else {
                    String defaultName = System.getProperty("user.name");
                    NameCallback nameCB = defaultName == null ? new NameCallback("username: ") : new NameCallback("username: ", defaultName);
                    this.handler.handle(new Callback[]{nameCB});
                    this.U = nameCB.getName();
                }
                if (this.properties.containsKey("gnu.crypto.sasl.password")) {
                    this.password = ((String)this.properties.get("gnu.crypto.sasl.password")).toCharArray();
                } else {
                    PasswordCallback pwdCB = new PasswordCallback("password: ", false);
                    this.handler.handle(new Callback[]{pwdCB});
                    this.password = pwdCB.getPassword();
                }
            }
            if (this.U == null) {
                throw new AuthenticationException("null username supplied");
            }
            if (this.password == null) {
                throw new AuthenticationException("null password supplied");
            }
        }
        catch (UnsupportedCallbackException x) {
            throw new AuthenticationException("getUsernameAndPassword()", x);
        }
        catch (IOException x) {
            throw new AuthenticationException("getUsernameAndPassword()", x);
        }
    }

    private final String createO(String aol) throws AuthenticationException {
        boolean confidentiality;
        boolean replaydetectionAvailable = false;
        boolean integrityAvailable = false;
        boolean confidentialityAvailable = false;
        String mandatory = "replay_detection";
        String mdName = SRPRegistry.SRP_DEFAULT_DIGEST_NAME;
        StringTokenizer st = new StringTokenizer(aol, ",");
        block2 : while (st.hasMoreTokens()) {
            int i;
            String option = st.nextToken();
            if (option.startsWith("mda=")) {
                option = option.substring(option.indexOf(61) + 1);
                i = 0;
                while (i < SRPRegistry.INTEGRITY_ALGORITHMS.length) {
                    if (SRPRegistry.SRP_ALGORITHMS[i].equals(option)) {
                        mdName = option;
                        continue block2;
                    }
                    ++i;
                }
                continue;
            }
            if (option.equals("replay_detection")) {
                replaydetectionAvailable = true;
                continue;
            }
            if (option.startsWith("integrity=")) {
                option = option.substring(option.indexOf(61) + 1);
                i = 0;
                while (i < SRPRegistry.INTEGRITY_ALGORITHMS.length) {
                    if (SRPRegistry.INTEGRITY_ALGORITHMS[i].equals(option)) {
                        this.chosenIntegrityAlgorithm = option;
                        integrityAvailable = true;
                        continue block2;
                    }
                    ++i;
                }
                continue;
            }
            if (option.startsWith("confidentiality=")) {
                option = option.substring(option.indexOf(61) + 1);
                i = 0;
                while (i < SRPRegistry.CONFIDENTIALITY_ALGORITHMS.length) {
                    if (SRPRegistry.CONFIDENTIALITY_ALGORITHMS[i].equals(option)) {
                        this.chosenConfidentialityAlgorithm = option;
                        confidentialityAvailable = true;
                        continue block2;
                    }
                    ++i;
                }
                continue;
            }
            if (option.startsWith("mandatory=")) {
                mandatory = option.substring(option.indexOf(61) + 1);
                continue;
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
        boolean bl = false;
        if (replaydetectionAvailable && Boolean.valueOf((String)this.properties.get("gnu.crypto.sasl.srp.replay.detection")).booleanValue()) {
            bl = true;
        }
        this.replayDetection = bl;
        boolean bl2 = false;
        if (integrityAvailable && Boolean.valueOf((String)this.properties.get("gnu.crypto.sasl.srp.integrity")).booleanValue()) {
            bl2 = true;
        }
        boolean integrity = bl2;
        boolean bl3 = false;
        if (confidentialityAvailable && Boolean.valueOf((String)this.properties.get("gnu.crypto.sasl.srp.confidentiality")).booleanValue()) {
            bl3 = confidentiality = true;
        }
        if ("replay_detection".equals(mandatory)) {
            this.replayDetection = true;
            integrity = true;
        } else if ("integrity".equals(mandatory)) {
            integrity = true;
        } else if ("confidentiality".equals(mandatory)) {
            confidentiality = true;
        }
        if (this.replayDetection && this.chosenIntegrityAlgorithm == null) {
            throw new AuthenticationException("Replay detection is required but no integrity protection algorithm was chosen");
        }
        if (integrity && this.chosenIntegrityAlgorithm == null) {
            throw new AuthenticationException("Integrity protection is required but no algorithm was chosen");
        }
        if (confidentiality && this.chosenConfidentialityAlgorithm == null) {
            throw new AuthenticationException("Confidentiality protection is required but no algorithm was chosen");
        }
        if (this.chosenConfidentialityAlgorithm == null) {
            this.cIV = new byte[0];
        } else {
            IBlockCipher cipher = CipherFactory.getInstance(this.chosenConfidentialityAlgorithm);
            if (cipher == null) {
                throw new AuthenticationException("createO()", new NoSuchAlgorithmException());
            }
            int blockSize = cipher.defaultBlockSize();
            this.cIV = new byte[blockSize];
            PRNG.nextBytes(this.cIV);
        }
        this.srp = SRP.instance(mdName);
        StringBuffer sb = new StringBuffer();
        sb.append("mda").append("=").append(mdName).append(",");
        if (this.replayDetection) {
            sb.append("replay_detection").append(",");
        }
        if (integrity) {
            sb.append("integrity").append("=").append(this.chosenIntegrityAlgorithm).append(",");
        }
        if (confidentiality) {
            sb.append("confidentiality").append("=").append(this.chosenConfidentialityAlgorithm).append(",");
        }
        String result = sb.append("maxbuffersize").append("=").append(2147483643).toString();
        return result;
    }

    private final void setupSecurityServices(boolean sessionReUse) throws SaslException {
        this.complete = true;
        if (!sessionReUse) {
            this.inCounter = 0;
            this.outCounter = 0;
            if (this.chosenConfidentialityAlgorithm != null) {
                SRPClient.debug(INFO, "Activating confidentiality protection filter");
                this.inCipher = CALG.getInstance(this.chosenConfidentialityAlgorithm);
                this.outCipher = CALG.getInstance(this.chosenConfidentialityAlgorithm);
            }
            if (this.chosenIntegrityAlgorithm != null) {
                SRPClient.debug(INFO, "Activating integrity protection filter");
                this.inMac = IALG.getInstance(this.chosenIntegrityAlgorithm);
                this.outMac = IALG.getInstance(this.chosenIntegrityAlgorithm);
            }
        } else {
            this.K = this.srp.generateKn(this.K, this.cn, this.sn);
        }
        KDF kdf = KDF.getInstance(this.K);
        if (this.inCipher != null) {
            this.inCipher.init(kdf, this.sIV, Direction.REVERSED);
            this.outCipher.init(kdf, this.cIV, Direction.FORWARD);
        }
        if (this.inMac != null) {
            this.inMac.init(kdf);
            this.outMac.init(kdf);
        }
        if (this.sid != null && this.sid.length != 0) {
            SRPClient.debug(INFO, "Updating security context for UID = " + this.uid);
            ClientStore.instance().cacheSession(this.uid, this.ttl, new SecurityContext(this.srp.getAlgorithm(), this.sid, this.K, this.cIV, this.sIV, this.replayDetection, this.inCounter, this.outCounter, this.inMac, this.outMac, this.inCipher, this.outCipher));
        }
    }

    private final /* synthetic */ void this() {
        this.rawSendSize = 2147483643;
        this.replayDetection = true;
        this.inCounter = 0;
        this.outCounter = 0;
        this.clientHandler = KeyAgreementFactory.getPartyAInstance("srp-sasl");
    }

    public SRPClient() {
        super("SRP");
        this.this();
    }
}

