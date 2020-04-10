package com.alientech.classroyale

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult


// Activity for when the user is in-game
class ThirdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)
    }

    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    val mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

    var displayName = user!!.displayName.toString()

    var gameLogs = db.collection("games").document()
    var events = gameLogs.collection("events")
    lateinit var gameLog: HashMap<Any, Any>
    var i = 1

    // Game cards
    var cardCollection = db.collection("cards").document("cards")
    var normalCards = cardCollection.collection("normal")
    var personCards = cardCollection.collection("person")

    // User Cards
    var userDecks = db.collection("userDecks").document(displayName)
    var userCardCollection = db.collection("userCollections").document(displayName)
    var userNormalCards = userCardCollection.collection("normal")
    var userPersonCards = userCardCollection.collection("person")

    private lateinit var functions: FirebaseFunctions

    private val mInputMessageView: EditText? = null

    private fun startGame(gamemode: String): Task<Any> {
        val data = hashMapOf(
            "uid" to user!!.uid,
            "gamemode" to gamemode
        )

        return functions.getHttpsCallable("startGame").call(data).continueWith { task ->
            var event = events.document(user!!.uid + i)
            event.set(
                "startTime" to FieldValue.serverTimestamp()
            )

            val result = task.result?.data as String
            i++
        }
    }

    fun placeCard(position: Array<Int>,name: String, HP: Int, attackDamage: Int, description: String, rarity: String, isPersonCard: Boolean, isDisplayingProperties: Boolean, level: Int, XP: Int, XPToLevelUp: Int) {
        Card(name, HP, attackDamage, description, rarity, isPersonCard, isDisplayingProperties, level, XP, XPToLevelUp)
        var event = events.document(user!!.uid + i)

        i++
    }

    fun endGame(): Task<Any> {
        val data = hashMapOf(
            "uid" to user!!.uid
        )

        return functions.getHttpsCallable("endGame").call(data).continueWith { task ->
            var event = events.document(user!!.uid + i)
            event.set(
                "endTime" to FieldValue.serverTimestamp()
            )

            val result = task.result?.data as String
            // Do final UI stuff
            // Set content layout back to the main menu
        }
    }
}
