/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.hash;

import gnu.crypto.hash.BaseHash;
import gnu.crypto.util.Util;

public class MD2
extends BaseHash {
    private static final int DIGEST_LENGTH = 16;
    private static final int BLOCK_LENGTH = 16;
    private static final byte[] PI;
    private static final String DIGEST0 = "8350E5A3E24C153DF2275C9F80692773";
    private static Boolean valid;
    private byte[] checksum;
    private byte[] work;

    public Object clone() {
        return new MD2(this);
    }

    protected byte[] getResult() {
        byte[] result = new byte[16];
        this.encryptBlock(this.checksum, 0);
        int i = 0;
        while (i < 16) {
            result[i] = this.work[i];
            ++i;
        }
        return result;
    }

    protected void resetContext() {
        this.checksum = new byte[16];
        this.work = new byte[48];
    }

    public boolean selfTest() {
        if (valid == null) {
            valid = new Boolean(DIGEST0.equals(Util.toString(new MD2().digest())));
        }
        return valid;
    }

    protected byte[] padBuffer() {
        int length = 16 - (int)(this.count % 16L);
        if (length == 0) {
            length = 16;
        }
        byte[] pad = new byte[length];
        int i = 0;
        while (i < length) {
            pad[i] = (byte)length;
            ++i;
        }
        return pad;
    }

    protected void transform(byte[] in, int off) {
        this.updateCheckSumAndEncryptBlock(in, off);
    }

    private final void encryptBlock(byte[] in, int off) {
        int i = 0;
        while (i < 16) {
            byte b;
            this.work[16 + i] = b = in[off + i];
            this.work[32 + i] = (byte)(this.work[i] ^ b);
            ++i;
        }
        int t = 0;
        int i2 = 0;
        while (i2 < 18) {
            int j = 0;
            while (j < 48) {
                this.work[j] = t = (int)((byte)(this.work[j] ^ PI[t & 255]));
                ++j;
            }
            t = (byte)(t + i2);
            ++i2;
        }
    }

    private final void updateCheckSumAndEncryptBlock(byte[] in, int off) {
        byte l = this.checksum[15];
        int i = 0;
        while (i < 16) {
            byte b;
            this.work[16 + i] = b = in[off + i];
            this.work[32 + i] = (byte)(this.work[i] ^ b);
            this.checksum[i] = l = (byte)(this.checksum[i] ^ PI[(b ^ l) & 255]);
            ++i;
        }
        int t = 0;
        int i2 = 0;
        while (i2 < 18) {
            int j = 0;
            while (j < 48) {
                this.work[j] = t = (int)((byte)(this.work[j] ^ PI[t & 255]));
                ++j;
            }
            t = (byte)(t + i2);
            ++i2;
        }
    }

    public MD2() {
        super("md2", 16, 16);
    }

    private MD2(MD2 md2) {
        this();
        this.count = md2.count;
        this.buffer = (byte[])md2.buffer.clone();
        this.checksum = (byte[])md2.checksum.clone();
        this.work = (byte[])md2.work.clone();
    }

    private static final {
        byte[] arrby = new byte[256];
        arrby[0] = 41;
        arrby[1] = 46;
        arrby[2] = 67;
        arrby[3] = -55;
        arrby[4] = -94;
        arrby[5] = -40;
        arrby[6] = 124;
        arrby[7] = 1;
        arrby[8] = 61;
        arrby[9] = 54;
        arrby[10] = 84;
        arrby[11] = -95;
        arrby[12] = -20;
        arrby[13] = -16;
        arrby[14] = 6;
        arrby[15] = 19;
        arrby[16] = 98;
        arrby[17] = -89;
        arrby[18] = 5;
        arrby[19] = -13;
        arrby[20] = -64;
        arrby[21] = -57;
        arrby[22] = 115;
        arrby[23] = -116;
        arrby[24] = -104;
        arrby[25] = -109;
        arrby[26] = 43;
        arrby[27] = -39;
        arrby[28] = -68;
        arrby[29] = 76;
        arrby[30] = -126;
        arrby[31] = -54;
        arrby[32] = 30;
        arrby[33] = -101;
        arrby[34] = 87;
        arrby[35] = 60;
        arrby[36] = -3;
        arrby[37] = -44;
        arrby[38] = -32;
        arrby[39] = 22;
        arrby[40] = 103;
        arrby[41] = 66;
        arrby[42] = 111;
        arrby[43] = 24;
        arrby[44] = -118;
        arrby[45] = 23;
        arrby[46] = -27;
        arrby[47] = 18;
        arrby[48] = -66;
        arrby[49] = 78;
        arrby[50] = -60;
        arrby[51] = -42;
        arrby[52] = -38;
        arrby[53] = -98;
        arrby[54] = -34;
        arrby[55] = 73;
        arrby[56] = -96;
        arrby[57] = -5;
        arrby[58] = -11;
        arrby[59] = -114;
        arrby[60] = -69;
        arrby[61] = 47;
        arrby[62] = -18;
        arrby[63] = 122;
        arrby[64] = -87;
        arrby[65] = 104;
        arrby[66] = 121;
        arrby[67] = -111;
        arrby[68] = 21;
        arrby[69] = -78;
        arrby[70] = 7;
        arrby[71] = 63;
        arrby[72] = -108;
        arrby[73] = -62;
        arrby[74] = 16;
        arrby[75] = -119;
        arrby[76] = 11;
        arrby[77] = 34;
        arrby[78] = 95;
        arrby[79] = 33;
        arrby[80] = -128;
        arrby[81] = 127;
        arrby[82] = 93;
        arrby[83] = -102;
        arrby[84] = 90;
        arrby[85] = -112;
        arrby[86] = 50;
        arrby[87] = 39;
        arrby[88] = 53;
        arrby[89] = 62;
        arrby[90] = -52;
        arrby[91] = -25;
        arrby[92] = -65;
        arrby[93] = -9;
        arrby[94] = -105;
        arrby[95] = 3;
        arrby[96] = -1;
        arrby[97] = 25;
        arrby[98] = 48;
        arrby[99] = -77;
        arrby[100] = 72;
        arrby[101] = -91;
        arrby[102] = -75;
        arrby[103] = -47;
        arrby[104] = -41;
        arrby[105] = 94;
        arrby[106] = -110;
        arrby[107] = 42;
        arrby[108] = -84;
        arrby[109] = 86;
        arrby[110] = -86;
        arrby[111] = -58;
        arrby[112] = 79;
        arrby[113] = -72;
        arrby[114] = 56;
        arrby[115] = -46;
        arrby[116] = -106;
        arrby[117] = -92;
        arrby[118] = 125;
        arrby[119] = -74;
        arrby[120] = 118;
        arrby[121] = -4;
        arrby[122] = 107;
        arrby[123] = -30;
        arrby[124] = -100;
        arrby[125] = 116;
        arrby[126] = 4;
        arrby[127] = -15;
        arrby[128] = 69;
        arrby[129] = -99;
        arrby[130] = 112;
        arrby[131] = 89;
        arrby[132] = 100;
        arrby[133] = 113;
        arrby[134] = -121;
        arrby[135] = 32;
        arrby[136] = -122;
        arrby[137] = 91;
        arrby[138] = -49;
        arrby[139] = 101;
        arrby[140] = -26;
        arrby[141] = 45;
        arrby[142] = -88;
        arrby[143] = 2;
        arrby[144] = 27;
        arrby[145] = 96;
        arrby[146] = 37;
        arrby[147] = -83;
        arrby[148] = -82;
        arrby[149] = -80;
        arrby[150] = -71;
        arrby[151] = -10;
        arrby[152] = 28;
        arrby[153] = 70;
        arrby[154] = 97;
        arrby[155] = 105;
        arrby[156] = 52;
        arrby[157] = 64;
        arrby[158] = 126;
        arrby[159] = 15;
        arrby[160] = 85;
        arrby[161] = 71;
        arrby[162] = -93;
        arrby[163] = 35;
        arrby[164] = -35;
        arrby[165] = 81;
        arrby[166] = -81;
        arrby[167] = 58;
        arrby[168] = -61;
        arrby[169] = 92;
        arrby[170] = -7;
        arrby[171] = -50;
        arrby[172] = -70;
        arrby[173] = -59;
        arrby[174] = -22;
        arrby[175] = 38;
        arrby[176] = 44;
        arrby[177] = 83;
        arrby[178] = 13;
        arrby[179] = 110;
        arrby[180] = -123;
        arrby[181] = 40;
        arrby[182] = -124;
        arrby[183] = 9;
        arrby[184] = -45;
        arrby[185] = -33;
        arrby[186] = -51;
        arrby[187] = -12;
        arrby[188] = 65;
        arrby[189] = -127;
        arrby[190] = 77;
        arrby[191] = 82;
        arrby[192] = 106;
        arrby[193] = -36;
        arrby[194] = 55;
        arrby[195] = -56;
        arrby[196] = 108;
        arrby[197] = -63;
        arrby[198] = -85;
        arrby[199] = -6;
        arrby[200] = 36;
        arrby[201] = -31;
        arrby[202] = 123;
        arrby[203] = 8;
        arrby[204] = 12;
        arrby[205] = -67;
        arrby[206] = -79;
        arrby[207] = 74;
        arrby[208] = 120;
        arrby[209] = -120;
        arrby[210] = -107;
        arrby[211] = -117;
        arrby[212] = -29;
        arrby[213] = 99;
        arrby[214] = -24;
        arrby[215] = 109;
        arrby[216] = -23;
        arrby[217] = -53;
        arrby[218] = -43;
        arrby[219] = -2;
        arrby[220] = 59;
        arrby[222] = 29;
        arrby[223] = 57;
        arrby[224] = -14;
        arrby[225] = -17;
        arrby[226] = -73;
        arrby[227] = 14;
        arrby[228] = 102;
        arrby[229] = 88;
        arrby[230] = -48;
        arrby[231] = -28;
        arrby[232] = -90;
        arrby[233] = 119;
        arrby[234] = 114;
        arrby[235] = -8;
        arrby[236] = -21;
        arrby[237] = 117;
        arrby[238] = 75;
        arrby[239] = 10;
        arrby[240] = 49;
        arrby[241] = 68;
        arrby[242] = 80;
        arrby[243] = -76;
        arrby[244] = -113;
        arrby[245] = -19;
        arrby[246] = 31;
        arrby[247] = 26;
        arrby[248] = -37;
        arrby[249] = -103;
        arrby[250] = -115;
        arrby[251] = 51;
        arrby[252] = -97;
        arrby[253] = 17;
        arrby[254] = -125;
        arrby[255] = 20;
        PI = arrby;
    }
}

