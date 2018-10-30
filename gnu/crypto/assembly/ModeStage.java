/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.assembly;

import gnu.crypto.assembly.Direction;
import gnu.crypto.assembly.Stage;
import gnu.crypto.mode.IMode;
import java.security.InvalidKeyException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class ModeStage
extends Stage {
    private IMode delegate;
    private transient Set cachedBlockSizes;

    public Set blockSizes() {
        if (this.cachedBlockSizes == null) {
            HashSet result = new HashSet();
            Iterator it = this.delegate.blockSizes();
            while (it.hasNext()) {
                result.add(it.next());
            }
            this.cachedBlockSizes = Collections.unmodifiableSet(result);
        }
        return this.cachedBlockSizes;
    }

    void initDelegate(Map attributes) throws InvalidKeyException {
        Direction flow = (Direction)attributes.get("gnu.crypto.assembly.stage.direction");
        attributes.put("gnu.crypto.mode.state", new Integer(2 - flow.equals(this.forward)));
        this.delegate.init(attributes);
    }

    public int currentBlockSize() throws IllegalStateException {
        return this.delegate.currentBlockSize();
    }

    void resetDelegate() {
        this.delegate.reset();
    }

    void updateDelegate(byte[] in, int inOffset, byte[] out, int outOffset) {
        this.delegate.update(in, inOffset, out, outOffset);
    }

    public boolean selfTest() {
        return this.delegate.selfTest();
    }

    ModeStage(IMode mode, Direction forwardDirection) {
        super(forwardDirection);
        this.delegate = mode;
        this.cachedBlockSizes = null;
    }
}

