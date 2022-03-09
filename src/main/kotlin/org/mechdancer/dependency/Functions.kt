package org.mechdancer.dependency

import kotlin.reflect.KClass
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure

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
 * Operator of [DynamicScope.teardown]
 */
operator fun DynamicScope.minusAssign(component: Component) {
    teardown(component)
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
inline fun scope(block: DynamicScope.() -> Unit) =
    DynamicScope().apply(block)

/**
 * Find generic type in super class hierarchies which has is subtype of [upper] recursively
 * For example, we have the following two classes:
 * ```kotlin
 *  abstract class Foo<T : Foo<T>>
 *  class Bar : Foo<Bar>()
 *```
 * And we can find type parameter `T` instantiated with `Bar`:
 * ```kotlin
 *  val bar = Bar()
 *  // This returns KClass of Bar
 *  bar.javaClass.kotlin.findSuperGenericTypeRecursively(Foo::class))
 * ```
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any> KClass<*>.findSuperGenericTypeRecursively(upper: KClass<T>): KClass<out T> =
    supertypes
        .find { it.isSubtypeOf(upper.starProjectedType) }
        ?.arguments
        ?.firstNotNullOfOrNull {
            it.type?.takeIf { type -> type.isSubtypeOf(upper.starProjectedType) }
        }
        ?.jvmErasure as? KClass<out T>
        ?: supertypes.firstNotNullOfOrNull {
            (it as? KClass<*>)?.findSuperGenericTypeRecursively(upper)
        }
        ?: throw RuntimeException("Unable to find generic type.")
