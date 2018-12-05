package org.mechdancer.dependency

import java.util.concurrent.ConcurrentHashMap

/**
 * 动态域
 *   动态域是域的一种，允许向其中动态地添加新的组件
 *   组件被添加到动态域时，将执行一系列操作，以自动解算依赖关系和建立组件关联
 */
class DynamicScope {
    //组件集
    //  用于查找特定组件类型和判定类型冲突
    //  其中的组件只增加不减少
    private val _components = ConcurrentHashSet<Component>()

    //依赖者列表
    //  用于在在新的依赖项到来时接收通知
    //  其中的组件一旦集齐依赖项就会离开列表，不再接收通知
    private val dependents = mutableListOf<(Component) -> Boolean>()

    //标记组件表
    //  通过保存标记组件的引用来加速访问
    private val _tagComponents = ConcurrentHashMap<String, TagComponent>()

    /** 浏览所有组件 */
    val components = _components.view

    /** 浏览标记的组件 */
    val tagComponents = buildView(_tagComponents)

    /**
     * 将一个新的组件加入到动态域，返回是否成功添加
     * @return 若组件被添加到域，返回`true`
     *         与已有的组件发生冲突时返回`false`
     */
    infix fun setup(component: Component) =
        _components
            .add(component)
            .also {
                // 更新依赖关系
                if (it) synchronized(dependents) {
                    dependents.removeIf { it(component) }

                    if (component is Dependent)
                        component::sync
                            .takeIf { sync -> components.none(sync) }
                            ?.let(dependents::add)
                }

                // 保存标记组件
                if (component is TagComponent)
                    if (null != _tagComponents.putIfAbsent(component.tag, component))
                        throw RuntimeException("try to add the second component with tag ${component.tag}")
            }

    /** 线程安全的哈希集，仿照跳表集，基于映射构造 */
    private class ConcurrentHashSet<T : Any> {
        private object PlaceHolder

        private val core = ConcurrentHashMap<T, PlaceHolder>()
        val view = object : Set<T> by core.keys {}

        fun add(it: T) = core.putIfAbsent(it, PlaceHolder) == null
    }
}
