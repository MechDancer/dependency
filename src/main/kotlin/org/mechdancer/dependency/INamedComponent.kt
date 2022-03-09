package org.mechdancer.dependency

import kotlin.reflect.KClass
import kotlin.reflect.safeCast

/**
 * [INamedComponent] is a type of [Component] associated with unique [name] and [T]
 *
 * [INamedComponent]s with the same type and name can not coexist in the scope
 */
interface INamedComponent<T : INamedComponent<T>> : Component {

    val name: String

    val type: KClass<out INamedComponent<*>>

    fun defaultType() = javaClass.kotlin.findSuperGenericTypeRecursively(INamedComponent::class)

    fun defaultEquals(other: Any?) =
        this === other || type.safeCast(other)?.name == name

    fun defaultHashCode() =
        (type.hashCode() shl 31) + name.hashCode()

}
