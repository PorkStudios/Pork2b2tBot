/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.assembly;

import gnu.crypto.assembly.Cascade;
import gnu.crypto.assembly.CascadeStage;
import gnu.crypto.assembly.Direction;
import gnu.crypto.assembly.ModeStage;
import gnu.crypto.mode.IMode;
import java.security.InvalidKeyException;
import java.util.Map;
import java.util.Set;

public abstract class Stage {
    public static final String DIRECTION = "gnu.crypto.assembly.stage.direction";
    protected Direction forward;
    protected Direction wired;

    public static final Stage getInstance(IMode mode, Direction forwardDirection) {
        return new ModeStage(mode, forwardDirection);
    }

    public static final Stage getInstance(Cascade cascade, Direction forwardDirection) {
        return new CascadeStage(cascade, forwardDirection);
    }

    public abstract Set blockSizes();

    public void init(Map attributes) throws InvalidKeyException {
        if (this.wired != null) {
            throw new IllegalStateException();
        }
        Direction flow = (Direction)attributes.get("gnu.crypto.assembly.stage.direction");
        if (flow == null) {
            flow = Direction.FORWARD;
            attributes.put("gnu.crypto.assembly.stage.direction", flow);
        }
        this.initDelegate(attributes);
        this.wired = flow;
    }

    public abstract int currentBlockSize() throws IllegalStateException;

    public void reset() {
        this.resetDelegate();
        this.wired = null;
    }

    public void update(byte[] in, int inOffset, byte[] out, int outOffset) {
        if (this.wired == null) {
            throw new IllegalStateException();
        }
        this.updateDelegate(in, inOffset, out, outOffset);
    }

    public abstract boolean selfTest();

    abstract void initDelegate(Map var1) throws InvalidKeyException;

    abstract void resetDelegate();

    abstract void updateDelegate(byte[] var1, int var2, byte[] var3, int var4);

    protected Stage(Direction forwardDirection) {
        this.forward = forwardDirection;
        this.wired = null;
    }
}

