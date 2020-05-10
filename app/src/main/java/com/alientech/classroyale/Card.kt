@file:Suppress("UNUSED_VARIABLE", "UNUSED_ANONYMOUS_PARAMETER", "UNUSED_PARAMETER")

package com.alientech.classroyale

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception
import kotlin.math.ceil


val db = FirebaseFirestore.getInstance()
var games = db.collection("games")

interface Card {

    val name: String
        get() = ""

    val HP: Int
        get() = 0

    val type: String
        get() = ""

    val description: String
        get() = ""

    val rarity: String
        get() = ""

    val attackDamage: Int
        get() = 0

    val level: Int
        get() = 0

    val xp: Int
        get() = 0

    val xpToLevelUp: Int
        get() = 0

    fun _HP(value: Int): Int {
        var result = value
        for (i in 0 until this.level - 1) { // 11 is usually dynamic, just showing a constant for now
            result += ceil((0.15 * result)).toInt()
        }
        return result
    }

    fun _attackDamage(value: Int): Int {
        var result = value
        for (i in 0 until this.level - 1) {
            TODO("This needs to dynamically (increasing by 1 each time, starting at 5) increase the value of result == attackDamage")
        }
        return result
    }

    fun _xpToLevelUp(value: Int): Int {
        var result = value
        for (i in 0 until this.level - 1) {
            TODO("This needs to dynamically (increasing by 100 each time, starting at 400) increase the value of result == xpToLevelUp")
        }
        return result
    }
}

class CardView (context: Context, attrs: AttributeSet) : androidx.cardview.widget.CardView (context, attrs), Card {
    var cardData = context.theme.obtainStyledAttributes(attrs, R.styleable.CardView, 0, 0)

    override var name = ""
    override var HP = 0
    override var type = ""
    override var description = ""
    override var rarity = ""
    override var level = 0
    override var attackDamage = 0
    override var xp = 0
    override var xpToLevelUp = 0

    init {
        setOnClickListener {
            if (context is SecondActivity) {
                Log.d("CardView", "You clicked on a card in the card menu!")
            } else if (context is ThirdActivity) {
                Log.d("CardView", "You clicked on a card in the card menu!")
            } else {
                Log.e("CardView", "Context does not match either SecondActivity or ThirdActivity")
            }
        }

        try {
            name = cardData.getString(R.styleable.CardView_name)
            HP = _HP(cardData.getInteger(R.styleable.CardView_hp, 0))
            type = cardData.getString(R.styleable.CardView_type)
            description = cardData.getString(R.styleable.CardView_description)
            rarity = cardData.getString(R.styleable.CardView_rarity)
            level = cardData.getInteger(R.styleable.CardView_level, 1)
            xp = cardData.getInteger(R.styleable.CardView_xp, 0)
            xpToLevelUp = cardData.getInteger(R.styleable.CardView_xpToLevelUp, 0)

            if (type == "Person" || type == "Normal") {
                attackDamage = cardData.getInteger(R.styleable.CardView_attackDamage, 1)
            }

            Log.d("CardView Data", "$name,$HP,$type,$description,$rarity,$level,$attackDamage,$xp,$xpToLevelUp")
        } finally {
            cardData.recycle()
        }
    }

    fun checkContext(): String? {
        if (context is SecondActivity) {
            Log.d("CardView", "Found in SecondActivity")
            return "SecondActivity"
        } else if (context is ThirdActivity) {
            Log.d("CardView", "Found in ThirdActivity")
            return "ThirdActivity"
        } else {
            Log.e("CardView", "Context does not match either SecondActivity or ThirdActivity")
            return null
        }
    }
}

class OutOfDeckPopup : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;
            builder.setView(inflater.inflate(R.layout.collection_card_popup, null))
                .setPositiveButton("Add To Deck",
                    DialogInterface.OnClickListener { dialog, id ->
                        Log.d("OutOfDeckPopup", "Something's fishy about this...")
                    })
                .setNegativeButton("Close",
                    DialogInterface.OnClickListener { dialog, id ->
                        getDialog()?.cancel()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private lateinit var listener: NoticeDialogListener

    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as NoticeDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement NoticeDialogListener"))
        }
    }
}