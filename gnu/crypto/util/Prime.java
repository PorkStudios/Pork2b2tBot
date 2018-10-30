/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.util;

import gnu.crypto.util.PRNG;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.util.Map;
import java.util.WeakHashMap;

public class Prime {
    private static final String NAME = "prime";
    private static final boolean DEBUG = false;
    private static final int debuglevel = 5;
    private static final PrintWriter err = new PrintWriter(System.out, true);
    private static final int DEFAULT_CERTAINTY = 20;
    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private static final int SMALL_PRIME_COUNT = 1000;
    private static final BigInteger[] SMALL_PRIME = new BigInteger[1000];
    private static final Map knownPrimes;

    private static final void debug(String s) {
        err.println(">>> prime: " + s);
    }

    public static boolean hasSmallPrimeDivisor(BigInteger w) {
        int i = 0;
        while (i < 1000) {
            BigInteger prime = SMALL_PRIME[i];
            if (w.mod(prime).equals(ZERO)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    public static boolean passEulerCriterion(BigInteger w) {
        int k;
        BigInteger w_minus_one;
        BigInteger A;
        WeakReference obj = (WeakReference)knownPrimes.get(w);
        if (obj != null && w.equals(obj.get())) {
            return true;
        }
        BigInteger e = w_minus_one = w.subtract(ONE);
        int l = e.and(BigInteger.valueOf(7L)).intValue();
        int j = 1;
        if ((l & 7) != 0) {
            e = e.shiftRight(1);
            A = TWO.modPow(e, w);
            if ((l & 7) == 6) {
                if (A.bitCount() != 1) {
                    return false;
                }
                k = 1;
            } else {
                if (!(A = A.add(ONE)).equals(w)) {
                    return false;
                }
                k = 1;
                if ((l & 4) != 0) {
                    e = e.shiftRight(1);
                    k = 2;
                }
            }
        } else {
            A = TWO.modPow(e = e.shiftRight(2), w);
            if (A.bitCount() == 1) {
                j = 0;
            } else if (!(A = A.add(ONE)).equals(w)) {
                return false;
            }
            k = e.getLowestSetBit();
            e = e.shiftRight(k);
            k += 2;
        }
        int i = j;
        while (i < 13) {
            A = SMALL_PRIME[i];
            if ((A = A.modPow(e, w)).bitCount() != 1) {
                l = k;
                while (!A.equals(w_minus_one)) {
                    if (--l == 0) {
                        return false;
                    }
                    if ((A = A.modPow(TWO, w)).bitCount() != 1) continue;
                    return false;
                }
            }
            ++i;
        }
        knownPrimes.put(w, new WeakReference<BigInteger>(w));
        return true;
    }

    public static boolean passFermatLittleTheorem(BigInteger w, int t) {
        BigInteger w_minus_one = w.subtract(ONE);
        if (t <= 0) {
            t = 10;
        }
        if (!TWO.modPow(w_minus_one, w).equals(ONE)) {
            return false;
        }
        int i = 0;
        while (i < t) {
            byte[] buf = new byte[(w.bitLength() + 7) / 8 - 1];
            BigInteger base = null;
            do {
                PRNG.nextBytes(buf);
            } while ((base = new BigInteger(1, buf)).compareTo(TWO) < 0 || base.compareTo(w_minus_one) > 0);
            if (!base.modPow(w_minus_one, w).equals(ONE)) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public static boolean passMillerRabin(BigInteger n, int t) {
        BigInteger n_minus_1;
        int nbytes = (n.bitLength() + 7) / 8;
        byte[] ab = new byte[nbytes];
        BigInteger r = n_minus_1 = n.subtract(ONE);
        int s = 0;
        while (!r.testBit(0)) {
            r = r.shiftRight(1);
            ++s;
        }
        int i = 0;
        while (i < t) {
            BigInteger a;
            do {
                PRNG.nextBytes(ab);
            } while ((a = new BigInteger(1, ab)).compareTo(TWO) < 0 || a.compareTo(n) > 0);
            BigInteger y = a.modPow(r, n);
            if (!y.equals(ONE) && !y.equals(n_minus_1)) {
                int j = 1;
                while (j < s - 1 && !y.equals(n_minus_1)) {
                    if ((y = y.modPow(TWO, n)).equals(ONE)) {
                        return false;
                    }
                    ++j;
                }
                if (!y.equals(n_minus_1)) {
                    return false;
                }
            }
            ++i;
        }
        return true;
    }

    public static boolean isProbablePrime(BigInteger w) {
        return Prime.isProbablePrime(w, 20);
    }

    public static boolean isProbablePrime(BigInteger w, int certainty) {
        if (w == null) {
            return false;
        }
        if (w.equals(ZERO) || w.equals(ONE)) {
            return false;
        }
        int i = 0;
        while (i < 1000) {
            if (w.equals(SMALL_PRIME[i])) {
                return true;
            }
            ++i;
        }
        if (Prime.hasSmallPrimeDivisor(w)) {
            return false;
        }
        if (!Prime.passFermatLittleTheorem(w, certainty)) {
            return false;
        }
        if (!Prime.passEulerCriterion(w)) {
            return false;
        }
        if (!Prime.passMillerRabin(w, certainty)) {
            return false;
        }
        return true;
    }

    private Prime() {
    }

    private static final {
        long time = - System.currentTimeMillis();
        Prime.SMALL_PRIME[0] = TWO;
        int N = 3;
        int J = 0;
        block0 : do {
            Prime.SMALL_PRIME[++J] = BigInteger.valueOf(N);
            if (J >= 999) break;
            block1 : do {
                int K = 1;
                do {
                    int prime;
                    if ((N += 2) % (prime = SMALL_PRIME[K].intValue()) == 0) continue block1;
                    if (N / prime <= prime) continue block0;
                    ++K;
                } while (true);
                break;
            } while (true);
            break;
        } while (true);
        time += System.currentTimeMillis();
        knownPrimes = new WeakHashMap();
    }
}

