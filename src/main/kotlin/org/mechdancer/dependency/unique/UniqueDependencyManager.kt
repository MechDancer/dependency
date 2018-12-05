package org.mechdancer.dependency.unique

import org.mechdancer.dependency.Component
import org.mechdancer.dependency.Dependent
import org.mechdancer.dependency.unique.UniqueDependency.Dependency
import org.mechdancer.dependency.unique.UniqueDependency.WeakDependency
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * 以类型区分的依赖项管理
 *   应聚合到 `Dependent` 内部
 */
class UniqueDependencyManager {
    // 尚未装载的依赖项集
    private val dependencies = hashSetOf<UniqueDependency<*>>()

    // 添加依赖项到集合，发生冲突时产生异常
    private fun <T : Component> add(dependency: UniqueDependency<T>, type: KClass<T>) =
        synchronized(dependencies) {
            if (!dependencies.add(dependency))
                throw RuntimeException("try to add the second ${type.qualifiedName}")
        }

    /** 每一次扫描都清除成功装载的依赖项 */
    fun sync(dependency: Component) =
        synchronized(dependencies) {
            dependencies.removeIf { it.set(dependency) != null } && dependencies.isEmpty()
        }

    /** 构造一个 [C] 类型的强依赖 */
    infix fun <C : Component> dependOnStrict(type: KClass<C>) =
        Dependency(type).also { add(it, type) }

    /** 构造一个 [C] 类型的弱依赖 */
    infix fun <C : Component> dependOnWeak(type: KClass<C>) =
        WeakDependency(type).also { add(it, type) }

    /** 构造一个 [C] 类型的强依赖 */
    inline fun <reified C : Component> dependency() = dependOnStrict(C::class)

    /** 构造一个 [C] 类型的弱依赖 */
    inline fun <reified C : Component> weakDependency() = dependOnWeak(C::class)

    /** 从一个 [C] 类型的强依赖取值 */
    inline fun <reified C : Component, T> must(crossinline block: (C) -> T): Lazy<T> {
        val dependency = dependency<C>()
        return lazy { dependency.field.let(block) }
    }

    /** 从一个 [C] 类型的弱依赖取值 */
    inline fun <reified C : Component, T> maybe(default: T, crossinline block: (C) -> T): Lazy<T> {
        val dependency = weakDependency<C>()
        return lazy { dependency.field?.let(block) ?: default }
    }

    /** 构造一个 [C] 类型的强依赖属性代理 */
    inline fun <reified C : Component> must() =
        object : ReadOnlyProperty<Dependent, C> {
            private val core = dependency<C>()
            override fun getValue(thisRef: Dependent, property: KProperty<*>) = core.field
        }

    /** 构造一个 [C] 类型的弱依赖属性代理 */
    inline fun <reified C : Component> maybe() =
        object : ReadOnlyProperty<Dependent, C?> {
            private val core = weakDependency<C>()
            override fun getValue(thisRef: Dependent, property: KProperty<*>) = core.field
        }
}