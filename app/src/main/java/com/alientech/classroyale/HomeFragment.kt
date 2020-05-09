package com.alientech.classroyale

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var startGameButton = view.findViewById<Button>(R.id.enterQueue)!!
        var disconnectButton = view.findViewById<Button>(R.id.disconnectButton)!!
        var arButton = view.findViewById<Button>(R.id.enterAR)!!

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
