package com.k1dave6412.kotlintaichung

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.Url
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

private val GetHttpCLient: HttpClient = HttpClient(Android) {
    engine {
        connectTimeout = 120_000
        socketTimeout = 120_000
    }
}


class API(urlString: String) {
    private val httpClient = GetHttpCLient
    private val requestAddress = Url(urlString)
    private val json = Json(JsonConfiguration.Stable)

    suspend fun getOrders(): Orders {
        val deferredText = GlobalScope.async {
            try {
                return@async httpClient.get<String> {
                    url("${requestAddress}orders")
                }
            } catch (e: Exception) {
                throw e
            }
        }
        return json.parse(Orders.serializer(), deferredText.await())
    }

    suspend fun getOrder(id: String): Order {
        val deferredText = GlobalScope.async {
            try {
                return@async httpClient.get<String> {
                    url("${requestAddress}order/?id=$id")
                }
            } catch (e: Exception) {
                throw e
            }
        }
        return json.parse(Order.serializer(), deferredText.await())
    }

    suspend fun getReceiver(id: String): FromTask {

        val deferredText = GlobalScope.async {
            try {
                return@async httpClient.post<String> {
                    url("${requestAddress}api/v1/task/")
                    body = json.stringify(Task.serializer(), task)
                }
            } catch (e: Exception) {
                throw e
            }
        }
        return json.parse(FromTask.serializer(), deferredText.await())
    }


}