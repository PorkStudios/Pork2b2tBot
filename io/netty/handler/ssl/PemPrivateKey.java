/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.ssl.PemEncoded;
import io.netty.handler.ssl.SslUtils;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.CharsetUtil;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;
import java.nio.charset.Charset;
import java.security.PrivateKey;

public final class PemPrivateKey
extends AbstractReferenceCounted
implements PrivateKey,
PemEncoded {
    private static final long serialVersionUID = 7978017465645018936L;
    private static final byte[] BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n".getBytes(CharsetUtil.US_ASCII);
    private static final byte[] END_PRIVATE_KEY = "\n-----END PRIVATE KEY-----\n".getBytes(CharsetUtil.US_ASCII);
    private static final String PKCS8_FORMAT = "PKCS#8";
    private final ByteBuf content;

    /*
     * Exception decompiling
     */
    static PemEncoded toPEM(ByteBufAllocator allocator, boolean useDirect, PrivateKey key) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 3[TRYBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:416)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:468)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:2960)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:818)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:196)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:141)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:95)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:372)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:867)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:768)
        // org.benf.cfr.reader.Main.doJar(Main.java:141)
        // org.benf.cfr.reader.Main.main(Main.java:242)
        throw new IllegalStateException("Decompilation failed");
    }

    public static PemPrivateKey valueOf(byte[] key) {
        return PemPrivateKey.valueOf(Unpooled.wrappedBuffer(key));
    }

    public static PemPrivateKey valueOf(ByteBuf key) {
        return new PemPrivateKey(key);
    }

    private PemPrivateKey(ByteBuf content) {
        this.content = ObjectUtil.checkNotNull(content, "content");
    }

    @Override
    public boolean isSensitive() {
        return true;
    }

    @Override
    public ByteBuf content() {
        int count = this.refCnt();
        if (count <= 0) {
            throw new IllegalReferenceCountException(count);
        }
        return this.content;
    }

    @Override
    public PemPrivateKey copy() {
        return this.replace(this.content.copy());
    }

    @Override
    public PemPrivateKey duplicate() {
        return this.replace(this.content.duplicate());
    }

    @Override
    public PemPrivateKey retainedDuplicate() {
        return this.replace(this.content.retainedDuplicate());
    }

    @Override
    public PemPrivateKey replace(ByteBuf content) {
        return new PemPrivateKey(content);
    }

    @Override
    public PemPrivateKey touch() {
        this.content.touch();
        return this;
    }

    @Override
    public PemPrivateKey touch(Object hint) {
        this.content.touch(hint);
        return this;
    }

    @Override
    public PemPrivateKey retain() {
        return (PemPrivateKey)super.retain();
    }

    @Override
    public PemPrivateKey retain(int increment) {
        return (PemPrivateKey)super.retain(increment);
    }

    @Override
    protected void deallocate() {
        SslUtils.zerooutAndRelease(this.content);
    }

    @Override
    public byte[] getEncoded() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAlgorithm() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFormat() {
        return PKCS8_FORMAT;
    }

    @Override
    public void destroy() {
        this.release(this.refCnt());
    }

    @Override
    public boolean isDestroyed() {
        return this.refCnt() == 0;
    }
}

