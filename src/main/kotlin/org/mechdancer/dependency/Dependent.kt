package org.mechdancer.dependency

/**
 * [Dependent] is a type of [Component] which needs other components as dependencies
 */
interface Dependent : Component, ScopeEventHandler
