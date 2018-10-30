/*
 * Decompiled with CFR 0_132.
 */
package com.google.errorprone.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(value=RetentionPolicy.CLASS)
@Target(value={ElementType.PARAMETER})
public @interface FormatString {
}

