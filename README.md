 null-safety [![Build Status](https://travis-ci.com/marycha/null-safety.svg?branch=master)](https://travis-ci.com/marycha/null-safety)
============

There are many `@NotNull`/`@Nullable` annotations and tools interpreting them. There is no standard solution.
The aim of this project is to show some of the possible choices which together make a holistic approach to eliminate NPEs. 

The idea to bring nullsafety to Java was [inspired by Kotlin](../../wiki/Inspiration:-null-safety-in-Kotlin).

:construction: :construction: :construction:


 Contents
----------------

Compile-time verification based on data-flow analysis:
1. [`@Nullable` annotations with rationale of API choices](src/main/java/org/anyname/nullsafety/package-info.java) + [test](src/test/java/org/anyname/nullsafety/NullableTest.java) to document the behaviour (with different tools)
2. External libraries - TBD
3. Legacy code: [some tools and thought](../../wiki/Legacy-code:-tips-for-migration) on how to approach annotating legacy code

Runtime validation of input data: \
4. System boundaries: [test](src/test/java/org/anyname/nullsafety/NullsafetyBounderyTest.java) to verify that each field of types implementing `NullsafetyBoundary` is either `@NotNull` (from *Java Beans Validation API*, validated at runtime during deserialization) or `@Nullable` (verified at compile-time) + [example POJOs](src/main/java/org/anyname/xml/) where bindings framework, in this case jaxb, injects values at runtime

5. [Maven config and IDEs setup](../../wiki/Tools-configuration)


 Levels of nullsafety
----------------------------------

Aim: detect as early as possible as many issues as possible.
The earlier an issues is detected, the better - less overhead to fix it, shorter feedback loop. We don't want to do it at a cost of introducing noticeable time overhead for IDE parser or local compilation.

1. readability - bring attention to potential issues by explicitly marking references which can hold null value
2. IDE highlights potential issues - bring attention (without forcing to annotate whole legacy codebase) \
    `IntelliJ IDEA` and `Eclipse` - both can be configured with custom annotations
3. compile-time verification of the code \
    I - detect most NPEs, with [NullAway](https://github.com/uber/NullAway) via [error-prone](http://errorprone.info) - during local build, usually up to 10% compilation time overhead \
    II - guarantees no NPEs, with [Checker Framework](https://checkerframework.org/releases/0.8/checkers-manual.html#htoc14) - remotely, in CI pipeline due to significant time overhead for compiler (on sample codebase: 2s->17s)

4. system boundaries: runtime validation of values injected during program execution (bindings: XML, json, JPA) with Java Beans Validation API\
    test which ensures that fields with input values are either @NotNull (validated by binding framework) or @Nullable for compile time verification of correct usage further on
    
5. 3rd party libs contracts: what they return and what we pass to them. \
    If annotated: trust or not? \
    If not annotated: pessimistic approach or annotate externally?
6. NPE thrown by 3rd party libs (other then because of incorrect input)

7. legacy code - there are tools which can be used to automatically add annotations to already existing code. As the codebase grows, new code can hold to higher standards and existing code can stay unannotated.

---
More details can be found on our [wiki](../../wiki) pages.
