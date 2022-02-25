package org.mechdancer.dependency

/**
 * [TreeComponent] is a special type of [Component] which has its children and parent
 *
 * Use [setupRecursively] to add the [TreeComponent] to the scope.
 */
abstract class TreeComponent(
    val name: String,
    val parent: TreeComponent? = null
) : Component {

    private val _children = mutableListOf<TreeComponent>()

    /**
     * Attach another tree component into this tree component
     */
    protected fun <T : TreeComponent> T.attach() =
        this@attach.also(this@TreeComponent._children::plusAssign)

    private val ancestors: List<TreeComponent> = parent?.ancestors?.plus(parent) ?: listOf()
    val children = object : List<TreeComponent> by _children {}


    final override fun equals(other: Any?): Boolean {
        if (other !is TreeComponent) return false
        return other.parent == parent && other.name == name
    }

    final override fun hashCode() =
        (parent.hashCode() shl 31) + name.hashCode()

}