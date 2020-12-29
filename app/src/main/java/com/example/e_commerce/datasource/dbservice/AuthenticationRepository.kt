package com.example.e_commerce.datasource.dbservice

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AuthenticationRepository
@Inject
constructor(
    private val auth: FirebaseAuth
) {
    suspend fun signIn(email: String, password: String): Boolean {
        kotlin.runCatching {
            auth.signInWithEmailAndPassword(email, password).await()
            return true
        }.getOrElse {
            return false
        }
    }

    suspend fun signOut(): Boolean {
        kotlin.runCatching {
            auth.signOut()
            return true
        }.getOrElse {
            return false
        }
    }

    suspend fun signUp(email: String, password: String): Boolean {
        kotlin.runCatching {
            auth.createUserWithEmailAndPassword(email, password).await()
            return true
        }.getOrElse {
            return false
        }
    }

    suspend fun updatePassword(password: String) {
        auth.currentUser!!.updatePassword(password).await()

    }
}

//object AuthFireBase {
//    fun getAuth(auth: FirebaseAuth = FirebaseAuth.getInstance()) = auth
//}