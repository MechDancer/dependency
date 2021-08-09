//package org.mechdancer.dependency.annotated
//
//import org.mechdancer.dependency.Component
//import org.mechdancer.dependency.Dependent
//import org.mechdancer.dependency.NamedComponent
//import org.mechdancer.dependency.TypeSafeDependency
//import kotlin.reflect.KClass
//import kotlin.reflect.KProperty1
//
//@Suppress("UNCHECKED_CAST")
//abstract class AnnotatedDependent : Dependent {
//
//    private val strictPropertiesMap = mutableMapOf<TypeSafeDependency<*>, KProperty1<*, *>>()
//    private val weakPropertiesMap = mutableMapOf<TypeSafeDependency<*>, KProperty1<*, *>>()
//
//    init {
//        val allFields = this::class.declaredMemberProperties
//
//        fun KProperty1<*, *>.getName() = javaField!!.annotations.mapNotNull { it as? Name }.firstOrNull()?.name
//        fun Component.toPredicate(name: String?) =
//            if (this is NamedComponent<*>)
//                name == this.name
//            else true
//
//        allFields
//            .filter { it.javaField!!.isAnnotationPresent(Must::class.java) }
//            .associateBy {
//                val name = it.getName()
//                TypeSafeDependency.Dependency(it.returnType.jvmErasure as KClass<out Component>) { component ->
//                    component.toPredicate(name)
//                }
//            }
//            .let { strictPropertiesMap.putAll(it) }
//
//        allFields
//            // 优先满足非空
//            .filter { it.javaField!!.isAnnotationPresent(Maybe::class.java) }
//            .sortedBy { it.returnType.isMarkedNullable }
//            .associateBy {
//                val name = it.getName()
//                TypeSafeDependency.WeakDependency(it.returnType.jvmErasure as KClass<out Component>) { component ->
//                    component.toPredicate(name)
//                }
//            }
//            .let { weakPropertiesMap.putAll(it) }
//
//    }
//
//    override fun sync(dependency: Component): Boolean {
//        (strictPropertiesMap.keys.find { it.set(dependency) != null }
//            ?: weakPropertiesMap.keys.find { it.set(dependency) != null })?.let {
//            when (it) {
//                is TypeSafeDependency.Dependency<*>     -> {
//                    synchronized(strictPropertiesMap) {
//                        strictPropertiesMap[it]?.javaField!!
//                            .also { f -> f.isAccessible = true }.set(this, it.field)
//                        strictPropertiesMap.remove(it)
//                    }
//                }
//                is TypeSafeDependency.WeakDependency<*> -> {
//                    synchronized(weakPropertiesMap) {
//                        weakPropertiesMap[it]?.javaField!!
//                            .also { f -> f.isAccessible = true }.set(this, it.field)
//                        weakPropertiesMap.remove(it)
//                    }
//                }
//            }
//        }
//        return weakPropertiesMap.isEmpty() && strictPropertiesMap.isEmpty()
//    }
//
//}
