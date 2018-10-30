/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.hash;

import gnu.crypto.hash.BaseHash;
import gnu.crypto.util.Util;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public class Haval
extends BaseHash {
    public static final int HAVAL_VERSION = 1;
    public static final int HAVAL_128_BIT = 16;
    public static final int HAVAL_160_BIT = 20;
    public static final int HAVAL_192_BIT = 24;
    public static final int HAVAL_224_BIT = 28;
    public static final int HAVAL_256_BIT = 32;
    public static final int HAVAL_3_ROUND = 3;
    public static final int HAVAL_4_ROUND = 4;
    public static final int HAVAL_5_ROUND = 5;
    private static final int BLOCK_SIZE = 128;
    private static final String DIGEST0 = "C68F39913F901F3DDF44C707357A7D70";
    private static Boolean valid;
    private int rounds;
    private int h0;
    private int h1;
    private int h2;
    private int h3;
    private int h4;
    private int h5;
    private int h6;
    private int h7;

    public Object clone() {
        return new Haval(this);
    }

    protected synchronized void transform(byte[] in, int i) {
        int X0 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X1 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X2 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X3 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X4 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X5 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X6 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X7 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X8 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X9 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X10 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X11 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X12 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X13 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X14 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X15 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X16 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X17 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X18 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X19 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X20 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X21 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X22 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X23 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X24 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X25 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X26 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X27 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X28 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X29 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X30 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int X31 = in[i++] & 255 | (in[i++] & 255) << 8 | (in[i++] & 255) << 16 | (in[i++] & 255) << 24;
        int t0 = this.h0;
        int t1 = this.h1;
        int t2 = this.h2;
        int t3 = this.h3;
        int t4 = this.h4;
        int t5 = this.h5;
        int t6 = this.h6;
        int t7 = this.h7;
        t7 = this.FF1(t7, t6, t5, t4, t3, t2, t1, t0, X0);
        t6 = this.FF1(t6, t5, t4, t3, t2, t1, t0, t7, X1);
        t5 = this.FF1(t5, t4, t3, t2, t1, t0, t7, t6, X2);
        t4 = this.FF1(t4, t3, t2, t1, t0, t7, t6, t5, X3);
        t3 = this.FF1(t3, t2, t1, t0, t7, t6, t5, t4, X4);
        t2 = this.FF1(t2, t1, t0, t7, t6, t5, t4, t3, X5);
        t1 = this.FF1(t1, t0, t7, t6, t5, t4, t3, t2, X6);
        t0 = this.FF1(t0, t7, t6, t5, t4, t3, t2, t1, X7);
        t7 = this.FF1(t7, t6, t5, t4, t3, t2, t1, t0, X8);
        t6 = this.FF1(t6, t5, t4, t3, t2, t1, t0, t7, X9);
        t5 = this.FF1(t5, t4, t3, t2, t1, t0, t7, t6, X10);
        t4 = this.FF1(t4, t3, t2, t1, t0, t7, t6, t5, X11);
        t3 = this.FF1(t3, t2, t1, t0, t7, t6, t5, t4, X12);
        t2 = this.FF1(t2, t1, t0, t7, t6, t5, t4, t3, X13);
        t1 = this.FF1(t1, t0, t7, t6, t5, t4, t3, t2, X14);
        t0 = this.FF1(t0, t7, t6, t5, t4, t3, t2, t1, X15);
        t7 = this.FF1(t7, t6, t5, t4, t3, t2, t1, t0, X16);
        t6 = this.FF1(t6, t5, t4, t3, t2, t1, t0, t7, X17);
        t5 = this.FF1(t5, t4, t3, t2, t1, t0, t7, t6, X18);
        t4 = this.FF1(t4, t3, t2, t1, t0, t7, t6, t5, X19);
        t3 = this.FF1(t3, t2, t1, t0, t7, t6, t5, t4, X20);
        t2 = this.FF1(t2, t1, t0, t7, t6, t5, t4, t3, X21);
        t1 = this.FF1(t1, t0, t7, t6, t5, t4, t3, t2, X22);
        t0 = this.FF1(t0, t7, t6, t5, t4, t3, t2, t1, X23);
        t7 = this.FF1(t7, t6, t5, t4, t3, t2, t1, t0, X24);
        t6 = this.FF1(t6, t5, t4, t3, t2, t1, t0, t7, X25);
        t5 = this.FF1(t5, t4, t3, t2, t1, t0, t7, t6, X26);
        t4 = this.FF1(t4, t3, t2, t1, t0, t7, t6, t5, X27);
        t3 = this.FF1(t3, t2, t1, t0, t7, t6, t5, t4, X28);
        t2 = this.FF1(t2, t1, t0, t7, t6, t5, t4, t3, X29);
        t1 = this.FF1(t1, t0, t7, t6, t5, t4, t3, t2, X30);
        t0 = this.FF1(t0, t7, t6, t5, t4, t3, t2, t1, X31);
        t7 = this.FF2(t7, t6, t5, t4, t3, t2, t1, t0, X5, 1160258022);
        t6 = this.FF2(t6, t5, t4, t3, t2, t1, t0, t7, X14, 953160567);
        t5 = this.FF2(t5, t4, t3, t2, t1, t0, t7, t6, X26, -1101764913);
        t4 = this.FF2(t4, t3, t2, t1, t0, t7, t6, t5, X18, 887688300);
        t3 = this.FF2(t3, t2, t1, t0, t7, t6, t5, t4, X11, -1062458953);
        t2 = this.FF2(t2, t1, t0, t7, t6, t5, t4, t3, X28, -914599715);
        t1 = this.FF2(t1, t0, t7, t6, t5, t4, t3, t2, X7, 1065670069);
        t0 = this.FF2(t0, t7, t6, t5, t4, t3, t2, t1, X16, -1253635817);
        t7 = this.FF2(t7, t6, t5, t4, t3, t2, t1, t0, X0, -1843997223);
        t6 = this.FF2(t6, t5, t4, t3, t2, t1, t0, t7, X23, -1988494565);
        t5 = this.FF2(t5, t4, t3, t2, t1, t0, t7, t6, X20, -785314906);
        t4 = this.FF2(t4, t3, t2, t1, t0, t7, t6, t5, X22, -1730169428);
        t3 = this.FF2(t3, t2, t1, t0, t7, t6, t5, t4, X1, 805139163);
        t2 = this.FF2(t2, t1, t0, t7, t6, t5, t4, t3, X10, -803545161);
        t1 = this.FF2(t1, t0, t7, t6, t5, t4, t3, t2, X4, -1193168915);
        t0 = this.FF2(t0, t7, t6, t5, t4, t3, t2, t1, X8, 1780907670);
        t7 = this.FF2(t7, t6, t5, t4, t3, t2, t1, t0, X30, -1166241723);
        t6 = this.FF2(t6, t5, t4, t3, t2, t1, t0, t7, X3, -248741991);
        t5 = this.FF2(t5, t4, t3, t2, t1, t0, t7, t6, X21, 614570311);
        t4 = this.FF2(t4, t3, t2, t1, t0, t7, t6, t5, X9, -1282315017);
        t3 = this.FF2(t3, t2, t1, t0, t7, t6, t5, t4, X17, 134345442);
        t2 = this.FF2(t2, t1, t0, t7, t6, t5, t4, t3, X24, -2054226922);
        t1 = this.FF2(t1, t0, t7, t6, t5, t4, t3, t2, X29, 1667834072);
        t0 = this.FF2(t0, t7, t6, t5, t4, t3, t2, t1, X6, 1901547113);
        t7 = this.FF2(t7, t6, t5, t4, t3, t2, t1, t0, X19, -1537671517);
        t6 = this.FF2(t6, t5, t4, t3, t2, t1, t0, t7, X12, -191677058);
        t5 = this.FF2(t5, t4, t3, t2, t1, t0, t7, t6, X15, 227898511);
        t4 = this.FF2(t4, t3, t2, t1, t0, t7, t6, t5, X13, 1921955416);
        t3 = this.FF2(t3, t2, t1, t0, t7, t6, t5, t4, X2, 1904987480);
        t2 = this.FF2(t2, t1, t0, t7, t6, t5, t4, t3, X25, -2112533778);
        t1 = this.FF2(t1, t0, t7, t6, t5, t4, t3, t2, X31, 2069144605);
        t0 = this.FF2(t0, t7, t6, t5, t4, t3, t2, t1, X27, -1034266187);
        t7 = this.FF3(t7, t6, t5, t4, t3, t2, t1, t0, X19, -1674521287);
        t6 = this.FF3(t6, t5, t4, t3, t2, t1, t0, t7, X9, 720527379);
        t5 = this.FF3(t5, t4, t3, t2, t1, t0, t7, t6, X4, -976113629);
        t4 = this.FF3(t4, t3, t2, t1, t0, t7, t6, t5, X20, 677414384);
        t3 = this.FF3(t3, t2, t1, t0, t7, t6, t5, t4, X28, -901678824);
        t2 = this.FF3(t2, t1, t0, t7, t6, t5, t4, t3, X17, -1193592593);
        t1 = this.FF3(t1, t0, t7, t6, t5, t4, t3, t2, X8, -1904616272);
        t0 = this.FF3(t0, t7, t6, t5, t4, t3, t2, t1, X22, 1614419982);
        t7 = this.FF3(t7, t6, t5, t4, t3, t2, t1, t0, X29, 1822297739);
        t6 = this.FF3(t6, t5, t4, t3, t2, t1, t0, t7, X14, -1340175810);
        t5 = this.FF3(t5, t4, t3, t2, t1, t0, t7, t6, X25, -686458943);
        t4 = this.FF3(t4, t3, t2, t1, t0, t7, t6, t5, X12, -1120842969);
        t3 = this.FF3(t3, t2, t1, t0, t7, t6, t5, t4, X24, 2024746970);
        t2 = this.FF3(t2, t1, t0, t7, t6, t5, t4, t3, X30, 1432378464);
        t1 = this.FF3(t1, t0, t7, t6, t5, t4, t3, t2, X16, -430627341);
        t0 = this.FF3(t0, t7, t6, t5, t4, t3, t2, t1, X26, -1437226092);
        t7 = this.FF3(t7, t6, t5, t4, t3, t2, t1, t0, X31, 1464375394);
        t6 = this.FF3(t6, t5, t4, t3, t2, t1, t0, t7, X15, 1676153920);
        t5 = this.FF3(t5, t4, t3, t2, t1, t0, t7, t6, X7, 1439316330);
        t4 = this.FF3(t4, t3, t2, t1, t0, t7, t6, t5, X3, 715854006);
        t3 = this.FF3(t3, t2, t1, t0, t7, t6, t5, t4, X1, -1261675468);
        t2 = this.FF3(t2, t1, t0, t7, t6, t5, t4, t3, X0, 289532110);
        t1 = this.FF3(t1, t0, t7, t6, t5, t4, t3, t2, X18, -1588296017);
        t0 = this.FF3(t0, t7, t6, t5, t4, t3, t2, t1, X27, 2087905683);
        t7 = this.FF3(t7, t6, t5, t4, t3, t2, t1, t0, X13, -1276242927);
        t6 = this.FF3(t6, t5, t4, t3, t2, t1, t0, t7, X6, 1668267050);
        t5 = this.FF3(t5, t4, t3, t2, t1, t0, t7, t6, X21, 732546397);
        t4 = this.FF3(t4, t3, t2, t1, t0, t7, t6, t5, X10, 1947742710);
        t3 = this.FF3(t3, t2, t1, t0, t7, t6, t5, t4, X23, -832815594);
        t2 = this.FF3(t2, t1, t0, t7, t6, t5, t4, t3, X11, -1685613794);
        t1 = this.FF3(t1, t0, t7, t6, t5, t4, t3, t2, X5, -1344882125);
        t0 = this.FF3(t0, t7, t6, t5, t4, t3, t2, t1, X2, 1814351708);
        if (this.rounds >= 4) {
            t7 = this.FF4(t7, t6, t5, t4, t3, t2, t1, t0, X24, 2050118529);
            t6 = this.FF4(t6, t5, t4, t3, t2, t1, t0, t7, X4, 680887927);
            t5 = this.FF4(t5, t4, t3, t2, t1, t0, t7, t6, X0, 999245976);
            t4 = this.FF4(t4, t3, t2, t1, t0, t7, t6, t5, X14, 1800124847);
            t3 = this.FF4(t3, t2, t1, t0, t7, t6, t5, t4, X2, -994056165);
            t2 = this.FF4(t2, t1, t0, t7, t6, t5, t4, t3, X7, 1713906067);
            t1 = this.FF4(t1, t0, t7, t6, t5, t4, t3, t2, X28, 1641548236);
            t0 = this.FF4(t0, t7, t6, t5, t4, t3, t2, t1, X23, -81679983);
            t7 = this.FF4(t7, t6, t5, t4, t3, t2, t1, t0, X26, 1216130144);
            t6 = this.FF4(t6, t5, t4, t3, t2, t1, t0, t7, X6, 1575780402);
            t5 = this.FF4(t5, t4, t3, t2, t1, t0, t7, t6, X30, -276538019);
            t4 = this.FF4(t4, t3, t2, t1, t0, t7, t6, t5, X20, -377129551);
            t3 = this.FF4(t3, t2, t1, t0, t7, t6, t5, t4, X18, -601480446);
            t2 = this.FF4(t2, t1, t0, t7, t6, t5, t4, t3, X25, -345695352);
            t1 = this.FF4(t1, t0, t7, t6, t5, t4, t3, t2, X19, 596196993);
            t0 = this.FF4(t0, t7, t6, t5, t4, t3, t2, t1, X3, -745100091);
            t7 = this.FF4(t7, t6, t5, t4, t3, t2, t1, t0, X22, 258830323);
            t6 = this.FF4(t6, t5, t4, t3, t2, t1, t0, t7, X11, -2081144263);
            t5 = this.FF4(t5, t4, t3, t2, t1, t0, t7, t6, X31, 772490370);
            t4 = this.FF4(t4, t3, t2, t1, t0, t7, t6, t5, X21, -1534844924);
            t3 = this.FF4(t3, t2, t1, t0, t7, t6, t5, t4, X8, 1774776394);
            t2 = this.FF4(t2, t1, t0, t7, t6, t5, t4, t3, X27, -1642095778);
            t1 = this.FF4(t1, t0, t7, t6, t5, t4, t3, t2, X12, 566650946);
            t0 = this.FF4(t0, t7, t6, t5, t4, t3, t2, t1, X9, -152474470);
            t7 = this.FF4(t7, t6, t5, t4, t3, t2, t1, t0, X1, 1728879713);
            t6 = this.FF4(t6, t5, t4, t3, t2, t1, t0, t7, X29, -1412200208);
            t5 = this.FF4(t5, t4, t3, t2, t1, t0, t7, t6, X5, 1783734482);
            t4 = this.FF4(t4, t3, t2, t1, t0, t7, t6, t5, X15, -665571480);
            t3 = this.FF4(t3, t2, t1, t0, t7, t6, t5, t4, X17, -1777359064);
            t2 = this.FF4(t2, t1, t0, t7, t6, t5, t4, t3, X10, -1420741725);
            t1 = this.FF4(t1, t0, t7, t6, t5, t4, t3, t2, X16, 1861159788);
            t0 = this.FF4(t0, t7, t6, t5, t4, t3, t2, t1, X13, 326777828);
            if (this.rounds == 5) {
                t7 = this.FF5(t7, t6, t5, t4, t3, t2, t1, t0, X27, -1170476976);
                t6 = this.FF5(t6, t5, t4, t3, t2, t1, t0, t7, X3, 2130389656);
                t5 = this.FF5(t5, t4, t3, t2, t1, t0, t7, t6, X21, -1578015459);
                t4 = this.FF5(t4, t3, t2, t1, t0, t7, t6, t5, X26, 967770486);
                t3 = this.FF5(t3, t2, t1, t0, t7, t6, t5, t4, X17, 1724537150);
                t2 = this.FF5(t2, t1, t0, t7, t6, t5, t4, t3, X11, -2109534584);
                t1 = this.FF5(t1, t0, t7, t6, t5, t4, t3, t2, X20, -1930525159);
                t0 = this.FF5(t0, t7, t6, t5, t4, t3, t2, t1, X29, 1164943284);
                t7 = this.FF5(t7, t6, t5, t4, t3, t2, t1, t0, X19, 2105845187);
                t6 = this.FF5(t6, t5, t4, t3, t2, t1, t0, t7, X0, 998989502);
                t5 = this.FF5(t5, t4, t3, t2, t1, t0, t7, t6, X12, -529566248);
                t4 = this.FF5(t4, t3, t2, t1, t0, t7, t6, t5, X7, -2050940813);
                t3 = this.FF5(t3, t2, t1, t0, t7, t6, t5, t4, X13, 1075463327);
                t2 = this.FF5(t2, t1, t0, t7, t6, t5, t4, t3, X8, 1455516326);
                t1 = this.FF5(t1, t0, t7, t6, t5, t4, t3, t2, X31, 1322494562);
                t0 = this.FF5(t0, t7, t6, t5, t4, t3, t2, t1, X10, 910128902);
                t7 = this.FF5(t7, t6, t5, t4, t3, t2, t1, t0, X5, 469688178);
                t6 = this.FF5(t6, t5, t4, t3, t2, t1, t0, t7, X9, 1117454909);
                t5 = this.FF5(t5, t4, t3, t2, t1, t0, t7, t6, X14, 936433444);
                t4 = this.FF5(t4, t3, t2, t1, t0, t7, t6, t5, X30, -804646328);
                t3 = this.FF5(t3, t2, t1, t0, t7, t6, t5, t4, X18, -619713837);
                t2 = this.FF5(t2, t1, t0, t7, t6, t5, t4, t3, X6, 1240580251);
                t1 = this.FF5(t1, t0, t7, t6, t5, t4, t3, t2, X28, 122909385);
                t0 = this.FF5(t0, t7, t6, t5, t4, t3, t2, t1, X24, -2137449605);
                t7 = this.FF5(t7, t6, t5, t4, t3, t2, t1, t0, X2, 634681816);
                t6 = this.FF5(t6, t5, t4, t3, t2, t1, t0, t7, X23, -152510729);
                t5 = this.FF5(t5, t4, t3, t2, t1, t0, t7, t6, X16, -469872614);
                t4 = this.FF5(t4, t3, t2, t1, t0, t7, t6, t5, X22, -1233564613);
                t3 = this.FF5(t3, t2, t1, t0, t7, t6, t5, t4, X4, -1754472259);
                t2 = this.FF5(t2, t1, t0, t7, t6, t5, t4, t3, X1, 79693498);
                t1 = this.FF5(t1, t0, t7, t6, t5, t4, t3, t2, X25, -1045868618);
                t0 = this.FF5(t0, t7, t6, t5, t4, t3, t2, t1, X15, 1084186820);
            }
        }
        this.h7 += t7;
        this.h6 += t6;
        this.h5 += t5;
        this.h4 += t4;
        this.h3 += t3;
        this.h2 += t2;
        this.h1 += t1;
        this.h0 += t0;
    }

    protected byte[] padBuffer() {
        int n = (int)(this.count % 128L);
        int padding = n < 118 ? 118 - n : 246 - n;
        byte[] result = new byte[padding + 10];
        result[0] = 1;
        int bl = this.hashSize * 8;
        result[padding++] = (byte)((bl & 3) << 6 | (this.rounds & 7) << 3 | 1);
        result[padding++] = (byte)(bl >>> 2);
        long bits = this.count << 3;
        result[padding++] = (byte)bits;
        result[padding++] = (byte)(bits >>> 8);
        result[padding++] = (byte)(bits >>> 16);
        result[padding++] = (byte)(bits >>> 24);
        result[padding++] = (byte)(bits >>> 32);
        result[padding++] = (byte)(bits >>> 40);
        result[padding++] = (byte)(bits >>> 48);
        result[padding] = (byte)(bits >>> 56);
        return result;
    }

    protected byte[] getResult() {
        this.tailorDigestBits();
        byte[] result = new byte[this.hashSize];
        if (this.hashSize >= 32) {
            result[31] = (byte)(this.h7 >>> 24);
            result[30] = (byte)(this.h7 >>> 16);
            result[29] = (byte)(this.h7 >>> 8);
            result[28] = (byte)this.h7;
        }
        if (this.hashSize >= 28) {
            result[27] = (byte)(this.h6 >>> 24);
            result[26] = (byte)(this.h6 >>> 16);
            result[25] = (byte)(this.h6 >>> 8);
            result[24] = (byte)this.h6;
        }
        if (this.hashSize >= 24) {
            result[23] = (byte)(this.h5 >>> 24);
            result[22] = (byte)(this.h5 >>> 16);
            result[21] = (byte)(this.h5 >>> 8);
            result[20] = (byte)this.h5;
        }
        if (this.hashSize >= 20) {
            result[19] = (byte)(this.h4 >>> 24);
            result[18] = (byte)(this.h4 >>> 16);
            result[17] = (byte)(this.h4 >>> 8);
            result[16] = (byte)this.h4;
        }
        result[15] = (byte)(this.h3 >>> 24);
        result[14] = (byte)(this.h3 >>> 16);
        result[13] = (byte)(this.h3 >>> 8);
        result[12] = (byte)this.h3;
        result[11] = (byte)(this.h2 >>> 24);
        result[10] = (byte)(this.h2 >>> 16);
        result[9] = (byte)(this.h2 >>> 8);
        result[8] = (byte)this.h2;
        result[7] = (byte)(this.h1 >>> 24);
        result[6] = (byte)(this.h1 >>> 16);
        result[5] = (byte)(this.h1 >>> 8);
        result[4] = (byte)this.h1;
        result[3] = (byte)(this.h0 >>> 24);
        result[2] = (byte)(this.h0 >>> 16);
        result[1] = (byte)(this.h0 >>> 8);
        result[0] = (byte)this.h0;
        return result;
    }

    protected void resetContext() {
        this.h0 = 608135816;
        this.h1 = -2052912941;
        this.h2 = 320440878;
        this.h3 = 57701188;
        this.h4 = -1542899678;
        this.h5 = 698298832;
        this.h6 = 137296536;
        this.h7 = -330404727;
    }

    public boolean selfTest() {
        if (valid == null) {
            valid = new Boolean(DIGEST0.equals(Util.toString(new Haval().digest())));
        }
        return valid;
    }

    private final void tailorDigestBits() {
        switch (this.hashSize) {
            case 16: {
                int t = this.h7 & 255 | this.h6 & -16777216 | this.h5 & 16711680 | this.h4 & 65280;
                this.h0 += t >>> 8 | t << 24;
                t = this.h7 & 65280 | this.h6 & 255 | this.h5 & -16777216 | this.h4 & 16711680;
                this.h1 += t >>> 16 | t << 16;
                t = this.h7 & 16711680 | this.h6 & 65280 | this.h5 & 255 | this.h4 & -16777216;
                this.h2 += t >>> 24 | t << 8;
                t = this.h7 & -16777216 | this.h6 & 16711680 | this.h5 & 65280 | this.h4 & 255;
                this.h3 += t;
                break;
            }
            case 20: {
                int t = this.h7 & 63 | this.h6 & -33554432 | this.h5 & 33030144;
                this.h0 += t >>> 19 | t << 13;
                t = this.h7 & 4032 | this.h6 & 63 | this.h5 & -33554432;
                this.h1 += t >>> 25 | t << 7;
                t = this.h7 & 520192 | this.h6 & 4032 | this.h5 & 63;
                this.h2 += t;
                t = this.h7 & 33030144 | this.h6 & 520192 | this.h5 & 4032;
                this.h3 += t >>> 6;
                t = this.h7 & -33554432 | this.h6 & 33030144 | this.h5 & 520192;
                this.h4 += t >>> 12;
                break;
            }
            case 24: {
                int t = this.h7 & 31 | this.h6 & -67108864;
                this.h0 += t >>> 26 | t << 6;
                t = this.h7 & 992 | this.h6 & 31;
                this.h1 += t;
                t = this.h7 & 64512 | this.h6 & 992;
                this.h2 += t >>> 5;
                t = this.h7 & 2031616 | this.h6 & 64512;
                this.h3 += t >>> 10;
                t = this.h7 & 65011712 | this.h6 & 2031616;
                this.h4 += t >>> 16;
                t = this.h7 & -67108864 | this.h6 & 65011712;
                this.h5 += t >>> 21;
                break;
            }
            case 28: {
                this.h0 += this.h7 >>> 27 & 31;
                this.h1 += this.h7 >>> 22 & 31;
                this.h2 += this.h7 >>> 18 & 15;
                this.h3 += this.h7 >>> 13 & 31;
                this.h4 += this.h7 >>> 9 & 15;
                this.h5 += this.h7 >>> 4 & 31;
                this.h6 += this.h7 & 15;
            }
        }
    }

    private final int FF1(int x7, int x6, int x5, int x4, int x3, int x2, int x1, int x0, int w) {
        int t;
        switch (this.rounds) {
            case 3: {
                t = this.f1(x1, x0, x3, x5, x6, x2, x4);
                break;
            }
            case 4: {
                t = this.f1(x2, x6, x1, x4, x5, x3, x0);
                break;
            }
            default: {
                t = this.f1(x3, x4, x1, x0, x5, x2, x6);
            }
        }
        return (t >>> 7 | t << 25) + (x7 >>> 11 | x7 << 21) + w;
    }

    private final int FF2(int x7, int x6, int x5, int x4, int x3, int x2, int x1, int x0, int w, int c) {
        int t;
        switch (this.rounds) {
            case 3: {
                t = this.f2(x4, x2, x1, x0, x5, x3, x6);
                break;
            }
            case 4: {
                t = this.f2(x3, x5, x2, x0, x1, x6, x4);
                break;
            }
            default: {
                t = this.f2(x6, x2, x1, x0, x3, x4, x5);
            }
        }
        return (t >>> 7 | t << 25) + (x7 >>> 11 | x7 << 21) + w + c;
    }

    private final int FF3(int x7, int x6, int x5, int x4, int x3, int x2, int x1, int x0, int w, int c) {
        int t;
        switch (this.rounds) {
            case 3: {
                t = this.f3(x6, x1, x2, x3, x4, x5, x0);
                break;
            }
            case 4: {
                t = this.f3(x1, x4, x3, x6, x0, x2, x5);
                break;
            }
            default: {
                t = this.f3(x2, x6, x0, x4, x3, x1, x5);
            }
        }
        return (t >>> 7 | t << 25) + (x7 >>> 11 | x7 << 21) + w + c;
    }

    private final int FF4(int x7, int x6, int x5, int x4, int x3, int x2, int x1, int x0, int w, int c) {
        int t;
        switch (this.rounds) {
            case 4: {
                t = this.f4(x6, x4, x0, x5, x2, x1, x3);
                break;
            }
            default: {
                t = this.f4(x1, x5, x3, x2, x0, x4, x6);
            }
        }
        return (t >>> 7 | t << 25) + (x7 >>> 11 | x7 << 21) + w + c;
    }

    private final int FF5(int x7, int x6, int x5, int x4, int x3, int x2, int x1, int x0, int w, int c) {
        int t = this.f5(x2, x5, x0, x6, x4, x3, x1);
        return (t >>> 7 | t << 25) + (x7 >>> 11 | x7 << 21) + w + c;
    }

    private final int f1(int x6, int x5, int x4, int x3, int x2, int x1, int x0) {
        return x1 & (x0 ^ x4) ^ x2 & x5 ^ x3 & x6 ^ x0;
    }

    private final int f2(int x6, int x5, int x4, int x3, int x2, int x1, int x0) {
        return x2 & (x1 & ~ x3 ^ x4 & x5 ^ x6 ^ x0) ^ x4 & (x1 ^ x5) ^ x3 & x5 ^ x0;
    }

    private final int f3(int x6, int x5, int x4, int x3, int x2, int x1, int x0) {
        return x3 & (x1 & x2 ^ x6 ^ x0) ^ x1 & x4 ^ x2 & x5 ^ x0;
    }

    private final int f4(int x6, int x5, int x4, int x3, int x2, int x1, int x0) {
        return x4 & (x5 & ~ x2 ^ x3 & ~ x6 ^ x1 ^ x6 ^ x0) ^ x3 & (x1 & x2 ^ x5 ^ x6) ^ x2 & x6 ^ x0;
    }

    private final int f5(int x6, int x5, int x4, int x3, int x2, int x1, int x0) {
        return x0 & (x1 & x2 & x3 ^ ~ x5) ^ x1 & x4 ^ x2 & x5 ^ x3 & x6;
    }

    private final /* synthetic */ void this() {
        this.rounds = 3;
    }

    public Haval() {
        this(16, 3);
    }

    public Haval(int size) {
        this(size, 3);
    }

    public Haval(int size, int rounds) {
        super("haval", size, 128);
        this.this();
        if (size != 16 && size != 20 && size != 24 && size != 28 && size != 32) {
            throw new IllegalArgumentException("Invalid HAVAL output size");
        }
        if (rounds != 3 && rounds != 4 && rounds != 5) {
            throw new IllegalArgumentException("Invalid HAVAL number of rounds");
        }
        this.rounds = rounds;
    }

    private Haval(Haval md) {
        this(md.hashSize, md.rounds);
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

