 null-safety [![Build Status](https://travis-ci.com/mariha/null-safety.svg?branch=master)](https://travis-ci.com/mariha/null-safety)
============

Annotations which are meant to bring null-safety to the codebase. 

In particular, we'd like to catch cases when:
* we try to dereference something nullable, or
* we assign something nullable (or `null`) to a variable otherwise assumed to be non-null

There already exist many `@NotNull`/`@Nullable` annotations and tools interpreting them, so why create yet another one? All of them have some limitations, there is no standard solution that fits everywhere. The aim of this project is to show some of the possible choices which together make a holistic approach to eliminate `NullPointerException`s. 

Our objective was to detect as early as possible as many issues as possible. The earlier an issues is detected, the better - less overhead to fix it, shorter feedback loop to learn on it. Though, we didn't want to do it at a cost of introducing noticeable time overhead for IDE parser or local compilation.
 
The idea was [inspired by Kotlin](../../wiki/Inspiration:-null-safety-in-Kotlin), the API and implementation by [Spring](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#null-safety). Feel free to inspire with what we've done too.
 
---

**Goals**:
* increase visibility of potential NPE issues (code itself, IDEs highlighting)
* automatically detect and prevent most of NPE at compile time (by analyzing data-flow graph)
* guarantee their absence (enhance java type system for null-safety)
* at the system boundaries (untrusted 3rd party libs, reflection-based bindings frameworks), ensure at runtime that actual data is ok with its declared nullability - which flow was verified at compile-time. 

**Priorities**: 1. expressiveness 2. tools support

### Why null safety?
It's best practice to avoid null values all together. Usually, when we try to understand or implement an algorithm, we think about happy path only, what positive steps need to happen to get the answer. We generalize by ignoring corner cases. `null` is a reverse, it is used to represent such a corner case. We tend to forget about them, and then we get NPEs. Even if we do remember, we have to handle them with null checks, which distract attention of those who read our code from that straight-to-the-point path. They add unnecessary complexity to the code making it less readable and harder to understand.

Often there are better ways to model _absence of a value_ and they should be used instead (e.g. for constructor params: try builder or factory methods, for return values try Optional, for collections and arrays try using empty instances instead, sometimes Null Object pattern or Strategy may be the way, and probably there are many other alternatives to consider).

### What do we do?
We **assume everything to be non-null by default**. For the rare cases when we want to make an exception from this rule, [`@Nullable`]((src/main/java/org/anyname/nullsafety/Nullable.java)) annotation should be used. Marking non-null scope (package or class) with [`@NonNullScope`](src/main/java/org/anyname/nullsafety/NonNullScope.java) (or [`@NonNullFields`](src/main/java/org/anyname/nullsafety/NonNullFields.java) and [`@NonNullApi`](src/main/java/org/anyname/nullsafety/NonNullApi.java)) annotations allows static code analysis tools (built into IDEs or integrating with javac compiler or build systems) to infer intended non-nullness and automatically detect and highlight potential issues: dereference of nullable or assign of null to something expected to be non-null - which leads to unchecked dereference later on.

Intentionally, there is no `NotNull`/`Nonnull` annotation for compile-time verification. Explicitly annotating code with it would work for compiler and static code analysis tools, would make it harder for people though. `@Nullable` quantifiers would get lost from our perception in between many `@Nonnull` quantifiers, the code would become unnecessarily verbose and so less readable and harder to process by human brain.

At the system boundaries, where binding frameworks parse and inject input-data during program execution, values are not known at compile-time. In such cases, verification based on static code analysis can (and should) be complimented by runtime validation of constraints for that unverified input data. This is where `@javax.validation.constraints.NotNull` annotation and alike come to play. We wrote a [test](src/test/java/org/anyname/nullsafety/NullsafetyBounderyTest.java) which ensures that each field of a class implementing [`NullsafetyBoundary`](src/main/java/org/anyname/nullsafety/NullsafetyBoundery.java) is either `@NotNull` (correctness guarded by bindings framework or our own deserializer ([example](src/main/java/org/anyname/xml/XMLSerializer.java#L61)), and the null-safety default) or `@Nullable` for compile-time verification of correct usage further-on based on data-flow analyses.

Our null-safety annotations are implemented using JSR-305 annotations (`javax.annotation.Nullable` and `javax.annotation.CheckForNull`) and meta-annotations (`javax.annotation.meta.TypeQualifierDefault` and `javax.annotation.meta.TypeQualifierNickname`) so that we can leverage on tools that understand them.

###### Why not using JSR-305 annotations directly?
In general, a level of indirection gives us more flexibility. Specifically:    
* provides 'default' mechanism, it would be too verbose to annotate everything with @NotNull, elsewhere used @ParametersAreNonnullByDefault doesn't apply to return values which we care most about
* provides 'nickname' mechanism, javax.annotation.Nullable is misleading, javax.annotation.CheckForNull is what we want with @Nullable alias
* we can group under one alias two or more annotations (for different tools, e.g. checker framework),
* we can easily replace with something else (see next point),
* tools support as for jsr305 annotations, without adding it as a dependency (java 9: split packages, unclear license, dormant), see also maven provided scope
* we can have different scope (e.g. javax.annotations.Nullable can not be applied to generics, ours can)
* for migration: gives us control on annotation place, so that we can annotate all packages and then gradually add where default applies: return, param, field, generic type


 Contents
----------------

* [`@Nullable` annotations](src/main/java/org/anyname/nullsafety/)
* Compile-time verification of declared nullability based on data-flow analysis:
    1. [maven config and IDEs setup](../../wiki/Tools-configuration) and [test documenting the behaviour](src/test/java/org/anyname/nullsafety/NullableTest.java) with different tools, 
    2. How to handle external libraries - TBD
    3. Legacy code: [some tools and ideas](../../wiki/Legacy-code:-tips-for-migration) on how to approach annotating already existing codebase

* Runtime validation of data at the system boundaries:

    4. [test](src/test/java/org/anyname/nullsafety/NullsafetyBounderyTest.java) to verify that each field of types implementing [`NullsafetyBoundary`](src/main/java/org/anyname/nullsafety/NullsafetyBoundery.java) is either `@NotNull` (from *Java Beans Validation API*, [validated at runtime during deserialization](src/main/java/org/anyname/xml/XMLSerializer.java#L61)) or `@Nullable` (verified at compile-time) 
    5. [example POJOs](src/main/java/org/anyname/xml/) to which bindings framework, in this case jaxb, deserializes values

---
More details can be found on our [wiki](../../wiki) pages.
