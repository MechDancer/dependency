package org.mechdancer.dependency

/**
 * [Dependent] is a type of [Component] which needs other components as dependencies
 *
 * [handle] will be called when a new component arrives [DynamicScope], until all dependencies are retrieved.
 */
interface Dependent : Component, DependencyHandler {
    override fun handle(dependency: Component): Boolean
}
