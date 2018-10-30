/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto;

public interface Registry {
    public static final String GNU_CRYPTO = "GNU-CRYPTO";
    public static final String ANUBIS_CIPHER = "anubis";
    public static final String BLOWFISH_CIPHER = "blowfish";
    public static final String DES_CIPHER = "des";
    public static final String KHAZAD_CIPHER = "khazad";
    public static final String RIJNDAEL_CIPHER = "rijndael";
    public static final String SERPENT_CIPHER = "serpent";
    public static final String SQUARE_CIPHER = "square";
    public static final String TRIPLEDES_CIPHER = "tripledes";
    public static final String TWOFISH_CIPHER = "twofish";
    public static final String CAST5_CIPHER = "cast5";
    public static final String NULL_CIPHER = "null";
    public static final String AES_CIPHER = "aes";
    public static final String DESEDE_CIPHER = "desede";
    public static final String CAST128_CIPHER = "cast128";
    public static final String CAST_128_CIPHER = "cast-128";
    public static final String WHIRLPOOL_HASH = "whirlpool";
    public static final String RIPEMD128_HASH = "ripemd128";
    public static final String RIPEMD160_HASH = "ripemd160";
    public static final String SHA160_HASH = "sha-160";
    public static final String SHA256_HASH = "sha-256";
    public static final String SHA384_HASH = "sha-384";
    public static final String SHA512_HASH = "sha-512";
    public static final String TIGER_HASH = "tiger";
    public static final String HAVAL_HASH = "haval";
    public static final String MD5_HASH = "md5";
    public static final String MD4_HASH = "md4";
    public static final String MD2_HASH = "md2";
    public static final String RIPEMD_128_HASH = "ripemd-128";
    public static final String RIPEMD_160_HASH = "ripemd-160";
    public static final String SHA_1_HASH = "sha-1";
    public static final String SHA1_HASH = "sha1";
    public static final String SHA_HASH = "sha";
    public static final String ECB_MODE = "ecb";
    public static final String CTR_MODE = "ctr";
    public static final String ICM_MODE = "icm";
    public static final String OFB_MODE = "ofb";
    public static final String CBC_MODE = "cbc";
    public static final String CFB_MODE = "cfb";
    public static final String PKCS7_PAD = "pkcs7";
    public static final String TBC_PAD = "tbc";
    public static final String EME_PKCS1_V1_5_PAD = "eme-pkcs1-v1.5";
    public static final String ARCFOUR_PRNG = "arcfour";
    public static final String RC4_PRNG = "rc4";
    public static final String ICM_PRNG = "icm";
    public static final String MD_PRNG = "md";
    public static final String UMAC_PRNG = "umac-kdf";
    public static final String PBKDF2_PRNG_PREFIX = "pbkdf2-";
    public static final String DSS_KPG = "dss";
    public static final String RSA_KPG = "rsa";
    public static final String DH_KPG = "dh";
    public static final String SRP_KPG = "srp";
    public static final String DSA_KPG = "dsa";
    public static final String DSS_SIG = "dss";
    public static final String RSA_PSS_SIG = "rsa-pss";
    public static final String RSA_PKCS1_V1_5_SIG = "rsa-pkcs1-v1.5";
    public static final String DSA_SIG = "dsa";
    public static final String DH_KA = "dh";
    public static final String ELGAMAL_KA = "elgamal";
    public static final String SRP6_KA = "srp6";
    public static final String SRP_SASL_KA = "srp-sasl";
    public static final String SRP_TLS_KA = "srp-tls";
    public static final String HMAC_NAME_PREFIX = "hmac-";
    public static final String UHASH32 = "uhash32";
    public static final String UMAC32 = "umac32";
    public static final String TMMH16 = "tmmh16";
    public static final String RAW_ENCODING = "gnu.crypto.raw.format";
    public static final int RAW_ENCODING_ID = 1;
    public static final byte[] MAGIC_RAW_DSS_PUBLIC_KEY = new byte[]{71, 1, 68, 80};
    public static final byte[] MAGIC_RAW_DSS_PRIVATE_KEY = new byte[]{71, 1, 68, 112};
    public static final byte[] MAGIC_RAW_DSS_SIGNATURE = new byte[]{71, 1, 68, 83};
    public static final byte[] MAGIC_RAW_RSA_PUBLIC_KEY = new byte[]{71, 1, 82, 80};
    public static final byte[] MAGIC_RAW_RSA_PRIVATE_KEY = new byte[]{71, 1, 82, 112};
    public static final byte[] MAGIC_RAW_RSA_PSS_SIGNATURE = new byte[]{71, 1, 82, 83};
    public static final byte[] MAGIC_RAW_DH_PUBLIC_KEY = new byte[]{71, 1, 72, 80};
    public static final byte[] MAGIC_RAW_DH_PRIVATE_KEY = new byte[]{71, 1, 72, 112};
    public static final byte[] MAGIC_RAW_SRP_PUBLIC_KEY = new byte[]{71, 1, 83, 80};
    public static final byte[] MAGIC_RAW_SRP_PRIVATE_KEY = new byte[]{71, 1, 83, 112};
    public static final String SASL_PREFIX = "gnu.crypto.sasl";
    public static final String SASL_USERNAME = "gnu.crypto.sasl.username";
    public static final String SASL_PASSWORD = "gnu.crypto.sasl.password";
    public static final String SASL_AUTH_INFO_PROVIDER_PKGS = "gnu.crypto.sasl.auth.info.provider.pkgs";
    public static final String SASL_AUTHORISATION_ID = "gnu.crypto.sasl.authorisation.ID";
    public static final String SASL_PROTOCOL = "gnu.crypto.sasl.protocol";
    public static final String SASL_SERVER_NAME = "gnu.crypto.sasl.server.name";
    public static final String SASL_CALLBACK_HANDLER = "gnu.crypto.sasl.callback.handler";
    public static final String SASL_CHANNEL_BINDING = "gnu.crypto.sasl.channel.binding";
    public static final int SASL_ONE_BYTE_MAX_LIMIT = 255;
    public static final int SASL_TWO_BYTE_MAX_LIMIT = 65535;
    public static final int SASL_FOUR_BYTE_MAX_LIMIT = 2147483383;
    public static final int SASL_BUFFER_MAX_LIMIT = 2147483643;
    public static final String SASL_ANONYMOUS_MECHANISM = "ANONYMOUS";
    public static final String SASL_CRAM_MD5_MECHANISM = "CRAM-MD5";
    public static final String SASL_PLAIN_MECHANISM = "PLAIN";
    public static final String SASL_SRP_MECHANISM = "SRP";
    public static final String SASL_HMAC_MD5_IALG = "HMACwithMD5";
    public static final String SASL_HMAC_SHA_IALG = "HMACwithSHA";
    public static final String QOP_AUTH = "auth";
    public static final String QOP_AUTH_INT = "auth-int";
    public static final String QOP_AUTH_CONF = "auth-conf";
    public static final String STRENGTH_HIGH = "high";
    public static final String STRENGTH_MEDIUM = "medium";
    public static final String STRENGTH_LOW = "low";
    public static final String SERVER_AUTH_TRUE = "true";
    public static final String SERVER_AUTH_FALSE = "false";
    public static final String REUSE_TRUE = "true";
    public static final String REUSE_FALSE = "false";
    public static final byte[] GKR_MAGIC = new byte[]{71, 75, 82, 1};
    public static final int GKR_PRIVATE_KEYS = 0;
    public static final int GKR_PUBLIC_CREDENTIALS = 1;
    public static final int GKR_CERTIFICATES = 3;
    public static final int GKR_HMAC_MD5_128 = 0;
    public static final int GKR_HMAC_SHA_160 = 1;
    public static final int GKR_HMAC_MD5_96 = 2;
    public static final int GKR_HMAC_SHA_96 = 3;
    public static final int GKR_CIPHER_AES_128_OFB = 0;
    public static final int GKR_CIPHER_AES_128_CBC = 1;
}

