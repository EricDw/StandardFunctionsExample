@file:Suppress("KDocUnresolvedReference")

package com.examples.standardfunctions

import java.util.*


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
 **/
fun main() {

    // region Person
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

    data class Purl(
        private val knownProgrammingLanguages: List<String> = emptyList()
    ) : Person(
        id = UserId(),
        name = "Purl",
        age = 25,
        profession = "Software Developer"
    ) {
        val clairPrinter = {
            println("$name $age $profession $friends $knownProgrammingLanguages")
        }
    }

    data class Lacy(
        private val knownDesignPatterns: List<String> = emptyList(),
        private val knownProgrammingLanguages: List<String> = emptyList()
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
    /*
    printDetailsFor(bob)

    bob.print()

    bob.printExpression()

    personDetailsPrinter(bob)

    getPersonPrinter()(bob)

    getPersonPrinterExpression()(bob)
*/
    // endregion Person Functions

    // region run()
    /*
    * Java can run a block of code of arbitrary code like this:
    *
     {
       someMethod()
     }
    *
    * But Kotlin cant do that so we can use run() to execute a block of
    * arbitrary code but with more semantic clarity.
    * */

    // Signature ==
    fun <R> runArbitraryCode(block: () -> R): R = block()

    run {
        println("Arbitrary Code Was Run")
    }

    runArbitraryCode {
        println("Arbitrary Code Was Run")
    }

    // Syntactically == too?
    fun randomCode() {
        println("Arbitrary Code Was Run")
    }
    randomCode()


    // Q: When do I use it?
    // A: When you just gotta run something like right there

    // Q: When do I use run() instead of T.run()?
    // A: When you don't need T and you don't need a reference to the function

    // endregion run()


    // region with()
    // Signature ==
    fun <T, R> runCodeScopedTo(receiver: T, block: T.() -> R): R = receiver.block()

    // Different ways to call with()
    /*
    run {

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
    */
    // endregion with()

    // region T.run()

    // Signature ==
    fun <T, R> T.runArbitraryScopedCode(block: T.() -> R): R = block()

    /*
    Syntactically == too?
    When to use it?
    When do I use it instead of ?
     */
    // endregion T.run()


    // region T.let()
    /* T.let()
    Syntactically == too?
    When to use it?
    When do I use it instead of ?
     */
    // endregion T.let()


    // region T.apply()
    /* T.apply()
    Syntactically == too?
    When to use it?
    When do I use it instead of ?
     */
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

    buildLacy {
        addDesignPattern {
            "Builder Pattern"
        }

        addProgrammingLanguage {
            "Kotlin"
        }
    }

    // endregion Putting It All Together


}