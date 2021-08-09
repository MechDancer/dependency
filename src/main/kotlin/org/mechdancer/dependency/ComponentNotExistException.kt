package org.mechdancer.dependency

import kotlin.reflect.KClass

/**
 * 组件不存在异常
 *
 * @param which 要找的组件类型
 */
class ComponentNotExistException(which: KClass<out Component>) :
    RuntimeException("Cannot find this component: ${which.qualifiedName}")
