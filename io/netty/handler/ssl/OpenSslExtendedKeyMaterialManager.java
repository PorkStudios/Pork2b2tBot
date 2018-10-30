/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.OpenSslKeyMaterialManager;
import io.netty.handler.ssl.ReferenceCountedOpenSslEngine;
import java.security.Principal;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;
import javax.security.auth.x500.X500Principal;

final class OpenSslExtendedKeyMaterialManager
extends OpenSslKeyMaterialManager {
    private final X509ExtendedKeyManager keyManager;

    OpenSslExtendedKeyMaterialManager(X509ExtendedKeyManager keyManager, String password) {
        super(keyManager, password);
        this.keyManager = keyManager;
    }

    @Override
    protected String chooseClientAlias(ReferenceCountedOpenSslEngine engine, String[] keyTypes, X500Principal[] issuer) {
        return this.keyManager.chooseEngineClientAlias(keyTypes, issuer, engine);
    }

    @Override
    protected String chooseServerAlias(ReferenceCountedOpenSslEngine engine, String type) {
        return this.keyManager.chooseEngineServerAlias(type, null, engine);
    }
}

