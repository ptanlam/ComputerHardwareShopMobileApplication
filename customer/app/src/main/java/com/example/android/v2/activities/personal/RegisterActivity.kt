package com.example.android.v2.activities.personal

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android.v2.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*


@Suppress("NAME_SHADOWING")
class RegisterActivity : AppCompatActivity() {

    companion object {
        private var domain_name = "gmail.com"
    }

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val btnSignUp = findViewById<Button>(R.id.btn_sign_up)

        // Set button Login
        btnSignUp.setOnClickListener(View.OnClickListener {
            registerUser()
        })
    }

    private fun registerUser() {
        val username: String = inputUserName.text.toString()
        val phone: String = inputUserPhone.text.toString()
        val email: String = inputUserEmail.text.toString()
        val password: String = inputUserPassword.text.toString()
        val passwordConfirmation: String = inputUserPasswordConfirmation.text.toString()

        if (email.isNotEmpty()
                && password.isNotEmpty()
                && passwordConfirmation.isNotEmpty()) {
            if (isValidDomain(email)) {
                if (password == passwordConfirmation) {
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { it ->
                                if (it.isSuccessful) {
                                    val user = auth.currentUser
                                    user?.sendEmailVerification()!!.addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            Toast.makeText(
                                                    this,
                                                    "Đăng ký thành công",
                                                    Toast.LENGTH_SHORT
                                            ).show()
                                            // add data
                                            val userID = auth.currentUser!!.uid
                                            val documentRef: DocumentReference =
                                                    firestore.collection("users")
                                                            .document(userID)

                                            val user = hashMapOf(
                                                    "avatar" to "",
                                                    "username" to username,
                                                    "phone" to phone,
                                                    "email" to email,
                                                    "password" to password
                                            )

                                            documentRef.set(user).addOnCompleteListener {
                                                if (it.isSuccessful) {
                                                    Toast.makeText(
                                                            this,
                                                            "Tạo profile thành công : $userID",
                                                            Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }.addOnFailureListener(OnFailureListener {
                                                Toast.makeText(
                                                        this,
                                                        "Tạo profile thất bại",
                                                        Toast.LENGTH_SHORT
                                                ).show()
                                            })
                                        }
                                    }
                                }
                            }
                } else {
                    Toast.makeText(
                            this,
                            "Mật khẩu không trùng khớp",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                        this,
                        "Đăng nhập băng Gmail",
                        Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                    this,
                    "Điền đầy đủ thông tin",
                    Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun isValidDomain(email: String): Boolean {
        val domain = email.substring(email.indexOf("@") + 1).toLowerCase()
        return domain == domain_name
    }


}
