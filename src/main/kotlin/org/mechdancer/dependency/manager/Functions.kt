package org.mechdancer.dependency.manager

import org.mechdancer.dependency.NamedComponent
import org.mechdancer.dependency.UniqueComponent
import kotlin.reflect.KClass

/**
 * Create a interface delegate of [ManagedHandler]
 */
fun managedHandler() = object : ManagedHandler {
    override val manager: DependencyManager = DependencyManager()
}

/**
 * Declare a strict [UniqueComponent] dependency with type [C],
 *
 * @return the dependency declaration
 */
fun <C : UniqueComponent<C>>
    DependencyManager.dependOnStrict(type: KClass<C>) =
    dependOnStrict(type) { true }

/**
 * Declare a strict [NamedComponent] dependency with [name] and type [C],
 *
 * @return the dependency declaration
 */
fun <C : NamedComponent<C>>
    DependencyManager.dependOnStrict(type: KClass<C>, name: String) =
    dependOnStrict(type) { it.name == name }

/**
 * Declare a weak [UniqueComponent] dependency with type [C],
 *
 * @return the dependency declaration
 */
fun <C : UniqueComponent<C>>
    DependencyManager.dependOnWeak(type: KClass<C>) =
    dependOnWeak(type) { true }

/**
 * Declare a weak [NamedComponent] dependency with [name] and type [C],
 *
 * @return the dependency declaration
 */
fun <C : NamedComponent<C>>
    DependencyManager.dependOnWeak(type: KClass<C>, name: String) =
    dependOnWeak(type) { it.name == name }

/**
 * Declare a strict [UniqueComponent] dependency with type [C],
 *
 * @return the dependency declaration
 */
inline fun <reified C : UniqueComponent<C>>
    DependencyManager.dependency() =
    dependOnStrict(C::class) { true }

/**
 * Declare a strict [NamedComponent] dependency with [name] and type [C],
 *
 * @return the dependency declaration
 */
inline fun <reified C : NamedComponent<C>>
    DependencyManager.dependency(name: String) =
    dependOnStrict(C::class) { it.name == name }

/**
 * Declare a weak [UniqueComponent] dependency with type [C],
 *
 * @return the dependency declaration
 */
inline fun <reified C : UniqueComponent<C>>
    DependencyManager.weakDependency() =
    dependOnWeak(C::class) { true }

/**
 * Declare a weak [NamedComponent] dependency with [name] and type [C],
 *
 * @return the dependency declaration
 */
inline fun <reified C : NamedComponent<C>>
    DependencyManager.weakDependency(name: String) =
    dependOnWeak(C::class) { it.name == name }

/**
 * Declare a strict [UniqueComponent] dependency with type [C],
 * creating a lazy initialized delegate that obtains its value via [block]
 *
 * @return a property delegate using [lazy]
 */
inline fun <reified C : UniqueComponent<C>, T>
    DependencyManager.mustUnique(crossinline block: (C) -> T) =
    must({ true }, block)

/**
 * Declare a strict [NamedComponent] dependency with [name] and type [C],
 * creating a lazy initialized delegate that obtains its value via [block]
 *
 * @return a property delegate using [lazy]
 */
inline fun <reified C : NamedComponent<C>, T>
    DependencyManager.mustNamed(name: String, crossinline block: (C) -> T) =
    must({ it.name == name }, block)

/**
 * Declare a weak [UniqueComponent] dependency with type [C],
 * creating a lazy initialized delegate that obtains its value via [block]
 *
 * @return a property delegate using [lazy]
 */
inline fun <reified C : UniqueComponent<C>, T>
    DependencyManager.maybe(default: T, crossinline block: (C) -> T) =
    maybe({ true }, default, block)

/**
 * Declare a weak [NamedComponent] dependency with [name] and type [C],
 * creating a lazy initialized delegate that obtains its value via [block]
 *
 * @return a property delegate using [lazy]
 */
inline fun <reified C : NamedComponent<C>, T>
    DependencyManager.maybe(name: String, default: T, crossinline block: (C) -> T) =
    maybe({ it.name == name }, default, block)

/**
 * Declare a strict [UniqueComponent] dependency with type [C],
 * creating a delegate that obtains its value
 *
 * @return a property delegate
 */
inline fun <reified C : UniqueComponent<C>>
    DependencyManager.must() = must<C> { true }

/**
 * Declare a strict [NamedComponent] dependency with [name] and type [C],
 * creating a delegate that obtains its value
 *
 * @return a property delegate
 */
inline fun <reified C : NamedComponent<C>>
    DependencyManager.must(name: String) = must<C> { it.name == name }

/**
 * Declare a weak [UniqueComponent] dependency with type [C],
 * creating a delegate that obtains its value
 *
 * @return a property delegate
 */
inline fun <reified C : UniqueComponent<C>>
    DependencyManager.maybe() = maybe<C> { true }

/**
 * Declare a weak [NamedComponent] dependency with [name] and type [C],
 * creating a delegate that obtains its value
 *
 * @return a property delegate
 */
inline fun <reified C : NamedComponent<C>>
    DependencyManager.maybe(name: String) = maybe<C> { it.name == name }
