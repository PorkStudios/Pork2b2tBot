/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.mqtt;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;
import java.nio.charset.Charset;

public final class MqttConnectPayload {
    private final String clientIdentifier;
    private final String willTopic;
    private final byte[] willMessage;
    private final String userName;
    private final byte[] password;

    @Deprecated
    public MqttConnectPayload(String clientIdentifier, String willTopic, String willMessage, String userName, String password) {
        this(clientIdentifier, willTopic, willMessage.getBytes(CharsetUtil.UTF_8), userName, password.getBytes(CharsetUtil.UTF_8));
    }

    public MqttConnectPayload(String clientIdentifier, String willTopic, byte[] willMessage, String userName, byte[] password) {
        this.clientIdentifier = clientIdentifier;
        this.willTopic = willTopic;
        this.willMessage = willMessage;
        this.userName = userName;
        this.password = password;
    }

    public String clientIdentifier() {
        return this.clientIdentifier;
    }

    public String willTopic() {
        return this.willTopic;
    }

    @Deprecated
    public String willMessage() {
        return this.willMessage == null ? null : new String(this.willMessage, CharsetUtil.UTF_8);
    }

    public byte[] willMessageInBytes() {
        return this.willMessage;
    }

    public String userName() {
        return this.userName;
    }

    @Deprecated
    public String password() {
        return this.password == null ? null : new String(this.password, CharsetUtil.UTF_8);
    }

    public byte[] passwordInBytes() {
        return this.password;
    }

    public String toString() {
        return StringUtil.simpleClassName(this) + '[' + "clientIdentifier=" + this.clientIdentifier + ", willTopic=" + this.willTopic + ", willMessage=" + this.willMessage + ", userName=" + this.userName + ", password=" + this.password + ']';
    }
}

