/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.cipher;

import gnu.crypto.cipher.IBlockCipher;
import gnu.crypto.cipher.IBlockCipherSpi;
import java.io.PrintStream;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public abstract class BaseCipher
implements IBlockCipher,
IBlockCipherSpi {
    protected String name;
    protected int defaultBlockSize;
    protected int defaultKeySize;
    protected int currentBlockSize;
    protected transient Object currentKey;
    protected Object lock;

    public abstract Object clone();

    public String name() {
        StringBuffer sb = new StringBuffer(this.name).append('-');
        if (this.currentKey == null) {
            sb.append(String.valueOf(8 * this.defaultBlockSize));
        } else {
            sb.append(String.valueOf(8 * this.currentBlockSize));
        }
        return sb.toString();
    }

    public int defaultBlockSize() {
        return this.defaultBlockSize;
    }

    public int defaultKeySize() {
        return this.defaultKeySize;
    }

    /*
     * Exception decompiling
     */
    public void init(Map attributes) throws InvalidKeyException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // java.lang.IllegalStateException: Backjump on non jumping statement [] lbl7 : TryStatement: try { 0[TRYBLOCK]

        // org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.Cleaner$1.call(Cleaner.java:44)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.Cleaner$1.call(Cleaner.java:22)
        // org.benf.cfr.reader.util.graph.GraphVisitorDFS.process(GraphVisitorDFS.java:67)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.Cleaner.removeUnreachableCode(Cleaner.java:54)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.RemoveDeterministicJumps.apply(RemoveDeterministicJumps.java:35)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:497)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:196)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:141)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:95)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:372)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:867)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:768)
        // org.benf.cfr.reader.Main.doJar(Main.java:141)
        // org.benf.cfr.reader.Main.main(Main.java:242)
        throw new IllegalStateException("Decompilation failed");
    }

    public int currentBlockSize() {
        if (this.currentKey == null) {
            throw new IllegalStateException();
        }
        return this.currentBlockSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void reset() {
        Object object = this.lock;
        synchronized (object) {
            this.currentKey = null;
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void encryptBlock(byte[] in, int inOffset, byte[] out, int outOffset) throws IllegalStateException {
        Object object = this.lock;
        synchronized (object) {
            if (this.currentKey == null) {
                throw new IllegalStateException();
            }
            this.encrypt(in, inOffset, out, outOffset, this.currentKey, this.currentBlockSize);
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void decryptBlock(byte[] in, int inOffset, byte[] out, int outOffset) throws IllegalStateException {
        Object object = this.lock;
        synchronized (object) {
            if (this.currentKey == null) {
                throw new IllegalStateException();
            }
            this.decrypt(in, inOffset, out, outOffset, this.currentKey, this.currentBlockSize);
            return;
        }
    }

    public boolean selfTest() {
        Iterator kit = this.keySizes();
        while (kit.hasNext()) {
            int ks = (Integer)kit.next();
            Iterator bit = this.blockSizes();
            while (bit.hasNext()) {
                if (this.testSymmetry(ks, (Integer)bit.next())) continue;
                return false;
            }
        }
        return true;
    }

    private final boolean testSymmetry(int ks, int bs) {
        try {
            byte[] kb = new byte[ks];
            byte[] pt = new byte[bs];
            byte[] ct = new byte[bs];
            byte[] cpt = new byte[bs];
            int i = 0;
            while (i < ks) {
                kb[i] = (byte)i;
                ++i;
            }
            i = 0;
            while (i < bs) {
                pt[i] = (byte)i;
                ++i;
            }
            Object k = this.makeKey(kb, bs);
            this.encrypt(pt, 0, ct, 0, k, bs);
            this.decrypt(ct, 0, cpt, 0, k, bs);
            return Arrays.equals(pt, cpt);
        }
        catch (Exception x) {
            x.printStackTrace(System.err);
            return false;
        }
    }

    protected boolean testKat(byte[] kb, byte[] ct) {
        return this.testKat(kb, ct, new byte[ct.length]);
    }

    protected boolean testKat(byte[] kb, byte[] ct, byte[] pt) {
        try {
            int bs = pt.length;
            byte[] t = new byte[bs];
            Object k = this.makeKey(kb, bs);
            this.encrypt(pt, 0, t, 0, k, bs);
            if (!Arrays.equals(t, ct)) {
                return false;
            }
            this.decrypt(t, 0, t, 0, k, bs);
            return Arrays.equals(t, pt);
        }
        catch (Exception x) {
            x.printStackTrace(System.err);
            return false;
        }
    }

    private final /* synthetic */ void this() {
        this.lock = new Object();
    }

    protected BaseCipher(String name, int defaultBlockSize, int defaultKeySize) {
        this.this();
        this.name = name;
        this.defaultBlockSize = defaultBlockSize;
        this.defaultKeySize = defaultKeySize;
    }
}

