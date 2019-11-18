package org.mechdancer.dependency.annotated

@Target(AnnotationTarget.FIELD)
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class Must

@Target(AnnotationTarget.FIELD)
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class Maybe

@Target(AnnotationTarget.FIELD)
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class Name(val name: String)