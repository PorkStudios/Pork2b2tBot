/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.EmptyHeaders;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.util.AsciiString;
import java.util.Iterator;

public final class EmptyHttp2Headers
extends EmptyHeaders<CharSequence, CharSequence, Http2Headers>
implements Http2Headers {
    public static final EmptyHttp2Headers INSTANCE = new EmptyHttp2Headers();

    private EmptyHttp2Headers() {
    }

    @Override
    public EmptyHttp2Headers method(CharSequence method) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EmptyHttp2Headers scheme(CharSequence status) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EmptyHttp2Headers authority(CharSequence authority) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EmptyHttp2Headers path(CharSequence path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EmptyHttp2Headers status(CharSequence status) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CharSequence method() {
        return (CharSequence)this.get(Http2Headers.PseudoHeaderName.METHOD.value());
    }

    @Override
    public CharSequence scheme() {
        return (CharSequence)this.get(Http2Headers.PseudoHeaderName.SCHEME.value());
    }

    @Override
    public CharSequence authority() {
        return (CharSequence)this.get(Http2Headers.PseudoHeaderName.AUTHORITY.value());
    }

    @Override
    public CharSequence path() {
        return (CharSequence)this.get(Http2Headers.PseudoHeaderName.PATH.value());
    }

    @Override
    public CharSequence status() {
        return (CharSequence)this.get(Http2Headers.PseudoHeaderName.STATUS.value());
    }
}

