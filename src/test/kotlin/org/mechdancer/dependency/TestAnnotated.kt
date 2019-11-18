package org.mechdancer.dependency

import org.junit.Assert
import org.junit.Test
import org.mechdancer.dependency.annotated.AnnotatedDependent
import org.mechdancer.dependency.annotated.Maybe
import org.mechdancer.dependency.annotated.Must
import org.mechdancer.dependency.annotated.Name
import java.util.concurrent.atomic.AtomicInteger

class A : UniqueComponent<A>() {
    val i = int.getAndIncrement()

    companion object {
        val int = AtomicInteger(0)
    }

    override fun toString(): String = "${javaClass.simpleName} $i"
}

class B : NamedComponent<B>("B") {
    val i = int.getAndIncrement()

    companion object {
        val int = AtomicInteger(2)
    }

    override fun toString(): String = "${javaClass.simpleName} $i"
}

class C : AnnotatedDependent() {

    @Must
    lateinit var a: A

    @Maybe
    @Name("B")
    var b: B? = null

    override fun equals(other: Any?): Boolean = false

    override fun hashCode(): Int = 233

}

class TestAnnotated {
    @Test
    fun test() {
        val c = C()
        val a0 = A()
        val a1 = A()
        val b0 = B()
        val b1 = B()
        scope {
            this += a0
            this += a1
            this += c
            this += b1
            this += b0
        }
        Assert.assertEquals(a0, c.a)
        Assert.assertEquals(b1, c.b)
    }
}