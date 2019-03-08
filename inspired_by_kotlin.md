## Null-safety in Kotlin

In Kotlin, null pointer exceptions are detected and eliminated at compile time. 
The type system distinguishes between references that can hold null and those that can not.

```kotlin
var a: String = null // compilation error
var b: String? = null // ok
```

`.` operator can be used to dereference non-nullable variable only:
```kotlin
val l = a.length // ok
val l = b.length // compile-time error: variable 'b' can be null
```

Safe calls `?.` operator can be used to dereference nullable variable:
```kotlin
val l = b?.length
val l: Int? = if (b != null) b.length else null
```

Elvis operator `?:` can be used to replace null-evaluated expression with non-null value:
```kotlin
val l = b?.length ?: -1
val l: Int = if (b != null) b.length else -1
```

More on null-safety in Kotlin:
https://kotlinlang.org/docs/reference/null-safety.html
