package com.yourappname.petapp.data.repository

import com.yourappname.petapp.data.firebase.FirebaseSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepository(private val firebaseSource: FirebaseSource) {
    suspend fun signInWithPhoneNumber(phoneNumber: String): Flow<Result<Unit>> = flow {
        try {
            firebaseSource.signInWithPhoneNumber(phoneNumber)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun verifyCode(verificationId: String, code: String): Flow<Result<Unit>> = flow {
        try {
            firebaseSource.verifyCode(verificationId, code)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 