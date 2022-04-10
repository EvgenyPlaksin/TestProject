package com.example.testproject.model.repository

import com.example.testproject.game.fragments.utils.NetworkResult
import com.example.testproject.model.data.ApiResponse
import com.example.testproject.model.datasouce.RemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class Repository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : BaseApiResponse() {

    // тут запускается функция которая возвращает флоу, она ранится на ио(айо) потоке
    suspend fun getData(): Flow<NetworkResult<ApiResponse>> {
        return flow{
            emit(safeApiCall { remoteDataSource.getData() })
        }.flowOn(Dispatchers.IO)
    }

}