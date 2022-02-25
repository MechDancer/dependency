package org.mechdancer.dependency

import kotlin.reflect.KClass

/**
 * [ComponentNotExistException] is the exception indicates that failed to find
 * the component with a specific type
 *
 * @param type Type of the component
 */
class ComponentNotExistException(type: KClass<out Component>) :
    RuntimeException("Cannot find this component: ${type.qualifiedName}")
