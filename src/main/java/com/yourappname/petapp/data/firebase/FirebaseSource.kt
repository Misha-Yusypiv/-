package com.yourappname.petapp.data.firebase

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.yourappname.petapp.domain.model.Pet
import com.yourappname.petapp.domain.model.VeterinaryPassport
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseSource {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    fun getGoogleSignInClient(clientId: String): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(GoogleSignIn.getLastSignedInAccount(null)?.context ?: throw IllegalStateException("Context is null"), gso)
    }

    suspend fun signInWithGoogle(intent: Intent): Result<Unit> = try {
        val account = GoogleSignIn.getSignedInAccountFromIntent(intent).await()
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun getCurrentUser() = auth.currentUser

    fun signOut() {
        auth.signOut()
        GoogleSignIn.getClient(
            GoogleSignIn.getLastSignedInAccount(null)?.context ?: throw IllegalStateException("Context is null"),
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut()
    }

    // Pet-related methods
    suspend fun addPet(pet: Pet): Result<Pet> = try {
        val petId = UUID.randomUUID().toString()
        val petWithId = pet.copy(id = petId)
        firestore.collection("pets")
            .document(petId)
            .set(petWithId)
            .await()
        Result.success(petWithId)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getPetsByOwnerId(ownerId: String): Result<List<Pet>> = try {
        val snapshot = firestore.collection("pets")
            .whereEqualTo("ownerId", ownerId)
            .get()
            .await()
        val pets = snapshot.documents.mapNotNull { it.toObject(Pet::class.java) }
        Result.success(pets)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updatePet(pet: Pet): Result<Unit> = try {
        firestore.collection("pets")
            .document(pet.id)
            .set(pet)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deletePet(petId: String): Result<Unit> = try {
        firestore.collection("pets")
            .document(petId)
            .delete()
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Document-related methods
    suspend fun uploadDocument(petId: String, documentUrl: String): Result<String> = try {
        val documentId = UUID.randomUUID().toString()
        firestore.collection("pets")
            .document(petId)
            .collection("documents")
            .document(documentId)
            .set(mapOf("url" to documentUrl, "uploadedAt" to System.currentTimeMillis()))
            .await()
        Result.success(documentId)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Help-related methods
    suspend fun uploadHelpPhoto(uri: android.net.Uri): Result<String> = try {
        val fileName = "help_photos/${UUID.randomUUID()}"
        val ref = storage.reference.child(fileName)
        ref.putFile(uri).await()
        val url = ref.downloadUrl.await().toString()
        Result.success(url)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun saveHelpRequest(
        subject: String,
        description: String,
        photoUrl: String?,
        location: android.location.Location?,
        feedback: Boolean,
        userId: String?
    ): Result<Unit> = try {
        val helpId = UUID.randomUUID().toString()
        val helpData = mutableMapOf<String, Any>(
            "id" to helpId,
            "subject" to subject,
            "description" to description,
            "feedback" to feedback,
            "createdAt" to System.currentTimeMillis()
        )
        if (photoUrl != null && photoUrl.isNotEmpty()) helpData["photoUrl"] = photoUrl
        if (location != null) {
            helpData["location"] = mapOf(
                "latitude" to location.latitude,
                "longitude" to location.longitude
            )
        }
        if (userId != null) helpData["userId"] = userId
        firestore.collection("help_requests")
            .document(helpId)
            .set(helpData)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // VeterinaryPassport methods
    suspend fun createVeterinaryPassport(passport: VeterinaryPassport): Result<VeterinaryPassport> = try {
        val passportId = if (passport.id.isNotEmpty()) passport.id else UUID.randomUUID().toString()
        val passportWithId = passport.copy(id = passportId)
        firestore.collection("veterinary_passports")
            .document(passportId)
            .set(passportWithId)
            .await()
        Result.success(passportWithId)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateVeterinaryPassport(passport: VeterinaryPassport): Result<Unit> = try {
        firestore.collection("veterinary_passports")
            .document(passport.id)
            .set(passport)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getVeterinaryPassportsByUser(ownerName: String): Result<List<VeterinaryPassport>> = try {
        val snapshot = firestore.collection("veterinary_passports")
            .whereEqualTo("ownerName", ownerName)
            .get()
            .await()
        val passports = snapshot.documents.mapNotNull { it.toObject(VeterinaryPassport::class.java) }
        Result.success(passports)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getVeterinaryPassportsByPet(petId: String): Result<List<VeterinaryPassport>> = try {
        val snapshot = firestore.collection("veterinary_passports")
            .whereEqualTo("petId", petId)
            .get()
            .await()
        val passports = snapshot.documents.mapNotNull { it.toObject(VeterinaryPassport::class.java) }
        Result.success(passports)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteVeterinaryPassport(passportId: String): Result<Unit> = try {
        firestore.collection("veterinary_passports")
            .document(passportId)
            .delete()
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
} 