/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec;

import io.netty.handler.codec.ProtocolDetectionState;
import io.netty.util.internal.ObjectUtil;

public final class ProtocolDetectionResult<T> {
    private static final ProtocolDetectionResult NEEDS_MORE_DATE = new ProtocolDetectionResult<Object>(ProtocolDetectionState.NEEDS_MORE_DATA, null);
    private static final ProtocolDetectionResult INVALID = new ProtocolDetectionResult<Object>(ProtocolDetectionState.INVALID, null);
    private final ProtocolDetectionState state;
    private final T result;

    public static <T> ProtocolDetectionResult<T> needsMoreData() {
        return NEEDS_MORE_DATE;
    }

    public static <T> ProtocolDetectionResult<T> invalid() {
        return INVALID;
    }

    public static <T> ProtocolDetectionResult<T> detected(T protocol) {
        return new ProtocolDetectionResult<T>(ProtocolDetectionState.DETECTED, ObjectUtil.checkNotNull(protocol, "protocol"));
    }

    private ProtocolDetectionResult(ProtocolDetectionState state, T result) {
        this.state = state;
        this.result = result;
    }

    public ProtocolDetectionState state() {
        return this.state;
    }

    public T detectedProtocol() {
        return this.result;
    }
}

