@file:Suppress("UNUSED_VARIABLE", "UNUSED_ANONYMOUS_PARAMETER", "UNUSED_PARAMETER")

package com.alientech.classroyale

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import kotlin.reflect.typeOf

// Activity for the Home Screen
class SecondActivity : AppCompatActivity() {

    val user = FirebaseAuth.getInstance().currentUser
    var uid = user!!.uid
    var displayName = user!!.displayName.toString()

    val db = FirebaseFirestore.getInstance()
    val mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

    var games = db.collection("games")
    var myGameDoc = ""
    var mine = false
    var status = "READY"
    var currentGame = ""
//
//    var cardCollection = db.collection("cards").document("cards")
//    var normalCards = cardCollection.collection("normal")
//    var personCards = cardCollection.collection("person")
//
//    var userDecks = db.collection("userDecks").document(displayName)
//    var userCardCollection = db.collection("userCollections").document(displayName)
//    var userNormalCards = userCardCollection.collection("normal")
//    var userPersonCards = userCardCollection.collection("person")

    var functions: FirebaseFunctions = FirebaseFunctions.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val startGameButton = findViewById<Button>(R.id.enterQueue)
        val disconnectButton = findViewById<Button>(R.id.disconnectButton)

        startGameButton.setOnClickListener {
            startGameButton.visibility = View.INVISIBLE
            disconnectButton.visibility = View.VISIBLE

            startGame()
        }

        disconnectButton.setOnClickListener {
            safeDisconnect()

            startGameButton.visibility = View.VISIBLE
            disconnectButton.visibility = View.INVISIBLE
        }

        if (getUserStatus() == "CHECKING") {
            startGameButton.visibility = View.INVISIBLE
            disconnectButton.visibility = View.VISIBLE

            opponentListener(getUserGame())
        }
    }

    fun safeDisconnect() {
        if (getUserStatus() == "READY") {
            Log.d(TAG, "Safe to go!")
        } else if (getUserStatus() == "PENDING" && myGameDoc != "" && mine && getUserGame() != null) {
            games.document(myGameDoc).delete()
            removeUserGame(myGameDoc)
            setUserStatus("READY")
            Log.d(TAG, myGameDoc)
        } else if (getUserStatus() == "CHECKING" && getUserGame() != null) {
            var disconnectData = mapOf(
                "gameId" to myGameDoc,
                "uid" to uid
            )
            functions.getHttpsCallable("disconnect").call(disconnectData)

            Log.d(TAG, disconnectData.toString())
        }

        Log.d(TAG, getUserStatus() + " " + myGameDoc)
    }

    public override fun onDestroy() {
        super.onDestroy()
        safeDisconnect()
    }

    fun getUserStatus(): String {
        db.collection("users").document(uid).get().addOnSuccessListener { document ->
            if (document != null) {
                status = if (document.data!!["gameStatus"] == null) "READY" else document.data!!["gameStatus"].toString()
            }
        }
        return status
    }

    fun setUserStatus(newStatus: String) {
        db.collection("users").document(uid).update(mapOf(
            "gameStatus" to newStatus
        ))
    }

    fun getUserGame(): String {
        db.collection("users").document(uid).get().addOnSuccessListener { document ->
            currentGame = document.data!!["currentGame"].toString()
        }
        return currentGame
    }

    fun setUserGame(gameDocId: String) {
        db.collection("users").document(uid).update(mapOf(
            "currentGame" to gameDocId
        ))
    }

    fun removeUserGame(gameDocId: String) {
        db.collection("users").document(uid).update(mapOf(
            "currentGame" to FieldValue.delete()
        ))
    }

    private fun startGame() {
        if (getUserStatus() != "READY") {
            Log.d(TAG, getUserGame())
            return
        }

        val queueData = hashMapOf(
            "status" to "CHECKING",
            "queue.${uid}" to FieldValue.serverTimestamp()
        )

        val userData = hashMapOf(
            "status" to "PENDING",
            "user1" to mapOf(
                "uid" to uid,
                "name" to displayName
            )
        )

        games.whereEqualTo("status", "PENDING").limit(1).get().addOnSuccessListener { documents ->
            if (documents.size() != 0) {
                for (doc in documents) {
                    setUserStatus("PENDING")
                    mine = false
                    setUserGame(doc.id)

                    if (doc.data["user1.uid"] == uid) {
                        myGameDoc = doc.id
                        mine = true
                        opponentListener(doc.id)
                    } else {
                        games.document(doc.id).update(queueData)

                        checkListener(doc.id)
                    }
                }
            } else {
                var gameDoc = games.document()
                gameDoc.set(userData).addOnSuccessListener {
                    setUserStatus("PENDING")
                    myGameDoc = gameDoc.id
                    mine = true
                    setUserGame(myGameDoc)

                    opponentListener(myGameDoc)
                }
            }
        }
    }

    fun opponentListener(gameDocId: String) {
        var matchListener = games.document(gameDocId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                var documentStatus = snapshot.data!!["status"]

                if (documentStatus == "CHECKING" || documentStatus == "CHOSEN") {
                    setUserStatus("CHECKING")

                    var opponentuid = snapshot.data!!["user2.uid"]

                    Toast.makeText(this, "Found game with user $opponentuid", Toast.LENGTH_LONG).show()

//                                val intent = Intent(this, ThirdActivity::class.java)
//                                val b = Bundle()
//                                b.putStringArrayList("gameData", arrayListOf("user1", gameDoc.id))
//                                intent.putExtras(b)
//
//                                startActivity(intent)
                }
            } else {
                Log.w(TAG, "Game confirmation data missing.")
            }
        }

        matchListener.remove()
    }

    fun checkListener(gameDocId: String) {
        var matchListener = games.document(gameDocId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                var documentStatus = snapshot.data!!["status"]

                // CLOUD FUNCTION FOR DISCONNECT SHOULD, IF THERE WAS ANOTHER AVAILABLE USER STILL KNOWN, GET THAT USER AND JOIN
                if (documentStatus == "CHECKING" || documentStatus == "CHOSEN") {
                    setUserStatus("CHECKING")
                    myGameDoc = gameDocId

                    var accepteduid = snapshot.data!!["user2.uid"]
                    if (accepteduid == uid) {
                        var opponentuid = snapshot.data!!["user1.uid"]
                        Toast.makeText(this, "Found game with user $opponentuid", Toast.LENGTH_LONG).show()
//
//                                    val intent = Intent(this, ThirdActivity::class.java)
//                                    val b = Bundle()
//                                    b.putStringArrayList("gameData", arrayListOf("user1", myGameDoc))
//                                    intent.putExtras(b)
//
//                                    startActivity(intent)
                    } else {
                        Toast.makeText(this, "Game join rejected", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Log.w(TAG, "Game confirmation data missing.")
            }
        }

        matchListener.remove()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            var docRef = data!!.getStringExtra("docRef")
            var gameLogs = games.document(docRef)
        }
    }

    // GAME STUFF END

    // CARD STUFF

//    fun cardData(name: String, type: String) {
//        when(type) {
//            "normal" -> {
//                normalCards.document(name).get().addOnSuccessListener { document ->
//                    if (document != null) {
//                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
//                    } else {
//                        Log.e(TAG, "No such document")
//                    }
//                }.addOnFailureListener { exception ->
//                    Log.e(TAG, "get failed with ", exception)
//                }
//            }
//
//            "person" -> {
//                personCards.document(name).get().addOnSuccessListener { document ->
//                    if (document != null) {
//                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
//                    } else {
//                        Log.e(TAG, "No such document")
//                    }
//                }.addOnFailureListener { exception ->
//                    Log.e(TAG, "get failed with ", exception)
//                }
//            }
//        }
//    }
//
//    fun cardUnlock (cardType: String, cardName: String) {
//        var newCard = cardCollection.collection(cardType).document(cardName)
//        var newCardData = newCard.get()
//        var newCardDestination = userCardCollection.collection(cardType)
//
//        newCardDestination.document(cardName).set(newCardData)
//    }
//
//    fun editDeck (deckNumber: String, addCardName: String, addCardType: String,  removeCardName: String, removeCardType: String) {
//        var addCard = userCardCollection.collection(addCardType).document(addCardName)
//        var addCardData = addCard.get()
//        var addCardDeck = userDecks.collection(deckNumber)
//
//        userDecks.collection(deckNumber).document(removeCardName).delete()
//
//        addCardDeck.document(addCardName).set(addCardData)
//    }

    // CARD STUFF END

    companion object {
        private const val TAG = "HomeScreenActivity"
    }
}
