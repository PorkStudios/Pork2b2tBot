/*
 * Decompiled with CFR 0_132.
 */
package org.codehaus.mojo.animal_sniffer;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.CLASS)
@Documented
@Target(value={ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface IgnoreJRERequirement {
}

