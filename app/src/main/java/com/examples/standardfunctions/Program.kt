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

    data class Person(
        val id: UserId,
        val name: String,
        val age: Int?,
        val profession: String?,
        val friends: List<UserId> = emptyList()
    )

    val bob = Person(
        UserId(),
        "Bob",
        25,
        "Programmer"
    )
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
    printDetailsFor(bob)

    bob.print()

    bob.printExpression()

    personDetailsPrinter(bob)

    getPersonPrinter()(bob)

    getPersonPrinterExpression()(bob)

    // endregion Person Functions

    /* with()
    * Signature ==
    */
    fun <T, R> runCodeScopedTo(receiver: T, block: T.() -> R ): R = receiver.block()


    // Different ways to call with()

    with(bob) {
        println("$name $age $profession $friends")
    }

    with(bob) {
        printDetailsFor(this)
    }

    with(bob) {
        printExpression()
    }

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
    // A: It really depends but almost never

    /* run()
    Syntactically == too?
    When to use it?
    When do I use it instead of ?
     */


    /* T.run()
    Syntactically == too?
    When to use it?
    When do I use it instead of ?
     */


    /* T.let()
    Syntactically == too?
    When to use it?
    When do I use it instead of ?
     */


    /* T.apply()
    Syntactically == too?
    When to use it?
    When do I use it instead of ?
     */


}