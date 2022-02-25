package org.mechdancer.dependency

import org.junit.Assert
import org.junit.Test
import org.mechdancer.dependency.annotated.Must
import org.mechdancer.dependency.annotated.annotatedInjector
import org.mechdancer.dependency.manager.ManagedHandler
import org.mechdancer.dependency.manager.managedHandler

class QQQ : Component {
    override fun equals(other: Any?) = true
    override fun hashCode() = javaClass.hashCode()
}

class WWW : Dependent, ManagedHandler by managedHandler() {

    val qqq1 by manager.must<QQQ> { true }
    val qqq2 by manager.must<QQQ> { true }
    val qqq3 by manager.must<QQQ> { true }
    val qqq4 by manager.must<QQQ> { true }

    override fun equals(other: Any?): Boolean = true
    override fun hashCode(): Int = javaClass.hashCode()
}

class RRR : Dependent {
    @Must
    lateinit var qqq1: QQQ

    @Must
    lateinit var qqq2: QQQ

    @Must
    lateinit var qqq3: QQQ

    @Must
    lateinit var qqq4: QQQ

    private val injector by annotatedInjector()

    override fun equals(other: Any?): Boolean = true

    override fun hashCode(): Int = javaClass.hashCode()

    override fun handle(dependency: Component): Boolean = injector.handle(dependency)
}

class TestDuplicate {
    @Test
    fun test() {
        val qqq = QQQ()
        val www = WWW()
        val rrr = RRR()
        scope {
            this += qqq
            this += www
            this += rrr
        }
        Assert.assertEquals(www.qqq1, qqq)
        Assert.assertEquals(www.qqq2, qqq)
        Assert.assertEquals(www.qqq3, qqq)
        Assert.assertEquals(www.qqq4, qqq)
        Assert.assertEquals(rrr.qqq1, qqq)
        Assert.assertEquals(rrr.qqq2, qqq)
        Assert.assertEquals(rrr.qqq3, qqq)
        Assert.assertEquals(rrr.qqq4, qqq)
    }
}