package com.thing.allied

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*

import com.google.firebase.auth.FirebaseAuth
import com.thing.allied.handler.UserHandler

import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppActivity() {

    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        loginBtn.setOnClickListener {

            if (loginInputEmail.text.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "Enter email address!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (loginInputPass.text.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "Enter password!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar!!.visibility = View.VISIBLE

            //authenticate user
            auth!!.signInWithEmailAndPassword(loginInputEmail.text.toString(), loginInputPass.text.toString())
                .addOnCompleteListener(this@LoginActivity) { task ->
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    progressBar!!.visibility = View.GONE
                    if (!task.isSuccessful) {
                        // there was an error
                        if (loginInputPass.text.length < 6) {
                            loginInputPass!!.error = "Enter Password with minimum 6 characters"
                        } else {
                            Log.e(TAG, task.exception!!.message)
                            Toast.makeText(this@LoginActivity, "Authentication Failed", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        UserHandler.updateUserFromAuth(auth!!.uid!!, onUserUpdate = { user ->
                            if (user == null) {
                                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                            } else {
                                finish()
                            }
                        })
                    }
                }
        }

    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
