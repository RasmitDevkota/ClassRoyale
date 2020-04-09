package com.alientech.classroyale

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_second.*
import com.alientech.classroyale.R

// Activity for the Home Screen
class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
    }

    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    val mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

    var displayName = user!!.displayName.toString()

    var games = db.collection("games")

    // Game cards
    var cardCollection = db.collection("cards").document("cards")
    var normalCards = cardCollection.collection("normal")
    var personCards = cardCollection.collection("person")

    // User Cards
    var userDecks = db.collection("userDecks").document(displayName)
    var userCardCollection = db.collection("userCollections").document(displayName)
    var userNormalCards = userCardCollection.collection("normal")
    var userPersonCards = userCardCollection.collection("person")

    fun cardData(name: String, type: String) {
        when(type) {
            "normal" -> {
                normalCards.document(name).get().addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    } else {
                        Log.e(TAG, "No such document")
                    }
                }.addOnFailureListener { exception ->
                    Log.e(TAG, "get failed with ", exception)
                }
            }

            "person" -> {
                personCards.document(name).get().addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    } else {
                        Log.e(TAG, "No such document")
                    }
                }.addOnFailureListener { exception ->
                    Log.e(TAG, "get failed with ", exception)
                }
            }
        }
    }

    fun cardUnlock (cardType: String, cardName: String) {
        var newCard = cardCollection.collection(cardType).document(cardName)
        var newCardData = newCard.get()
        var newCardDestination = userCardCollection.collection(cardType)

        newCardDestination.document(cardName).set(newCardData)
    }

    fun editDeck (deckNumber: String, addCardName: String, addCardType: String,  removeCardName: String, removeCardType: String) {
        var addCard = userCardCollection.collection(addCardType).document(addCardName)
        var addCardData = addCard.get()
        var addCardDeck = userDecks.collection(deckNumber)

        userDecks.collection(deckNumber).document(removeCardName).delete()

        addCardDeck.document(addCardName).set(addCardData)
    }

    fun startGame() {
        startActivityForResult(Intent(this, ThirdActivity::class.java), 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            var docRef = data!!.getStringExtra("docRef")
            var gameLogs = games.document(docRef)
        }
    }

    companion object {
        private const val TAG = "HomeScreenActivity"
    }
//
//    class MyActivity : FragmentActivity() {
//        companion object {
//            private const val TAG_FLUTTER_FRAGMENT = "flutter_fragment"
//        }
//
//        private var flutterFragment: FlutterFragment? = null
//
//        override fun onCreate(savedInstanceState: Bundle?) {
//            super.onCreate(savedInstanceState)
//            setContentView(R.layout.activity_second)
//
//            val fragmentManager: FragmentManager = supportFragmentManager
//            flutterFragment = fragmentManager.findFragmentByTag(TAG_FLUTTER_FRAGMENT) as FlutterFragment?
//        }
//    }
}
