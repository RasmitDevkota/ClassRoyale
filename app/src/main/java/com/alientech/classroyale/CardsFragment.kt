package com.alientech.classroyale

import android.os.Bundle
import android.view.View
import android.widget.TableLayout
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import java.lang.reflect.Field

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
    var userCardCollection = db.collection("userCollections").document(displayName)
    var userNormalCards = userCardCollection.collection("normal")
    var userPersonCards = userCardCollection.collection("person")
    var userStructureCards = userCardCollection.collection("structure")

    var functions: FirebaseFunctions = FirebaseFunctions.getInstance()

    lateinit var decks: TableLayout
    lateinit var card1: CardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        decks = view.findViewById(R.id.deck)

        card1 = view.findViewById(R.id.first_card)
    }

}
