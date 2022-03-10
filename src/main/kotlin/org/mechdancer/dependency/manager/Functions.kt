package org.mechdancer.dependency.manager

import org.mechdancer.dependency.INamedComponent
import org.mechdancer.dependency.IUniqueComponent
import org.mechdancer.dependency.UniqueComponentWrapper
import kotlin.reflect.KClass

/**
 * Create a interface delegate of [ManagedHandler]
 */
fun managedHandler() = object : ManagedHandler {
    override val manager: DependencyManager = DependencyManager()
}

/**
 * Declare a strict [IUniqueComponent] dependency with type [C],
 *
 * @return the dependency declaration
 */
fun <C : IUniqueComponent<C>>
    DependencyManager.dependOnStrict(type: KClass<C>) =
    dependOnStrict(type) { true }

/**
 * Declare a strict [INamedComponent] dependency with [name] and type [C],
 *
 * @return the dependency declaration
 */
fun <C : INamedComponent<C>>
    DependencyManager.dependOnStrict(type: KClass<C>, name: String) =
    dependOnStrict(type) { it.name == name }

/**
 * Declare a weak [IUniqueComponent] dependency with type [C],
 *
 * @return the dependency declaration
 */
fun <C : IUniqueComponent<C>>
    DependencyManager.dependOnWeak(type: KClass<C>) =
    dependOnWeak(type) { true }

/**
 * Declare a weak [INamedComponent] dependency with [name] and type [C],
 *
 * @return the dependency declaration
 */
fun <C : INamedComponent<C>>
    DependencyManager.dependOnWeak(type: KClass<C>, name: String) =
    dependOnWeak(type) { it.name == name }

/**
 * Declare a strict [IUniqueComponent] dependency with type [C],
 *
 * @return the dependency declaration
 */
inline fun <reified C : IUniqueComponent<C>>
    DependencyManager.dependency() =
    dependOnStrict(C::class) { true }

/**
 * Declare a strict [INamedComponent] dependency with [name] and type [C],
 *
 * @return the dependency declaration
 */
inline fun <reified C : INamedComponent<C>>
    DependencyManager.dependency(name: String) =
    dependOnStrict(C::class) { it.name == name }

/**
 * Declare a weak [IUniqueComponent] dependency with type [C],
 *
 * @return the dependency declaration
 */
inline fun <reified C : IUniqueComponent<C>>
    DependencyManager.weakDependency() =
    dependOnWeak(C::class) { true }

/**
 * Declare a weak [INamedComponent] dependency with [name] and type [C],
 *
 * @return the dependency declaration
 */
inline fun <reified C : INamedComponent<C>>
    DependencyManager.weakDependency(name: String) =
    dependOnWeak(C::class) { it.name == name }

/**
 * Declare a strict [IUniqueComponent] dependency with type [C],
 * creating a lazy initialized delegate that obtains its value via [block]
 *
 * @return a property delegate using [lazy]
 */
inline fun <reified C : IUniqueComponent<C>, T>
    DependencyManager.mustUnique(crossinline block: (C) -> T) =
    must({ true }, block)

/**
 * Declare a strict [INamedComponent] dependency with [name] and type [C],
 * creating a lazy initialized delegate that obtains its value via [block]
 *
 * @return a property delegate using [lazy]
 */
inline fun <reified C : INamedComponent<C>, T>
    DependencyManager.mustNamed(name: String, crossinline block: (C) -> T) =
    must({ it.name == name }, block)

/**
 * Declare a weak [IUniqueComponent] dependency with type [C],
 * creating a lazy initialized delegate that obtains its value via [block]
 *
 * @return a property delegate using [lazy]
 */
inline fun <reified C : IUniqueComponent<C>, T>
    DependencyManager.maybe(default: T, crossinline block: (C) -> T) =
    maybe({ true }, default, block)

/**
 * Declare a weak [INamedComponent] dependency with [name] and type [C],
 * creating a lazy initialized delegate that obtains its value via [block]
 *
 * @return a property delegate using [lazy]
 */
inline fun <reified C : INamedComponent<C>, T>
    DependencyManager.maybe(name: String, default: T, crossinline block: (C) -> T) =
    maybe({ it.name == name }, default, block)

/**
 * Declare a strict [IUniqueComponent] dependency with type [C],
 * creating a delegate that obtains its value
 *
 * @return a property delegate
 */
inline fun <reified C : IUniqueComponent<C>>
    DependencyManager.must() = must<C> { true }

/**
 * Declare a strict [INamedComponent] dependency with [name] and type [C],
 * creating a delegate that obtains its value
 *
 * @return a property delegate
 */
inline fun <reified C : INamedComponent<C>>
    DependencyManager.must(name: String) = must<C> { it.name == name }

/**
 * Declare a weak [IUniqueComponent] dependency with type [C],
 * creating a delegate that obtains its value
 *
 * @return a property delegate
 */
inline fun <reified C : IUniqueComponent<C>>
    DependencyManager.maybe() = maybe<C> { true }

/**
 * Declare a weak [INamedComponent] dependency with [name] and type [C],
 * creating a delegate that obtains its value
 *
 * @return a property delegate
 */
inline fun <reified C : INamedComponent<C>>
    DependencyManager.maybe(name: String) = maybe<C> { it.name == name }

/**
 * Declare a strict [UniqueComponentWrapper] dependency with type [C] that wrappers type [T],
 * creating a delegate that obtains its value
 *
 * @return a property delegate
 */
inline fun <reified C : UniqueComponentWrapper<T>, reified T>
    DependencyManager.mustWrapped() = must<C, T>({ it.type == T::class }) { it.wrapped }

/**
 * Declare a weak [UniqueComponentWrapper] dependency with type [C] that wrappers type [T],
 * creating a delegate that obtains its value
 *
 * @return a property delegate
 */
inline fun <reified C : UniqueComponentWrapper<T>, reified T>
    DependencyManager.maybeWrapped() = must<C, T>({ it.type == T::class }) { it.wrapped }

/**
 * Wrap [this] to a unique component
 */
inline fun <reified T : Any> T.wrapToUniqueComponent() = UniqueComponentWrapper(this)