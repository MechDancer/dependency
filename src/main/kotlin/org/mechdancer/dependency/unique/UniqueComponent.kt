package org.mechdancer.dependency.unique

import org.mechdancer.dependency.Component
import java.lang.reflect.ParameterizedType
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

    val type = type
        ?: (javaClass.genericSuperclass as? ParameterizedType)
            ?.actualTypeArguments
            ?.asSequence()
            ?.mapNotNull { it as? Class<*> }
            ?.find { t -> UniqueComponent::class.java.isAssignableFrom(t) }
            ?.kotlin
        ?: throw RuntimeException("Unable to find component type.")

    override fun equals(other: Any?) =
        this === other || type.safeCast(other) !== null

    override fun hashCode() =
        type.hashCode()
}
