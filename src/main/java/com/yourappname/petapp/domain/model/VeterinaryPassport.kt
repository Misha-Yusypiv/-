package com.yourappname.petapp.domain.model

data class VeterinaryPassport(
    val id: String = "",
    val petId: String = "",
    val petName: String = "",
    val petBreed: String = "",
    val petColor: String = "",
    val petGender: String = "",
    val petBirthDate: String = "",
    val petImageUrl: String = "",
    val ownerName: String = "",
    val ownerContact: String = "",
    val passportNumber: String = "",
    val issueDate: String = "",
    val vaccinations: List<String> = emptyList(),
    val specialMarks: String = "",
    val createdAt: Long = System.currentTimeMillis()
) 