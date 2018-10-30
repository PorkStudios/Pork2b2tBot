/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl.srp;

import gnu.crypto.sasl.IAuthInfoProvider;
import gnu.crypto.sasl.NoSuchUserException;
import gnu.crypto.sasl.srp.PasswordFile;
import gnu.crypto.util.Util;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.security.sasl.AuthenticationException;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public class SRPAuthInfoProvider
implements IAuthInfoProvider {
    private PasswordFile passwordFile;

    public void activate(Map context) throws AuthenticationException {
        try {
            if (context == null) {
                this.passwordFile = new PasswordFile();
            } else {
                this.passwordFile = (PasswordFile)context.get("gnu.crypto.sasl.srp.password.db");
                if (this.passwordFile == null) {
                    String pfn = (String)context.get("gnu.crypto.sasl.srp.password.file");
                    this.passwordFile = pfn == null ? new PasswordFile() : new PasswordFile(pfn);
                }
            }
        }
        catch (IOException x) {
            throw new AuthenticationException("activate()", x);
        }
    }

    public void passivate() throws AuthenticationException {
        this.passwordFile = null;
    }

    public boolean contains(String userName) throws AuthenticationException {
        if (this.passwordFile == null) {
            throw new AuthenticationException("contains()", new IllegalStateException());
        }
        boolean result = false;
        try {
            result = this.passwordFile.contains(userName);
        }
        catch (IOException x) {
            throw new AuthenticationException("contains()", x);
        }
        return result;
    }

    public Map lookup(Map userID) throws AuthenticationException {
        if (this.passwordFile == null) {
            throw new AuthenticationException("lookup()", new IllegalStateException());
        }
        HashMap<String, String> result = new HashMap<String, String>();
        try {
            String userName = (String)userID.get("gnu.crypto.sasl.username");
            if (userName == null) {
                throw new NoSuchUserException("");
            }
            String mdName = (String)userID.get("srp.md.name");
            String[] data = this.passwordFile.lookup(userName, mdName);
            result.put("srp.user.verifier", data[0]);
            result.put("srp.salt", data[1]);
            result.put("srp.config.ndx", data[2]);
        }
        catch (Exception x) {
            if (x instanceof AuthenticationException) {
                throw (AuthenticationException)x;
            }
            throw new AuthenticationException("lookup()", x);
        }
        return result;
    }

    public void update(Map userCredentials) throws AuthenticationException {
        if (this.passwordFile == null) {
            throw new AuthenticationException("update()", new IllegalStateException());
        }
        try {
            String userName = (String)userCredentials.get("gnu.crypto.sasl.username");
            String password = (String)userCredentials.get("gnu.crypto.sasl.password");
            String salt = (String)userCredentials.get("srp.salt");
            String config = (String)userCredentials.get("srp.config.ndx");
            if (salt == null || config == null) {
                this.passwordFile.changePasswd(userName, password);
            } else {
                this.passwordFile.add(userName, password, Util.fromBase64(salt), config);
            }
        }
        catch (Exception x) {
            if (x instanceof AuthenticationException) {
                throw (AuthenticationException)x;
            }
            throw new AuthenticationException("update()", x);
        }
    }

    public Map getConfiguration(String mode) throws AuthenticationException {
        if (this.passwordFile == null) {
            throw new AuthenticationException("getConfiguration()", new IllegalStateException());
        }
        HashMap<String, String> result = new HashMap<String, String>();
        try {
            String[] data = this.passwordFile.lookupConfig(mode);
            result.put("srp.N", data[0]);
            result.put("srp.g", data[1]);
        }
        catch (Exception x) {
            if (x instanceof AuthenticationException) {
                throw (AuthenticationException)x;
            }
            throw new AuthenticationException("getConfiguration()", x);
        }
        return result;
    }

    private final /* synthetic */ void this() {
        this.passwordFile = null;
    }

    public SRPAuthInfoProvider() {
        this.this();
    }
}

