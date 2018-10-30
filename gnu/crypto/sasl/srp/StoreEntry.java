/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl.srp;

class StoreEntry {
    private boolean perenial;
    private long timeToDie;

    boolean isAlive() {
        boolean bl = true;
        if (!this.perenial) {
            bl = false;
            if (System.currentTimeMillis() < this.timeToDie) {
                bl = true;
            }
        }
        return bl;
    }

    StoreEntry(int ttl) {
        if (ttl == 0) {
            this.perenial = true;
            this.timeToDie = 0L;
        } else {
            this.perenial = false;
            this.timeToDie = System.currentTimeMillis() + ((long)ttl & 0xFFFFFFFFL) * 1000L;
        }
    }
}

