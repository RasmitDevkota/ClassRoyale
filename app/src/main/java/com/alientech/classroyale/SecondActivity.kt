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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions

// Activity for the Home Screen
class SecondActivity : AppCompatActivity(), OutOfDeckPopup.NoticeDialogListener {

    val user = FirebaseAuth.getInstance().currentUser
    var uid = user!!.uid
    var displayName = user!!.displayName.toString()

    val db = FirebaseFirestore.getInstance()
    val mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

    var games = db.collection("games")
    var mine = false
    var status = "READY"
    var currentGame = ""
    var rejected = false
    var abandoned = false

    var cardCollection = db.collection("cards").document("cards")
    var normalCards = cardCollection.collection("normal")
    var personCards = cardCollection.collection("person")

    var userDecks = db.collection("userDecks").document(displayName)
    var userCardCollection = db.collection("userCollections").document(displayName)
    var userNormalCards = userCardCollection.collection("normal")
    var userPersonCards = userCardCollection.collection("person")

    var functions: FirebaseFunctions = FirebaseFunctions.getInstance()

    lateinit var startGameButton: Button
    lateinit var disconnectButton: Button
    lateinit var arButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val navigation = findViewById<BottomNavigationView>(R.id.navigtion_menu)
        navigation.setOnNavigationItemSelectedListener{ item ->
                lateinit var selectedFragment: Fragment
                when (item.itemId) {
                    R.id.home -> selectedFragment = HomeFragment()
                    R.id.friends -> selectedFragment = FriendsFragment()
                    R.id.cards -> selectedFragment = CardsFragment()
                }
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_frame,
                    selectedFragment
                ).commit()
                true
        }

        supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, HomeFragment()).commit()

        if (getUserStatus() == "CHECKING") {
            startGameButton.visibility = View.INVISIBLE
            disconnectButton.visibility = View.VISIBLE

            opponentListener(getUserGame())
        }
        else if (getUserStatus() == "LOADING" || getUserStatus() == "STARTED") {
            val intent = Intent(this, ThirdActivity::class.java)
            val b = Bundle()
            b.putStringArrayList("gameData", arrayListOf("user1", getUserGame()))
            intent.putExtras(b)

            startActivity(intent)
        }
    }

    fun safeDisconnect() {
        if (status == "READY") {
            Log.d(TAG, "Safe to go!")
        } else if ((status == "PENDING" || status == "CHECKING") && mine) {
            // PENDING: My Game - Delete document, change user status + CHECKING: My Game - Delete document, change user status

            games.document(currentGame).delete()
            removeUserGame()
            setUserStatus("READY")
        } else if (status == "PENDING" && !mine) {
            // PENDING: Game Queue - Change user status

            removeUserGame()
            setUserStatus("READY")
        } else if (status == "CHECKING" && !mine) {
            // CHECKING: Game Queue - Leave queue, change user status

            games.document(currentGame).update(mapOf(
                "queue.${uid}" to FieldValue.delete()
            ))
            removeUserGame()
            setUserStatus("READY")
        } else if (status == "CHOSEN" && mine) {
            games.document(currentGame).delete()
            removeUserGame()
            setUserStatus("READY")
        } else if (status == "CHOSEN" && mine) {
            games.document(currentGame).delete()
            removeUserGame()
            setUserStatus("READY")
        } else if (rejected) {
            // CHOSEN: Game Queue (Rejected Only)  - Clean Disconnect
            Log.d(TAG, "Safe to go!")
        } else if (getUserStatus() == "ENDED") {
            removeUserGame()
            setUserStatus("READY")
        } else {
            Log.e(TAG, "UHHHHHHHHHHHHHHHH THIS ISN'T SUPPOSED TO HAPPEN-")
        }

        Log.d(TAG, getUserStatus() + " " + getUserGame())
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

    fun removeUserGame() {
        db.collection("users").document(uid).update(mapOf(
            "currentGame" to FieldValue.delete()
        ))
    }

    fun startGame(): Any {
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

        setUserStatus("PENDING")

        games.whereEqualTo("status", "PENDING").limit(1).get().addOnSuccessListener { documents ->

            if (documents.size() != 0) {
                for (doc in documents) {
                    setUserStatus("CHECKING")
                    mine = false

                    games.document(doc.id).update(queueData)

                    var attempt = checkListener(doc.id)
                    if (attempt == "FAILURE") {
                        return@addOnSuccessListener
                    }
                }
            } else {
                var gameDoc = games.document()
                gameDoc.set(userData).addOnSuccessListener {
                    setUserStatus("PENDING")
                    currentGame = gameDoc.id
                    mine = true
                    setUserGame(currentGame)

                    var attempt = opponentListener(currentGame)
                    if (attempt == "FAILURE") {
                        return@addOnSuccessListener
                    }
                }
            }
        }

        return if (rejected) {
            startGame()
        } else {
            recap()
        }
    }

    fun opponentListener(gameDocId: String): Any {
        var matchListener = games.document(gameDocId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists() && !snapshot.metadata.hasPendingWrites()) {
                var documentStatus = snapshot.data!!["status"]

                if (documentStatus == "CHECKING") {
                    setUserStatus("CHECKING")
                } else if (documentStatus == "CHOSEN") {
                    disconnectButton.setOnClickListener(null)
                    setUserStatus("CHOSEN")

                    var opponentuid = snapshot.data!!["user2.uid"]

                    Toast.makeText(this, "Found game with user $opponentuid", Toast.LENGTH_LONG).show()

//                    val intent = Intent(this, ThirdActivity::class.java)
//                    val b = Bundle()
//                    b.putStringArrayList("gameData", arrayListOf("user1", gameDocId))
//                    intent.putExtras(b)
//
//                    startActivity(intent)
                } else {
                    abandoned = true
                    return@addSnapshotListener
                }
            } else {
                Log.w(TAG, "Game confirmation data missing.")
            }
        }
        matchListener.remove()

        if (abandoned) {
            return opponentListener(gameDocId)
        } else {
            return "FAILURE"
        }
    }

    fun checkListener(gameDocId: String): String {
        var matchListener = games.document(gameDocId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists() && !snapshot.metadata.hasPendingWrites()) {
                var documentStatus = snapshot.data!!["status"]

                if (documentStatus == "CHOSEN") {
                    currentGame = gameDocId

                    var accepteduid = snapshot.data!!["user2.uid"]
                    if (accepteduid == uid) {
                        setUserStatus("CHOSEN")
                        setUserGame(gameDocId)
                        disconnectButton.setOnClickListener(null)

                        var opponentuid = snapshot.data!!["user1.uid"]
                        Toast.makeText(this, "Found game with user $opponentuid", Toast.LENGTH_LONG).show()

//                        val intent = Intent(this, ThirdActivity::class.java)
//                        val b = Bundle()
//                        b.putStringArrayList("gameData", arrayListOf("user1", currentGame))
//                        intent.putExtras(b)
//
//                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Game join rejected", Toast.LENGTH_LONG).show()
                        rejected = true
                        setUserStatus("PENDING")
                        return@addSnapshotListener
                    }
                }
            } else {
                Log.w(TAG, "Game confirmation data missing.")
            }
        }

        matchListener.remove()

        return if (rejected) {
            "FAILURE"
        } else {
            "SUCCESS"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            var docRef = data!!.getStringExtra("docRef")
            var gameLogs = games.document(docRef)
        }
    }

    fun recap() {

    }

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
        lateinit var addCardData: DocumentSnapshot
        addCard.get().addOnSuccessListener { documentSnapshot -> addCardData = documentSnapshot }

        userDecks.collection(deckNumber).document(removeCardName).delete()

        var addCardDeck = userDecks.collection(deckNumber)
        addCardDeck.document(addCardName).set(addCardData)
    }

    fun showNoticeDialog() {
        val dialog = OutOfDeckPopup()
        supportFragmentManager?.let { dialog.show(it, "NoticeDialogFragment") }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        Log.d("CardsFragment", "Add card to deck")
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        Log.d("CardsFragment", "Closed Popup")
    }

    companion object {
        private const val TAG = "HomeScreenActivity"
    }
}
