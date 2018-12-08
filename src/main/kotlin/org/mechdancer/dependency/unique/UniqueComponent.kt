package org.mechdancer.dependency.unique

import org.mechdancer.dependency.Component
import org.mechdancer.dependency.firstGenericType
import kotlin.reflect.KClass
import kotlin.reflect.full.safeCast

/**
 * 抽象组件
 *   封装了默认的哈希函数和判等条件
 *   需要实现类提供自己的具体类型 [type]
 *   泛型 [T] 可保证此类型来自这个实现类
 */
abstract class UniqueComponent<T : UniqueComponent<T>>
(type: KClass<T>? = null) : Component {

    val type = type ?: javaClass.kotlin.firstGenericType(UniqueComponent::class)

    override fun equals(other: Any?) =
            this === other || type.safeCast(other) !== null

    override fun hashCode() =
            type.hashCode()
}
