/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl;

import java.util.Map;
import javax.security.sasl.AuthenticationException;

public interface IAuthInfoProvider {
    public void activate(Map var1) throws AuthenticationException;

    public void passivate() throws AuthenticationException;

    public boolean contains(String var1) throws AuthenticationException;

    public Map lookup(Map var1) throws AuthenticationException;

    public void update(Map var1) throws AuthenticationException;

    public Map getConfiguration(String var1) throws AuthenticationException;
}

