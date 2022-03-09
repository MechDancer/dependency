package org.mechdancer.dependency

/**
 * [TeardownStrictDependencyException] is the exception indicates that
 * a component which was depended on strictly was being removed from the scope
 */
class TeardownStrictDependencyException(
    toRemove: Component,
    dependency: TypeSafeDependency.Dependency<*>
) : RuntimeException("Cannot teardown $toRemove from scope, $dependency depends strictly on this")
