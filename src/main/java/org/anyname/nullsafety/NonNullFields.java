package org.anyname.nullsafety;

/**
 * Declares that fields in an annotated scope are non-null unless otherwise explicitly specified as {@link Nullable}.
 * Annotated scope can be package (in package-info.java), a class or enum type. Inner and nested classes are in scope of
 * enclosing them type.
 *
 * @see org.anyname.nullsafety
 */
public @interface NonNullFields {
}
