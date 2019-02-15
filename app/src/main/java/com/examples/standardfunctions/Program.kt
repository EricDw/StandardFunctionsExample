@file:Suppress("KDocUnresolvedReference")

package com.examples.standardfunctions

import androidx.annotation.Nullable
import java.util.*
import java.util.function.Function

// region Person

// region Abstract Person
data class UserId(val value: String = UUID.randomUUID().toString())

abstract class Person(
    val id: UserId,
    val name: String,
    val age: Int?,
    val profession: String?,
    val friends: List<UserId> = emptyList()
)

val bob = object : Person(
    UserId(),
    "Bob",
    25,
    "Kotlin Programmer"
) {}

// endregion Abstract Person

// region Purl
data class Purl(
    val knownProgrammingLanguages: List<String> = emptyList()
) : Person(
    id = UserId(),
    name = "Purl",
    age = 25,
    profession = "Software Developer"
) {
    val purlPrinter = {
        println("$name $age $profession $friends $knownProgrammingLanguages")
    }
}
// endregion Purl

// region Lacy
data class Lacy(
    val knownDesignPatterns: List<String> = emptyList(),
    val knownProgrammingLanguages: List<String> = emptyList()
) : Person(
    id = UserId(),
    name = "Lacy",
    age = 32,
    profession = "Software Architect"
) {
    val lacyPrinter = {
        println("$name $age $profession $friends $knownProgrammingLanguages $knownDesignPatterns")
    }
}
// endregion Lacy

// endregion Person

// region Person Functions

// Different ways of declaring functions
fun printDetailsFor(person: Person) {
    println("${person.name} ${person.age} ${person.profession} ${person.friends}")
}

val personDetailsPrinter = { person: Person ->
    println("${person.name} ${person.age} ${person.profession} ${person.friends}")
}

fun Person.print() {
    println("$name $age $profession $friends")
}

fun Person.printExpression(): Unit = println("$name $age $profession $friends")

// A function that returns a function
fun getPersonPrinter(): (Person) -> Unit {
    return personDetailsPrinter
}

// A function expression that returns a function
fun getPersonPrinterExpression(): (Person) -> Unit = personDetailsPrinter


// Different ways of calling functions
fun callFunctions(vararg functions: () -> Unit) {
    functions.forEach { it() }
}

fun callPrinters() = callFunctions(
    { printDetailsFor(bob) },
    { bob.print() },
    { bob.printExpression() },
    { personDetailsPrinter(bob) },
    { getPersonPrinter()(bob) },
    { getPersonPrinterExpression()(bob) }
)

// endregion Person Functions

/** TERMINOLOGY LEGEND
 *
 * Receiver == [this]
 *
 * block == A Function
 *
 * [it] == Automatically declared variable based on type
 *
 * Scope == Everything that can be accessed from in a function,
 *          [this] is often implied to be the Scope.
 *
 * Jump/Return/Break Label == Also known as scope targets e.g. return@run
 **/
fun main() {
//    callPrinters()

//    doRun()
//    doWith()
//    doTRun()
    doLet()
//    doApply()
//    putItAllTogether()

}

// region run()
fun doRun() {
    // Used to execute a block of code from the scope of another function

    // Signature ==
    fun <R> runArbitraryCode(block: () -> R): R = block()

    run {
        println("Arbitrary Code Was Run")
    }

    runArbitraryCode {
        println("Arbitrary Code Was Run")
    }

    // Functionally ==
    fun randomCode() {
        println("Arbitrary Code Was Run")
    }
    randomCode()

    // Q: When do I use it?
    // A: When you need to run something in a function

    // Q: When do I use run() instead of T.run()?
    // A: When you don't need T and you don't need a reference to the function

}
// endregion run()

// region with()
// Signature ==
fun <T, R> runCodeScopedTo(receiver: T, block: T.() -> R): R = receiver.block()

// Different ways to call with()
fun doWith() {

    with(bob) {
        println("$name $age $profession $friends")
    }

    with(bob) {
        printDetailsFor(this)
    }

    with(bob) {
        printExpression()
    }

    with(bob, { print() })

    runCodeScopedTo(bob) {
        print()
    }

    // Syntactically ~= too but using generics:
    fun doStuffWithVariable(variable: Person) {
        val name = variable.name
        val age = variable.age
        val profession = variable.profession
        val friends = variable.friends

        println("$name $age $profession $friends")
    }

    doStuffWithVariable(bob)

    println("${bob.name} ${bob.age} ${bob.profession} ${bob.friends}")

    // Q: When do I use it?
    // A: When accessing multiple properties / functions of an object

    // Q: When do I use with() instead of T.run() or T.apply()?
    // A: It really depends but almost never due to awkward null handling
    //    and a more verbose syntax.
}
// endregion with()

// region T.run()
// Signature ==
fun <T, R> T.runArbitraryScopedCode(block: T.() -> R): R = block()

// Functionally ~=
fun javaStyleRun(argument: Any?, function: Function<Any?, Any?>) {
    function.apply(argument)
}

fun doTRun() {
    // Usage
    bob.run { println("$name $age $profession $friends") }

    // Argument automatically filled in!! Freakin Sweet!
    bob.runArbitraryScopedCode(personDetailsPrinter)

    javaStyleRun(bob, object : Function<Any?, Any?> {
        override fun apply(t: Any?): Any? {
            t as Person
            println("${t.name} ${t.age} ${t.profession} ${t.friends}")
            return t
        }
    })
    // Or this
    javaStyleRun(bob, Function { person ->
        person as Person
        println("${person.name} ${person.age} ${person.profession} ${person.friends}")
    })

}

/*
     Q: When do I use it?
     A: When accessing multiple properties / functions of an object.

     Q: When do I use T.run() instead of with()?
     A: It really depends but almost always due to better null handling
        and a less verbose syntax.

     Q: When do I use T.run() instead of T.let()?
     A: I want the function to be scoped to T and
        I don't need the scope to be named explicitly.

     Q: When do I use T.run() instead of T.apply()?
     A: I want the function to be scoped to T and
        I am not modifying the state of T or
        I need to return a different value then T.
 */
// endregion T.run()

// region T.let()

// When do I use it?
// Mostly to reduce ambiguity caused from nested T.run()s.

// Signature ==
inline fun <T, R> T.letRunOnIt(block: (T) -> R): R = block(this)

fun doLet() {

    val oldestAgeWithRuns: Int =
        bob.age?.run bob@{
            buildStockLacy().age?.run lacy@{
                buildStockPurl().age?.run purl@{

                    var result = this@bob
                    // do work
                    if (result < this@lacy) result = this@lacy
                    if (result < this@purl) result = this@purl

                    return@bob result

                    // Wow, that's a lot of @s!
                    // We can do better!

                }
            }
        } ?: 0

    val oldestAgeWithLets: Int =
        bob.age?.let theOuterLet@{ bobsAge ->
            buildStockLacy().age?.let { laciesAge ->
                buildStockPurl().age?.letRunOnIt { purlsAge ->

                    var result = bobsAge
                    // do work
                    if (result < laciesAge) result = laciesAge
                    if (result < purlsAge) result = purlsAge

                    return@theOuterLet result

                    // That's super nice!!

                }
            }
        } ?: 0

    println(oldestAgeWithLets)

    println(oldestAgeWithRuns)

/*
Functionally == too?
When to use it?
 */
}

/*
     Q: When do I use it?
     A: The same reason you would use run but you need a named object.
        To remove ambiguity in nested T.run()s.

     Q: When do I use T.let() instead of with()?
     A: It really depends but almost always due to better null handling,
        less verbose syntax and to reduce ambiguity around [this]

     Q: When do I use T.let() instead of T.run()?
     A: Mostly for the same reasons to use it over with()

     Q: When do I use T.let() instead of T.apply()?
     A: I want the function to be scoped named and
        I need to return a different value then T.
 */

// endregion T.let()

// region T.apply()

fun doApply() = {

    // We need to make sure the algorithm works
    // so we can use T.apply() to inject some logging between the age!
    val oldestAgeWithLets: Int =
        bob.age?.let theOuterLet@{ bobsAge ->
            buildStockLacy().age?.let { laciesAge ->
                buildStockPurl().age?.let { purlsAge ->

                    var result = bobsAge

                    if (result < laciesAge) result = laciesAge
                    if (result < purlsAge) result = purlsAge

                    return@theOuterLet result
                }
            }
        } ?: 0

    println(oldestAgeWithLets)

}
// endregion T.apply()

// region Putting It All Together

// Lacy builder DSL made using what we have learned
class LacyBuilderScope {
    private val _programmingLanguages = mutableListOf<String>()
    private val _designPatterns = mutableListOf<String>()

    fun addProgrammingLanguage(
        block: () -> String
    ) = _programmingLanguages.add(block())


    fun addDesignPattern(
        block: () -> String
    ) = _designPatterns.add(block())

    fun build() = Lacy(_designPatterns, _programmingLanguages)

}

fun buildLacy(block: LacyBuilderScope.() -> Unit): Lacy =
    LacyBuilderScope().run {
        block()
        return@run build()
    }

fun buildStockLacy(block: LacyBuilderScope.() -> Unit = {}): Lacy =
    LacyBuilderScope().run {
        addProgrammingLanguage { "Kotlin" }
        block()
        return@run build()
    }

// Purl builder DSL also made using what we have learned
class PurlBuilderScope {
    private val _programmingLanguages = mutableListOf<String>()

    fun addProgrammingLanguage(
        block: () -> String
    ) = _programmingLanguages.add(block())


    fun build() = Purl(_programmingLanguages)

}

fun buildPurl(block: PurlBuilderScope.() -> Unit): Purl =
    PurlBuilderScope().run {
        block()
        return@run build()
    }

fun buildStockPurl(block: PurlBuilderScope.() -> Unit = {}): Purl =
    PurlBuilderScope().run {
        addProgrammingLanguage { "Kotlin" }
        block()
        return@run build()
    }

fun putItAllTogether() {
    val lacy = buildLacy {
        addDesignPattern {
            "Builder Pattern"
        }

        addProgrammingLanguage {
            "Kotlin"
        }
    }

    val purl = buildPurl {
        addProgrammingLanguage {
            "Kotlin"
        }
    }

    fun girlsHaveTheSameLanguageSkillSets(
        maybePurl: Purl?,
        maybeLacy: Lacy?
    ): Boolean {
        maybePurl?.run {
            maybeLacy?.run {
                // Both of the class specific functions are available to us!
                purlPrinter()
                lacyPrinter()
                // but how do we differentiate from the two with elegance?
            }
        }

        // With a let run or a let let!
        var result = false
        result = maybePurl?.let { purl: Purl ->
            maybeLacy?.run {
                return@let knownProgrammingLanguages == purl.knownProgrammingLanguages
            }
        } ?: false

        return result
    }

    // Why is this better then Java?
    fun girlsHaveTheSameLanguageSkillSetsJavaStyle(
        @Nullable maybePurl: Purl?,
        @Nullable maybeLacy: Lacy?
    ): Boolean {
        var result: Boolean = false
        if (maybePurl != null && maybeLacy != null) {
            maybePurl.purlPrinter()
            maybeLacy.lacyPrinter()
            result = maybeLacy.knownProgrammingLanguages == maybePurl.knownProgrammingLanguages
        }
        return result
    }

    // Look at everything that is happening here in just 9 lines of code!
    fun girlsHaveTheSameLanguageSkillSetsExpression(
        maybePurl: Purl? = buildStockPurl(),
        maybeLacy: Lacy? = buildStockLacy()
    ): Boolean = maybePurl?.let { purl: Purl ->
        maybeLacy?.run {
            purl.purlPrinter()
            lacyPrinter()
            return@let knownProgrammingLanguages == purl.knownProgrammingLanguages
        }
    } ?: false

}

// endregion Putting It All Together
