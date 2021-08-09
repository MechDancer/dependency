package org.mechdancer.dependency

import kotlin.reflect.KClass
import kotlin.reflect.safeCast

/**
 * 具名组件
 *
 * 同类型且同名的组件不可共存
 */
abstract class NamedComponent<T : NamedComponent<T>>
    (val name: String, type: KClass<T>? = null) : Component {

    @Suppress("UNCHECKED_CAST")
    val type = type ?: javaClass.kotlin.firstGenericType(NamedComponent::class) as KClass<T>

    override fun equals(other: Any?) =
        this === other || type.safeCast(other)?.name == name

    override fun hashCode() =
        (type.hashCode() shl 31) + name.hashCode()
}
