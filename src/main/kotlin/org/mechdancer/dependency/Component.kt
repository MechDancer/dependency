package org.mechdancer.dependency

/**
 * 组件
 *
 * 通过类型反射、哈希值和判等条件与其他组件区分开
 * 因此组件可以安全快捷地保存到一个哈希集合中
 */
interface Component {
    /** 判断能否与[other]共存 */
    override fun equals(other: Any?): Boolean

    /** 计算组件哈希值 */
    override fun hashCode(): Int
}
