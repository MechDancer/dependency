package org.mechdancer.dependency

import org.mechdancer.dependency.utils.ConcurrentHashSet
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * [DynamicScope] is a scope that accepts components dynamically
 * When we call [setup] to add components, the dependency will be calculated and injected.
 */
open class DynamicScope {
    /**
     * Component set
     * We look up specific types of components from here.
     * There's no chance to remove a component.
     */
    private val _components = ConcurrentHashSet<Component>()

    /**
     * Dependent list
     * Dependents' [Dependent.handle] will be called once new component arrives.
     * Dependents will be removed from the list once they are satisfied, i.e., their [Dependent.handle]
     * return `true`.
     */
    private val dependents = ConcurrentLinkedQueue<(Component) -> Boolean>()

    /**
     * Get a view of all components
     */
    val components = _components.view

    /**
     * Add a new [component] to the scope
     *
     * @return If [component] is already in scope
     */
    open infix fun setup(component: Component) =
        _components
            .add(component)
            .also {
                // Update the status of dependents
                if (it)
                    dependents.removeIf { it(component) }

                if (component is Dependent)
                    component::handle
                        .takeIf { handle -> _components.none(handle) }
                        ?.let(dependents::add)
            }

    /**
     * Clear everything
     */
    fun clear() {
        _components.clear()
        dependents.clear()
    }
}
