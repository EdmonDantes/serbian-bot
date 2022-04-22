package ru.loginov.http

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.forms.formData
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod

class HttpClient(engineFactory: HttpClientEngineFactory<*> = CIO) {

    private val mapper: ObjectMapper = ObjectMapper()

    init {
        mapper.registerModule(
                KotlinModule.Builder()
                        .withReflectionCacheSize(1024)
                        .configure(KotlinFeature.NullToEmptyCollection, false)
                        .configure(KotlinFeature.NullToEmptyMap, false)
                        .configure(KotlinFeature.NullIsSameAsDefault, false)
                        .configure(KotlinFeature.SingletonSupport, false)
                        .configure(KotlinFeature.StrictNullChecks, true)
                        .build()
        )
    }

    private val client: HttpClient = HttpClient(engineFactory) {
        expectSuccess = false
    }

    suspend fun request(
            method: HttpMethod,
            url: String,
            body: ByteArray? = null,
            queryParameters: Map<String, String> = emptyMap(),
            contentType: ContentType? = null
    ): HttpResponse = client.request {
        this.method = method
        this.url(url)

        if (queryParameters.isNotEmpty()) {
            formData {
                queryParameters.forEach {
                    parameter(it.key, it.value)
                }
            }
        }

        if (body != null) {
            this.body = CustomOutgoingContext(contentType, body)
        }
    }

    suspend fun <T> requestWithJsonResponse(
            method: HttpMethod,
            url: String,
            responseTypeReference: TypeReference<T>,
            body: ByteArray? = null,
            queryParameters: Map<String, String> = emptyMap(),
            contentType: ContentType? = null
    ): Pair<T?, HttpResponse> {
        val response = request(method, url, body, queryParameters, ContentType.Application.Json)
        return mapper.readValue(response.readBytes(), responseTypeReference) to response
    }

    suspend fun requestWithJsonRequest(
            method: HttpMethod,
            url: String,
            body: Any? = null,
            queryParameters: Map<String, String> = emptyMap(),
    ): HttpResponse = request(
            method,
            url,
            body?.let { mapper.writeValueAsBytes(it) },
            queryParameters,
            ContentType.Application.Json
    )

    suspend fun <T> requestJson(
            method: HttpMethod,
            url: String,
            responseTypeReference: TypeReference<T>,
            body: Any? = null,
            queryParameters: Map<String, String> = emptyMap(),
    ): Pair<T?, HttpResponse> =
            requestWithJsonResponse(
                    method,
                    url,
                    responseTypeReference,
                    body?.let { mapper.writeValueAsBytes(it) },
                    queryParameters,
                    ContentType.Application.Json
            )
}

suspend inline fun <reified T> ru.loginov.http.HttpClient.requestWithJsonResponse(
        method: HttpMethod,
        url: String,
        body: ByteArray? = null,
        queryParameters: Map<String, String> = emptyMap(),
        contentType: ContentType? = null
): Pair<T?, HttpResponse> = this.requestWithJsonResponse(
        method,
        url,
        object : TypeReference<T>() {},
        body,
        queryParameters,
        contentType
)

suspend inline fun <reified T> ru.loginov.http.HttpClient.requestJson(
        method: HttpMethod,
        url: String,
        body: ByteArray? = null,
        queryParameters: Map<String, String> = emptyMap(),
): Pair<T?, HttpResponse> = this.requestJson(
        method,
        url,
        object : TypeReference<T>() {},
        body,
        queryParameters,
)
