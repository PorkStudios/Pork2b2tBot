/*
 * Decompiled with CFR 0_132.
 */
package javax.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.meta.TypeQualifier;
import javax.annotation.meta.When;

@Documented
@TypeQualifier
@Retention(value=RetentionPolicy.RUNTIME)
public @interface PropertyKey {
    public When when() default When.ALWAYS;
}

