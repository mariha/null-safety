package org.anyname.nullsafety;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;

/**
 * Declares that methods return value and parameters in an annotated scope are non-null unless otherwise explicitly
 * specified as {@link Nullable}. Annotated scope can be a package (in package-info.java), a class or an enum type.
 * Inner and nested classes are in the scope of enclosing them type.<p/>
 *
 * Rules for methods overwriting in non-null scope:<br/>
 *  - params: {@code @NotNull -> @Nullable} and {@code @NotNull -> unknown}<br/>
 *    If a method accepts nullable parameter, overwriting it method has to be prepared for null value as well.
 *    In non-null api scope {@code Nullable} qualifier should be specified explicitly. Not annotating inherited
 *    method's parameter as such would be violation of extended class/interface contract. <br/>
 *  - params: {@code @Nullable -> @NotNull}<br/>
 *    it is ok to for an inherited method to accept values which generalized interface don't accept<br/>
 *  - return type: {@code @NotNull -> @Nullable} and {@code @NotNull -> unknown}<br/>
 *    nullable qualifiers are not inherited and unless repeated explicitly, the default non-null takes precedence<br/>
 *  - return type: {@code @Nullable -> @NotNull}<br/>
 *    returning null from a method overwriting something that is expected to never return null would violate super type
 *    contract. You should never do this.<p/>
 *
 * @see org.anyname.nullsafety
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Nonnull
@TypeQualifierDefault({ElementType.METHOD, ElementType.PARAMETER})
public @interface NonNullApi {
}