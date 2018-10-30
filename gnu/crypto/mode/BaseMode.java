/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.mode;

import gnu.crypto.cipher.IBlockCipher;
import gnu.crypto.mode.IMode;
import java.io.PrintStream;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public abstract class BaseMode
implements IMode {
    protected String name;
    protected int state;
    protected IBlockCipher cipher;
    protected int cipherBlockSize;
    protected int modeBlockSize;
    protected byte[] iv;
    protected Object lock;

    /*
     * Exception decompiling
     */
    public void update(byte[] in, int inOffset, byte[] out, int outOffset) throws IllegalStateException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: First case is not immediately after switch.
        // org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.examineSwitchContiguity(SwitchReplacer.java:366)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.rebuildSwitches(SwitchReplacer.java:334)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:517)
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

    public String name() {
        return this.name + '(' + this.cipher.name() + ')';
    }

    public int defaultBlockSize() {
        return this.cipherBlockSize;
    }

    public int defaultKeySize() {
        return this.cipher.defaultKeySize();
    }

    public Iterator blockSizes() {
        ArrayList<Integer> al = new ArrayList<Integer>();
        al.add(new Integer(this.cipherBlockSize));
        return Collections.unmodifiableList(al).iterator();
    }

    public Iterator keySizes() {
        return this.cipher.keySizes();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public void init(Map attributes) throws InvalidKeyException, IllegalStateException {
        var2_2 = this.lock;
        // MONITORENTER : var2_2
        if (this.state != -1) {
            throw new IllegalStateException();
        }
        want = (Integer)attributes.get("gnu.crypto.mode.state");
        if (want != null) {
            switch (want) {
                case 1: {
                    this.state = 1;
                    ** break;
                }
                case 2: {
                    this.state = 2;
                    ** break;
                }
            }
            throw new IllegalArgumentException();
        }
lbl20: // 4 sources:
        this.modeBlockSize = (bs = (Integer)attributes.get("gnu.crypto.mode.block.size")) == null ? this.cipherBlockSize : bs;
        iv = (byte[])attributes.get("gnu.crypto.mode.iv");
        this.iv = iv != null ? (byte[])iv.clone() : new byte[this.modeBlockSize];
        this.cipher.init(attributes);
        this.setup();
        // MONITOREXIT : var2_2
    }

    public int currentBlockSize() {
        if (this.state == -1) {
            throw new IllegalStateException();
        }
        return this.modeBlockSize;
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
            this.state = -1;
            this.iv = null;
            this.cipher.reset();
            this.teardown();
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

    public abstract Object clone();

    public abstract void setup();

    public abstract void teardown();

    public abstract void encryptBlock(byte[] var1, int var2, byte[] var3, int var4);

    public abstract void decryptBlock(byte[] var1, int var2, byte[] var3, int var4);

    private final boolean testSymmetry(int ks, int bs) {
        try {
            IMode mode = (IMode)this.clone();
            byte[] iv = new byte[this.cipherBlockSize];
            byte[] k = new byte[ks];
            int i = 0;
            while (i < ks) {
                k[i] = (byte)i;
                ++i;
            }
            int blockCount = 5;
            int limit = blockCount * bs;
            byte[] pt = new byte[limit];
            i = 0;
            while (i < limit) {
                pt[i] = (byte)i;
                ++i;
            }
            byte[] ct = new byte[limit];
            byte[] cpt = new byte[limit];
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("gnu.crypto.cipher.key.material", k);
            map.put("gnu.crypto.cipher.block.size", new Integer(bs));
            map.put("gnu.crypto.mode.state", new Integer(1));
            map.put("gnu.crypto.mode.iv", iv);
            map.put("gnu.crypto.mode.block.size", new Integer(bs));
            mode.reset();
            mode.init(map);
            i = 0;
            while (i < blockCount) {
                mode.update(pt, i * bs, ct, i * bs);
                ++i;
            }
            mode.reset();
            map.put("gnu.crypto.mode.state", new Integer(2));
            mode.init(map);
            i = 0;
            while (i < blockCount) {
                mode.update(ct, i * bs, cpt, i * bs);
                ++i;
            }
            return Arrays.equals(pt, cpt);
        }
        catch (Exception x) {
            x.printStackTrace(System.err);
            return false;
        }
    }

    private final /* synthetic */ void this() {
        this.lock = new Object();
    }

    protected BaseMode(String name, IBlockCipher underlyingCipher, int cipherBlockSize) {
        this.this();
        this.name = name;
        this.cipher = underlyingCipher;
        this.cipherBlockSize = cipherBlockSize;
        this.state = -1;
    }
}

