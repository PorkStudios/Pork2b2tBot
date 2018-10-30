/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl.plain;

import gnu.crypto.sasl.IAuthInfoProvider;
import gnu.crypto.sasl.NoSuchUserException;
import gnu.crypto.sasl.plain.PasswordFile;
import gnu.crypto.sasl.plain.PlainRegistry;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.security.sasl.AuthenticationException;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public class PlainAuthInfoProvider
implements IAuthInfoProvider,
PlainRegistry {
    private PasswordFile passwordFile;

    public void activate(Map context) throws AuthenticationException {
        try {
            String pfn;
            this.passwordFile = context == null ? new PasswordFile() : ((pfn = (String)context.get("gnu.crypto.sasl.plain.password.file")) == null ? new PasswordFile() : new PasswordFile(pfn));
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
            String[] data = this.passwordFile.lookup(userName);
            result.put("gnu.crypto.sasl.username", data[0]);
            result.put("gnu.crypto.sasl.password", data[1]);
            result.put("plain.uid", data[2]);
            result.put("plain.gid", data[3]);
            result.put("plain.gecos", data[4]);
            result.put("plain.dir", data[5]);
            result.put("plain.shell", data[6]);
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
            String uid = (String)userCredentials.get("plain.uid");
            String gid = (String)userCredentials.get("plain.gid");
            String gecos = (String)userCredentials.get("plain.gecos");
            String dir = (String)userCredentials.get("plain.dir");
            String shell = (String)userCredentials.get("plain.shell");
            if (uid == null || gid == null || gecos == null || dir == null || shell == null) {
                this.passwordFile.changePasswd(userName, password);
            } else {
                String[] attributes = new String[]{uid, gid, gecos, dir, shell};
                this.passwordFile.add(userName, password, attributes);
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
        throw new AuthenticationException("", new UnsupportedOperationException());
    }

    private final /* synthetic */ void this() {
        this.passwordFile = null;
    }

    public PlainAuthInfoProvider() {
        this.this();
    }
}

