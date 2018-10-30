/*
 * Decompiled with CFR 0_132.
 */
package com.google.errorprone.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface Immutable {
    public String[] containerOf() default {};
}

