@file:Suppress("UNUSED_VARIABLE", "UNUSED_ANONYMOUS_PARAMETER")

package com.alientech.classroyale

import android.content.Intent
import android.os.Bundle
import android.util.Log
import kotlin.io.println as println
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Integer.valueOf
import java.util.*


// Activity for the Deck Screen
class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
    }

    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    var displayName = user!!.displayName.toString()

    // Game cards
    var cardCollection = db.collection("cards").document("cards")
    var normalCards = cardCollection.collection("normal")
    var personCards = cardCollection.collection("person")

    // User Cards
    var userDecks = db.collection("userDecks").document(displayName)
    var userCardCollection = db.collection("userCollections").document(displayName)
    var userNormalCards = userCardCollection.collection("normal")
    var userPersonCards = userCardCollection.collection("person")

    object Main {
        @JvmStatic
        fun main(name: String) {
            when (name){
                "Rasmit" -> {
                    val card = Card("Rasmit", 600, 80, "Polymath Chairman and CEO of AlienTech Industries", Card.MYTHICAL, true, false, 1, 0, 1000)
                }
                "Chittebbayi" -> {
                    val card = Card("Chittebbayi", 600, 80, "Mr. Chittebbayi Java Microsoft-Certified Penugonda", Card.MYTHICAL, true, false, 1, 0, 1000)
                }
                "Shinde" -> {
                    val card = Card("Shinde", 600, 80, "Fly Shinde Airlines", Card.LEGENDARY, true, false, 1, 0, 1000)
                }
                "Monish" -> {
                    val card = Card("Monish", 600, 80, "Central Pleb", Card.MYTHICAL, true, false, 1, 0, 1000)
                }
                "Royce" -> {
                    val card = Card("Royce", 600, 80, "Installing malware into your device... [87%]", Card.MYTHICAL, true, false, 1, 0, 1000)
                }
                "Anshi" -> {
                    val card = Card("Anshi", 600, 80, "Aneesh's sister", Card.MYTHICAL, true, false, 1, 0, 1000)
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

}
