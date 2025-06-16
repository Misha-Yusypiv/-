package com.yourappname.petapp.data.repository

import com.yourappname.petapp.data.firebase.FirebaseSource
import com.yourappname.petapp.domain.model.Pet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepository(private val firebaseSource: FirebaseSource) {
    suspend fun addPet(pet: Pet): Flow<Result<Pet>> = flow {
        emit(firebaseSource.addPet(pet))
    }

    suspend fun getPetsByOwnerId(ownerId: String): Flow<Result<List<Pet>>> = flow {
        emit(firebaseSource.getPetsByOwnerId(ownerId))
    }

    suspend fun updatePet(pet: Pet): Flow<Result<Unit>> = flow {
        emit(firebaseSource.updatePet(pet))
    }

    suspend fun deletePet(petId: String): Flow<Result<Unit>> = flow {
        emit(firebaseSource.deletePet(petId))
    }

    suspend fun uploadDocument(petId: String, documentUrl: String): Flow<Result<String>> = flow {
        emit(firebaseSource.uploadDocument(petId, documentUrl))
    }
} 