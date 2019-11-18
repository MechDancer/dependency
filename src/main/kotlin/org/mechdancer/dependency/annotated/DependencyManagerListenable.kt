package org.mechdancer.dependency.annotated

import org.mechdancer.dependency.Component
import org.mechdancer.dependency.DependencyManager
import org.mechdancer.dependency.TypeSafeDependency

class DependencyManagerListenable : DependencyManager() {

    var onDependencySetup = { _: TypeSafeDependency<*> -> }

    override fun sync(dependency: Component): Boolean {
        synchronized(dependencies) {
            var result = false
            dependencies.find { it.set(dependency) != null }?.also {
                result = dependencies.remove(it)
                onDependencySetup(it)
            }
            return result && dependencies.isEmpty()
        }
    }
}