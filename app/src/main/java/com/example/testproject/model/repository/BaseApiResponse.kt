package com.example.testproject.model.repository

import com.example.testproject.game.fragments.utils.NetworkResult
import retrofit2.Response

abstract class BaseApiResponse {
    // абстактный класс, от которого наследуется репозиторий. Тут основная логика запроса
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return NetworkResult.Success(body)
                }
            }
            return error("${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }
    private fun <T> error(errorMessage: String): NetworkResult<T> =
        NetworkResult.Error("Api call failed $errorMessage")
}