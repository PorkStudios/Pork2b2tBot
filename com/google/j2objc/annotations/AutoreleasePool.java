/*
 * Decompiled with CFR 0_132.
 */
package com.google.j2objc.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.LOCAL_VARIABLE})
@Retention(value=RetentionPolicy.SOURCE)
public @interface AutoreleasePool {
}

