package com.alientech.classroyale

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.analytics.FirebaseAnalytics

// Activity for when the user is in-game
class ThirdActivity : AppCompatActivity() {

    var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    fun placeCard(view: View, position: Array<Any>) {
        SecondActivity.Main.main("Rasmit")
    }
}