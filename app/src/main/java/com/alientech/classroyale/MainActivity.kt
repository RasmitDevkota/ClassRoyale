package com.alientech.classroyale

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


// Activity for the Login Screen
class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = MyArrayAdapter(this, android.R.layout.simple_list_item_2, CLASSES as Array<Class<*>>)
        adapter.setDescriptionIds(DESCRIPTION_IDS)

        this.listView.adapter = adapter
        this.listView.onItemClickListener = this

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val clicked = CLASSES[position]
        startActivity(Intent(this, clicked))
    }

    class MyArrayAdapter(
        private val ctx: Context,
        resource: Int,
        private val classes: Array<Class<*>>
    ) :
        ArrayAdapter<Class<*>>(ctx, resource, classes) {
        private var descriptionIds: IntArray? = null

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            var view = convertView

            if (convertView == null) {
                val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                view = inflater.inflate(android.R.layout.simple_list_item_2, null)
            }

            view?.findViewById<TextView>(android.R.id.text1)?.text = classes[position].simpleName
            view?.findViewById<TextView>(android.R.id.text2)?.setText(descriptionIds!![position])

            return view
        }

        fun setDescriptionIds(descriptionIds: IntArray) {
            this.descriptionIds = descriptionIds
        }
    }

    companion object {
        private val CLASSES = arrayOf(
            GoogleSignInActivity::class.java,
            EmailPasswordActivity::class.java
        )
        private val DESCRIPTION_IDS = intArrayOf(
            R.string.desc_google_sign_in,
            R.string.desc_emailpassword
        )
    }

    fun getUserDetails() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val name = user.displayName
            val email = user.email
            val uid = user.uid
        }
    }
}