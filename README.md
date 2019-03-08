# null-safety [![Build Status](https://travis-ci.com/marycha/null-safety.svg?branch=master)](https://travis-ci.com/marycha/null-safety)

There are many `@NotNull`/`@Nullable` annotations and tools interpreting them. There is no standard solution.
It may be overwhelming to choose which of them to use. The aim of this project is to 
show some of the possible choices. 

The idea to bring nullsafety to Java was [inspired by Kotlin](https://github.com/marycha/null-safety/wiki/Inspiration:-null-safety-in-Kotlin).

:construction: :construction: :construction:

##### Contents
1. [`@Nullable` annotations with rationale of API choices](src/main/java/org/anyname/nullsafety/package-info.java) + [test](src/test/java/org/anyname/nullsafety/NullableTest.java) to document the behaviour (with different tools)
2. System boundaries: [test](src/test/java/org/anyname/nullsafety/NullsafetyBounderyTest.java) to verify that each field of types implementing `NullsafetyBoundary` is either `@NotNull` (from *Java Beans Validation API*, validated at runtime during deserialization) or `@Nullable` (verified at compile-time) + [example POJOs](src/main/java/org/anyname/xml/) where bindings framework, in this case jaxb, injects values at runtime
3. Maven config for compile time verification: NullAway (via error-prone) and Checker Framework - TBD
4. Legacy code: [some tools and thought](https://github.com/marycha/null-safety/wiki/Legacy-code:-tips-for-migration) on how to approach annotating legacy code
5. IDEs setup - TBD


##### Levels of nullsafety
1. readability - bring attention to potential issues by explicitly marking references which can hold null value - humans verified
2. IDE highlights potential issues - bring attention without forcing to annotate whole legacy codebase
3. compile time verification of the code (I - locally: 90%, II - remotely: 100%)

4. system boundaries: runtime validation of values injected at runtime (bindings: XML, json, JPA) with Java Beans Validation API\
    test which ensures that fields with input values are either @NotNull (validated by binding framework) or @Nullable for compile time verification of correct usage further on
    
5. 3rd party libs contracts: what they return and what we pass to them. \
    If annotated: trust or not? \
    If not annotated: pessimistic approach or annotate externally?
6. NPE thrown by 3rd party libs (other then because of incorrect input)

7. legacy code - there are tools which can be used to automatically add annotations to already existing code. As the codebase grows, new code can hold to higher standards and existing code can stay unannotated.

##### How? - Tools

Aim: detect as early as possible as many issues as possible.
The earlier an issues is detected, the better (less overhead to fix it, shorter feedback loop), but we don't want to do it at a cost of introducing noticeable time overhead for IDE parser or local compilation.

* IntelliJ IDE and Eclipse - brings attention while writing - both can be configured with custom annotations

* NullAway (via errorprone) - detects most NPEs - local compilation
* Checker Framework - guarantees no NPEs - CI pipeline (significant time overhead for compiler (on sample codebase: 2s->17s))

* Sonar - does not recognize meta-annotations so doesn't understand our null-safety annotations
