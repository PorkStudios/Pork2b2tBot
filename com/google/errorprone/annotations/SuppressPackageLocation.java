/*
 * Decompiled with CFR 0_132.
 */
package com.google.errorprone.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.PACKAGE})
@Retention(value=RetentionPolicy.CLASS)
public @interface SuppressPackageLocation {
}

