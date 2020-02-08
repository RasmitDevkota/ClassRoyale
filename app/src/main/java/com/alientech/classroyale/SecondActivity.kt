@file:Suppress("UNUSED_VARIABLE", "UNUSED_ANONYMOUS_PARAMETER")

package com.alientech.classroyale

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


// Activity for the Deck Screen
class SecondActivity : AppCompatActivity() {

    private val TAG = "DeckScreen"

    var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    var displayName = user!!.displayName.toString()

    // Game cards
    public var cardCollection = db.collection("cards").document("cards")
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

    class Card(
        name: String,
        HP: Int,
        attackDamage: Int,
        description: String,
        rarity: String,
        isPersonCard: Boolean,
        isDisplayingProperties: Boolean,
        level: Int,
        XP: Int,
        XPToLevelUp: Int
    ) {
        var name: String
            get() {return this.name}
            set(value) {this.name = name}

        var HP: Int
            get() {return this.HP}
            set(value) {
                this.HP = HP
                for (i in 0 until this.level - 1) {
                    upgradeHP()
                }
            }

        var attackDamage: Int
            get() {return this.attackDamage}
            set(value) {
                this.attackDamage = attackDamage
                for (i in 0 until this.level - 1) {
                    upgradeAttackDamage()
                }
            }

        var description: String
            get() {return this.description}
            set(value) {this.description = description}

        var rarity: String
            get() {return this.rarity}
            set(value) {this.rarity = rarity}

        var isPersonCard: Boolean
            get() {return this.isPersonCard}
            set(value) {this.isPersonCard = isPersonCard}

        var isDisplayingProperties: Boolean
            get() {return this.isDisplayingProperties}
            set(value) {this.isDisplayingProperties = isDisplayingProperties}

        var level: Int
            get() {return this.level}
            set(value) {this.level = level}

        var xp: Int
            get() {return xp}
            set(value) {this.xp = xp}

        var xpToLevelUp: Int
            get() {return xpToLevelUp}
            set(value) {
                this.xpToLevelUp = xpToLevelUp
                for (i in 0 until level - 1) {
                    upgradeXPToLevelUp()
                }
            }

        fun upgradeXPToLevelUp() {
            this.xpToLevelUp += (0.3 * this.xpToLevelUp).toInt()
        }

        fun upgradeHP() {
            this.HP += (0.15 * this.HP).toInt()
        }

        fun upgradeAttackDamage() {
            this.attackDamage += (0.15 * this.attackDamage).toInt()
        }

        fun study() {
            if (this.level < 12) {
                if (this.xp >= this.xpToLevelUp) {
                    upgradeHP()
                    upgradeAttackDamage()
                    this.level = this.level + 1
                    this.xp = this.xp - this.xpToLevelUp
                    upgradeXPToLevelUp()
                }
            }
        }

        fun play() {
            val performance = this.evaluatePerformance()
            this.xp = this.xp + performance
        }

        fun evaluatePerformance(): Int {
            return 34433434;
        }

        companion object {
            const val COMMON = "Common"
            const val UNCOMMON = "Uncommon"
            const val RARE = "Rare"
            const val ULTRA_RARE = "Ultra Rare"
            const val LEGENDARY = "Legendary"
            const val MYTHICAL = "Mythical"
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
}
