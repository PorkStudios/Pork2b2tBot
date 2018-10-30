/*
 * Decompiled with CFR 0_132.
 */
package com.google.gson.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD, ElementType.TYPE})
public @interface Since {
    public double value();
}

