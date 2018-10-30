/*
 * Decompiled with CFR 0_132.
 */
package com.google.j2objc.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE, ElementType.PACKAGE})
@Retention(value=RetentionPolicy.SOURCE)
public @interface ReflectionSupport {
    public Level value();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Level {
        NATIVE_ONLY,
        FULL;
        

        private Level() {
        }
    }

}

