package org.mechdancer.dependency.annotated

import org.mechdancer.dependency.Component
import org.mechdancer.dependency.Dependent
import org.mechdancer.dependency.NamedComponent
import org.mechdancer.dependency.TypeSafeDependency
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.jvmErasure

@Suppress("UNCHECKED_CAST")
abstract class AnnotatedDependent : Dependent {

    private val manager = DependencyManagerListenable().also {
        it.onDependencySetup = { dependency ->
            when (dependency) {
                is TypeSafeDependency.Dependency<*>     -> {
                    synchronized(strictPropertiesMap) {
                        strictPropertiesMap[dependency]?.javaField!!
                            .also { f -> f.isAccessible = true }.set(this, dependency.field)
                        strictPropertiesMap.remove(dependency)
                    }
                }
                is TypeSafeDependency.WeakDependency<*> -> {
                    synchronized(weakPropertiesMap) {
                        weakPropertiesMap[dependency]?.javaField!!
                            .also { f -> f.isAccessible = true }.set(this, dependency.field)
                        weakPropertiesMap.remove(dependency)
                    }
                }
            }

        }
    }

    private val strictPropertiesMap = mutableMapOf<TypeSafeDependency<*>, KProperty1<*, *>>()
    private val weakPropertiesMap = mutableMapOf<TypeSafeDependency<*>, KProperty1<*, *>>()

    init {
        val allFields = this::class.declaredMemberProperties

        fun KProperty1<*, *>.getName() = javaField!!.annotations.mapNotNull { it as? Name }.firstOrNull()?.name
        fun Component.toPredicate(name: String?) =
            if (this is NamedComponent<*>)
                name == this.name
            else true

        allFields
            .filter { it.javaField!!.isAnnotationPresent(Must::class.java) }
            .associateBy {
                val name = it.getName()
                manager.dependOnStrict(it.returnType.jvmErasure as KClass<out Component>) { component ->
                    component.toPredicate(name)
                }
            }
            .let { strictPropertiesMap.putAll(it) }

        allFields
            // 优先满足非空
            .sortedBy { it.returnType.isMarkedNullable }
            .filter { it.javaField!!.isAnnotationPresent(Maybe::class.java) }
            // 可存在默认值
            // .apply { forEach { require(it.returnType.isMarkedNullable) { "Weak dependencies must be nullable." } } }
            .associateBy {
                val name = it.getName()
                manager.dependOnWeak(it.returnType.jvmErasure as KClass<out Component>) { component ->
                    component.toPredicate(name)
                }
            }
            .let { weakPropertiesMap.putAll(it) }

    }

    override fun sync(dependency: Component): Boolean = manager.sync(dependency)

}


