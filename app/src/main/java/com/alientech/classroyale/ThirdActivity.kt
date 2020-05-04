package com.alientech.classroyale

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import kotlin.math.ceil
import kotlin.math.pow

// Activity for when the user is in-game
class ThirdActivity : AppCompatActivity() {

    val user = FirebaseAuth.getInstance().currentUser
    var uid = user!!.uid
    var displayName = user!!.displayName.toString()
    var userIcon = user!!.photoUrl

    val db = FirebaseFirestore.getInstance()
    val mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

    var usersUser = db.collection("users").document(uid)
    var userClass = 0
    var userGrade = 0

    // MAKE gameLogs TAKE A PARAMETER OF WHAT THE DOCUMENT IS
    lateinit var gameLogs: DocumentReference
    var events = gameLogs.collection("events")
    var i = 1
    var prngList: MutableList<String> = ArrayList()
    var j = 0

    var cardCollection = db.collection("cards").document("cards")
    var normalCards = cardCollection.collection("normal")
    var personCards = cardCollection.collection("person")

    var userDecks = db.collection("userDecks").document(displayName)
    var userCardCollection = db.collection("userCollections").document(displayName)
    var userNormalCards = userCardCollection.collection("normal")
    var userPersonCards = userCardCollection.collection("person")

    var functions: FirebaseFunctions = FirebaseFunctions.getInstance()

    private val mInputMessageView: EditText? = null

    var handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        var b = intent.extras
        if (b != null) {
            var whichUser = b.getStringArrayList("gameData")[0]
            var gameDocRef = b.getStringArrayList("gameData")[1]
            gameLogs = db.collection("game").document(gameDocRef)

            usersUser.get().addOnSuccessListener { document ->
                var userClass = document.data!!["class"]
                var userGrade = document.data!!["grade"]
            }

            if (whichUser == "user1") {
                var loadData = mapOf(
                    "startTime" to FieldValue.serverTimestamp(),
                    "user1" to mapOf(
                        "name" to displayName,
                        "uid" to uid,
                        "icon" to user!!.photoUrl,
                        "class" to userClass,
                        "grade" to userGrade
                    )
                )
                db.collection("games").document(gameDocRef).update(loadData)
            } else {
                var loadData = mapOf(
                    "user2" to mapOf(
                        "name" to displayName,
                        "uid" to uid,
                        "icon" to user!!.photoUrl,
                        "class" to ""
                    )
                )
                db.collection("games").document(gameDocRef).update(loadData)
            }

        }
    }

    fun actionHandler() {
        // ADD CODE TO PROCESS WHAT NEEDS TO HAPPEN
        lateinit var event: String

        functions.getHttpsCallable(event).call(event)
        handler.postDelayed({
            // ADD SWITCH CASE TO GET WHAT THE EVENT IS AND RUN THE CORRECT FUNCTION
        }, 10000)
    }

    fun onClick(view: View) {
        when (view.getId()) {

        }
    }

    fun prngGenerator() {
        var firstSeed = (0..prngList.size + 1).random()
        var secondSeed = (0..prngList.size + 1).random()
        var combinedSeed = firstSeed * secondSeed

        var randSeed = ceil((prngList.size - 1) * (combinedSeed/(prngList.size).toDouble().pow(2))).toInt()
        var randResult = prngList[randSeed]
    }

    fun placeCard(position: Array<Int>, name: String, HP: Int, attackDamage: Int, description: String, rarity: String, isPersonCard: Boolean, isDisplayingProperties: Boolean, level: Int, XP: Int, XPToLevelUp: Int) {
        var newCard = Card(name, HP, attackDamage, description, rarity, isPersonCard, isDisplayingProperties, level, XP, XPToLevelUp)
        var event = events.document(uid + i)

        var cardData = hashMapOf(
            "name" to HP,
            "HP" to HP,
            "attackDamage" to attackDamage,
            "description" to description,
            "rarity" to rarity,
            "isPersonCard" to isPersonCard,
            "isDisplayingProperties" to isDisplayingProperties,
            "level" to level,
            "XP" to XP,
            "XPToLevelUp" to XPToLevelUp
        )

        val data = hashMapOf(
            "uid" to  uid,
            "time" to FieldValue.serverTimestamp(),
            "eventFunction" to "placeCard",
            "eventParameters" to arrayOf(cardData, name, position),
            "userCall" to true
        )

        i++
    }

    fun endGame(): Task<Any> {
        val data = hashMapOf(
            "uid" to uid
        )

        return functions.getHttpsCallable("endGame").call(data).continueWith { task ->
            var event = events.document(uid + i)
            event.set(
                "endTime" to FieldValue.serverTimestamp()
            )

            val result = task.result!!.data as String
            // Do final UI stuff
            // Set content layout back to the main menu
        }
    }
}
