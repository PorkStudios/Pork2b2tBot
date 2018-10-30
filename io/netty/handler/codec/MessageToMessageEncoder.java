/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.CodecOutputList;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class MessageToMessageEncoder<I>
extends ChannelOutboundHandlerAdapter {
    private final TypeParameterMatcher matcher;

    protected MessageToMessageEncoder() {
        this.matcher = TypeParameterMatcher.find(this, MessageToMessageEncoder.class, "I");
    }

    protected MessageToMessageEncoder(Class<? extends I> outboundMessageType) {
        this.matcher = TypeParameterMatcher.get(outboundMessageType);
    }

    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return this.matcher.match(msg);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        block14 : {
            out = null;
            try {
                if (this.acceptOutboundMessage(msg)) {
                    out = CodecOutputList.newInstance();
                    cast = msg;
                    try {
                        this.encode(ctx, cast, out);
                    }
                    finally {
                        ReferenceCountUtil.release(cast);
                    }
                    if (out.isEmpty()) {
                        out.recycle();
                        out = null;
                        throw new EncoderException(StringUtil.simpleClassName(this) + " must produce at least one message.");
                    }
                } else {
                    ctx.write(msg, promise);
                }
                if (out == null) return;
            }
            catch (EncoderException e) {
                try {
                    throw e;
                    catch (Throwable t) {
                        throw new EncoderException(t);
                    }
                }
                catch (Throwable var10_14) {
                    block16 : {
                        block17 : {
                            block15 : {
                                if (out == null) throw var10_14;
                                sizeMinusOne = out.size() - 1;
                                if (sizeMinusOne != 0) break block15;
                                ctx.write(out.get(0), promise);
                                break block16;
                            }
                            if (sizeMinusOne <= 0) break block16;
                            voidPromise = ctx.voidPromise();
                            isVoidPromise = promise == voidPromise;
                            break block17;
lbl45: // 2 sources:
                            for (i = 0; i < sizeMinusOne; ++i) {
                                p = isVoidPromise != false ? voidPromise : ctx.newPromise();
                                ctx.write(out.getUnsafe(i), p);
                            }
                            ctx.write(out.getUnsafe(sizeMinusOne), promise);
lbl50: // 3 sources:
                            out.recycle();
                            return;
                        }
                        for (i = 0; i < sizeMinusOne; ++i) {
                            p = isVoidPromise != false ? voidPromise : ctx.newPromise();
                            ctx.write(out.getUnsafe(i), p);
                        }
                        ctx.write(out.getUnsafe(sizeMinusOne), promise);
                    }
                    out.recycle();
                    throw var10_14;
                }
            }
            sizeMinusOne = out.size() - 1;
            if (sizeMinusOne != 0) break block14;
            ctx.write(out.get(0), promise);
            ** GOTO lbl50
        }
        if (sizeMinusOne <= 0) ** GOTO lbl50
        voidPromise = ctx.voidPromise();
        isVoidPromise = promise == voidPromise;
        ** GOTO lbl45
    }

    protected abstract void encode(ChannelHandlerContext var1, I var2, List<Object> var3) throws Exception;
}

