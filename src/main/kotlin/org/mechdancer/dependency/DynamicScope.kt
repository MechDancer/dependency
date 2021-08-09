package org.mechdancer.dependency

import java.util.concurrent.ConcurrentHashMap

/**
 * 动态域
 *
 * 动态域允许向其中动态地添加新的组件
 * 组件被添加到动态域时，将执行一系列操作，以自动解算依赖关系和建立组件关联
 */
open class DynamicScope {
    //组件集
    //  用于查找特定组件类型和判定类型冲突
    //  其中的组件只增加不减少
    private val _components = ConcurrentHashSet<Component>()

    //依赖者列表
    //  用于在在新依赖项到来时接收通知
    //  其中的组件一旦集齐依赖项就会离开列表，不再接收通知
    private val dependents = mutableListOf<(Component) -> Boolean>()

    /** 浏览所有组件 */
    @Suppress("unused")
    val components = _components.view

    /**
     * 将一个新的组件加入到动态域，返回是否成功添加
     *
     * @return 若组件被添加到域，返回 `true`
     *         与已有的组件发生冲突时返回 `false`
     */
    open infix fun setup(component: Component) =
        _components
            .add(component)
            .also {
                // 更新依赖关系
                if (it) synchronized(dependents) {
                    dependents.removeIf { it(component) }

                    if (component is Dependent)
                        component::sync
                            .takeIf { sync -> _components.none(sync) }
                            ?.let(dependents::add)
                }
            }

    /** 线程安全的哈希集，仿照跳表集，基于映射构造 */
    private class ConcurrentHashSet<T : Any> : MutableSet<T> {

        private object PlaceHolder

        private val core = ConcurrentHashMap<T, PlaceHolder>()
        val view = object : Set<T> by core.keys {}
        override val size get() = core.size

        override fun iterator(): MutableIterator<T> = core.keys.iterator()

        override fun add(element: T) = core.putIfAbsent(element, PlaceHolder) == null
        override fun addAll(elements: Collection<T>) = elements.all(::add)

        override fun remove(element: T) = core.remove(element) != null
        override fun removeAll(elements: Collection<T>) = elements.all(::remove)
        override fun clear() = core.clear()

        override fun retainAll(elements: Collection<T>) = removeAll(core.keys.filter { it !in elements })

        override operator fun contains(element: T) = core.containsKey(element)
        override fun containsAll(elements: Collection<T>) = elements.all(::contains)

        override fun isEmpty() = core.isEmpty()
    }
}
