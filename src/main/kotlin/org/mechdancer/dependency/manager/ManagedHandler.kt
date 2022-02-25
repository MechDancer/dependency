package org.mechdancer.dependency.manager

import org.mechdancer.dependency.Component
import org.mechdancer.dependency.DependencyHandler

/**
 * An implementation of [DependencyHandler] using [DependencyManager]
 *
 * An example:
 * ```kotlin
 * class Bar : UniqueComponent<Bar>()
 * class Foo : Dependent, ManagedHandler by managedHandler() {
 *   val component : Bar by manager.must()
 * }
 * ```
 */
interface ManagedHandler : DependencyHandler {
    val manager: DependencyManager

    override fun handle(dependency: Component): Boolean = manager.handle(dependency)
}



