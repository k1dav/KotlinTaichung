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


class API() {
    private val httpClient = GetHttpCLient
    private val requestAddress = Url("http://192.168.66.120:5000/")

    private val json = Json(JsonConfiguration.Stable)

    suspend fun getTasks(): FromTask {

        val deferredText = GlobalScope.async {
            try {
                return@async httpClient.get<String> {
                    url("${requestAddress}api/v1/tasks")
                }
            } catch (e: Exception) {
                throw e
            }
        }
        return json.parse(FromTask.serializer(), deferredText.await())
    }

    suspend fun getTask(id: Int): FromTask {

        val deferredText = GlobalScope.async {
            try {
                return@async httpClient.get<String> {
                    url("${requestAddress}api/v1/tasks/$id")
                }
            } catch (e: Exception) {
                throw e
            }
        }
        return json.parse(FromTask.serializer(), deferredText.await())
    }

    suspend fun postTask(task: Task): FromTask {

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