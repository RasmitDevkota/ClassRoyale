package com.alientech.classroyale

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

// Activity for the Login Screen
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun getUserDetails() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val name = user.displayName
            val email = user.email
            val uid = user.uid
        }
    }

}