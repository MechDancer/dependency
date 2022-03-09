package org.mechdancer.dependency

import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

/**
 * Dependency declaration
 *
 * It holds the type [T] and the reference of the [Component].
 * The implementation is thread-safe.
 */
sealed class TypeSafeDependency<T : Component>(
    val type: KClass<T>,
    private val predicate: (T) -> Boolean
) {
    private var onSetListener: ((T) -> Unit)? = null

    protected val fieldRef = AtomicReference<T?>(null)

    /**
     * Try to get the value
     */
    fun fieldOrNull() = fieldRef.get()

    /**
     * Try to set [value]
     *
     * Fail if unable to cast [value] to desired type or the predication fails
     */
    fun set(value: Component): T? =
        fieldRef.updateAndGet {
            type.safeCast(value)?.takeIf(predicate) ?: it
        }?.also { onSetListener?.invoke(it) }

    /**
     * Called when [fieldRef] was successfully set
     */
    fun setOnSetListener(listener: ((T) -> Unit)? = null) {
        onSetListener = listener
    }

    /**
     * Weak dependency with type [T]
     */
    class WeakDependency<T : Component>(type: KClass<T>, predicate: (T) -> Boolean) :
        TypeSafeDependency<T>(type, predicate) {
        private var onClearListener: (() -> Unit)? = null

        /**
         * Called when [fieldRef] was successfully cleared
         */
        fun setOnClearListener(listener: (() -> Unit)? = null) {
            onClearListener = listener
        }

        /**
         * Clear [fieldRef]
         */
        fun clear() {
            fieldRef.set(null)
            onClearListener?.invoke()
        }
    }

    /**
     * Strict dependency with type [T]
     */
    class Dependency<T : Component>(type: KClass<T>, predicate: (T) -> Boolean) :
        TypeSafeDependency<T>(type, predicate) {
        /**
         * Try to get the value
         * @throws ComponentNotExistException if unable to get
         */
        val field: T get() = super.fieldOrNull() ?: throw ComponentNotExistException(type)
    }

    override fun equals(other: Any?) =
        this === other || (other as? TypeSafeDependency<*>)?.type == type

    override fun hashCode() = type.hashCode()
}
