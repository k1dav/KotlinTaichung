package com.k1dave6412.kotlintaichung

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.Url
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

private val GetHttpCLient: HttpClient = HttpClient(Android) {
    engine {
        connectTimeout = 60_000
        socketTimeout = 60_000
    }
}


class API(urlString: String) {
    private val httpClient = GetHttpCLient
    private val requestAddress = Url(urlString)
    private val json = Json(JsonConfiguration.Stable)

    suspend fun getOrders(page: Int): Orders {
        val deferredText = GlobalScope.async {
            try {
                return@async httpClient.get<String> {
                    url(
                        "${requestAddress}orders/?date_field=order_created_at&" +
                                "start_date=2017-02-01&end_date=2017-02-28&page=$page"
                    )
                }
            } catch (e: Exception) {
                throw e
            }
        }
        return json.parse(Orders.serializer(), deferredText.await())
    }

    suspend fun getReceiver(id: String): Receiver {
        val deferredText = GlobalScope.async {
            try {
                return@async httpClient.get<String> {
                    url("${requestAddress}receiver/?id=$id")
                }
            } catch (e: Exception) {
                throw e
            }
        }
        return json.parse(Receiver.serializer(), deferredText.await())
    }
}