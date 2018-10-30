/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.cors;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.util.internal.StringUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public final class CorsConfig {
    private final Set<String> origins;
    private final boolean anyOrigin;
    private final boolean enabled;
    private final Set<String> exposeHeaders;
    private final boolean allowCredentials;
    private final long maxAge;
    private final Set<HttpMethod> allowedRequestMethods;
    private final Set<String> allowedRequestHeaders;
    private final boolean allowNullOrigin;
    private final Map<CharSequence, Callable<?>> preflightHeaders;
    private final boolean shortCircuit;

    CorsConfig(CorsConfigBuilder builder) {
        this.origins = new LinkedHashSet<String>(builder.origins);
        this.anyOrigin = builder.anyOrigin;
        this.enabled = builder.enabled;
        this.exposeHeaders = builder.exposeHeaders;
        this.allowCredentials = builder.allowCredentials;
        this.maxAge = builder.maxAge;
        this.allowedRequestMethods = builder.requestMethods;
        this.allowedRequestHeaders = builder.requestHeaders;
        this.allowNullOrigin = builder.allowNullOrigin;
        this.preflightHeaders = builder.preflightHeaders;
        this.shortCircuit = builder.shortCircuit;
    }

    public boolean isCorsSupportEnabled() {
        return this.enabled;
    }

    public boolean isAnyOriginSupported() {
        return this.anyOrigin;
    }

    public String origin() {
        return this.origins.isEmpty() ? "*" : this.origins.iterator().next();
    }

    public Set<String> origins() {
        return this.origins;
    }

    public boolean isNullOriginAllowed() {
        return this.allowNullOrigin;
    }

    public Set<String> exposedHeaders() {
        return Collections.unmodifiableSet(this.exposeHeaders);
    }

    public boolean isCredentialsAllowed() {
        return this.allowCredentials;
    }

    public long maxAge() {
        return this.maxAge;
    }

    public Set<HttpMethod> allowedRequestMethods() {
        return Collections.unmodifiableSet(this.allowedRequestMethods);
    }

    public Set<String> allowedRequestHeaders() {
        return Collections.unmodifiableSet(this.allowedRequestHeaders);
    }

    public HttpHeaders preflightResponseHeaders() {
        if (this.preflightHeaders.isEmpty()) {
            return EmptyHttpHeaders.INSTANCE;
        }
        DefaultHttpHeaders preflightHeaders = new DefaultHttpHeaders();
        for (Map.Entry<CharSequence, Callable<?>> entry : this.preflightHeaders.entrySet()) {
            Object value = CorsConfig.getValue(entry.getValue());
            if (value instanceof Iterable) {
                preflightHeaders.add(entry.getKey(), (Iterable)value);
                continue;
            }
            preflightHeaders.add(entry.getKey(), value);
        }
        return preflightHeaders;
    }

    public boolean isShortCircuit() {
        return this.shortCircuit;
    }

    @Deprecated
    public boolean isShortCurcuit() {
        return this.isShortCircuit();
    }

    private static <T> T getValue(Callable<T> callable) {
        try {
            return callable.call();
        }
        catch (Exception e) {
            throw new IllegalStateException("Could not generate value for callable [" + callable + ']', e);
        }
    }

    public String toString() {
        return StringUtil.simpleClassName(this) + "[enabled=" + this.enabled + ", origins=" + this.origins + ", anyOrigin=" + this.anyOrigin + ", exposedHeaders=" + this.exposeHeaders + ", isCredentialsAllowed=" + this.allowCredentials + ", maxAge=" + this.maxAge + ", allowedRequestMethods=" + this.allowedRequestMethods + ", allowedRequestHeaders=" + this.allowedRequestHeaders + ", preflightHeaders=" + this.preflightHeaders + ']';
    }

    @Deprecated
    public static Builder withAnyOrigin() {
        return new Builder();
    }

    @Deprecated
    public static Builder withOrigin(String origin) {
        if ("*".equals(origin)) {
            return new Builder();
        }
        return new Builder(origin);
    }

    @Deprecated
    public static /* varargs */ Builder withOrigins(String ... origins) {
        return new Builder(origins);
    }

    @Deprecated
    public static final class DateValueGenerator
    implements Callable<Date> {
        @Override
        public Date call() throws Exception {
            return new Date();
        }
    }

    @Deprecated
    public static class Builder {
        private final CorsConfigBuilder builder;

        @Deprecated
        public /* varargs */ Builder(String ... origins) {
            this.builder = new CorsConfigBuilder(origins);
        }

        @Deprecated
        public Builder() {
            this.builder = new CorsConfigBuilder();
        }

        @Deprecated
        public Builder allowNullOrigin() {
            this.builder.allowNullOrigin();
            return this;
        }

        @Deprecated
        public Builder disable() {
            this.builder.disable();
            return this;
        }

        @Deprecated
        public /* varargs */ Builder exposeHeaders(String ... headers) {
            this.builder.exposeHeaders(headers);
            return this;
        }

        @Deprecated
        public Builder allowCredentials() {
            this.builder.allowCredentials();
            return this;
        }

        @Deprecated
        public Builder maxAge(long max) {
            this.builder.maxAge(max);
            return this;
        }

        @Deprecated
        public /* varargs */ Builder allowedRequestMethods(HttpMethod ... methods) {
            this.builder.allowedRequestMethods(methods);
            return this;
        }

        @Deprecated
        public /* varargs */ Builder allowedRequestHeaders(String ... headers) {
            this.builder.allowedRequestHeaders(headers);
            return this;
        }

        @Deprecated
        public /* varargs */ Builder preflightResponseHeader(CharSequence name, Object ... values) {
            this.builder.preflightResponseHeader(name, values);
            return this;
        }

        @Deprecated
        public <T> Builder preflightResponseHeader(CharSequence name, Iterable<T> value) {
            this.builder.preflightResponseHeader(name, value);
            return this;
        }

        @Deprecated
        public <T> Builder preflightResponseHeader(String name, Callable<T> valueGenerator) {
            this.builder.preflightResponseHeader((CharSequence)name, valueGenerator);
            return this;
        }

        @Deprecated
        public Builder noPreflightResponseHeaders() {
            this.builder.noPreflightResponseHeaders();
            return this;
        }

        @Deprecated
        public CorsConfig build() {
            return this.builder.build();
        }

        @Deprecated
        public Builder shortCurcuit() {
            this.builder.shortCircuit();
            return this;
        }
    }

}

