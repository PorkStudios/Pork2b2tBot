/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.smtp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.smtp.DefaultLastSmtpContent;
import io.netty.handler.codec.smtp.SmtpContent;
import io.netty.util.ReferenceCounted;

public interface LastSmtpContent
extends SmtpContent {
    public static final LastSmtpContent EMPTY_LAST_CONTENT = new LastSmtpContent(){

        @Override
        public LastSmtpContent copy() {
            return this;
        }

        @Override
        public LastSmtpContent duplicate() {
            return this;
        }

        @Override
        public LastSmtpContent retainedDuplicate() {
            return this;
        }

        @Override
        public LastSmtpContent replace(ByteBuf content) {
            return new DefaultLastSmtpContent(content);
        }

        @Override
        public LastSmtpContent retain() {
            return this;
        }

        @Override
        public LastSmtpContent retain(int increment) {
            return this;
        }

        @Override
        public LastSmtpContent touch() {
            return this;
        }

        @Override
        public LastSmtpContent touch(Object hint) {
            return this;
        }

        @Override
        public ByteBuf content() {
            return Unpooled.EMPTY_BUFFER;
        }

        @Override
        public int refCnt() {
            return 1;
        }

        @Override
        public boolean release() {
            return false;
        }

        @Override
        public boolean release(int decrement) {
            return false;
        }
    };

    @Override
    public LastSmtpContent copy();

    @Override
    public LastSmtpContent duplicate();

    @Override
    public LastSmtpContent retainedDuplicate();

    @Override
    public LastSmtpContent replace(ByteBuf var1);

    @Override
    public LastSmtpContent retain();

    @Override
    public LastSmtpContent retain(int var1);

    @Override
    public LastSmtpContent touch();

    @Override
    public LastSmtpContent touch(Object var1);

}

