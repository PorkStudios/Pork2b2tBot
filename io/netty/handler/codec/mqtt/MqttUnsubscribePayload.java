/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;
import java.util.Collections;
import java.util.List;

public final class MqttUnsubscribePayload {
    private final List<String> topics;

    public MqttUnsubscribePayload(List<String> topics) {
        this.topics = Collections.unmodifiableList(topics);
    }

    public List<String> topics() {
        return this.topics;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(StringUtil.simpleClassName(this)).append('[');
        for (int i = 0; i < this.topics.size() - 1; ++i) {
            builder.append("topicName = ").append(this.topics.get(i)).append(", ");
        }
        builder.append("topicName = ").append(this.topics.get(this.topics.size() - 1)).append(']');
        return builder.toString();
    }
}

