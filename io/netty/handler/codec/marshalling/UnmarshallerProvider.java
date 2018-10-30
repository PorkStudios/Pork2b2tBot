/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.jboss.marshalling.Unmarshaller
 */
package io.netty.handler.codec.marshalling;

import io.netty.channel.ChannelHandlerContext;
import org.jboss.marshalling.Unmarshaller;

public interface UnmarshallerProvider {
    public Unmarshaller getUnmarshaller(ChannelHandlerContext var1) throws Exception;
}

