package org.mechdancer.dependency

import kotlin.reflect.KClass
import kotlin.reflect.safeCast

/**
 * [NamedComponent] is a type of [Component] associated with unique [name] and [T]
 *
 * [NamedComponent]s with the same type and name can not coexist in the scope
 */
abstract class NamedComponent<T : NamedComponent<T>>
(val name: String, type: KClass<T>? = null) : Component {

    @Suppress("UNCHECKED_CAST")
    val type = type ?: javaClass.kotlin.firstGenericType(NamedComponent::class) as KClass<T>

    override fun equals(other: Any?) =
        this === other || type.safeCast(other)?.name == name

    override fun hashCode() =
        (type.hashCode() shl 31) + name.hashCode()
}
