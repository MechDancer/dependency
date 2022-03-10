package org.mechdancer.dependency

import kotlin.reflect.KClass
import kotlin.reflect.safeCast

/**
 * [IUniqueComponent] is a type of [Component] associated with unique type [T]
 *
 * [IUniqueComponent]s with the same type can not coexist in the scope
 */
interface IUniqueComponent<T : IUniqueComponent<T>> : Component {
    val type: KClass<out IUniqueComponent<*>>

    fun defaultType() = javaClass.kotlin.findSuperGenericTypeRecursively(IUniqueComponent::class)

    fun defaultHashCode() = type.hashCode()

    fun defaultEquals(other: Any?) = this === other || type.safeCast(other) !== null
}