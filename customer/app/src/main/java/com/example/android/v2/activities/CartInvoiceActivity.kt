package com.example.android.v2.activities

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.android.v2.R
import com.example.android.v2.fragments.cart_invoice.CartFragment
import com.example.android.v2.fragments.cart_invoice.InvoiceFragment

class CartInvoiceActivity : AppCompatActivity() {

    private val cartFragment = CartFragment()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_invoice)
        openFragment(cartFragment)
    }

    private fun openFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply {
        replace(R.id.cartInvoiceContainer, fragment)
        commit()
    }
}