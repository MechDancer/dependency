package org.mechdancer.dependency

import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

/**
 * 依赖项
 *
 * 具体到类型 [T] 并依类型判别冲突
 * 保存到组件实例的引用
 * 线程安全
 */
sealed class TypeSafeDependency<T : Component>(
    val type: KClass<T>,
    private val predicate: (T) -> Boolean
) {
    private val _field = AtomicReference<T?>(null)

    /** 尝试置入值 [value]，若无法转换到目标类型则不产生作用 */
    fun set(value: Component): T? =
        _field.updateAndGet {
            type.safeCast(value)?.takeIf(predicate) ?: it
        }

    /** 尝试获取值 */
    open val field: T? get() = _field.get()

    /** 类型 [T] 的弱依赖项 */
    class WeakDependency<T : Component>(type: KClass<T>, predicate: (T) -> Boolean)
        : TypeSafeDependency<T>(type, predicate)

    /** 类型 [T] 的强依赖项 */
    class Dependency<T : Component>(type: KClass<T>, predicate: (T) -> Boolean)
        : TypeSafeDependency<T>(type, predicate) {
        override val field: T get() = super.field ?: throw ComponentNotExistException(type)
    }

    override fun equals(other: Any?) = this === other || (other as? TypeSafeDependency<*>)?.type == type
    override fun hashCode() = type.hashCode()
}
