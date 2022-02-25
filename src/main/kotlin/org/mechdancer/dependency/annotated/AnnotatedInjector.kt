package org.mechdancer.dependency.annotated

import org.mechdancer.dependency.Component
import org.mechdancer.dependency.DependencyHandler
import org.mechdancer.dependency.NamedComponent
import org.mechdancer.dependency.TypeSafeDependency
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.jvmErasure

/**
 * Annotation style dependency manager
 */
@Suppress("UNCHECKED_CAST")
class AnnotatedInjector<T : Any>(private val dependent: T, type: KClass<T>) : DependencyHandler {

    private val fields = ConcurrentLinkedQueue<Pair<TypeSafeDependency<*>, Field>>()

    init {
        val allFields = type.declaredMemberProperties

        fun KProperty1<*, *>.getName() = javaField!!.annotations.firstNotNullOfOrNull { it as? Name }?.name
        fun Component.toPredicate(name: String?) =
            if (this is NamedComponent<*>)
                name == this.name
            else true

        allFields
            .filter { it.javaField?.isAnnotationPresent(Must::class.java) ?: false }
            .map {
                val name = it.getName()
                TypeSafeDependency.Dependency(it.returnType.jvmErasure as KClass<out Component>) { component ->
                    component.toPredicate(name)
                } to it.javaField!!
            }
            .let {
                fields.addAll(it)
            }

        allFields
            // prioritize non-null fields
            .filter { it.javaField?.isAnnotationPresent(Maybe::class.java) ?: false }
            .sortedBy { it.returnType.isMarkedNullable }
            .map {
                val name = it.getName()
                TypeSafeDependency.WeakDependency(it.returnType.jvmErasure as KClass<out Component>) { component ->
                    component.toPredicate(name)
                } to it.javaField!!
            }
            .let { fields.addAll(it) }

    }

    override fun handle(dependency: Component): Boolean {
        fields.removeIf { (k, v) ->
            k.set(dependency)?.let {
                runCatching {
                    v.isAccessible = true
                    v.set(dependent, dependency)
                }.map { true }.getOrElse { false }
            } ?: false
        }
        return fields.isEmpty()
    }

}
