package org.anyname.nullsafety;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;

/**
 * Declares that API and fields in the annotated scope are non-null unless otherwise explicitly specified as
 * {@link Nullable}. Annotated scope can be package (in package-info.java), a class or enum type. Inner and nested
 * classes are in the scope of enclosing them type.
 *
 * {@link NonNullApi} for rules of how methods overwriting works with nullsafety.
 *
 * @see org.anyname.nullsafety
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Nonnull
@TypeQualifierDefault({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
public @interface NonNullScope {
}