package org.mechdancer.dependency.manager

import org.mechdancer.dependency.Component
import org.mechdancer.dependency.DependencyHandler
import org.mechdancer.dependency.Dependent
import org.mechdancer.dependency.TypeSafeDependency
import org.mechdancer.dependency.TypeSafeDependency.Dependency
import org.mechdancer.dependency.TypeSafeDependency.WeakDependency
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Canonical dependency manager
 */
class DependencyManager : DependencyHandler {

    /**
     * The set of unsatisfied dependencies
     */
    private val dependencies = ConcurrentLinkedQueue<TypeSafeDependency<*>>()

    /**
     * Add [dependency] to [dependencies]
     *
     * @return If [dependency] is already in [dependencies]
     */
    private fun <T : Component> add(dependency: TypeSafeDependency<T>) =
        dependencies.add(dependency)

    /**
     * Fill [dependency] to [dependencies], and remove satisfied elements from [dependencies]
     */
    override fun handle(dependency: Component) =
        dependencies.removeIf { it.set(dependency) != null } && dependencies.isEmpty()

    /**
     * Declare a strict dependency with type [C]
     *
     * @return the dependency declaration
     */
    fun <C : Component> dependOnStrict(type: KClass<C>, predicate: (C) -> Boolean) =
        Dependency(type, predicate).also { add(it) }

    /**
     * Declare a weak dependency with type [C]
     *
     * @return the dependency declaration
     */
    fun <C : Component> dependOnWeak(type: KClass<C>, predicate: (C) -> Boolean) =
        WeakDependency(type, predicate).also { add(it) }

    /**
     * Declare a strict dependency with type [C]
     *
     * @return the dependency declaration
     */
    inline fun <reified C : Component> dependency(noinline predicate: (C) -> Boolean) =
        dependOnStrict(C::class, predicate)

    /**
     * Declare a weak dependency with type [C]
     *
     * @return the dependency declaration
     */
    inline fun <reified C : Component> weakDependency(noinline predicate: (C) -> Boolean) =
        dependOnWeak(C::class, predicate)

    /**
     * Declare a strict dependency with type [C],
     * creating a lazy initialized delegate that obtains its value via [block]
     *
     * @return a property delegate using [lazy]
     */
    inline fun <reified C : Component, T> must(
        noinline predicate: (C) -> Boolean,
        crossinline block: (C) -> T
    ): Lazy<T> {
        val dependency = dependency(predicate)
        return lazy { dependency.field.let(block) }
    }

    /**
     * Declare a weak dependency with type [C],
     * creating a lazy initialized delegate that obtains its value via [block]
     *
     * @return a property delegate using [lazy]
     */
    inline fun <reified C : Component, T> maybe(
        noinline predicate: (C) -> Boolean,
        default: T,
        crossinline block: (C) -> T
    ): Lazy<T> {
        val dependency = weakDependency(predicate)
        return lazy { dependency.field?.let(block) ?: default }
    }

    /**
     * Declare a strict dependency with type [C],
     * creating a delegate that obtains its value
     *
     * @return a property delegate
     */
    inline fun <reified C : Component> must(noinline predicate: (C) -> Boolean) =
        object : ReadOnlyProperty<Dependent, C> {
            private val core = dependency(predicate)
            override fun getValue(thisRef: Dependent, property: KProperty<*>) = core.field
        }

    /**
     * Declare a weak dependency with type [C],
     * creating a delegate that obtains its value
     *
     * @return a property delegate
     */
    inline fun <reified C : Component> maybe(noinline predicate: (C) -> Boolean) =
        object : ReadOnlyProperty<Dependent, C?> {
            private val core = weakDependency(predicate)
            override fun getValue(thisRef: Dependent, property: KProperty<*>) = core.field
        }
}
