package org.mechdancer.dependency

/**
 * [Component] can be discriminated from each other
 * It needs implement [equals] and [hashCode] correctly to be saved into a hash set.
 */
interface Component {
    /**
     * Compatibility with [other] components
     */
    override fun equals(other: Any?): Boolean

    /**
     * Hash code of this component
     */
    override fun hashCode(): Int
}
