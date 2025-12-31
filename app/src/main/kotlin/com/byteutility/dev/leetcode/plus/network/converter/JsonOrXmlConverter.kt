package com.byteutility.dev.leetcode.plus.network.converter

import com.byteutility.dev.leetcode.plus.network.annotation.Format
import com.byteutility.dev.leetcode.plus.network.annotation.RequestFormat
import com.byteutility.dev.leetcode.plus.network.annotation.ResponseFormat
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import javax.inject.Inject

class JsonOrXmlConverter @Inject constructor() : Converter.Factory() {

    private val jsonConverterFactory: Converter.Factory = GsonConverterFactory.create()
    private val xmlConverterFactory: Converter.Factory = TikXmlConverterFactory.create(
        TikXml.Builder()
            .writeDefaultXmlDeclaration(true) // or false
            .build()
    )

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        for (annotation in annotations) {
            if (annotation is ResponseFormat) {
                return when (annotation.value) {
                    Format.JSON -> {
                        jsonConverterFactory.responseBodyConverter(type, annotations, retrofit)
                    }

                    Format.XML -> {
                        xmlConverterFactory.responseBodyConverter(type, annotations, retrofit)
                    }
                }
            }
        }
        return null
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        for (annotation in methodAnnotations) {
            if (annotation is RequestFormat) {
                return when (annotation.value) {
                    Format.JSON -> {
                        jsonConverterFactory.requestBodyConverter(
                            type,
                            parameterAnnotations,
                            methodAnnotations,
                            retrofit
                        )
                    }

                    Format.XML -> {
                        xmlConverterFactory.requestBodyConverter(
                            type,
                            parameterAnnotations,
                            methodAnnotations,
                            retrofit
                        )
                    }
                }
            }
        }
        return null
    }
}
