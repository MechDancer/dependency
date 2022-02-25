package org.mechdancer.dependency.annotated

import org.mechdancer.dependency.Dependent
import kotlin.properties.ReadOnlyProperty

/**
 * Create a property delegate of [AnnotatedInjector]
 */
inline fun <reified T : Dependent> annotatedInjector() =
    ReadOnlyProperty<T, AnnotatedInjector<T>> { thisRef, _ ->
        AnnotatedInjector(thisRef, T::class)
    }

