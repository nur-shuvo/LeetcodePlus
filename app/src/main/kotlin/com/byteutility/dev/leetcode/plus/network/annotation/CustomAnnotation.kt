package com.byteutility.dev.leetcode.plus.network.annotation

import okhttp3.Request
import retrofit2.Invocation

enum class Format {
    JSON, XML
}

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ResponseFormat(val value: Format)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequestFormat(val value: Format)

fun <T : Annotation> Request.getCustomAnnotation(annotationClass: Class<T>): T? =
    this.tag(Invocation::class.java)?.method()?.getAnnotation(annotationClass)
