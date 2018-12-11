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
operator fun DynamicScope.plusAssign(Component: Component) {
    setup(Component)
}

/** 构造动态域 */
fun scope(block: DynamicScope.() -> Unit) =
    DynamicScope().apply(block)

/** 构造映射浏览器 */
internal fun <T, U> buildView(map: Map<T, U>) =
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
