package org.mechdancer.dependency.manager

import org.mechdancer.dependency.*
import org.mechdancer.dependency.TypeSafeDependency.Dependency
import org.mechdancer.dependency.TypeSafeDependency.WeakDependency
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Canonical dependency manager
 */
class DependencyManager : ScopeEventHandler {

    /**
     * The set of dependencies
     */
    private val dependencies = ConcurrentLinkedQueue<TypeSafeDependency<*>>()

    /**
     * Add [dependency] to [dependencies]
     *
     * @return If [dependency] is already in [dependencies]
     */
    private fun <T : Component> add(dependency: TypeSafeDependency<T>) =
        dependencies.add(dependency)

    override fun handle(scopeEvent: ScopeEvent) {
        when (scopeEvent) {
            is ScopeEvent.DependencyArrivedEvent -> dependencies.forEach {
                if (it.fieldOrNull() == null)
                    it.set(scopeEvent.dependency)
            }
            is ScopeEvent.DependencyLeftEvent -> dependencies.filter {
                it.fieldOrNull() == scopeEvent.dependency
            }.forEach {
                when (it) {
                    is Dependency -> throw TeardownStrictDependencyException(
                        scopeEvent.dependency,
                        it
                    )
                    is WeakDependency -> it.clear()
                }
            }
        }
    }

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
        return lazy { dependency.fieldOrNull()?.let(block) ?: default }
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
            override fun getValue(thisRef: Dependent, property: KProperty<*>) = core.fieldOrNull()
        }
}
