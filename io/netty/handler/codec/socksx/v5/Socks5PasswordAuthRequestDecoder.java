/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.socksx.v5;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.socksx.v5.DefaultSocks5PasswordAuthRequest;
import io.netty.util.CharsetUtil;
import java.nio.charset.Charset;
import java.util.List;

public class Socks5PasswordAuthRequestDecoder
extends ReplayingDecoder<State> {
    public Socks5PasswordAuthRequestDecoder() {
        super(State.INIT);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            switch ((State)((Object)this.state())) {
                case INIT: {
                    int startOffset = in.readerIndex();
                    byte version = in.getByte(startOffset);
                    if (version != 1) {
                        throw new DecoderException("unsupported subnegotiation version: " + version + " (expected: 1)");
                    }
                    short usernameLength = in.getUnsignedByte(startOffset + 1);
                    short passwordLength = in.getUnsignedByte(startOffset + 2 + usernameLength);
                    int totalLength = usernameLength + passwordLength + 3;
                    in.skipBytes(totalLength);
                    out.add(new DefaultSocks5PasswordAuthRequest(in.toString(startOffset + 2, usernameLength, CharsetUtil.US_ASCII), in.toString(startOffset + 3 + usernameLength, passwordLength, CharsetUtil.US_ASCII)));
                    this.checkpoint(State.SUCCESS);
                }
                case SUCCESS: {
                    int readableBytes = this.actualReadableBytes();
                    if (readableBytes <= 0) break;
                    out.add(in.readRetainedSlice(readableBytes));
                    break;
                }
                case FAILURE: {
                    in.skipBytes(this.actualReadableBytes());
                }
            }
        }
        catch (Exception e) {
            this.fail(out, e);
        }
    }

    private void fail(List<Object> out, Exception cause) {
        if (!(cause instanceof DecoderException)) {
            cause = new DecoderException(cause);
        }
        this.checkpoint(State.FAILURE);
        DefaultSocks5PasswordAuthRequest m = new DefaultSocks5PasswordAuthRequest("", "");
        m.setDecoderResult(DecoderResult.failure(cause));
        out.add(m);
    }

    static enum State {
        INIT,
        SUCCESS,
        FAILURE;
        

        private State() {
        }
    }

}

