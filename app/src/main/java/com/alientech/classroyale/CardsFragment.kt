package com.alientech.classroyale

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_cards.*
import kotlinx.android.synthetic.main.fragment_cards.view.*


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

    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference

    lateinit var card1: CardView
    lateinit var card2: CardView
    lateinit var card3: CardView
    lateinit var card4: CardView
    lateinit var card5: CardView
    lateinit var card6: CardView
    lateinit var card7: CardView
    lateinit var card8: CardView

    var outOfDeckCards: Array<CardView> = arrayOf()
    var cardTypes: Array<String> = arrayOf("Person", "Structure", "Person", "Person", "Person", "Person", "Person", "Person")

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

        val deck = arrayListOf(card1, card2, card3, card4, card5, card6, card7, card8)

        for (card in deck) {
            card.setOnClickListener{
                val dialog = InDeckPopup("Hello! I will fix this soon")
                Log.d("Cards", "Clicked")
                dialog.show(activity!!.supportFragmentManager, "NoticeDialogFragment")
            }
        }

        userDecks.get().addOnSuccessListener { document ->
            if (document == null || document.data == null) {
                Log.d("Cards", "No deck found, returning to default deck")
                // Generate default deck/set it up in FS
                return@addOnSuccessListener
            }

            userDeck = document.data!!["deck1"]!! as Map<String, Array<Any>>

            for (c in 0..deck.size) {
                deck[c].apply {
                    val deckCard = userDeck["card$c"] as Array<Any>

                    name = deckCard[0] as String
                    hp = _hp(deckCard[1] as Int)
                    type = deckCard[2] as String
                    description = deckCard[3] as String
                    rarity = deckCard[4] as String
                    level = deckCard[5] as Int
                    xp = deckCard[6] as Int
                    xpToLevelUp = deckCard[7] as Int

                    if (type == "Person" || type == "Normal") {
                        attackDamage = deckCard[c] as Int
                    }

                    // TODO("SET DATA")
                }
            }

            for (i in 1..8) {
                outOfDeckCards[i - 1] = deck[i - 1]
            }
        }

        val adapter = OutOfDeckCardsAdapter(activity!!, android.R.layout.activity_list_item, outOfDeckCards, cardTypes)

        this.outOfDeckCardsList.adapter = adapter
    }

    fun cardUnlock(cardType: String, cardName: String) {
        val newCard = cardCollection.collection(cardType).document(cardName)
        lateinit var newCardData: DocumentSnapshot
        newCard.get().addOnSuccessListener { documentSnapshot -> newCardData = documentSnapshot }

        val newCardDestination = userCardCollection.collection(cardType)
        newCardDestination.document(cardName).set(newCardData)
    }

    fun editDeck(deckNumber: String, addCardName: String, addCardType: String,  removeCardName: String, removeCardType: String) {
        val addCard = userCardCollection.collection(addCardType).document(addCardName)
        lateinit var addCardData: DocumentSnapshot
        addCard.get().addOnSuccessListener { documentSnapshot -> addCardData = documentSnapshot }

        userDecks.collection(deckNumber).document(removeCardName).delete()

        val addCardDeck = userDecks.collection(deckNumber)
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
}