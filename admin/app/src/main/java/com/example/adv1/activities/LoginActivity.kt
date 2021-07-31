package com.example.adv1.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adv1.DOMAIN_NAME
import com.example.adv1.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupFirebaseAuth()

        btnLogin.setOnClickListener {
            // Check whether if all the required fields are filled
            loginProcess()
        }
        hideSoftKeyboard()
    }

    private fun loginProcess() {
        if (inputStaffEmail.text.toString().isNotEmpty() &&
                inputStaffPassword.text.toString().isNotEmpty()) {
            // Check the email is correct (domain name)
            if (isValidDomain(inputStaffEmail.text.toString())) {
                // Show the progress bar
                startProgressing()
                // Get email & password of the current user and then log in
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                        inputStaffEmail.text.toString(), inputStaffPassword.text.toString())
                        .addOnCompleteListener {
                            // Hide the progress bar
                            stopProgressing()
                        }
                        .addOnFailureListener {
                            // Hide the progress bar
                            stopProgressing()
                        }
            } else {
                Toast.makeText(
                        this@LoginActivity,
                        "Bạn phải dùng mail có tên miền của công ty.",
                        Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // Announce that user have to fill all the fields
            Toast.makeText(
                    this@LoginActivity,
                    "Bạn phải điền đầy đủ tất cả các thông tin được yêu cầu.",
                    Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun isValidDomain(email: String): Boolean {
        val domain: String = email.substring(email.indexOf("@") + 1)
                .toLowerCase(Locale.ROOT)
        return domain == DOMAIN_NAME
    }

    private fun startProgressing() {
        progressBar.visibility = View.VISIBLE
    }

    private fun stopProgressing() {
        if (progressBar.visibility == View.VISIBLE) {
            progressBar.visibility = View.INVISIBLE
        }
    }

    private fun hideSoftKeyboard() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    /*
   ---------------------------------------------------- Firebase setup ----------------------------------------------------
   */

    private fun setupFirebaseAuth() {
        // Check whether if user exists in database
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                Toast.makeText(
                        this@LoginActivity,
                        "Đăng nhập với: " + user.email,
                        Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this@LoginActivity, SignedInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener)
    }
}