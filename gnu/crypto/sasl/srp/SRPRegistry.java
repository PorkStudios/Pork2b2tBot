/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl.srp;

public interface SRPRegistry {
    public static final String N_2048_BITS = "1";
    public static final String N_1536_BITS = "2";
    public static final String N_1280_BITS = "3";
    public static final String N_1024_BITS = "4";
    public static final String N_768_BITS = "5";
    public static final String N_640_BITS = "6";
    public static final String N_512_BITS = "7";
    public static final String[] SRP_ALGORITHMS = new String[]{"sha-160", "md5", "ripemd128", "ripemd160", "sha-256", "sha-384", "sha-512"};
    public static final String SRP_DEFAULT_DIGEST_NAME = SRP_ALGORITHMS[0];
    public static final String SRP_DIGEST_NAME = "srp.digest.name";
    public static final String SHARED_MODULUS = "srp.N";
    public static final String FIELD_GENERATOR = "srp.g";
    public static final String AVAILABLE_OPTIONS = "srp.L";
    public static final String CHOSEN_OPTIONS = "srp.o";
    public static final String USER_NAME = "srp.U";
    public static final String USER_ROLE = "srp.I";
    public static final String USER_SALT = "srp.s";
    public static final String PASSWORD_VERIFIER = "srp.v";
    public static final String CLIENT_PUBLIC_KEY = "srp.A";
    public static final String SERVER_PUBLIC_KEY = "srp.B";
    public static final String CLIENT_EVIDENCE = "srp.M1";
    public static final String SERVER_EVIDENCE = "srp.M2";
    public static final String SRP_HASH = "gnu.crypto.sasl.srp.hash";
    public static final String SRP_MANDATORY = "gnu.crypto.sasl.srp.mandatory";
    public static final String SRP_REPLAY_DETECTION = "gnu.crypto.sasl.srp.replay.detection";
    public static final String SRP_INTEGRITY_PROTECTION = "gnu.crypto.sasl.srp.integrity";
    public static final String SRP_CONFIDENTIALITY = "gnu.crypto.sasl.srp.confidentiality";
    public static final String PASSWORD_FILE = "gnu.crypto.sasl.srp.password.file";
    public static final String PASSWORD_DB = "gnu.crypto.sasl.srp.password.db";
    public static final String DEFAULT_PASSWORD_FILE = "/etc/tpasswd";
    public static final boolean DEFAULT_REPLAY_DETECTION = true;
    public static final boolean DEFAULT_INTEGRITY = true;
    public static final boolean DEFAULT_CONFIDENTIALITY = false;
    public static final String HMAC_SHA1 = "hmac-sha1";
    public static final String HMAC_MD5 = "hmac-md5";
    public static final String HMAC_RIPEMD_160 = "hmac-ripemd-160";
    public static final String[] INTEGRITY_ALGORITHMS = new String[]{"hmac-sha1", "hmac-md5", "hmac-ripemd-160"};
    public static final String AES = "aes";
    public static final String BLOWFISH = "blowfish";
    public static final String[] CONFIDENTIALITY_ALGORITHMS = new String[]{"aes", "blowfish"};
    public static final String OPTION_MANDATORY = "mandatory";
    public static final String OPTION_SRP_DIGEST = "mda";
    public static final String OPTION_REPLAY_DETECTION = "replay_detection";
    public static final String OPTION_INTEGRITY = "integrity";
    public static final String OPTION_CONFIDENTIALITY = "confidentiality";
    public static final String OPTION_MAX_BUFFER_SIZE = "maxbuffersize";
    public static final String MANDATORY_NONE = "none";
    public static final String DEFAULT_MANDATORY = "replay_detection";
    public static final String MD_NAME_FIELD = "srp.md.name";
    public static final String USER_VERIFIER_FIELD = "srp.user.verifier";
    public static final String SALT_FIELD = "srp.salt";
    public static final String CONFIG_NDX_FIELD = "srp.config.ndx";
    public static final int MINIMUM_MODULUS_BITLENGTH = 512;
}

