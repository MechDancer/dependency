package org.mechdancer.dependency

import org.junit.Assert
import org.junit.Test
import org.mechdancer.dependency.annotated.Must
import org.mechdancer.dependency.annotated.Unwrap
import org.mechdancer.dependency.annotated.annotatedInjector
import org.mechdancer.dependency.manager.ManagedHandler
import org.mechdancer.dependency.manager.managedHandler
import org.mechdancer.dependency.manager.mustWrapped
import org.mechdancer.dependency.manager.wrapToUniqueComponent

class J

class K : UniqueComponent<K>(), Dependent, ManagedHandler by managedHandler() {
    val j: J by manager.mustWrapped()
    val int: Int by manager.mustWrapped()
}

class L : UniqueComponent<L>(), Dependent {
    private val injector by annotatedInjector()

    @Unwrap
    @Must
    lateinit var j: J

    @Unwrap
    @Must
    var int: Int? = null

    override fun handle(scopeEvent: ScopeEvent) = injector.handle(scopeEvent)
}

class TestWrapped {
    @Test
    fun test() {
        val j = J()
        val k = K()
        val int = 233
        val l = L()
        scope {
            this += j.wrapToUniqueComponent()
            this += int.wrapToUniqueComponent()
            this += k
            this += l
        }
        Assert.assertEquals(j, k.j)
        Assert.assertEquals(int, k.int)
        Assert.assertEquals(j, l.j)
        Assert.assertEquals(int, l.int)
    }
}