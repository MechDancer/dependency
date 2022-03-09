package org.mechdancer.dependency

import org.junit.Assert
import org.junit.Test
import org.mechdancer.dependency.annotated.Maybe
import org.mechdancer.dependency.annotated.annotatedInjector
import org.mechdancer.dependency.manager.ManagedHandler
import org.mechdancer.dependency.manager.managedHandler
import org.mechdancer.dependency.manager.maybe
import org.mechdancer.dependency.manager.must

class G : UniqueComponent<G>(), Dependent, ManagedHandler by managedHandler() {

    val h: H by manager.must()

    override fun toString(): String = "G"
}

class H : UniqueComponent<H>(), Dependent {

    @Maybe
    var g: G? = null

    private val injector by annotatedInjector()
    override fun handle(scopeEvent: ScopeEvent) = injector.handle(scopeEvent)

    override fun toString(): String = "H"
}

class I : UniqueComponent<I>(), Dependent, ManagedHandler by managedHandler() {

    val h: H? by manager.maybe<H>()
    val g: G? by manager.maybe<G>()

    override fun toString(): String = "I"
}


class TestTeardown {
    @Test
    fun test() {
        val g = G()
        val h = H()
        val i = I()
        val scope = scope {
            this += g
            this += h
            this += i
        }
        Assert.assertEquals(g, h.g)
        Assert.assertEquals(g, i.g)
        Assert.assertEquals(h, g.h)
        Assert.assertEquals(h, i.h)
        scope -= g
        Assert.assertEquals(null, h.g)
        Assert.assertEquals(null, i.g)
        scope -= h
        Assert.assertEquals(null, i.h)
    }
}