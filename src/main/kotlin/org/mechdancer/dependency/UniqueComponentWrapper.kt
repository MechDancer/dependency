package org.mechdancer.dependency

import kotlin.reflect.KClass
import kotlin.reflect.safeCast

/**
 * Wraps an object to a component which has a similar implementation to [UniqueComponent]
 *
 * This class is useful in the case that we can't modify some classes to make them components
 *
 * Invariance: the implementation in dependency manager and annotation injector enforce
 * the invariance on the wrapped type, which means there is no chance to cast an instance
 * to its supertype, and you should declare the dependency with the type exactly the same as you
 * wrapped and set up in scope.
 */
class UniqueComponentWrapper<T : Any> @PublishedApi internal constructor(
    val type: KClass<*>,
    val wrapped: T
) : Component {

    companion object {
        inline operator fun <reified T : Any> invoke(wrapped: T) =
            UniqueComponentWrapper(T::class, wrapped)
    }

    override fun equals(other: Any?): Boolean =
        this === other || (other is UniqueComponentWrapper<*> && type.safeCast(other.wrapped) !== null)

    override fun hashCode(): Int = type.hashCode()

}