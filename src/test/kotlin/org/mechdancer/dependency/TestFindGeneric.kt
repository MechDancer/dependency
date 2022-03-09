package org.mechdancer.dependency

import org.junit.Assert
import org.junit.Test

abstract class G1<T : G1<T>>

open class G2 : G1<G2>()

interface G3<T, U : G3<T, U, V>, V>

class G4 : G3<Int, G4, Double>

class G5<P, V> : G3<P, G5<P, V>, V>

class G6 : G1<Nothing>()

abstract class G7<T : G7<T>> : G1<T>()

class G8 : G7<G8>()

class TestFindGeneric {
    @Test
    fun test() {
        val g2 = G2()
        Assert.assertEquals(
            G2::class,
            g2.javaClass.kotlin.findSuperGenericTypeRecursively(G1::class)
        )
        val g4 = G4()
        Assert.assertEquals(
            G4::class,
            g4.javaClass.kotlin.findSuperGenericTypeRecursively(G3::class)
        )
        val g5 = G5<Int, Double>()
        Assert.assertEquals(
            G5::class,
            g5.javaClass.kotlin.findSuperGenericTypeRecursively(G3::class)
        )
        val g6 = G6()
        Assert.assertEquals(
            Void::class,
            g6.javaClass.kotlin.findSuperGenericTypeRecursively(G1::class)
        )
        val g8 = G8()
        Assert.assertEquals(
            G8::class,
            g8.javaClass.kotlin.findSuperGenericTypeRecursively(G1::class)
        )
        Assert.assertEquals(
            G8::class,
            g8.javaClass.kotlin.findSuperGenericTypeRecursively(G7::class)
        )
    }
}