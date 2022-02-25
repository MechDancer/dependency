package org.mechdancer.dependency.annotated

/**
 * Inject a strict dependency
 */
@Target(AnnotationTarget.FIELD)
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class Must

/**
 * Inject a weak dependency
 */
@Target(AnnotationTarget.FIELD)
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class Maybe

/**
 * Specify that the name of the dependency is [name],
 * instead of field name
 */
@Target(AnnotationTarget.FIELD)
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class Name(val name: String)