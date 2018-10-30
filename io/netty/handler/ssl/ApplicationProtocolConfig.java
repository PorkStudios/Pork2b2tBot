/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.ApplicationProtocolUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.List;

public final class ApplicationProtocolConfig {
    public static final ApplicationProtocolConfig DISABLED = new ApplicationProtocolConfig();
    private final List<String> supportedProtocols;
    private final Protocol protocol;
    private final SelectorFailureBehavior selectorBehavior;
    private final SelectedListenerFailureBehavior selectedBehavior;

    public ApplicationProtocolConfig(Protocol protocol, SelectorFailureBehavior selectorBehavior, SelectedListenerFailureBehavior selectedBehavior, Iterable<String> supportedProtocols) {
        this(protocol, selectorBehavior, selectedBehavior, ApplicationProtocolUtil.toList(supportedProtocols));
    }

    public /* varargs */ ApplicationProtocolConfig(Protocol protocol, SelectorFailureBehavior selectorBehavior, SelectedListenerFailureBehavior selectedBehavior, String ... supportedProtocols) {
        this(protocol, selectorBehavior, selectedBehavior, ApplicationProtocolUtil.toList(supportedProtocols));
    }

    private ApplicationProtocolConfig(Protocol protocol, SelectorFailureBehavior selectorBehavior, SelectedListenerFailureBehavior selectedBehavior, List<String> supportedProtocols) {
        this.supportedProtocols = Collections.unmodifiableList(ObjectUtil.checkNotNull(supportedProtocols, "supportedProtocols"));
        this.protocol = ObjectUtil.checkNotNull(protocol, "protocol");
        this.selectorBehavior = ObjectUtil.checkNotNull(selectorBehavior, "selectorBehavior");
        this.selectedBehavior = ObjectUtil.checkNotNull(selectedBehavior, "selectedBehavior");
        if (protocol == Protocol.NONE) {
            throw new IllegalArgumentException("protocol (" + (Object)((Object)Protocol.NONE) + ") must not be " + (Object)((Object)Protocol.NONE) + '.');
        }
        if (supportedProtocols.isEmpty()) {
            throw new IllegalArgumentException("supportedProtocols must be not empty");
        }
    }

    private ApplicationProtocolConfig() {
        this.supportedProtocols = Collections.emptyList();
        this.protocol = Protocol.NONE;
        this.selectorBehavior = SelectorFailureBehavior.CHOOSE_MY_LAST_PROTOCOL;
        this.selectedBehavior = SelectedListenerFailureBehavior.ACCEPT;
    }

    public List<String> supportedProtocols() {
        return this.supportedProtocols;
    }

    public Protocol protocol() {
        return this.protocol;
    }

    public SelectorFailureBehavior selectorFailureBehavior() {
        return this.selectorBehavior;
    }

    public SelectedListenerFailureBehavior selectedListenerFailureBehavior() {
        return this.selectedBehavior;
    }

    public static enum SelectedListenerFailureBehavior {
        ACCEPT,
        FATAL_ALERT,
        CHOOSE_MY_LAST_PROTOCOL;
        

        private SelectedListenerFailureBehavior() {
        }
    }

    public static enum SelectorFailureBehavior {
        FATAL_ALERT,
        NO_ADVERTISE,
        CHOOSE_MY_LAST_PROTOCOL;
        

        private SelectorFailureBehavior() {
        }
    }

    public static enum Protocol {
        NONE,
        NPN,
        ALPN,
        NPN_AND_ALPN;
        

        private Protocol() {
        }
    }

}

