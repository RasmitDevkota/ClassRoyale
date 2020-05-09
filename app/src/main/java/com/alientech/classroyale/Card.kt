@file:Suppress("UNUSED_VARIABLE", "UNUSED_ANONYMOUS_PARAMETER", "UNUSED_PARAMETER")

package com.alientech.classroyale

import android.R
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore


val db = FirebaseFirestore.getInstance()
var games = db.collection("games")

interface Card {
    var name: String
        get() { return name}
        set(value) {this.name = name}

    var HP: Int
        get() { return HP}
        set(value) {
            this.HP = HP
            for (i in 0 until this.level - 1) {
                upgradeHP()
            }
        }

    var attackDamage: Int
        get() { return attackDamage}
        set(value) {
            this.attackDamage = attackDamage
            for (i in 0 until this.level - 1) {
                upgradeAttackDamage()
            }
        }

    var description: String
        get() { return description }
        set(value) { this.description = description }

    var rarity: String
        get() { return rarity }
        set(value) { this.rarity = rarity }

    var isDisplayingProperties: Boolean
        get() { return isDisplayingProperties }
        set(value) { this.isDisplayingProperties = isDisplayingProperties }

    var level: Int
        get() { return level }
        set(value) {this.level = level }

    var xp: Int
        get() { return xp}
        set(value) { this.xp = xp }

    var xpToLevelUp: Int
        get() { return xpToLevelUp }
        set(value) {
            this.xpToLevelUp = xpToLevelUp
            for (i in 0 until level - 1) {
                this.xpToLevelUp = arrayListOf<Int>(1000, 1400, 1900, 2500, 3200, 4000, 4900, 5900, 7000, 8200, 9500, 11000)[level - 1]
            }
        }

    fun upgradeHP() {
        this.HP += (0.15 * this.HP).toInt()
    }

    fun upgradeAttackDamage() {
        this.attackDamage += (0.15 * this.attackDamage).toInt()
    }

    fun study(gameDocId: String) {
        games.document(gameDocId).get().addOnSuccessListener { document ->
            //
        }

        if (this.level < 12 && this.xp >= this.xpToLevelUp) {
            upgradeHP()
            upgradeAttackDamage()
            this.level = this.level + 1
            this.xp = this.xp - this.xpToLevelUp
            this.xpToLevelUp = arrayListOf<Int>(1000, 1400, 1900, 2500, 3200, 4000, 4900, 5900, 7000, 8200, 9500, 11000)[level - 1]
        }
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

class PersonCard (
    name: String,
    HP: Int,
    attackDamage: Int,
    description: String,
    rarity: String,
    isDisplayingProperties: Boolean,
    level: Int,
    XP: Int,
    XPToLevelUp: Int
) : Card {


    companion object {
        const val isPersonCard = true
    }
}

class NormalCard (
    name: String,
    HP: Int,
    attackDamage: Int,
    description: String,
    rarity: String,
    isDisplayingProperties: Boolean,
    level: Int,
    XP: Int,
    XPToLevelUp: Int
) : Card {
    companion object {
        const val isPersonCard = false
    }
}

class StructureCard (
    name: String,
    HP: Int,
    description: String,
    rarity: String,
    isDisplayingProperties: Boolean,
    level: Int,
    XP: Int,
    XPToLevelUp: Int
) : Card {
    companion object {
        const val isPersonCard = false
    }
}

open class CardView (context: Context, attrs: AttributeSet) : View (context, attrs), Card {
    private fun checkContext() {
        if (context is SecondActivity) {

        } else if (context is ThirdActivity) {

        } else {
            Log.e("CardView.checkContext()", "Context does not match either SecondActivity or ThirdActivity")
        }
    }
}

class PersonCardView (context: Context, attrs: AttributeSet) : CardView (context, attrs) {

}

class NormalCardView (context: Context, attrs: AttributeSet) : CardView (context, attrs) {

}

class StructureCardView (context: Context, attrs: AttributeSet) : CardView (context, attrs) {

}