package org.mechdancer.dependency

/** 计算类型的哈希值 */
inline fun <reified C : Component> hashOf() =
    C::class.java.hashCode()

/** 找到一种 [C] 类型的依赖 */
inline fun <reified C : Component> Set<Component>.get(): List<C> =
    mapNotNull { it as? C }

/** 找到一种 [C] 类型的依赖 */
inline fun <reified C : Component> Set<Component>.maybe(): C? =
    get<C>().singleOrNull()

/** 找到一种 [C] 类型的依赖 */
inline fun <reified C : Component> Set<Component>.must(): C =
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
