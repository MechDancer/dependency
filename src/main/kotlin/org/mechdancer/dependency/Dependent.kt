package org.mechdancer.dependency

/**
 * 依赖者
 *
 * 依赖者的内容不完整，需要获得依赖项的支持
 * 因此当一个新的依赖项到达，依赖者会得到通知，直到所有依赖项都集齐为止
 */
interface Dependent : Component {
    /** 向依赖者展示新的依赖项[dependency]，并返回是否已获得全部依赖项 */
    fun sync(dependency: Component): Boolean
}
