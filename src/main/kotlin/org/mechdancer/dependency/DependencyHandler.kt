package org.mechdancer.dependency

/**
 * [DependencyHandler] handles new dependencies that are added to [DynamicScope]
 */
interface DependencyHandler {
    /**
     * Accepts a new [dependency]
     *
     * @return If it is satisfied, i.e., telling the caller that it no longer needs more dependencies
     */
    fun handle(dependency: Component): Boolean
}