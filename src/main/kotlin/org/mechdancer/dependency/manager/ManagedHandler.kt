package org.mechdancer.dependency.manager

import org.mechdancer.dependency.ScopeEvent
import org.mechdancer.dependency.ScopeEventHandler

/**
 * An implementation of [ScopeEventHandler] using [DependencyManager]
 *
 * An example:
 * ```kotlin
 * class Bar : UniqueComponent<Bar>()
 * class Foo : Dependent, ManagedHandler by managedHandler() {
 *   val component : Bar by manager.must()
 * }
 * ```
 */
interface ManagedHandler : ScopeEventHandler {
    val manager: DependencyManager

    override fun handle(scopeEvent: ScopeEvent) = manager.handle(scopeEvent)
}



