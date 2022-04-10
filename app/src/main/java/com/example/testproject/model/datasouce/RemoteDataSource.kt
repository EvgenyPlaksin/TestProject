package com.example.testproject.model.datasouce

import com.example.testproject.model.api.ApiService
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val apiService: ApiService) {

    suspend fun getData() = apiService.getData()

}