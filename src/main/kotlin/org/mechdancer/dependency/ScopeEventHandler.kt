package org.mechdancer.dependency

/**
 * [ScopeEventHandler] handles [ScopeEvent]
 */
interface ScopeEventHandler {
    /**
     * Handle a [scopeEvent]
     */
    fun handle(scopeEvent: ScopeEvent)
}