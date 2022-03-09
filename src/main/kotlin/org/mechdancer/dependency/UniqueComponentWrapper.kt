package org.mechdancer.dependency

import kotlin.reflect.KClass
import kotlin.reflect.safeCast

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