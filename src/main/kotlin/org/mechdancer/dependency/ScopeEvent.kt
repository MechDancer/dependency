package org.mechdancer.dependency

/**
 * Events of a [DynamicScope]
 */
sealed interface ScopeEvent {
    /**
     * A new component was added to the scope
     */
    @JvmInline
    value class DependencyArrivedEvent(val dependency: Component) : ScopeEvent

    /**
     * A component was removed from the scope
     */
    @JvmInline
    value class DependencyLeftEvent(val dependency: Component) : ScopeEvent
}
