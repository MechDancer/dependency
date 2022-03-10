package org.mechdancer.dependency

import kotlin.reflect.KClass

/**
 * Class version of [INamedComponent] for convenience
 */
abstract class NamedComponent<T : NamedComponent<T>>(override val name: String) :
    INamedComponent<T> {
    override fun equals(other: Any?) = defaultEquals(other)

    override fun hashCode(): Int = defaultHashCode()

    override val type: KClass<out INamedComponent<*>> by lazy { defaultType() }

}