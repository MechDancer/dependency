package org.mechdancer.dependency.annotated

import org.mechdancer.dependency.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.jvmErasure

/**
 * Annotation style dependency manager
 */
@Suppress("UNCHECKED_CAST")
class AnnotatedInjector<T : Any>(private val dependent: T, type: KClass<T>) : ScopeEventHandler {

    private val dependencies = mutableListOf<TypeSafeDependency<*>>()

    init {
        val allFields = type.declaredMemberProperties

        fun KProperty1<*, *>.getName() =
            javaField!!.annotations.firstNotNullOfOrNull { it as? Name }?.name

        fun Component.toPredicate(name: String?) =
            if (this is INamedComponent<*>)
                name == this.name
            else true

        allFields
            .filter { it.javaField?.isAnnotationPresent(Must::class.java) ?: false }
            .map {
                val name = it.getName()
                val needsUnwrap = it.javaField!!.isAnnotationPresent(Unwrap::class.java)
                TypeSafeDependency.Dependency(
                    if (needsUnwrap) UniqueComponentWrapper::class
                    else it.returnType.jvmErasure as KClass<out Component>
                ) { component ->
                    (if (needsUnwrap)
                        (component is UniqueComponentWrapper<*> && it.returnType.jvmErasure ==
                            component.type)
                    else
                        true) && component.toPredicate(name)
                }.also { dep ->
                    dep.setOnSetListener { component ->
                        it.javaField!!.apply {
                            isAccessible = true
                            if (needsUnwrap && component is UniqueComponentWrapper<*>)
                                set(dependent, component.wrapped)
                            else
                                set(dependent, component)
                        }
                    }
                }
            }
            .let {
                dependencies.addAll(it)
            }

        allFields
            .filter { it.javaField?.isAnnotationPresent(Maybe::class.java) ?: false }
            .sortedBy { it.returnType.isMarkedNullable }
            .map {
                if (!it.returnType.isMarkedNullable)
                    throw RuntimeException("$it was annotated with Maybe, but ut is not a nullable property")
                val name = it.getName()
                val needsUnwrap = it.javaField!!.isAnnotationPresent(Unwrap::class.java)
                TypeSafeDependency.WeakDependency(
                    if (needsUnwrap) UniqueComponentWrapper::class
                    else it.returnType.jvmErasure as KClass<out Component>
                ) { component ->
                    (if (needsUnwrap)
                        component is UniqueComponentWrapper<*> && it.returnType.jvmErasure ==
                            component.type
                    else
                        true) && component.toPredicate(name)
                }.also { dep ->
                    dep.setOnSetListener { component ->
                        it.javaField!!.apply {
                            isAccessible = true
                            if (needsUnwrap && component is UniqueComponentWrapper<*>)
                                set(dependent, component.wrapped)
                            else
                                set(dependent, component)
                        }
                    }
                    dep.setOnClearListener {
                        it.javaField!!.set(dependent, null)
                    }
                }
            }
            .let { dependencies.addAll(it) }

    }


    override fun handle(scopeEvent: ScopeEvent) {
        when (scopeEvent) {
            is ScopeEvent.DependencyArrivedEvent -> {
                dependencies.forEach {
                    if (it.fieldOrNull() == null)
                        it.set(scopeEvent.dependency)
                }
            }
            is ScopeEvent.DependencyLeftEvent -> {
                dependencies.filter {
                    it.fieldOrNull() == scopeEvent.dependency
                }.forEach {
                    when (it) {
                        is TypeSafeDependency.Dependency -> throw TeardownStrictDependencyException(
                            scopeEvent.dependency,
                            it
                        )
                        is TypeSafeDependency.WeakDependency -> it.clear()
                    }
                }
            }
        }
    }

}
