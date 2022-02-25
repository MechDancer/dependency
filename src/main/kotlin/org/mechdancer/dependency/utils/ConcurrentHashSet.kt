package org.mechdancer.dependency.utils

import java.util.concurrent.ConcurrentHashMap

/**
 * A thread-safe implementation of [MutableSet]
 */
class ConcurrentHashSet<T : Any> : MutableSet<T> {

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