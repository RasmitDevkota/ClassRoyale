package com.alientech.classroyale

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions

class HomeFragment : Fragment(R.layout.fragment_home) {

    lateinit var startGameButton: Button
    lateinit var disconnectButton: Button
    lateinit var arButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startGameButton = view.findViewById<Button>(R.id.enterQueue)!!
        disconnectButton = view.findViewById<Button>(R.id.disconnectButton)!!
        arButton = view.findViewById<Button>(R.id.enterAR)!!

        startGameButton.setOnClickListener {
            startGameButton.visibility = View.INVISIBLE
            disconnectButton.visibility = View.VISIBLE

            SecondActivity().startGame()
        }

        disconnectButton.setOnClickListener {
            SecondActivity().safeDisconnect()

            startGameButton.visibility = View.VISIBLE
            disconnectButton.visibility = View.INVISIBLE
        }

        arButton.setOnClickListener{
            startActivity(Intent(SecondActivity(), ThirdActivity::class.java))
        }
    }

}
