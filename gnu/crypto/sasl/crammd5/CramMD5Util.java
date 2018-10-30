/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl.crammd5;

import gnu.crypto.mac.HMacFactory;
import gnu.crypto.mac.IMac;
import gnu.crypto.util.Util;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;
import javax.security.sasl.SaslException;

class CramMD5Util {
    static byte[] createMsgID() throws SaslException {
        String encoded;
        byte[] result;
        String hostname;
        try {
            encoded = Util.toBase64(Thread.currentThread().getName().getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException x) {
            throw new SaslException("createMsgID()", x);
        }
        hostname = "localhost";
        try {
            hostname = InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException unknownHostException) {}
        try {
            result = ("<" + encoded.substring(0, encoded.length()) + "." + String.valueOf(System.currentTimeMillis()) + "@" + hostname + ">").getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException x) {
            throw new SaslException("createMsgID()", x);
        }
        return result;
    }

    static byte[] createHMac(char[] passwd, byte[] data) throws InvalidKeyException, SaslException {
        byte[] km;
        IMac mac = HMacFactory.getInstance("hmac-md5");
        HashMap<String, byte[]> map = new HashMap<String, byte[]>();
        try {
            km = new String(passwd).getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException x) {
            throw new SaslException("createHMac()", x);
        }
        map.put("gnu.crypto.mac.key.material", km);
        mac.init(map);
        mac.update(data, 0, data.length);
        return mac.digest();
    }

    private CramMD5Util() {
    }
}

