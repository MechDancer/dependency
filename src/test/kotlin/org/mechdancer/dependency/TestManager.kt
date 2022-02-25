package org.mechdancer.dependency

import org.junit.Assert
import org.junit.Test
import org.mechdancer.dependency.manager.*


class E(name: String) : NamedComponent<E>(name) {
    val value = 20
    override fun toString(): String = "E"
}

object F : UniqueComponent<F>(){
    override fun toString(): String = "F"
}

class D : Dependent, ManagedHandler by managedHandler() {

    override fun equals(other: Any?): Boolean = false
    override fun hashCode(): Int = 0

    val error by manager.maybe<E>("error")

    val truly by manager.mustNamed("E") { e: E -> e.name }
    val value by manager.mustNamed("E") { e: E -> e.value }

    val f by manager.must<F>()

    override fun toString(): String = "D"
}

class TestManager {
    @Test
    fun test() {
        val d = D()
        val e = E("E")
        scope {
            this += e
            this += d
            this += F
        }.also {
            println(it.components.joinToString())
        }
        Assert.assertEquals(d.error, null)
        Assert.assertEquals(d.truly, "E")
        Assert.assertEquals(d.value, 20)
        Assert.assertEquals(d.f, F)
    }
}
