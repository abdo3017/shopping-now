package com.example.e_commerce.datasource.dbservice

import android.net.Uri
import com.example.e_commerce.state.DataState
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class StorageRepository
@Inject
constructor(
    private val mStorageRef: StorageReference
) {
    suspend fun uploadImage(uri: Uri, imageName: String): Flow<DataState<String>> = flow {
        try {
            val data = mStorageRef.child("images/$imageName")
            val upload = data.putFile(uri).await()

            if (upload.task.isSuccessful) {
                emit(DataState.Success(data.downloadUrl.toString()))
            } else
                emit(DataState.Error<Any>(Exception("error")) as DataState<String>)
        } catch (e: Exception) {
            emit(DataState.Error<Any>(e) as DataState<String>)
        }
    }


}