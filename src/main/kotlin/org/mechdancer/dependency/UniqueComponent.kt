package org.mechdancer.dependency

/**
 * Class version of [IUniqueComponent] for convenience
 */
abstract class UniqueComponent<T : UniqueComponent<T>> :
    IUniqueComponent<T> {

    override val type by lazy { defaultType() }

    override fun equals(other: Any?) = defaultEquals(other)

    override fun hashCode() = defaultHashCode()
}
