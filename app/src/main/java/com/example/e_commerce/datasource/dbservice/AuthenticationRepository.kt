package com.example.e_commerce.datasource.dbservice

import com.app.movie.domain.state.DataState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AuthenticationRepository
@Inject
constructor(
    private val auth: FirebaseAuth
) {
    suspend fun signIn(email: String, password: String): Flow<DataState<Boolean>> = flow {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            emit(DataState.Success(true) as DataState<Boolean>)
        } catch (e: Exception) {
            emit(DataState.Error<Any>(e) as DataState<Boolean>)
        }
    }

    suspend fun signOut(): Flow<DataState<Boolean>> = flow {
        try {
            auth.signOut()
            emit(DataState.Success(true) as DataState<Boolean>)
        } catch (e: Exception) {
            emit(DataState.Error<Any>(e) as DataState<Boolean>)
        }
    }

    suspend fun signUp(email: String, password: String): Flow<DataState<Boolean>> = flow {
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            emit(DataState.Success(true) as DataState<Boolean>)
        } catch (e: Exception) {
            emit(DataState.Error<Any>(e) as DataState<Boolean>)
        }
    }

    suspend fun updatePassword(password: String): Flow<DataState<Boolean>> = flow {
        try {
            auth.currentUser!!.updatePassword(password).await()
            emit(DataState.Success(true) as DataState<Boolean>)
        } catch (e: Exception) {
            emit(DataState.Error<Any>(e) as DataState<Boolean>)
        }
    }
}