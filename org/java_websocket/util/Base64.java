/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Base64 {
    public static final int NO_OPTIONS = 0;
    public static final int ENCODE = 1;
    public static final int DECODE = 0;
    public static final int GZIP = 2;
    public static final int DONT_GUNZIP = 4;
    public static final int DO_BREAK_LINES = 8;
    public static final int URL_SAFE = 16;
    public static final int ORDERED = 32;
    private static final int MAX_LINE_LENGTH = 76;
    private static final byte EQUALS_SIGN = 61;
    private static final byte NEW_LINE = 10;
    private static final String PREFERRED_ENCODING = "US-ASCII";
    private static final byte WHITE_SPACE_ENC = -5;
    private static final byte EQUALS_SIGN_ENC = -1;
    private static final byte[] _STANDARD_ALPHABET = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
    private static final byte[] _STANDARD_DECODABET = new byte[]{-9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, -9, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, -9, -9, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9};
    private static final byte[] _URL_SAFE_ALPHABET = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95};
    private static final byte[] _URL_SAFE_DECODABET = new byte[]{-9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, 63, -9, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9};
    private static final byte[] _ORDERED_ALPHABET = new byte[]{45, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 95, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122};
    private static final byte[] _ORDERED_DECODABET = new byte[]{-9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 0, -9, -9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, -9, -9, -9, -1, -9, -9, -9, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, -9, -9, -9, -9, 37, -9, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9};

    private static final byte[] getAlphabet(int options) {
        if ((options & 16) == 16) {
            return _URL_SAFE_ALPHABET;
        }
        if ((options & 32) == 32) {
            return _ORDERED_ALPHABET;
        }
        return _STANDARD_ALPHABET;
    }

    private static final byte[] getDecodabet(int options) {
        if ((options & 16) == 16) {
            return _URL_SAFE_DECODABET;
        }
        if ((options & 32) == 32) {
            return _ORDERED_DECODABET;
        }
        return _STANDARD_DECODABET;
    }

    private Base64() {
    }

    private static byte[] encode3to4(byte[] b4, byte[] threeBytes, int numSigBytes, int options) {
        Base64.encode3to4(threeBytes, 0, numSigBytes, b4, 0, options);
        return b4;
    }

    private static byte[] encode3to4(byte[] source, int srcOffset, int numSigBytes, byte[] destination, int destOffset, int options) {
        byte[] ALPHABET = Base64.getAlphabet(options);
        int inBuff = (numSigBytes > 0 ? source[srcOffset] << 24 >>> 8 : 0) | (numSigBytes > 1 ? source[srcOffset + 1] << 24 >>> 16 : 0) | (numSigBytes > 2 ? source[srcOffset + 2] << 24 >>> 24 : 0);
        switch (numSigBytes) {
            case 3: {
                destination[destOffset] = ALPHABET[inBuff >>> 18];
                destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 63];
                destination[destOffset + 2] = ALPHABET[inBuff >>> 6 & 63];
                destination[destOffset + 3] = ALPHABET[inBuff & 63];
                return destination;
            }
            case 2: {
                destination[destOffset] = ALPHABET[inBuff >>> 18];
                destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 63];
                destination[destOffset + 2] = ALPHABET[inBuff >>> 6 & 63];
                destination[destOffset + 3] = 61;
                return destination;
            }
            case 1: {
                destination[destOffset] = ALPHABET[inBuff >>> 18];
                destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 63];
                destination[destOffset + 2] = 61;
                destination[destOffset + 3] = 61;
                return destination;
            }
        }
        return destination;
    }

    public static String encodeBytes(byte[] source) {
        String encoded;
        block3 : {
            encoded = null;
            try {
                encoded = Base64.encodeBytes(source, 0, source.length, 0);
            }
            catch (IOException ex) {
                if ($assertionsDisabled) break block3;
                throw new AssertionError((Object)ex.getMessage());
            }
        }
        assert (encoded != null);
        return encoded;
    }

    public static String encodeBytes(byte[] source, int off, int len, int options) throws IOException {
        byte[] encoded = Base64.encodeBytesToBytes(source, off, len, options);
        try {
            return new String(encoded, PREFERRED_ENCODING);
        }
        catch (UnsupportedEncodingException uue) {
            return new String(encoded);
        }
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public static byte[] encodeBytesToBytes(byte[] source, int off, int len, int options) throws IOException {
        block22 : {
            if (source == null) {
                throw new IllegalArgumentException("Cannot serialize a null array.");
            }
            if (off < 0) {
                throw new IllegalArgumentException("Cannot have negative offset: " + off);
            }
            if (len < 0) {
                throw new IllegalArgumentException("Cannot have length offset: " + len);
            }
            if (off + len > source.length) {
                throw new IllegalArgumentException(String.format("Cannot have offset of %d and length of %d with array of length %d", new Object[]{off, len, source.length}));
            }
            if ((options & 2) == 0) break block22;
            baos = null;
            gzos = null;
            b64os = null;
            try {
                block21 : {
                    block20 : {
                        baos = new ByteArrayOutputStream();
                        b64os = new OutputStream(baos, 1 | options);
                        gzos = new GZIPOutputStream(b64os);
                        gzos.write(source, off, len);
                        gzos.close();
                        var9_10 = null;
                        gzos.close();
                        break block20;
                        catch (Exception exception2222) {
                            // empty catch block
                        }
                    }
                    b64os.close();
                    break block21;
                    catch (Exception exception2222) {
                        // empty catch block
                    }
                }
                baos.close();
                return baos.toByteArray();
                catch (Exception exception2222) {
                    return baos.toByteArray();
                }
            }
            catch (Throwable throwable) {
                block24 : {
                    block23 : {
                        var9_11 = null;
                        ** try [egrp 2[TRYBLOCK] [3 : 206->214)] { 
lbl45: // 1 sources:
                        gzos.close();
                        break block23;
lbl47: // 1 sources:
                        catch (Exception exception3) {
                            // empty catch block
                        }
                    }
                    ** try [egrp 3[TRYBLOCK] [4 : 216->224)] { 
lbl51: // 1 sources:
                    b64os.close();
                    break block24;
lbl53: // 1 sources:
                    catch (Exception exception3) {
                        // empty catch block
                    }
                }
                ** try [egrp 4[TRYBLOCK] [5 : 226->234)] { 
lbl57: // 1 sources:
                baos.close();
                throw throwable;
lbl59: // 1 sources:
                catch (Exception exception3) {
                    // empty catch block
                }
                throw throwable;
            }
        }
        breakLines = (options & 8) != 0;
        encLen = len / 3 * 4 + (len % 3 > 0 ? 4 : 0);
        if (breakLines) {
            encLen += encLen / 76;
        }
        outBuff = new byte[encLen];
        e = 0;
        len2 = len - 2;
        lineLength = 0;
        for (d = 0; d < len2; d += 3, e += 4) {
            Base64.encode3to4(source, d + off, 3, outBuff, e, options);
            if (!breakLines || (lineLength += 4) < 76) continue;
            outBuff[e + 4] = 10;
            ++e;
            lineLength = 0;
        }
        if (d < len) {
            Base64.encode3to4(source, d + off, len - d, outBuff, e, options);
            e += 4;
        }
        if (e > outBuff.length - 1) return outBuff;
        finalOut = new byte[e];
        System.arraycopy(outBuff, 0, finalOut, 0, e);
        return finalOut;
    }

    private static int decode4to3(byte[] source, int srcOffset, byte[] destination, int destOffset, int options) {
        if (source == null) {
            throw new IllegalArgumentException("Source array was null.");
        }
        if (destination == null) {
            throw new IllegalArgumentException("Destination array was null.");
        }
        if (srcOffset < 0 || srcOffset + 3 >= source.length) {
            throw new IllegalArgumentException(String.format("Source array with length %d cannot have offset of %d and still process four bytes.", source.length, srcOffset));
        }
        if (destOffset < 0 || destOffset + 2 >= destination.length) {
            throw new IllegalArgumentException(String.format("Destination array with length %d cannot have offset of %d and still store three bytes.", destination.length, destOffset));
        }
        byte[] DECODABET = Base64.getDecodabet(options);
        if (source[srcOffset + 2] == 61) {
            int outBuff = (DECODABET[source[srcOffset]] & 255) << 18 | (DECODABET[source[srcOffset + 1]] & 255) << 12;
            destination[destOffset] = (byte)(outBuff >>> 16);
            return 1;
        }
        if (source[srcOffset + 3] == 61) {
            int outBuff = (DECODABET[source[srcOffset]] & 255) << 18 | (DECODABET[source[srcOffset + 1]] & 255) << 12 | (DECODABET[source[srcOffset + 2]] & 255) << 6;
            destination[destOffset] = (byte)(outBuff >>> 16);
            destination[destOffset + 1] = (byte)(outBuff >>> 8);
            return 2;
        }
        int outBuff = (DECODABET[source[srcOffset]] & 255) << 18 | (DECODABET[source[srcOffset + 1]] & 255) << 12 | (DECODABET[source[srcOffset + 2]] & 255) << 6 | DECODABET[source[srcOffset + 3]] & 255;
        destination[destOffset] = (byte)(outBuff >> 16);
        destination[destOffset + 1] = (byte)(outBuff >> 8);
        destination[destOffset + 2] = (byte)outBuff;
        return 3;
    }

    public static byte[] decode(byte[] source) throws IOException {
        return Base64.decode(source, 0, source.length, 0);
    }

    public static byte[] decode(byte[] source, int off, int len, int options) throws IOException {
        if (source == null) {
            throw new IllegalArgumentException("Cannot decode null source array.");
        }
        if (off < 0 || off + len > source.length) {
            throw new IllegalArgumentException(String.format("Source array with length %d cannot have offset of %d and process %d bytes.", source.length, off, len));
        }
        if (len == 0) {
            return new byte[0];
        }
        if (len < 4) {
            throw new IllegalArgumentException("Base64-encoded string must have at least four characters, but length specified was " + len);
        }
        byte[] DECODABET = Base64.getDecodabet(options);
        int len34 = len * 3 / 4;
        byte[] outBuff = new byte[len34];
        int outBuffPosn = 0;
        byte[] b4 = new byte[4];
        int b4Posn = 0;
        for (int i = off; i < off + len; ++i) {
            byte sbiDecode = DECODABET[source[i] & 255];
            if (sbiDecode >= -5) {
                if (sbiDecode < -1) continue;
                b4[b4Posn++] = source[i];
                if (b4Posn <= 3) continue;
                outBuffPosn += Base64.decode4to3(b4, 0, outBuff, outBuffPosn, options);
                b4Posn = 0;
                if (source[i] != 61) continue;
                break;
            }
            throw new IOException(String.format("Bad Base64 input character decimal %d in array position %d", source[i] & 255, i));
        }
        byte[] out = new byte[outBuffPosn];
        System.arraycopy(outBuff, 0, out, 0, outBuffPosn);
        return out;
    }

    public static byte[] decode(String s) throws IOException {
        return Base64.decode(s, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public static byte[] decode(String s, int options) throws IOException {
        if (s == null) {
            throw new IllegalArgumentException("Input string was null.");
        }
        try {
            bytes = s.getBytes("US-ASCII");
        }
        catch (UnsupportedEncodingException uee) {
            bytes = s.getBytes();
        }
        bytes = Base64.decode(bytes, 0, bytes.length, options);
        dontGunzip = (options & 4) != 0;
        if (bytes == null) return bytes;
        if (bytes.length < 4) return bytes;
        if (dontGunzip != false) return bytes;
        head = bytes[0] & 255 | bytes[1] << 8 & 65280;
        if (35615 != head) return bytes;
        bais = null;
        gzis = null;
        baos = null;
        buffer = new byte[2048];
        try {
            block21 : {
                block20 : {
                    try {
                        baos = new ByteArrayOutputStream();
                        bais = new ByteArrayInputStream(bytes);
                        gzis = new GZIPInputStream(bais);
                        while ((length = gzis.read(buffer)) >= 0) {
                            baos.write(buffer, 0, length);
                        }
                        bytes = baos.toByteArray();
                    }
                    catch (IOException e) {
                        block23 : {
                            block22 : {
                                e.printStackTrace();
                                var12_12 = null;
                                ** try [egrp 3[TRYBLOCK] [5 : 198->206)] { 
lbl33: // 1 sources:
                                baos.close();
                                break block22;
lbl35: // 1 sources:
                                catch (Exception exception32222) {
                                    // empty catch block
                                }
                            }
                            ** try [egrp 4[TRYBLOCK] [6 : 208->216)] { 
lbl39: // 1 sources:
                            gzis.close();
                            break block23;
lbl41: // 1 sources:
                            catch (Exception exception32222) {
                                // empty catch block
                            }
                        }
                        try {}
                        catch (Exception exception32222) {
                            return bytes;
                        }
                        bais.close();
                        return bytes;
                    }
                    var12_11 = null;
                    baos.close();
                    break block20;
                    catch (Exception exception2222) {
                        // empty catch block
                    }
                }
                gzis.close();
                break block21;
                catch (Exception exception2222) {
                    // empty catch block
                }
            }
            bais.close();
            return bytes;
            catch (Exception exception2222) {
                return bytes;
            }
        }
        catch (Throwable throwable) {
            block25 : {
                block24 : {
                    var12_13 = null;
                    ** try [egrp 3[TRYBLOCK] [5 : 198->206)] { 
lbl72: // 1 sources:
                    baos.close();
                    break block24;
lbl74: // 1 sources:
                    catch (Exception exception4) {
                        // empty catch block
                    }
                }
                ** try [egrp 4[TRYBLOCK] [6 : 208->216)] { 
lbl78: // 1 sources:
                gzis.close();
                break block25;
lbl80: // 1 sources:
                catch (Exception exception4) {
                    // empty catch block
                }
            }
            ** try [egrp 5[TRYBLOCK] [7 : 218->226)] { 
lbl84: // 1 sources:
            bais.close();
            throw throwable;
lbl86: // 1 sources:
            catch (Exception exception4) {
                // empty catch block
            }
            throw throwable;
        }
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public static byte[] decodeFromFile(String filename) throws IOException {
        bis = null;
        try {
            file = new File(filename);
            length = 0;
            if (file.length() > Integer.MAX_VALUE) {
                throw new IOException("File is too big for this convenience method (" + file.length() + " bytes).");
            }
            buffer = new byte[(int)file.length()];
            bis = new InputStream(new BufferedInputStream(new FileInputStream(file)), 0);
            while ((numBytes = bis.read(buffer, length, 4096)) >= 0) {
                length += numBytes;
            }
            decodedData = new byte[length];
            System.arraycopy(buffer, 0, decodedData, 0, length);
            var8_8 = null;
            bis.close();
            return decodedData;
            catch (Exception exception) {
                return decodedData;
            }
        }
        catch (Throwable throwable) {
            var8_9 = null;
            ** try [egrp 2[TRYBLOCK] [3 : 154->161)] { 
lbl28: // 1 sources:
            bis.close();
            throw throwable;
lbl30: // 1 sources:
            catch (Exception exception) {
                // empty catch block
            }
            throw throwable;
        }
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public static String encodeFromFile(String filename) throws IOException {
        bis = null;
        try {
            file = new File(filename);
            buffer = new byte[Math.max((int)((double)file.length() * 1.4 + 1.0), 40)];
            length = 0;
            bis = new InputStream(new BufferedInputStream(new FileInputStream(file)), 1);
            while ((numBytes = bis.read(buffer, length, 4096)) >= 0) {
                length += numBytes;
            }
            encodedData = new String(buffer, 0, length, "US-ASCII");
            var8_8 = null;
            bis.close();
            return encodedData;
            catch (Exception exception) {
                return encodedData;
            }
        }
        catch (Throwable throwable) {
            var8_9 = null;
            ** try [egrp 2[TRYBLOCK] [3 : 120->127)] { 
lbl25: // 1 sources:
            bis.close();
            throw throwable;
lbl27: // 1 sources:
            catch (Exception exception) {
                // empty catch block
            }
            throw throwable;
        }
    }

    public static class OutputStream
    extends FilterOutputStream {
        private boolean encode;
        private int position;
        private byte[] buffer;
        private int bufferLength;
        private int lineLength;
        private boolean breakLines;
        private byte[] b4;
        private boolean suspendEncoding;
        private int options;
        private byte[] decodabet;

        public OutputStream(java.io.OutputStream out) {
            this(out, 1);
        }

        public OutputStream(java.io.OutputStream out, int options) {
            super(out);
            this.breakLines = (options & 8) != 0;
            this.encode = (options & 1) != 0;
            this.bufferLength = this.encode ? 3 : 4;
            this.buffer = new byte[this.bufferLength];
            this.position = 0;
            this.lineLength = 0;
            this.suspendEncoding = false;
            this.b4 = new byte[4];
            this.options = options;
            this.decodabet = Base64.getDecodabet(options);
        }

        public void write(int theByte) throws IOException {
            if (this.suspendEncoding) {
                this.out.write(theByte);
                return;
            }
            if (this.encode) {
                this.buffer[this.position++] = (byte)theByte;
                if (this.position >= this.bufferLength) {
                    this.out.write(Base64.encode3to4(this.b4, this.buffer, this.bufferLength, this.options));
                    this.lineLength += 4;
                    if (this.breakLines && this.lineLength >= 76) {
                        this.out.write(10);
                        this.lineLength = 0;
                    }
                    this.position = 0;
                }
            } else if (this.decodabet[theByte & 127] > -5) {
                this.buffer[this.position++] = (byte)theByte;
                if (this.position >= this.bufferLength) {
                    int len = Base64.decode4to3(this.buffer, 0, this.b4, 0, this.options);
                    this.out.write(this.b4, 0, len);
                    this.position = 0;
                }
            } else if (this.decodabet[theByte & 127] != -5) {
                throw new IOException("Invalid character in Base64 data.");
            }
        }

        public void write(byte[] theBytes, int off, int len) throws IOException {
            if (this.suspendEncoding) {
                this.out.write(theBytes, off, len);
                return;
            }
            for (int i = 0; i < len; ++i) {
                this.write(theBytes[off + i]);
            }
        }

        public void flushBase64() throws IOException {
            if (this.position > 0) {
                if (this.encode) {
                    this.out.write(Base64.encode3to4(this.b4, this.buffer, this.position, this.options));
                    this.position = 0;
                } else {
                    throw new IOException("Base64 input not properly padded.");
                }
            }
        }

        public void close() throws IOException {
            this.flushBase64();
            super.close();
            this.buffer = null;
            this.out = null;
        }

        public void suspendEncoding() throws IOException {
            this.flushBase64();
            this.suspendEncoding = true;
        }

        public void resumeEncoding() {
            this.suspendEncoding = false;
        }
    }

    public static class InputStream
    extends FilterInputStream {
        private boolean encode;
        private int position;
        private byte[] buffer;
        private int bufferLength;
        private int numSigBytes;
        private int lineLength;
        private boolean breakLines;
        private int options;
        private byte[] decodabet;

        public InputStream(java.io.InputStream in) {
            this(in, 0);
        }

        public InputStream(java.io.InputStream in, int options) {
            super(in);
            this.options = options;
            this.breakLines = (options & 8) > 0;
            this.encode = (options & 1) > 0;
            this.bufferLength = this.encode ? 4 : 3;
            this.buffer = new byte[this.bufferLength];
            this.position = -1;
            this.lineLength = 0;
            this.decodabet = Base64.getDecodabet(options);
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public int read() throws IOException {
            if (this.position < 0) {
                if (this.encode) {
                    int b;
                    byte[] b3 = new byte[3];
                    int numBinaryBytes = 0;
                    for (int i = 0; i < 3 && (b = this.in.read()) >= 0; ++i) {
                        b3[i] = (byte)b;
                        ++numBinaryBytes;
                    }
                    if (numBinaryBytes <= 0) return -1;
                    Base64.encode3to4(b3, 0, numBinaryBytes, this.buffer, 0, this.options);
                    this.position = 0;
                    this.numSigBytes = 4;
                } else {
                    byte[] b4;
                    int i;
                    block11 : {
                        b4 = new byte[4];
                        i = 0;
                        while (i < 4) {
                            int b;
                            while ((b = this.in.read()) >= 0 && this.decodabet[b & 127] <= -5) {
                            }
                            if (b >= 0) {
                                b4[i] = (byte)b;
                                ++i;
                                continue;
                            }
                            break block11;
                        }
                        return -1;
                    }
                    if (i == 4) {
                        this.numSigBytes = Base64.decode4to3(b4, 0, this.buffer, 0, this.options);
                        this.position = 0;
                    } else {
                        if (i != 0) throw new IOException("Improperly padded Base64 input.");
                        return -1;
                    }
                }
            }
            if (this.position < 0) throw new IOException("Error in Base64 code reading stream.");
            if (this.position >= this.numSigBytes) {
                return -1;
            }
            if (this.encode && this.breakLines && this.lineLength >= 76) {
                this.lineLength = 0;
                return 10;
            }
            ++this.lineLength;
            byte b = this.buffer[this.position++];
            if (this.position < this.bufferLength) return b & 255;
            this.position = -1;
            return b & 255;
        }

        public int read(byte[] dest, int off, int len) throws IOException {
            int i;
            for (i = 0; i < len; ++i) {
                int b = this.read();
                if (b < 0) {
                    if (i != 0) break;
                    return -1;
                }
                dest[off + i] = (byte)b;
            }
            return i;
        }
    }

}

