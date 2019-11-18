package org.mechdancer.dependency

import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

/** 找到一种 [C] 类型的依赖 */
inline fun <reified C : Component> Iterable<Component>.get(): List<C> =
    mapNotNull { it as? C }

/** 找到一种 [C] 类型的依赖 */
inline fun <reified C : Component> Iterable<Component>.maybe(): C? =
    get<C>().singleOrNull()

/** 找到一种 [C] 类型的依赖 */
inline fun <reified C : Component> Iterable<Component>.must(): C =
    maybe() ?: throw ComponentNotExistException(C::class)

/** 向动态域添加新的依赖项 */
operator fun DynamicScope.plusAssign(component: Component) {
    setup(component)
}

/** 向动态域添加并执行一个操作 */
inline fun <T : Component> DynamicScope.setup(component: T, block: T.() -> Unit) {
    if (setup(component)) block(component)
}

/** 构造动态域 */
fun scope(block: DynamicScope.() -> Unit) =
    DynamicScope().apply(block)

/** 构造映射浏览器 */
fun <T, U> buildView(map: Map<T, U>) =
    object : Map<T, U> by map {}

/** 找到第一个上界为 [upper] 的泛型 */
fun KClass<*>.firstGenericType(upper: KClass<*>) =
    (java.genericSuperclass as? ParameterizedType)
        ?.actualTypeArguments
        ?.asSequence()
        ?.mapNotNull { it as? Class<*> }
        ?.find { t -> upper.java.isAssignableFrom(t) }
        ?.kotlin
        ?: throw RuntimeException("Unable to find component type.")

/** 构造一个 [C] 类型的强依赖 */
fun <C : UniqueComponent<C>>
    DependencyManager.dependOnStrict(type: KClass<C>) =
    dependOnStrict(type) { true }

/** 构造一个 [C] 类型的强依赖 */
fun <C : NamedComponent<C>>
    DependencyManager.dependOnStrict(type: KClass<C>, name: String) =
    dependOnStrict(type) { it.name == name }

/** 构造一个 [C] 类型的弱依赖 */
fun <C : UniqueComponent<C>>
    DependencyManager.dependOnWeak(type: KClass<C>) =
    dependOnWeak(type) { true }

/** 构造一个 [C] 类型的弱依赖 */
fun <C : NamedComponent<C>>
    DependencyManager.dependOnWeak(type: KClass<C>, name: String) =
    dependOnWeak(type) { it.name == name }

/** 构造一个 [C] 类型的强依赖 */
inline fun <reified C : UniqueComponent<C>>
    DependencyManager.dependency() =
    dependOnStrict(C::class) { true }

/** 构造一个 [C] 类型的强依赖 */
inline fun <reified C : NamedComponent<C>>
    DependencyManager.dependency(name: String) =
    dependOnStrict(C::class) { it.name == name }

/** 构造一个 [C] 类型的弱依赖 */
inline fun <reified C : UniqueComponent<C>>
    DependencyManager.weakDependency() =
    dependOnWeak(C::class) { true }

/** 构造一个 [C] 类型的弱依赖 */
inline fun <reified C : NamedComponent<C>>
    DependencyManager.weakDependency(name: String) =
    dependOnWeak(C::class) { it.name == name }

/** 从一个 [C] 类型的强依赖取值 */
inline fun <reified C : UniqueComponent<C>, T>
    DependencyManager.mustUnique(crossinline block: (C) -> T) =
    must({ true }, block)

/** 从一个 [C] 类型的强依赖取值 */
inline fun <reified C : NamedComponent<C>, T>
    DependencyManager.mustNamed(name: String, crossinline block: (C) -> T) =
    must({ it.name == name }, block)

/** 从一个 [C] 类型的弱依赖取值 */
inline fun <reified C : UniqueComponent<C>, T>
    DependencyManager.maybe(default: T, crossinline block: (C) -> T) =
    maybe({ true }, default, block)

/** 从一个 [C] 类型的弱依赖取值 */
inline fun <reified C : NamedComponent<C>, T>
    DependencyManager.maybe(name: String, default: T, crossinline block: (C) -> T) =
    maybe({ it.name == name }, default, block)

/** 构造一个 [C] 类型的强依赖属性代理 */
inline fun <reified C : UniqueComponent<C>>
    DependencyManager.must() = must<C> { true }

/** 构造一个 [C] 类型的强依赖属性代理 */
inline fun <reified C : NamedComponent<C>>
    DependencyManager.must(name: String) = must<C> { it.name == name }

/** 构造一个 [C] 类型的弱依赖属性代理 */
inline fun <reified C : UniqueComponent<C>>
    DependencyManager.maybe() = maybe<C> { true }

/** 构造一个 [C] 类型的弱依赖属性代理 */
inline fun <reified C : NamedComponent<C>>
    DependencyManager.maybe(name: String) = maybe<C> { it.name == name }
