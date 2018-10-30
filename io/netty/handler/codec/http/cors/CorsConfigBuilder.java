/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.cors;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.util.AsciiString;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public final class CorsConfigBuilder {
    final Set<String> origins;
    final boolean anyOrigin;
    boolean allowNullOrigin;
    boolean enabled = true;
    boolean allowCredentials;
    final Set<String> exposeHeaders = new HashSet<String>();
    long maxAge;
    final Set<HttpMethod> requestMethods = new HashSet<HttpMethod>();
    final Set<String> requestHeaders = new HashSet<String>();
    final Map<CharSequence, Callable<?>> preflightHeaders = new HashMap();
    private boolean noPreflightHeaders;
    boolean shortCircuit;

    public static CorsConfigBuilder forAnyOrigin() {
        return new CorsConfigBuilder();
    }

    public static CorsConfigBuilder forOrigin(String origin) {
        if ("*".equals(origin)) {
            return new CorsConfigBuilder();
        }
        return new CorsConfigBuilder(origin);
    }

    public static /* varargs */ CorsConfigBuilder forOrigins(String ... origins) {
        return new CorsConfigBuilder(origins);
    }

    /* varargs */ CorsConfigBuilder(String ... origins) {
        this.origins = new LinkedHashSet<String>(Arrays.asList(origins));
        this.anyOrigin = false;
    }

    CorsConfigBuilder() {
        this.anyOrigin = true;
        this.origins = Collections.emptySet();
    }

    public CorsConfigBuilder allowNullOrigin() {
        this.allowNullOrigin = true;
        return this;
    }

    public CorsConfigBuilder disable() {
        this.enabled = false;
        return this;
    }

    public /* varargs */ CorsConfigBuilder exposeHeaders(String ... headers) {
        this.exposeHeaders.addAll(Arrays.asList(headers));
        return this;
    }

    public /* varargs */ CorsConfigBuilder exposeHeaders(CharSequence ... headers) {
        for (CharSequence header : headers) {
            this.exposeHeaders.add(header.toString());
        }
        return this;
    }

    public CorsConfigBuilder allowCredentials() {
        this.allowCredentials = true;
        return this;
    }

    public CorsConfigBuilder maxAge(long max) {
        this.maxAge = max;
        return this;
    }

    public /* varargs */ CorsConfigBuilder allowedRequestMethods(HttpMethod ... methods) {
        this.requestMethods.addAll(Arrays.asList(methods));
        return this;
    }

    public /* varargs */ CorsConfigBuilder allowedRequestHeaders(String ... headers) {
        this.requestHeaders.addAll(Arrays.asList(headers));
        return this;
    }

    public /* varargs */ CorsConfigBuilder allowedRequestHeaders(CharSequence ... headers) {
        for (CharSequence header : headers) {
            this.requestHeaders.add(header.toString());
        }
        return this;
    }

    public /* varargs */ CorsConfigBuilder preflightResponseHeader(CharSequence name, Object ... values) {
        if (values.length == 1) {
            this.preflightHeaders.put(name, new ConstantValueGenerator(values[0]));
        } else {
            this.preflightResponseHeader(name, Arrays.asList(values));
        }
        return this;
    }

    public <T> CorsConfigBuilder preflightResponseHeader(CharSequence name, Iterable<T> value) {
        this.preflightHeaders.put(name, new ConstantValueGenerator(value));
        return this;
    }

    public <T> CorsConfigBuilder preflightResponseHeader(CharSequence name, Callable<T> valueGenerator) {
        this.preflightHeaders.put(name, valueGenerator);
        return this;
    }

    public CorsConfigBuilder noPreflightResponseHeaders() {
        this.noPreflightHeaders = true;
        return this;
    }

    public CorsConfigBuilder shortCircuit() {
        this.shortCircuit = true;
        return this;
    }

    public CorsConfig build() {
        if (this.preflightHeaders.isEmpty() && !this.noPreflightHeaders) {
            this.preflightHeaders.put(HttpHeaderNames.DATE, DateValueGenerator.INSTANCE);
            this.preflightHeaders.put(HttpHeaderNames.CONTENT_LENGTH, new ConstantValueGenerator("0"));
        }
        return new CorsConfig(this);
    }

    private static final class DateValueGenerator
    implements Callable<Date> {
        static final DateValueGenerator INSTANCE = new DateValueGenerator();

        private DateValueGenerator() {
        }

        @Override
        public Date call() throws Exception {
            return new Date();
        }
    }

    private static final class ConstantValueGenerator
    implements Callable<Object> {
        private final Object value;

        private ConstantValueGenerator(Object value) {
            if (value == null) {
                throw new IllegalArgumentException("value must not be null");
            }
            this.value = value;
        }

        @Override
        public Object call() {
            return this.value;
        }
    }

}

