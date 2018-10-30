/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  com.google.protobuf.ExtensionRegistry
 *  com.google.protobuf.ExtensionRegistryLite
 *  com.google.protobuf.MessageLite
 *  com.google.protobuf.MessageLite$Builder
 *  com.google.protobuf.Parser
 */
package io.netty.handler.codec.protobuf;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.lang.reflect.Method;
import java.util.List;

@ChannelHandler.Sharable
public class ProtobufDecoder
extends MessageToMessageDecoder<ByteBuf> {
    private static final boolean HAS_PARSER;
    private final MessageLite prototype;
    private final ExtensionRegistryLite extensionRegistry;

    public ProtobufDecoder(MessageLite prototype) {
        this(prototype, (ExtensionRegistry)null);
    }

    public ProtobufDecoder(MessageLite prototype, ExtensionRegistry extensionRegistry) {
        this(prototype, (ExtensionRegistryLite)extensionRegistry);
    }

    public ProtobufDecoder(MessageLite prototype, ExtensionRegistryLite extensionRegistry) {
        if (prototype == null) {
            throw new NullPointerException("prototype");
        }
        this.prototype = prototype.getDefaultInstanceForType();
        this.extensionRegistry = extensionRegistry;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int offset;
        byte[] array;
        int length = msg.readableBytes();
        if (msg.hasArray()) {
            array = msg.array();
            offset = msg.arrayOffset() + msg.readerIndex();
        } else {
            array = new byte[length];
            msg.getBytes(msg.readerIndex(), array, 0, length);
            offset = 0;
        }
        if (this.extensionRegistry == null) {
            if (HAS_PARSER) {
                out.add(this.prototype.getParserForType().parseFrom(array, offset, length));
            } else {
                out.add((Object)this.prototype.newBuilderForType().mergeFrom(array, offset, length).build());
            }
        } else if (HAS_PARSER) {
            out.add(this.prototype.getParserForType().parseFrom(array, offset, length, this.extensionRegistry));
        } else {
            out.add((Object)this.prototype.newBuilderForType().mergeFrom(array, offset, length, this.extensionRegistry).build());
        }
    }

    static {
        boolean hasParser = false;
        try {
            MessageLite.class.getDeclaredMethod("getParserForType", new Class[0]);
            hasParser = true;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        HAS_PARSER = hasParser;
    }
}

