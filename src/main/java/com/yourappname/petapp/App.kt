package com.yourappname.petapp

import android.app.Application
import com.google.firebase.FirebaseApp
import com.yourappname.petapp.data.firebase.FirebaseSource
import com.yourappname.petapp.data.repository.AuthRepository
import com.yourappname.petapp.data.repository.UserRepository

class App : Application() {
    lateinit var firebaseSource: FirebaseSource
        private set
    
    lateinit var authRepository: AuthRepository
        private set
    
    lateinit var userRepository: UserRepository
        private set

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        
        // Initialize Firebase source
        firebaseSource = FirebaseSource()
        
        // Initialize repositories
        authRepository = AuthRepository(firebaseSource)
        userRepository = UserRepository(firebaseSource)
    }
} 