package com.example.android.v2.activities.personal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android.v2.AUTH
import com.example.android.v2.R
import com.example.android.v2.activities.MainActivity
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


class LoginActivity : AppCompatActivity() {

    companion object {
        private var domain_name = "gmail.com"
    }

    var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        prefs = getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)

        btn_login.setOnClickListener {
            loginUser()
        }

        tvRegister.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        })

    }

    private fun loginUser() {
        if (inputCusGmail.text.toString().isNotEmpty()
                && inputCusPass.text.toString().isNotEmpty()) {
            if (isValidDomain(inputCusGmail.text.toString())) {
                AUTH.signInWithEmailAndPassword(
                        inputCusGmail.text.toString(),
                        inputCusPass.text.toString()
                ).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                                baseContext, "Đăng nhập thành công",
                                Toast.LENGTH_SHORT
                        ).show()
                        val user = AUTH.currentUser
                        updateUI(user)
                    } else {
                        Toast.makeText(
                                baseContext, "Đăng nhập thất bại",
                                Toast.LENGTH_SHORT
                        ).show()
                        updateUI(null)
                    }
                }
            } else {
                Toast.makeText(this, "Đăng nhập bằng Gmail", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidDomain(email: String): Boolean {
        val domain = email.substring(email.indexOf("@") + 1).toLowerCase(Locale.ROOT)
        return domain == domain_name
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = AUTH.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                        baseContext,
                        "Xác nhận bằng email",
                        Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}