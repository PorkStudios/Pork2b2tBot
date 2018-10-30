/*
 * Decompiled with CFR 0_132.
 */
package com.google.j2objc.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.SOURCE)
@Target(value={ElementType.FIELD})
public @interface Property {
    public String value() default "";
}

