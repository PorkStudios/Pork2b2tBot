/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.smtp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.smtp.DefaultSmtpContent;
import io.netty.handler.codec.smtp.LastSmtpContent;
import io.netty.handler.codec.smtp.SmtpContent;
import io.netty.util.ReferenceCounted;

public final class DefaultLastSmtpContent
extends DefaultSmtpContent
implements LastSmtpContent {
    public DefaultLastSmtpContent(ByteBuf data) {
        super(data);
    }

    @Override
    public LastSmtpContent copy() {
        return (LastSmtpContent)super.copy();
    }

    @Override
    public LastSmtpContent duplicate() {
        return (LastSmtpContent)super.duplicate();
    }

    @Override
    public LastSmtpContent retainedDuplicate() {
        return (LastSmtpContent)super.retainedDuplicate();
    }

    @Override
    public LastSmtpContent replace(ByteBuf content) {
        return new DefaultLastSmtpContent(content);
    }

    @Override
    public DefaultLastSmtpContent retain() {
        super.retain();
        return this;
    }

    @Override
    public DefaultLastSmtpContent retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public DefaultLastSmtpContent touch() {
        super.touch();
        return this;
    }

    @Override
    public DefaultLastSmtpContent touch(Object hint) {
        super.touch(hint);
        return this;
    }
}

