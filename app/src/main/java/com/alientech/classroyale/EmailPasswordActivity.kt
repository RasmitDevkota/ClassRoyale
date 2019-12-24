@file:Suppress("UNUSED_VARIABLE", "UNUSED_ANONYMOUS_PARAMETER", "UNUSED_PARAMETER")

package com.alientech.classroyale

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_emailpassword.emailCreateAccountButton
import kotlinx.android.synthetic.main.activity_emailpassword.emailPasswordButtons
import kotlinx.android.synthetic.main.activity_emailpassword.emailPasswordFields
import kotlinx.android.synthetic.main.activity_emailpassword.emailSignInButton
import kotlinx.android.synthetic.main.activity_emailpassword.fieldUsername
import kotlinx.android.synthetic.main.activity_emailpassword.fieldEmail
import kotlinx.android.synthetic.main.activity_emailpassword.fieldPassword
import kotlinx.android.synthetic.main.activity_emailpassword.signOutButton
import kotlinx.android.synthetic.main.activity_emailpassword.signedInButtons
import kotlinx.android.synthetic.main.activity_google.*

class EmailPasswordActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emailpassword)

        emailSignInButton.setOnClickListener(this)
        emailCreateAccountButton.setOnClickListener(this)
        signOutButton.setOnClickListener(this)

        fieldEmail.visibility = View.GONE

        auth = FirebaseAuth.getInstance()
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun createAccount(email: String, password: String, displayName: String) {

        fieldEmail.visibility = View.VISIBLE

        val db = FirebaseFirestore.getInstance()

        Log.d(TAG, "createAccount:$email")

        if (!validateForm()) {
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser

                    var emailsUser = hashMapOf(
                        "email" to email,
                        "uid" to currentUser!!.uid
                    )
                    db.collection("emails").document(currentUser!!.displayName.toString()).set(emailsUser)

                    var usersUser = hashMapOf(
                        "displayName" to displayName,
                        "email" to email
                    )
                    db.collection("users").document().set(usersUser)

                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }

    }

    private fun signIn(password: String, displayName: String) {
        Log.d(TAG, "signIn:$displayName")

        if (!validateForm()) {
            return
        }

        db.collection("users").document(displayName).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    val email = document.get("email").toString()

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "signInWithEmail:success")
                                val user = auth.currentUser
                                updateUI(user)
                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.exception)
                                Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                                updateUI(null)
                            }
                        }
                } else {
                    Log.d(TAG, "No such document")
                    Toast.makeText(baseContext, "User does not exist in our database.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun signOut() {
        auth.signOut()
        updateUI(null)
    }

    private fun validateForm(): Boolean {
        var valid = true

        val displayName = fieldUsername.text.toString()
        if (TextUtils.isEmpty(displayName)) {
            fieldUsername.error = "Required."
            valid = false
        } else {
            fieldUsername.error = null
        }

        val password = fieldPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            fieldPassword.error = "Required."
            valid = false
        } else {
            fieldPassword.error = null
        }

        return valid
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            emailPasswordButtons.visibility = View.GONE
            emailPasswordFields.visibility = View.GONE
            signedInButtons.visibility = View.VISIBLE
        } else {
            emailPasswordButtons.visibility = View.VISIBLE
            emailPasswordFields.visibility = View.VISIBLE
            signedInButtons.visibility = View.GONE
        }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.emailCreateAccountButton -> createAccount(fieldEmail.text.toString(), fieldPassword.text.toString(), fieldUsername.text.toString())
            R.id.emailSignInButton -> signIn(fieldPassword.text.toString(), fieldUsername.text.toString())
            R.id.signOutButton -> signOut()
        }
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}