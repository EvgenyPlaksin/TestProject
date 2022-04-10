package com.example.testproject.model.api

import com.example.testproject.game.fragments.utils.Constants
import com.example.testproject.model.data.ApiResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("luckyCoins.json")
    suspend fun getData(): Response<ApiResponse>

}