## Legacy code: tips for migration = process of annotating

##### Criteria to split codebase into smaller chunks

* Independence of annotating modules one from each others
* Flexibility of approaches to annotate a module (see Yellow zone: possible paths)
* Clustering of modules (packages?):
    ** few dependencies (low coupling)
    ** biggest go alone, smallest can go together
    
##### [Clusters of] modules annotating order

* Where most NPE exceptions flee first
* Where we change code most often
* Where we can separate changes easiest
* Starting from top or bottom of dependency graph

##### ZONES during migration

Some IDEs, IntelliJ for instance, allow to define custom code formatter rules for user defined 
scopes in the code. During migration, different scopes can be used to easily distinguish between 
code which has been already annotated (but possibly has everything non-nullable) and code which hasn't.

* Red: not annotated
     
* Yellow: under migration – goal: bring attention to potential issues\
     IDE highlighting\
     @Nullable + @NonNullApi and @NonNullFields
     
* Green: annotated – compile time detection of most of NPE\
     NullAway (via errorprone)\
     CI pipeline + IDEs compiler
     
* Greener: all type uses annotated – compile time verification of NPE absence\
     Checker Framework
     
* Greenest: system boundaries guarded – runtime verification of input data
     
* Gray: outside scope of our code\
     until? - many libraries start describe API contracts by annotating it with nullable annotations\
     CheckerFramework provides method to annotate 3rd party jars by annotating API in external files
     
##### Yellow zone: possible paths
(to NullAway, some ideas and tools to consider)

* by scope: module, package, class, [method]

* by place: add defaults @NonNullApi and @NonNullFields
    Api can be split to return and param if we think it will make migration easier

* try analyzing flow\
    from entry points?

* try starting from adding @Nullable only and adding defaults later

* try searching for patterns\
    return null;, = null;, null,, Optional.ofNullable, etc.

* try IntelliJ Infer nullity\
    based on static analyzes

* try Daikon\
    based on null values detection during program execution
    
##### From yellow to the green land: dev tools thresholds
     
* IDEs 
     parser highlighting -> error-prone compiler
     
* NullAway and Checker Framework
     warn -> error
     
* CI pipeline - Github
     weak -> strong requirement to accept PR