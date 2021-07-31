package com.example.android.v2.fragments.cart_invoice

import android.content.Intent
import android.icu.text.DecimalFormat
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.android.v2.R
import com.example.android.v2.activities.personal.LoginActivity
import com.example.android.v2.adapters.CartAdapter
import com.example.android.v2.data.cart.CartRepository
import com.example.android.v2.data.cart.DatabaseHelper
import com.example.android.v2.models.product.ProductInCart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_cart.*

class CartFragment : Fragment() {

    lateinit var databaseHelper: DatabaseHelper
    private val  invoiceFragment = InvoiceFragment()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_cart, container, false)
        activity?.title = "Giỏ hàng"
        databaseHelper = DatabaseHelper(context, null)

        val cart = CartRepository.fetchCart(databaseHelper)
        val cartRecyclerView = root.findViewById<RecyclerView>(R.id.cartRecyclerView)
        val totalPrice = root.findViewById<TextView>(R.id.txvTotalPrice)
        setUpRecyclerCartView(cartRecyclerView, cart, totalPrice)
        updateTotalPrice(totalPrice)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnShowInvoice.setOnClickListener {
            when {
                checkAuthenticationState() == null -> {
                    Toast.makeText(context, "Vui lòng đăng nhập tài khoản", Toast.LENGTH_SHORT)
                            .show()
                    startActivity(Intent(context, LoginActivity::class.java))
                }
                cartRecyclerView.adapter?.itemCount == 0 -> {
                    Toast.makeText(context, "Giỏ hàng rỗng!", Toast.LENGTH_SHORT)
                            .show()
                }
                else -> {
                    activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.cartInvoiceContainer, invoiceFragment, invoiceFragment.tag)
                            ?.addToBackStack("Cart")
                            ?.commit()
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateTotalPrice(totalPrice: TextView) {
        val cart = CartRepository.fetchCart(databaseHelper)
        var total = 0;
        for (product in cart) {
            total += product.total
        }
        totalPrice.text =  DecimalFormat("#,###.##").format(total)
    }

    private fun setUpRecyclerCartView(cartRecyclerView: RecyclerView?,
                                      cart: ArrayList<ProductInCart>,
                                      totalPrice: TextView) {
        val adapter = context?.let { CartAdapter(it, cart, totalPrice) }
        cartRecyclerView?.adapter = adapter
    }

    private fun checkAuthenticationState(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }
}