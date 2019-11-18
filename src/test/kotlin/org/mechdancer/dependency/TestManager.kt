package org.mechdancer.dependency

import org.junit.Assert
import org.junit.Test

class E(name: String) : NamedComponent<E>(name) {
    val value = 20
}

object F : UniqueComponent<F>()

class D : Dependent {
    override fun sync(dependency: Component): Boolean = manager.sync(dependency)

    override fun equals(other: Any?): Boolean = false
    override fun hashCode(): Int = 0

    val manager = DependencyManager()

    val error by manager.maybe<E>("error")

    val truly by manager.mustNamed("E") { e: E -> e.name }
    val value by manager.mustNamed("E") { e: E -> e.value }

    val f by manager.must<F>()
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
        }
        Assert.assertEquals(d.error, null)
        Assert.assertEquals(d.truly, "E")
        Assert.assertEquals(d.value, 20)
        Assert.assertEquals(d.f, F)
    }
}
