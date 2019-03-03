/**
 * Annotations which are meant to bring null-safety to the codebase. Inspired by Kotlin and Spring.
 * <p/>
 * <h3>Goals:</h3>
 * <ol>
 *   <li>increase visibility of potential NPE issues (code itself, IDEs highlighting)</li>
 *   <li>automatically detect and prevent most of NPE at compile time</li>
 *   <li>guarantee their absence (enhance java type system for null-safety)</li>
 * </ol>
 * <p/>
 * Priorities: 1. expressiveness 2. tools support
 * <p/>
 * <h3>Why null safety?</h3>
 * <p/>
 * It's best practice to avoid null values all together. Usually, when we try to understand or implement an algorithm,
 * we think about happy path only, what positive steps need to happen to get the answer. We generalize by ignoring
 * corner cases. Null is a reverse, it is used to represent such a corner case. We tend to forget about them, and then
 * we get NPEs. Even if we do remember, we have to handle them with null checks, which distract attention of those who
 * read our code from that happy, positive path. They add unnecessary complexity to the code making it less readable and
 * harder to understand.
 * <p/>
 * Often there are better ways to model 'absence of a value' and they should be used instead (e.g. for constructor
 * params: try builder or factory methods, for return values try Optional, for collections and arrays try using empty
 * instead, sometimes Null Object pattern or Strategy may be the way, and probably there are many other alternatives to consider).
 * <p/>
 * <h3>What do we do?</h3>
 * <p/>
 * We assume everything to be non-null by default. For the rare cases when we want to make an exception
 * from this rule, {@link org.anyname.nullsafety.Nullable} annotation should be used. Marking non-null scope (package
 * or class) with {@link org.anyname.nullsafety.NonNullScope} or
 * {@link org.anyname.nullsafety.NonNullFields} and {@link org.anyname.nullsafety.NonNullApi}
 * annotations allows static code analysis tools (built into IntelliJ and Eclipse) to infer intended
 * non-nullness and automatically detect and highlight potential issues. In particular, we'd like to catch cases when:
 * <ul>
 *   <li>we try to dereference something nullable, or</li>
 *   <li>we assign something nullable (or null) to a variable otherwise assumed to be non-null</li>
 * </ul>
 * <p/>
 * Intentionally, there is no {@code NotNull/Nonnull} annotation. Explicitly annotating code with it would work for
 * compiler and static code analysis tools, would make it harder for people though. {@code @Nullable} quantifiers would get lost
 * from our perception in between many {@code @Nonnull} quantifiers, the code would become unnecessarily verbose and so
 * less readable and harder to process by human brain.
 * <p/>
 * Our null-safety annotations are implemented using JSR-305 annotations ({@link javax.annotation.Nullable} and
 * {@link javax.annotation.CheckForNull}) and meta-annotations ({@link javax.annotation.meta.TypeQualifierDefault} and
 * {@link javax.annotation.meta.TypeQualifierNickname}) so that we can leverage on tools that understand them.
 * <p/>
 * <h3>Why not using JSR-305 annotations directly?</h3>
 * <p/>
 * In general, a level of indirection gives us more flexibility. Specifically:
 * <ul>
 *   <li>provide 'default' mechanism, it would be too verbose to annotate everything with {@code @NotNull}, elsewhere
 *     used {@code @ParametersAreNonnullByDefault} doesn't apply to return values which we care most about</li>
 *   <li>provide 'nickname' mechanism, {@code javax.annotation.Nullable} is misleading,
 *     {@code javax.annotation.CheckForNull} is what we want with {@code @Nullable} alias</li>
 *   <li>we can group under one alias two or more annotations (for different tools, e.g. checker framework),</li>
 *   <li>we can easily replace with something else (see next point),</li>
 *   <li>tools support as for jsr305 annotations, without adding it as a dependency (java 9: split packages, unclear
 *     license, dormant), see also maven provided scope</li>
 *   <li>we can have different scope (e.g. javax.annotations.Nullable can not be applied to generics, ours can)</li>
 *   <li>for migration: gives us control on annotation place, so that we can annotate all packages and then gradually
 *     add where default applies: return, param, field, generic type</li>
 * </ul>
 *
 * @see org.anyname.nullsafety.Nullable
 * @see org.anyname.nullsafety.NonNullScope
 * @see org.anyname.nullsafety.NonNullApi
 * @see org.anyname.nullsafety.NonNullFields
 */
package org.anyname.nullsafety;