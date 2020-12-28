package com.example.e_commerce.datasource.dbservice

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AuthenticationService
@Inject
constructor() {
    suspend fun signIn(email: String, password: String): Boolean {
        kotlin.runCatching {
            AuthFireBase.getAuth().signInWithEmailAndPassword(email, password).await()
            return true
        }.getOrElse {
            return false
        }
    }

    suspend fun signUp(email: String, password: String): Boolean {
        kotlin.runCatching {
            AuthFireBase.getAuth().createUserWithEmailAndPassword(email, password).await()
            return true
        }.getOrElse {
            return false
        }
    }

    suspend fun updatePassword(password: String) {
        AuthFireBase.getAuth().currentUser!!.updatePassword(password).await()

    }
}

object AuthFireBase {
    fun getAuth(auth: FirebaseAuth = FirebaseAuth.getInstance()) = auth
}