package org.mechdancer.dependency

import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

/**
 * Find a list dependencies with type [C]
 */
inline fun <reified C : Component> Iterable<Component>.get(): List<C> =
    mapNotNull { it as? C }

/**
 * Find a dependency with type [C]
 */
inline fun <reified C : Component> Iterable<Component>.maybe(): C? =
    get<C>().singleOrNull()

/**
 * Find a dependency with type [C]
 *
 * @throws ComponentNotExistException
 */
inline fun <reified C : Component> Iterable<Component>.must(): C =
    maybe() ?: throw ComponentNotExistException(C::class)

/**
 * Operator of [setup]
 */
operator fun DynamicScope.plusAssign(component: Component) {
    setup(component)
}

/**
 * Add a [component] to the scope, executing [block] if success
 */
inline fun <T : Component> DynamicScope.setup(component: T, block: T.() -> Unit) {
    if (setup(component)) block(component)
}


/**
 * Add a tree component [root] to scope recursively
 */
fun DynamicScope.setupRecursively(root: TreeComponent) {
    setup(root)
    root.children.forEach(this::setupRecursively)
}

/**
 * Create a [DynamicScope]
 */
fun scope(block: DynamicScope.() -> Unit) =
    DynamicScope().apply(block)

/**
 * Find first generic type that has an upper bound [upper]
 * @receiver Type that has a generic type to be found
 */
fun KClass<*>.firstGenericType(upper: KClass<*>) =
    (java.genericSuperclass as? ParameterizedType)
        ?.actualTypeArguments
        ?.asSequence()
        ?.mapNotNull { it as? Class<*> }
        ?.find { t -> upper.java.isAssignableFrom(t) }
        ?.kotlin
        ?: throw RuntimeException("Unable to find component type.")
