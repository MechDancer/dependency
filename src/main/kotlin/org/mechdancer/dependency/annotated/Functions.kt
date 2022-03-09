package org.mechdancer.dependency.annotated

import org.mechdancer.dependency.Dependent
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Create a property delegate of [AnnotatedInjector]
 */
inline fun <reified T : Dependent> annotatedInjector() =
    object : ReadOnlyProperty<T, AnnotatedInjector<T>> {
        @Volatile
        private var injector: AnnotatedInjector<T>? = null

        override fun getValue(thisRef: T, property: KProperty<*>): AnnotatedInjector<T> {
            if (injector != null)
                return injector!!
            synchronized(this) {
                injector = AnnotatedInjector(thisRef, T::class)
                return injector!!
            }
        }
    }

