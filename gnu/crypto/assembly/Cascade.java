/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.assembly;

import gnu.crypto.assembly.Direction;
import gnu.crypto.assembly.Stage;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class Cascade {
    public static final String DIRECTION = "gnu.crypto.assembly.cascade.direction";
    protected HashMap stages = new HashMap(3);
    protected LinkedList stageKeys = new LinkedList();
    protected Direction wired = null;
    protected int blockSize = 0;

    private static final int lcm(int a, int b) {
        BigInteger A = BigInteger.valueOf(a);
        BigInteger B = BigInteger.valueOf(b);
        return A.multiply(B).divide(A.gcd(B)).abs().intValue();
    }

    public Object append(Stage stage) throws IllegalArgumentException {
        return this.insert(this.size(), stage);
    }

    public Object prepend(Stage stage) throws IllegalArgumentException {
        return this.insert(0, stage);
    }

    public Object insert(int index, Stage stage) throws IllegalArgumentException, IndexOutOfBoundsException {
        if (this.stages.containsValue(stage)) {
            throw new IllegalArgumentException();
        }
        if (this.wired != null || stage == null) {
            throw new IllegalStateException();
        }
        if (index < 0 || index > this.size()) {
            throw new IndexOutOfBoundsException();
        }
        Set set = stage.blockSizes();
        if (this.stages.isEmpty()) {
            if (set.isEmpty()) {
                throw new IllegalArgumentException("1st stage with no block sizes");
            }
        } else {
            Set common = this.blockSizes();
            common.retainAll(set);
            if (common.isEmpty()) {
                throw new IllegalArgumentException("no common block sizes found");
            }
        }
        Object result = new Object();
        this.stageKeys.add(index, result);
        this.stages.put(result, stage);
        return result;
    }

    public int size() {
        return this.stages.size();
    }

    public Iterator stages() {
        LinkedList result = new LinkedList();
        ListIterator it = this.stageKeys.listIterator();
        while (it.hasNext()) {
            result.addLast(this.stages.get(it.next()));
        }
        return result.listIterator();
    }

    public Set blockSizes() {
        HashSet result = null;
        Iterator it = this.stages.values().iterator();
        while (it.hasNext()) {
            Stage aStage = (Stage)it.next();
            if (result == null) {
                result = new HashSet(aStage.blockSizes());
                continue;
            }
            result.retainAll(aStage.blockSizes());
        }
        return result == null ? Collections.EMPTY_SET : result;
    }

    public void init(Map attributes) throws InvalidKeyException {
        if (this.wired != null) {
            throw new IllegalStateException();
        }
        Direction flow = (Direction)attributes.get(DIRECTION);
        if (flow == null) {
            flow = Direction.FORWARD;
        }
        int optimalSize = 0;
        ListIterator it = this.stageKeys.listIterator();
        while (it.hasNext()) {
            Object id = it.next();
            Map attr = (Map)attributes.get(id);
            attr.put("gnu.crypto.assembly.stage.direction", flow);
            Stage stage = (Stage)this.stages.get(id);
            stage.init(attr);
            int n = optimalSize = optimalSize == 0 ? stage.currentBlockSize() : Cascade.lcm(optimalSize, stage.currentBlockSize());
        }
        if (flow == Direction.REVERSED) {
            Collections.reverse(this.stageKeys);
        }
        this.wired = flow;
        this.blockSize = optimalSize;
    }

    public int currentBlockSize() {
        if (this.wired == null) {
            throw new IllegalStateException();
        }
        return this.blockSize;
    }

    public void reset() {
        ListIterator it = this.stageKeys.listIterator();
        while (it.hasNext()) {
            ((Stage)this.stages.get(it.next())).reset();
        }
        if (this.wired == Direction.REVERSED) {
            Collections.reverse(this.stageKeys);
        }
        this.wired = null;
        this.blockSize = 0;
    }

    public void update(byte[] in, int inOffset, byte[] out, int outOffset) {
        if (this.wired == null) {
            throw new IllegalStateException();
        }
        int i = this.stages.size();
        ListIterator it = this.stageKeys.listIterator();
        while (it.hasNext()) {
            Stage stage = (Stage)this.stages.get(it.next());
            int stageBlockSize = stage.currentBlockSize();
            int j = 0;
            while (j < this.blockSize) {
                stage.update(in, inOffset + j, out, outOffset + j);
                j += stageBlockSize;
            }
            if (--i <= 0) continue;
            System.arraycopy(out, outOffset, in, inOffset, this.blockSize);
        }
    }

    public boolean selfTest() {
        ListIterator it = this.stageKeys.listIterator();
        while (it.hasNext()) {
            if (((Stage)this.stages.get(it.next())).selfTest()) continue;
            return false;
        }
        return true;
    }
}

