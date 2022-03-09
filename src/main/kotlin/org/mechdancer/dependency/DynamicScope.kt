package org.mechdancer.dependency

import org.mechdancer.dependency.utils.ConcurrentHashSet

/**
 * [DynamicScope] is a scope that accepts components dynamically
 * When we call [setup] to add components, the dependency will be calculated and injected.
 */
open class DynamicScope {
    /**
     * Component set
     */
    protected val components = ConcurrentHashSet<Component>()

    /**
     * Get a view of all components
     */
    fun viewComponents() = components.view

    /**
     * Add a new [component] to the scope
     *
     * @return If [component] is already in scope
     */
    open infix fun setup(component: Component) =
        components
            .add(component)
            .also {
                components.forEach {
                    if (it is Dependent)
                        it.handle(ScopeEvent.DependencyArrivedEvent(component))
                    if (component is Dependent)
                        component.handle(ScopeEvent.DependencyArrivedEvent(it))
                }
            }

    /**
     * Remove [component] from the scope
     *
     * @return If [component] is in the scope
     */
    open infix fun teardown(component: Component) =
        components
            .remove(component)
            .also {
                components.forEach {
                    if (it is Dependent)
                        it.handle(ScopeEvent.DependencyLeftEvent(component))
                }
            }

    /**
     * Clear everything
     */
    fun clear() {
        components.clear()
    }
}
