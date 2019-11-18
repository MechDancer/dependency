package org.mechdancer.dependency

import org.mechdancer.dependency.TypeSafeDependency.Dependency
import org.mechdancer.dependency.TypeSafeDependency.WeakDependency
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * 典型依赖管理器
 */
class DependencyManager {
    // 尚未装载的依赖项集
    private val dependencies = mutableListOf<TypeSafeDependency<*>>()

    // 添加依赖项到集合，发生冲突时产生异常
    private fun <T : Component> add(dependency: TypeSafeDependency<T>) =
        synchronized(dependencies) { dependencies.add(dependency) }

    /** 每一次扫描都清除成功装载的依赖项 */
    fun sync(dependency: Component) =
        synchronized(dependencies) {
            dependencies.removeIf { it.set(dependency) != null } && dependencies.isEmpty()
        }

    /** 构造一个 [C] 类型的强依赖 */
    fun <C : Component> dependOnStrict(type: KClass<C>, predicate: (C) -> Boolean) =
        Dependency(type, predicate).also { add(it) }

    /** 构造一个 [C] 类型的弱依赖 */
    fun <C : Component> dependOnWeak(type: KClass<C>, predicate: (C) -> Boolean) =
        WeakDependency(type, predicate).also { add(it) }

    /** 构造一个 [C] 类型的强依赖 */
    inline fun <reified C : Component> dependency(noinline predicate: (C) -> Boolean) =
        dependOnStrict(C::class, predicate)

    /** 构造一个 [C] 类型的弱依赖 */
    inline fun <reified C : Component> weakDependency(noinline predicate: (C) -> Boolean) =
        dependOnWeak(C::class, predicate)

    /** 从一个 [C] 类型的强依赖取值 */
    inline fun <reified C : Component, T> must(noinline predicate: (C) -> Boolean,
                                               crossinline block: (C) -> T): Lazy<T> {
        val dependency = dependency(predicate)
        return lazy { dependency.field.let(block) }
    }

    /** 从一个 [C] 类型的弱依赖取值 */
    inline fun <reified C : Component, T> maybe(noinline predicate: (C) -> Boolean,
                                                default: T,
                                                crossinline block: (C) -> T): Lazy<T> {
        val dependency = weakDependency(predicate)
        return lazy { dependency.field?.let(block) ?: default }
    }

    /** 构造一个 [C] 类型的强依赖属性代理 */
    inline fun <reified C : Component> must(noinline predicate: (C) -> Boolean) =
        object : ReadOnlyProperty<Dependent, C> {
            private val core = dependency(predicate)
            override fun getValue(thisRef: Dependent, property: KProperty<*>) = core.field
        }

    /** 构造一个 [C] 类型的弱依赖属性代理 */
    inline fun <reified C : Component> maybe(noinline predicate: (C) -> Boolean) =
        object : ReadOnlyProperty<Dependent, C?> {
            private val core = weakDependency(predicate)
            override fun getValue(thisRef: Dependent, property: KProperty<*>) = core.field
        }
}
