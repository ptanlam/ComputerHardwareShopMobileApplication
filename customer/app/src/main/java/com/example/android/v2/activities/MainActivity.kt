package com.example.android.v2.activities


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.android.v2.AUTH
import com.example.android.v2.R
import com.example.android.v2.fragments.CategoriesFragment
import com.example.android.v2.fragments.HomeFragment
import com.example.android.v2.fragments.personal.AccountFragment
import com.example.android.v2.fragments.personal.signedInFragment.SignedInFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var prefs: SharedPreferences? = null
    private val homeFragment = HomeFragment()
    private val categoriesFragment = CategoriesFragment()
    private val accountFragment = AccountFragment()
    private val signedInFragment = SignedInFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "Trang chủ"
        openFragment(homeFragment)

        prefs = getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        val allProducts = intent?.getArrayLis


        fABCart.setOnClickListener {
            startActivity(Intent(this, CartInvoiceActivity::class.java))
        }

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    supportActionBar?.title = "Trang chủ"
                    openFragment(homeFragment)
                }
                R.id.categoriesFragment -> {
                    supportActionBar?.title = "Danh mục"
                    openFragment(categoriesFragment)
                }
                R.id.accountFragment -> {
                    supportActionBar?.title = "Thông tin cá nhân"
                    if (AUTH.currentUser?.isEmailVerified == true) {
                        openFragment(signedInFragment)
                    } else {
                        openFragment(accountFragment)
                    }
                }
            }
            true
        }
    }

    private fun openFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply {
        replace(R.id.container, fragment)
        commit()
    }
}