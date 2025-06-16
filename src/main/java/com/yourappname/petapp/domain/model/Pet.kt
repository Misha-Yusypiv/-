package com.yourappname.petapp.domain.model

data class Pet(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val breed: String = "",
    val age: Int = 0,
    val ownerId: String = "",
    val imageUrl: String = "",
    val medicalHistory: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
) 