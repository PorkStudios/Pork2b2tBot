/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.Headers;
import io.netty.handler.codec.http2.CharSequenceMap;
import io.netty.util.AsciiString;
import java.util.Iterator;
import java.util.Map;

public interface Http2Headers
extends Headers<CharSequence, CharSequence, Http2Headers> {
    @Override
    public Iterator<Map.Entry<CharSequence, CharSequence>> iterator();

    public Iterator<CharSequence> valueIterator(CharSequence var1);

    public Http2Headers method(CharSequence var1);

    public Http2Headers scheme(CharSequence var1);

    public Http2Headers authority(CharSequence var1);

    public Http2Headers path(CharSequence var1);

    public Http2Headers status(CharSequence var1);

    public CharSequence method();

    public CharSequence scheme();

    public CharSequence authority();

    public CharSequence path();

    public CharSequence status();

    public static enum PseudoHeaderName {
        METHOD(":method"),
        SCHEME(":scheme"),
        AUTHORITY(":authority"),
        PATH(":path"),
        STATUS(":status");
        
        private final AsciiString value;
        private static final CharSequenceMap<AsciiString> PSEUDO_HEADERS;

        private PseudoHeaderName(String value) {
            this.value = AsciiString.cached(value);
        }

        public AsciiString value() {
            return this.value;
        }

        public static boolean isPseudoHeader(CharSequence header) {
            return PSEUDO_HEADERS.contains(header);
        }

        static {
            PSEUDO_HEADERS = new CharSequenceMap();
            for (PseudoHeaderName pseudoHeader : PseudoHeaderName.values()) {
                PSEUDO_HEADERS.add(pseudoHeader.value(), AsciiString.EMPTY_STRING);
            }
        }
    }

}

