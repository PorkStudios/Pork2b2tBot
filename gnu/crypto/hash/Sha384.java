/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.hash;

import gnu.crypto.hash.BaseHash;
import gnu.crypto.util.Util;

public class Sha384
extends BaseHash {
    private static final long[] k = new long[]{4794697086780616226L, 8158064640168781261L, -5349999486874862801L, -1606136188198331460L, 4131703408338449720L, 6480981068601479193L, -7908458776815382629L, -6116909921290321640L, -2880145864133508542L, 1334009975649890238L, 2608012711638119052L, 6128411473006802146L, 8268148722764581231L, -9160688886553864527L, -7215885187991268811L, -4495734319001033068L, -1973867731355612462L, -1171420211273849373L, 1135362057144423861L, 2597628984639134821L, 3308224258029322869L, 5365058923640841347L, 6679025012923562964L, 8573033837759648693L, -7476448914759557205L, -6327057829258317296L, -5763719355590565569L, -4658551843659510044L, -4116276920077217854L, -3051310485924567259L, 489312712824947311L, 1452737877330783856L, 2861767655752347644L, 3322285676063803686L, 5560940570517711597L, 5996557281743188959L, 7280758554555802590L, 8532644243296465576L, -9096487096722542874L, -7894198246740708037L, -6719396339535248540L, -6333637450476146687L, -4446306890439682159L, -4076793802049405392L, -3345356375505022440L, -2983346525034927856L, -860691631967231958L, 1182934255886127544L, 1847814050463011016L, 2177327727835720531L, 2830643537854262169L, 3796741975233480872L, 4115178125766777443L, 5681478168544905931L, 6601373596472566643L, 7507060721942968483L, 8399075790359081724L, 8693463985226723168L, -8878714635349349518L, -8302665154208450068L, -8016688836872298968L, -6606660893046293015L, -4685533653050689259L, -4147400797238176981L, -3880063495543823972L, -3348786107499101689L, -1523767162380948706L, -757361751448694408L, 500013540394364858L, 748580250866718886L, 1242879168328830382L, 1977374033974150939L, 2944078676154940804L, 3659926193048069267L, 4368137639120453308L, 4836135668995329356L, 5532061633213252278L, 6448918945643986474L, 6902733635092675308L, 7801388544844847127L};
    private static final int BLOCK_SIZE = 128;
    private static final String DIGEST0 = "CB00753F45A35E8BB5A03D699AC65007272C32AB0EDED1631A8B605A43FF5BED8086072BA1E7CC2358BAECA134C825A7";
    private static final long[] w = new long[80];
    private static Boolean valid;
    private long h0;
    private long h1;
    private long h2;
    private long h3;
    private long h4;
    private long h5;
    private long h6;
    private long h7;

    public static final long[] G(long hh0, long hh1, long hh2, long hh3, long hh4, long hh5, long hh6, long hh7, byte[] in, int offset) {
        return Sha384.sha(hh0, hh1, hh2, hh3, hh4, hh5, hh6, hh7, in, offset);
    }

    public Object clone() {
        return new Sha384(this);
    }

    protected void transform(byte[] in, int offset) {
        long[] result = Sha384.sha(this.h0, this.h1, this.h2, this.h3, this.h4, this.h5, this.h6, this.h7, in, offset);
        this.h0 = result[0];
        this.h1 = result[1];
        this.h2 = result[2];
        this.h3 = result[3];
        this.h4 = result[4];
        this.h5 = result[5];
        this.h6 = result[6];
        this.h7 = result[7];
    }

    protected byte[] padBuffer() {
        int n = (int)(this.count % 128L);
        int padding = n < 112 ? 112 - n : 240 - n;
        byte[] result = new byte[padding + 16];
        result[0] = -128;
        long bits = this.count << 3;
        padding += 8;
        result[padding++] = (byte)(bits >>> 56);
        result[padding++] = (byte)(bits >>> 48);
        result[padding++] = (byte)(bits >>> 40);
        result[padding++] = (byte)(bits >>> 32);
        result[padding++] = (byte)(bits >>> 24);
        result[padding++] = (byte)(bits >>> 16);
        result[padding++] = (byte)(bits >>> 8);
        result[padding] = (byte)bits;
        return result;
    }

    protected byte[] getResult() {
        return new byte[]{(byte)(this.h0 >>> 56), (byte)(this.h0 >>> 48), (byte)(this.h0 >>> 40), (byte)(this.h0 >>> 32), (byte)(this.h0 >>> 24), (byte)(this.h0 >>> 16), (byte)(this.h0 >>> 8), (byte)this.h0, (byte)(this.h1 >>> 56), (byte)(this.h1 >>> 48), (byte)(this.h1 >>> 40), (byte)(this.h1 >>> 32), (byte)(this.h1 >>> 24), (byte)(this.h1 >>> 16), (byte)(this.h1 >>> 8), (byte)this.h1, (byte)(this.h2 >>> 56), (byte)(this.h2 >>> 48), (byte)(this.h2 >>> 40), (byte)(this.h2 >>> 32), (byte)(this.h2 >>> 24), (byte)(this.h2 >>> 16), (byte)(this.h2 >>> 8), (byte)this.h2, (byte)(this.h3 >>> 56), (byte)(this.h3 >>> 48), (byte)(this.h3 >>> 40), (byte)(this.h3 >>> 32), (byte)(this.h3 >>> 24), (byte)(this.h3 >>> 16), (byte)(this.h3 >>> 8), (byte)this.h3, (byte)(this.h4 >>> 56), (byte)(this.h4 >>> 48), (byte)(this.h4 >>> 40), (byte)(this.h4 >>> 32), (byte)(this.h4 >>> 24), (byte)(this.h4 >>> 16), (byte)(this.h4 >>> 8), (byte)this.h4, (byte)(this.h5 >>> 56), (byte)(this.h5 >>> 48), (byte)(this.h5 >>> 40), (byte)(this.h5 >>> 32), (byte)(this.h5 >>> 24), (byte)(this.h5 >>> 16), (byte)(this.h5 >>> 8), (byte)this.h5};
    }

    protected void resetContext() {
        this.h0 = -3766243637369397544L;
        this.h1 = 7105036623409894663L;
        this.h2 = -7973340178411365097L;
        this.h3 = 1526699215303891257L;
        this.h4 = 7436329637833083697L;
        this.h5 = -8163818279084223215L;
        this.h6 = -2662702644619276377L;
        this.h7 = 5167115440072839076L;
    }

    public boolean selfTest() {
        if (valid == null) {
            Sha384 md = new Sha384();
            md.update((byte)97);
            md.update((byte)98);
            md.update((byte)99);
            String result = Util.toString(md.digest());
            valid = new Boolean(DIGEST0.equals(result));
        }
        return valid;
    }

    private static final synchronized long[] sha(long hh0, long hh1, long hh2, long hh3, long hh4, long hh5, long hh6, long hh7, byte[] in, int offset) {
        long T2;
        long T;
        long A = hh0;
        long B = hh1;
        long C = hh2;
        long D = hh3;
        long E = hh4;
        long F = hh5;
        long G = hh6;
        long H = hh7;
        int r = 0;
        while (r < 16) {
            Sha384.w[r] = (long)in[offset++] << 56 | ((long)in[offset++] & 255L) << 48 | ((long)in[offset++] & 255L) << 40 | ((long)in[offset++] & 255L) << 32 | ((long)in[offset++] & 255L) << 24 | ((long)in[offset++] & 255L) << 16 | ((long)in[offset++] & 255L) << 8 | (long)in[offset++] & 255L;
            ++r;
        }
        r = 16;
        while (r < 80) {
            T = w[r - 2];
            T2 = w[r - 15];
            Sha384.w[r] = ((T >>> 19 | T << 45) ^ (T >>> 61 | T << 3) ^ T >>> 6) + w[r - 7] + ((T2 >>> 1 | T2 << 63) ^ (T2 >>> 8 | T2 << 56) ^ T2 >>> 7) + w[r - 16];
            ++r;
        }
        r = 0;
        while (r < 80) {
            T = H + ((E >>> 14 | E << 50) ^ (E >>> 18 | E << 46) ^ (E >>> 41 | E << 23)) + (E & F ^ (E ^ - 1L) & G) + k[r] + w[r];
            T2 = ((A >>> 28 | A << 36) ^ (A >>> 34 | A << 30) ^ (A >>> 39 | A << 25)) + (A & B ^ A & C ^ B & C);
            H = G;
            G = F;
            F = E;
            E = D + T;
            D = C;
            C = B;
            B = A;
            A = T + T2;
            ++r;
        }
        return new long[]{hh0 + A, hh1 + B, hh2 + C, hh3 + D, hh4 + E, hh5 + F, hh6 + G, hh7 + H};
    }

    public Sha384() {
        super("sha-384", 48, 128);
    }

    private Sha384(Sha384 md) {
        this();
        this.h0 = md.h0;
        this.h1 = md.h1;
        this.h2 = md.h2;
        this.h3 = md.h3;
        this.h4 = md.h4;
        this.h5 = md.h5;
        this.h6 = md.h6;
        this.h7 = md.h7;
        this.count = md.count;
        this.buffer = (byte[])md.buffer.clone();
    }
}

