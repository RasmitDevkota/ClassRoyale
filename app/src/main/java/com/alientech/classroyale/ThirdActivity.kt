@file:Suppress("UNUSED_VARIABLE", "UNUSED_ANONYMOUS_PARAMETER", "UNUSED_PARAMETER")

package com.alientech.classroyale

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.android.synthetic.main.activity_third.*
import kotlin.math.ceil
import kotlin.math.pow

// Activity for when the user is in-game
class ThirdActivity : AppCompatActivity(), View.OnClickListener {

    val user = FirebaseAuth.getInstance().currentUser
    var uid = user!!.uid
    var displayName = user!!.displayName.toString()
    var userIcon = user!!.photoUrl

    val db = FirebaseFirestore.getInstance()
    val mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

    var status = "LOADING"
    var gameDocRef = ""
    var mine = false

    var usersUser = db.collection("users").document(uid)
    var userClass = 0
    var userGrade = 0

    lateinit var gameLogs: DocumentReference
    lateinit var events: CollectionReference
    lateinit var localLogs: ArrayList<Map<Any, Any>>
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

    lateinit var scene: Scene

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        scene = sceneView.scene
        renderObject(Uri.parse("testModel.sfb"))

        var b = intent.extras
        if (b != null) {
            var whichUser = b.getStringArrayList("gameData")[0]
            gameDocRef = b.getStringArrayList("gameData")[1]
            gameLogs = db.collection("game").document(gameDocRef)

            if (getUserStatus() != "DISCONNECTED") {
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
                            "class" to userClass,
                            "grade" to userGrade
                        )
                    )
                    db.collection("games").document(gameDocRef).update(loadData)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun renderObject(parse: Uri) {
        ModelRenderable.builder().setSource(this, parse).build()
            .thenAccept {
                addNodeToScene(it)
            }
            .exceptionally {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(it.message).setTitle("error!")
                val dialog = builder.create()
                dialog.show()
                return@exceptionally null
            }
    }

    private fun addNodeToScene(model: ModelRenderable?) {
        model?.let {
            var testModel = Node().apply {
                setParent(scene)
                localPosition = Vector3(0f, 0f, -1f)
                localScale = Vector3(0.5f, 0.5f, 0.5f)
                name = "TestModel"
                renderable = it
            }

            val scene = findViewById<SceneView>(R.id.sceneView).scene
            scene.addChild(testModel)
        }
    }

    override fun onPause() {
        super.onPause()
        sceneView.pause()
    }

    override fun onResume() {
        super.onResume()
        sceneView.resume()
    }

    public override fun onDestroy() {
        super.onDestroy()
        safeDisconnect()
    }

    fun safeDisconnect() {
        if (getUserStatus() == "LOADING") {

        } else if (getUserStatus() == "STARTED") {

        } else {
            Log.e(TAG, "UHHHHHHHHHHHHHHHH THIS ISN'T SUPPOSED TO HAPPEN-")
        }

        Log.d(TAG, getUserStatus() + " " + getUserGame())
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
            gameDocRef = document.data!!["gameDocRef"].toString()
        }
        return gameDocRef
    }

    fun setUserGame(gameDocId: String) {
        db.collection("users").document(uid).update(mapOf(
            "gameDocRef" to gameDocId
        ))
    }

    fun removeUserGame() {
        db.collection("users").document(uid).update(mapOf(
            "gameDocRef" to FieldValue.delete()
        ))
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    fun actionHandler(event: String) {
        functions.getHttpsCallable("eventLogger").call(event)
        handler.postDelayed({
            when (event) {
                "placeCard" -> {

                }
                "cardDestroyed" -> {

                }
                "specialAttackButton" -> {

                }
            }
        }, 10000)
    }

    override fun onClick(view: View) {
        var nonStandardEvents = arrayOf("placeCard", "cardDestroyed")
        var nonStandardClicks = arrayOf("specialAttackButton")

        if (nonStandardClicks.contains(view.id.toString())) {

        } else if (view.id.toString().contains("Card")) {

        }

        actionHandler(view.id.toString())
    }

    fun prngGenerator() {
        var firstSeed = (0..prngList.size + 1).random()
        var secondSeed = (0..prngList.size + 1).random()
        var combinedSeed = firstSeed * secondSeed

        var randSeed = ceil((prngList.size - 1) * (combinedSeed/(prngList.size).toDouble().pow(2))).toInt()
        var randResult = prngList[randSeed]
    }

    fun newCard() {

    }

    fun placeCard(position: Array<Int>, name: String, HP: Int, attackDamage: Int, description: String, rarity: String, isPersonCard: Boolean, isDisplayingProperties: Boolean, level: Int, XP: Int, XPToLevelUp: Int) {
        // INSTANTIATE THE CARD

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

    fun forfeit() {

    }

    fun endGame() {
        val data = hashMapOf(
            "uid" to uid
        )

        functions.getHttpsCallable("endGame").call(data).continueWith { task ->
            var event = events.document(uid + i)
            event.set(
                "endTime" to FieldValue.serverTimestamp()
            )

            val result = task.result!!.data as String
            // Do final UI stuff
            // Set content layout back to the main menu
        }
    }

    companion object {
        private const val TAG = "GameActivity"
    }
}