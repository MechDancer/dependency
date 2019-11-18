package org.mechdancer.dependency

import kotlin.reflect.KClass
import kotlin.reflect.full.safeCast

/**
 * 唯一组件
 *
 * 同类型的组件不能共存
 */
abstract class UniqueComponent<T : UniqueComponent<T>>
(type: KClass<T>? = null) : Component {

    val type = type ?: javaClass.kotlin.firstGenericType(UniqueComponent::class)

    override fun equals(other: Any?) =
        this === other || type.safeCast(other) !== null

    override fun hashCode() =
        type.hashCode()
}
