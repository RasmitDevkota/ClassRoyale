package com.alientech.classroyale

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.android.synthetic.main.activity_main.*
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class CardsFragment : Fragment(R.layout.fragment_cards) {

    val user = FirebaseAuth.getInstance().currentUser
    var uid = user!!.uid
    var displayName = user!!.displayName.toString()

    val db = FirebaseFirestore.getInstance()
    val mFirebaseAnalytics = FirebaseAnalytics.getInstance(SecondActivity())

    var cardCollection = db.collection("cards").document("cards")
    var normalCards = cardCollection.collection("normal")
    var personCards = cardCollection.collection("person")
    var structureCards = cardCollection.collection("structure")

    var userDecks = db.collection("userDecks").document(displayName)
    lateinit var userDeck: Map<String, Array<Any>>
    var userCardCollection = db.collection("userCollections").document(displayName)
    var userNormalCards = userCardCollection.collection("normal")
    var userPersonCards = userCardCollection.collection("person")
    var userStructureCards = userCardCollection.collection("structure")

    var functions: FirebaseFunctions = FirebaseFunctions.getInstance()

    lateinit var deck: TableLayout
    lateinit var card1: CardView
    lateinit var card2: CardView
    lateinit var card3: CardView
    lateinit var card4: CardView
    lateinit var card5: CardView
    lateinit var card6: CardView
    lateinit var card7: CardView
    lateinit var card8: CardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        card1 = view.findViewById(R.id.card_1)
        card2 = view.findViewById(R.id.card_2)
        card3 = view.findViewById(R.id.card_3)
        card4 = view.findViewById(R.id.card_4)
        card5 = view.findViewById(R.id.card_5)
        card6 = view.findViewById(R.id.card_6)
        card7 = view.findViewById(R.id.card_7)
        card8 = view.findViewById(R.id.card_8)

        val deck = arrayListOf<CardView>(card1, card2, card3, card4, card5, card6, card7, card8)

        for (card in deck) {
            card.setOnClickListener{
                var dialog = OutOfDeckPopup(TODO("Get data from firebase and put here!"))
                Log.d("Cards", "Clicked")
                dialog.show(activity!!.supportFragmentManager, "NoticeDialogFragment")
            }
        }

        userDecks.get().addOnSuccessListener { document ->
            if (document == null || document.data == null) {
                Log.d("Cards", "No deck found, returning to default deck")
                return@addOnSuccessListener
            }

            userDeck = document.data!!["deck1"]!! as Map<String, Array<Any>>

            card1.apply {
                var deckCard = userDeck["card1"] as Array<Any>

                name = deckCard[0] as String
                HP = _HP(deckCard[1] as Int)
                type = deckCard[2] as String
                description = deckCard[3] as String
                rarity = deckCard[4] as String
                level = deckCard[5] as Int
                xp = deckCard[6] as Int
                xpToLevelUp = deckCard[7] as Int

                if (type == "Person" || type == "Normal") {
                    attackDamage = deckCard[8] as Int
                }
            }

            card2.apply {
                var deckCard = userDeck["card2"] as Array<Any>

                name = deckCard[0] as String
                HP = _HP(deckCard[1] as Int)
                type = deckCard[2] as String
                description = deckCard[3] as String
                rarity = deckCard[4] as String
                level = deckCard[5] as Int
                xp = deckCard[6] as Int
                xpToLevelUp = deckCard[7] as Int

                if (type == "Person" || type == "Normal") {
                    attackDamage = deckCard[8] as Int
                }
            }

            card3.apply {
                var deckCard = userDeck["card3"] as Array<Any>

                name = deckCard[0] as String
                HP = _HP(deckCard[1] as Int)
                type = deckCard[2] as String
                description = deckCard[3] as String
                rarity = deckCard[4] as String
                level = deckCard[5] as Int
                xp = deckCard[6] as Int
                xpToLevelUp = deckCard[7] as Int

                if (type == "Person" || type == "Normal") {
                    attackDamage = deckCard[8] as Int
                }
            }

            card4.apply {
                var deckCard = userDeck["card4"] as Array<Any>

                name = deckCard[0] as String
                HP = _HP(deckCard[1] as Int)
                type = deckCard[2] as String
                description = deckCard[3] as String
                rarity = deckCard[4] as String
                level = deckCard[5] as Int
                xp = deckCard[6] as Int
                xpToLevelUp = deckCard[7] as Int

                if (type == "Person" || type == "Normal") {
                    attackDamage = deckCard[8] as Int
                }
            }

            card5.apply {
                var deckCard = userDeck["card5"] as Array<Any>

                name = deckCard[0] as String
                HP = _HP(deckCard[1] as Int)
                type = deckCard[2] as String
                description = deckCard[3] as String
                rarity = deckCard[4] as String
                level = deckCard[5] as Int
                xp = deckCard[6] as Int
                xpToLevelUp = deckCard[7] as Int

                if (type == "Person" || type == "Normal") {
                    attackDamage = deckCard[8] as Int
                }
            }

            card6.apply {
                var deckCard = userDeck["card6"] as Array<Any>

                name = deckCard[0] as String
                HP = _HP(deckCard[1] as Int)
                type = deckCard[2] as String
                description = deckCard[3] as String
                rarity = deckCard[4] as String
                level = deckCard[5] as Int
                xp = deckCard[6] as Int
                xpToLevelUp = deckCard[7] as Int

                if (type == "Person" || type == "Normal") {
                    attackDamage = deckCard[8] as Int
                }
            }

            card7.apply {
                var deckCard = userDeck["card7"] as Array<Any>

                name = deckCard[0] as String
                HP = _HP(deckCard[1] as Int)
                type = deckCard[2] as String
                description = deckCard[3] as String
                rarity = deckCard[4] as String
                level = deckCard[5] as Int
                xp = deckCard[6] as Int
                xpToLevelUp = deckCard[7] as Int

                if (type == "Person" || type == "Normal") {
                    attackDamage = deckCard[8] as Int
                }
            }

            card8.apply {
                var deckCard = userDeck["card8"]  as Array<Any>

                name = deckCard[0] as String
                HP = _HP(deckCard[1] as Int)
                type = deckCard[2] as String
                description = deckCard[3] as String
                rarity = deckCard[4] as String
                level = deckCard[5] as Int
                xp = deckCard[6] as Int
                xpToLevelUp = deckCard[7] as Int

                if (type == "Person" || type == "Normal") {
                    attackDamage = deckCard[8] as Int
                }
            }
        }

//        card1.setOnClickListener{
//            var dialog = OutOfDeckPopup("Insert card properties here!!!!")
//            Log.d("Cards", "Clicked")
//            dialog.show(activity!!.supportFragmentManager, "NoticeDialogFragment")
//        }
//
//        card2.setOnClickListener{
//            var dialog = OutOfDeckPopup("Insert card properties here!!!!")
//            Log.d("Cards", "Clicked")
//            dialog.show(activity!!.supportFragmentManager, "NoticeDialogFragment")
//        }
//
//        card3.setOnClickListener{
//            var dialog = OutOfDeckPopup("Insert card properties here!!!!")
//            Log.d("Cards", "Clicked")
//            dialog.show(activity!!.supportFragmentManager, "NoticeDialogFragment")
//        }
//
//        card4.setOnClickListener{
//            var dialog = OutOfDeckPopup("Insert card properties here!!!!")
//            Log.d("Cards", "Clicked")
//            dialog.show(activity!!.supportFragmentManager, "NoticeDialogFragment")
//        }
//
//        card5.setOnClickListener{
//            var dialog = OutOfDeckPopup("Insert card properties here!!!!")
//            Log.d("Cards", "Clicked")
//            dialog.show(activity!!.supportFragmentManager, "NoticeDialogFragment")
//        }
//
//        card6.setOnClickListener{
//            var dialog = OutOfDeckPopup("Insert card properties here!!!!")
//            Log.d("Cards", "Clicked")
//            dialog.show(activity!!.supportFragmentManager, "NoticeDialogFragment")
//        }
//
//        card7.setOnClickListener{
//            var dialog = OutOfDeckPopup("Insert card properties here!!!!")
//            Log.d("Cards", "Clicked")
//            dialog.show(activity!!.supportFragmentManager, "NoticeDialogFragment")
//        }
//
//        card8.setOnClickListener{
//            var dialog = OutOfDeckPopup("Insert card properties here!!!!")
//            Log.d("Cards", "Clicked")
//            dialog.show(activity!!.supportFragmentManager, "NoticeDialogFragment")
//        }

//        val adapter = OutOfDeckCardsAdapter(activity!!, android.R.layout.activity_list_item, outOfDeckCards)
//        adapter.setDescriptionIds(TODO("Insert stuff here (card image urls?)"))
//
//        this.listView.adapter = adapter
//        this.listView.onItemClickListener = TODO("Sorry I have no clue what goes here tbh")
    }

    fun cardUnlock (cardType: String, cardName: String) {
        var newCard = cardCollection.collection(cardType).document(cardName)
        lateinit var newCardData: DocumentSnapshot
        newCard.get().addOnSuccessListener { documentSnapshot -> newCardData = documentSnapshot }
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

    fun cardData(name: String, type: String) {
        when(type) {
            "normal" -> {
                normalCards.document(name).get().addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d("Cards", "DocumentSnapshot data: ${document.data}")
                    } else {
                        Log.e("Cards", "No such document")
                    }
                }.addOnFailureListener { exception ->
                    Log.e("Cards", "get failed with ", exception)
                }
            }

            "person" -> {
                personCards.document(name).get().addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d("Cards", "DocumentSnapshot data: ${document.data}")
                    } else {
                        Log.e("Cards", "No such document")
                    }
                }.addOnFailureListener { exception ->
                    Log.e("Cards", "get failed with ", exception)
                }
            }
        }
    }

//    lateinit var outOfDeckCards: Array<String>
//
//    fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//        val clicked = outOfDeckCards[position]
//        TODO("Insert dialog stuff idk")
//    }
//
//    class OutOfDeckCardsAdapter(private val ctx: Context, resource: Int, private val outOfDeckCards: Array<String>) : ArrayAdapter<String>(ctx, resource, outOfDeckCards) {
//        private var descriptionIds: Array<String>? = null
//
//        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
//            var view = convertView
//
//            if (convertView == null) {
//                val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//                view = inflater.inflate(android.R.layout.activity_list_item, null)
//            }
//
//            view?.findViewById<ImageView>(android.R.id.icon)?.setImageBitmap(getBitmapFromURL(descriptionIds?.get(position)))
//            view?.findViewById<TextView>(android.R.id.text1)?.text = outOfDeckCards!![position]
//
//            return view
//        }
//
//        fun setDescriptionIds(descriptionIds: Array<String>) {
//            this.descriptionIds = descriptionIds
//        }
//
//        fun getBitmapFromURL(src: String?): Bitmap? {
//            return try {
//                val url = URL(src)
//                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
//                connection.setDoInput(true)
//                connection.connect()
//                val input: InputStream = connection.getInputStream()
//                val myBitmap = BitmapFactory.decodeStream(input)
//                myBitmap
//            } catch (e: Exception) {
//                e.printStackTrace()
//                null
//            }
//        }
//    }
}
