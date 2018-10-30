/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.ApplicationProtocolAccessor;
import java.nio.ByteBuffer;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;

class JdkSslEngine
extends SSLEngine
implements ApplicationProtocolAccessor {
    private final SSLEngine engine;
    private volatile String applicationProtocol;

    JdkSslEngine(SSLEngine engine) {
        this.engine = engine;
    }

    @Override
    public String getNegotiatedApplicationProtocol() {
        return this.applicationProtocol;
    }

    void setNegotiatedApplicationProtocol(String applicationProtocol) {
        this.applicationProtocol = applicationProtocol;
    }

    @Override
    public SSLSession getSession() {
        return this.engine.getSession();
    }

    public SSLEngine getWrappedEngine() {
        return this.engine;
    }

    @Override
    public void closeInbound() throws SSLException {
        this.engine.closeInbound();
    }

    @Override
    public void closeOutbound() {
        this.engine.closeOutbound();
    }

    @Override
    public String getPeerHost() {
        return this.engine.getPeerHost();
    }

    @Override
    public int getPeerPort() {
        return this.engine.getPeerPort();
    }

    @Override
    public SSLEngineResult wrap(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws SSLException {
        return this.engine.wrap(byteBuffer, byteBuffer2);
    }

    @Override
    public SSLEngineResult wrap(ByteBuffer[] byteBuffers, ByteBuffer byteBuffer) throws SSLException {
        return this.engine.wrap(byteBuffers, byteBuffer);
    }

    @Override
    public SSLEngineResult wrap(ByteBuffer[] byteBuffers, int i, int i2, ByteBuffer byteBuffer) throws SSLException {
        return this.engine.wrap(byteBuffers, i, i2, byteBuffer);
    }

    @Override
    public SSLEngineResult unwrap(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws SSLException {
        return this.engine.unwrap(byteBuffer, byteBuffer2);
    }

    @Override
    public SSLEngineResult unwrap(ByteBuffer byteBuffer, ByteBuffer[] byteBuffers) throws SSLException {
        return this.engine.unwrap(byteBuffer, byteBuffers);
    }

    @Override
    public SSLEngineResult unwrap(ByteBuffer byteBuffer, ByteBuffer[] byteBuffers, int i, int i2) throws SSLException {
        return this.engine.unwrap(byteBuffer, byteBuffers, i, i2);
    }

    @Override
    public Runnable getDelegatedTask() {
        return this.engine.getDelegatedTask();
    }

    @Override
    public boolean isInboundDone() {
        return this.engine.isInboundDone();
    }

    @Override
    public boolean isOutboundDone() {
        return this.engine.isOutboundDone();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return this.engine.getSupportedCipherSuites();
    }

    @Override
    public String[] getEnabledCipherSuites() {
        return this.engine.getEnabledCipherSuites();
    }

    @Override
    public void setEnabledCipherSuites(String[] strings) {
        this.engine.setEnabledCipherSuites(strings);
    }

    @Override
    public String[] getSupportedProtocols() {
        return this.engine.getSupportedProtocols();
    }

    @Override
    public String[] getEnabledProtocols() {
        return this.engine.getEnabledProtocols();
    }

    @Override
    public void setEnabledProtocols(String[] strings) {
        this.engine.setEnabledProtocols(strings);
    }

    @Override
    public SSLSession getHandshakeSession() {
        return this.engine.getHandshakeSession();
    }

    @Override
    public void beginHandshake() throws SSLException {
        this.engine.beginHandshake();
    }

    @Override
    public SSLEngineResult.HandshakeStatus getHandshakeStatus() {
        return this.engine.getHandshakeStatus();
    }

    @Override
    public void setUseClientMode(boolean b) {
        this.engine.setUseClientMode(b);
    }

    @Override
    public boolean getUseClientMode() {
        return this.engine.getUseClientMode();
    }

    @Override
    public void setNeedClientAuth(boolean b) {
        this.engine.setNeedClientAuth(b);
    }

    @Override
    public boolean getNeedClientAuth() {
        return this.engine.getNeedClientAuth();
    }

    @Override
    public void setWantClientAuth(boolean b) {
        this.engine.setWantClientAuth(b);
    }

    @Override
    public boolean getWantClientAuth() {
        return this.engine.getWantClientAuth();
    }

    @Override
    public void setEnableSessionCreation(boolean b) {
        this.engine.setEnableSessionCreation(b);
    }

    @Override
    public boolean getEnableSessionCreation() {
        return this.engine.getEnableSessionCreation();
    }

    @Override
    public SSLParameters getSSLParameters() {
        return this.engine.getSSLParameters();
    }

    @Override
    public void setSSLParameters(SSLParameters sslParameters) {
        this.engine.setSSLParameters(sslParameters);
    }
}

