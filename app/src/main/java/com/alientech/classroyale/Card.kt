@file:Suppress("UNUSED_VARIABLE", "UNUSED_ANONYMOUS_PARAMETER", "UNUSED_PARAMETER")

package com.alientech.classroyale

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_cards.view.*
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.ceil

val user = FirebaseAuth.getInstance().currentUser
val db = FirebaseFirestore.getInstance()
val storage = FirebaseStorage.getInstance()
val storageRef = storage.reference
val storageUrl = "https://firebasestorage.googleapis.com/v0/b/class-royale.appspot.com/o"

interface Card {
    val name: String
        get() = ""

    val hp: Int
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

    fun _hp(value: Int): Int {
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

class CardView(context: Context, attributeSet: AttributeSet? = null) : androidx.cardview.widget.CardView(context, attributeSet), Card {

    override var name = ""
    override var hp = 0
    override var type = ""
    override var description = ""
    override var rarity = ""
    override var level = 0
    override var attackDamage = 0
    override var xp = 0
    override var xpToLevelUp = 0

    fun setData(cardData: MutableMap<String, Any>) {
        try {
            name = cardData["name"] as String
            hp = (cardData["hp"] as Long).toInt()
            type = cardData["type"] as String
            description = cardData["description"] as String
            rarity = cardData["rarity"] as String
            level = (cardData["level"] as Long).toInt()
            xp = (cardData["xp"] as Long).toInt()
            xpToLevelUp = (cardData["xpToLevelUp"] as Long).toInt()

            if (type == "Person" || type == "Normal") {
                attackDamage = (cardData["attackDamage"] as Long).toInt()
            }

            this.addView(ImageView(context).also { tag = name })
        } finally {
            Log.d("CardView Data", "$name,$hp,$type,$description,$rarity,$level,$attackDamage,$xp,$xpToLevelUp")
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

    override fun addView(child: View?) {
        super.addView(child)
    }
}

class InDeckPopup(texts: String) : DialogFragment() {
    var textt = texts

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            builder.setView(inflater.inflate(R.layout.collection_card_popup, null))
                .setPositiveButton("Remove From Deck") { dialog, id ->
                    Log.d("InDeckPopup", "Something's fishy about this...")
                }
                .setNegativeButton("Close") { dialog, id ->
                    Log.d("InDeckPopup", "Something's fishy about this...")

                    getDialog()?.cancel()
                }
                .setTitle("Card Details")
                .setMessage(textt)
                .setIcon(R.drawable.ic_content_copy_black_24dp)
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private lateinit var listener: NoticeDialogListener

    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = context as NoticeDialogListener
    }
}

class OutOfDeckPopup(texts: String) : DialogFragment() {
    var textt = texts

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            builder.setView(inflater.inflate(R.layout.collection_card_popup, null))
                .setPositiveButton("Add To Deck") { dialog, id ->
                    Log.d("OutOfDeckPopup", "Something's fishy about this...")
                }
                .setNegativeButton("Close") { dialog, id ->
                    Log.d("OutOfDeckPopup", "Something's fishy about this...")

                    getDialog()?.cancel()
                }
                .setTitle("Card Details")
                .setMessage(textt)
                .setIcon(R.drawable.ic_content_copy_black_24dp)
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private lateinit var listener: NoticeDialogListener

    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = context as NoticeDialogListener
    }
}

class OutOfDeckCardsAdapter(private val ctx: Context, resource: Int, private val outOfDeckCards: Array<CardView>, private val types: Array<String>) : ArrayAdapter<CardView>(ctx, resource, outOfDeckCards) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var view = convertView

        if (convertView == null) {
            val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.out_of_deck_cards_list, parent, false)
        }

//        TODO("When setting the 'parent' parameter of 'inflater.inflate' as the 'parent' parameter of 'view', 'view' is apparently null, find out how the 'convertView' parameter is defined and why it's null")
//        TODO("When setting the 'parent' parameter of 'inflater.inflate' as the 'parent' parameter of 'getView', why is 'addView' not supported in 'AdapterView?'")

        db.document("cards/cards/${types[position].toLowerCase()}/outOfDeckCards[position]").get().addOnSuccessListener { document ->
            val data = document.data!!

            Log.d("OutOfDeckCardsAdapter", "$data")

            val cardView = CardView(ctx)
            cardView.setData(data)
            view?.outOfDeckCardsList!!.addView(cardView)

            val thumbnailRef = data["storageRef"].toString()

            Log.d("OutOfDeckCardsAdapter", "${types[position]}, ${outOfDeckCards[position]}, $thumbnailRef")

            val storagePath = "$storageUrl/card_thumbnails%2F${outOfDeckCards[position]}?alt=media"

            val imageView = ImageView(ctx)
            imageView.layoutParams = view.layoutParams
            (view as CardView).addView(imageView)

            BitmapFromURL().execute(listOf(storagePath, imageView))
        }

        view?.findViewById<TextView>(android.R.id.text1)?.text = outOfDeckCards[position].name

        return view
    }

    private open class BitmapFromURL : AsyncTask<List<Any>, Int?, Bitmap?>() {
        override fun doInBackground(vararg params: List<Any>): Bitmap? {
            return try {
                val url = URL(params[0][0] as String)
                val imageView = params[0][1] as ImageView

                Log.d("BitmapFromURL", params[0][0] as String)

                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

                connection.doInput = true
                connection.connect()

                val input: InputStream = connection.inputStream
                val myBitmap = BitmapFactory.decodeStream(input)

                imageView.setImageBitmap(myBitmap)

                Log.d("BitmapFromURL", "returned")

                myBitmap
            } catch (e: Exception) {
                e.printStackTrace()

                null
            }
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate()
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
        }
    }
}