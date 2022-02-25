package org.mechdancer.dependency

import kotlin.reflect.KClass
import kotlin.reflect.safeCast

/**
 * [UniqueComponent] is a type of [Component] associated with unique type [T]
 *
 * [UniqueComponent]s with the same type can not coexist in the scope
 */
abstract class UniqueComponent<T : UniqueComponent<T>>(type: KClass<T>? = null) : Component {

    val type = type ?: javaClass.kotlin.firstGenericType(UniqueComponent::class)

    override fun equals(other: Any?) =
        this === other || type.safeCast(other) !== null

    override fun hashCode() =
        type.hashCode()
}
