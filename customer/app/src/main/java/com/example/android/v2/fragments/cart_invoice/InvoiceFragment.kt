package com.example.android.v2.fragments.cart_invoice

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
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
import com.example.android.v2.AUTH
import com.example.android.v2.R
import com.example.android.v2.adapters.CartAdapter
import com.example.android.v2.data.cart.CartRepository
import com.example.android.v2.data.cart.DatabaseHelper
import com.example.android.v2.models.Order
import com.example.android.v2.models.product.ProductInCart
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_invoice.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class InvoiceFragment : Fragment() {

    private lateinit var dialog: AlertDialog
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var orderRef: CollectionReference
    private lateinit var cartAdapter: CartAdapter

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_invoice, container, false)
        activity?.title = "Hoá đơn"
        databaseHelper = DatabaseHelper(context, null)

        val cart = CartRepository.fetchCart(databaseHelper)
        val cartRecyclerView = root.findViewById<RecyclerView>(R.id.cartRecyclerView)
        val totalPrice = root.findViewById<TextView>(R.id.txvTotalPrice)
        updateTotalPrice(totalPrice)
        setUpRecyclerCartView(cartRecyclerView, cart, totalPrice)
        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CutPasteId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = view.findViewById<TextView>(R.id.txvUsername)
        val phoneNumber = view.findViewById<TextView>(R.id.txvPhone)
        val email = view.findViewById<TextView>(R.id.txvEmail)
        val currentUser = AUTH.currentUser

        setUpUserInformation(username, phoneNumber, email, currentUser)

        btnBackToCart.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        btnAccept.setOnClickListener {
            if (eTAddress.text.isNullOrEmpty()) {
                Toast.makeText(context, "Vui lòng điền địa chỉ giao hàng!", Toast.LENGTH_SHORT)
                        .show()
            } else {
                showDialogConfirmOrder(currentUser)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDialogConfirmOrder(currentUser: FirebaseUser?) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Xác nhận duyệt đơn đặt hàng?")

        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    confirmOrder(currentUser)
                    Thread.sleep(1000)
                    Toast.makeText(
                            context,
                            "Đã đặt hàng thành công",
                            Toast.LENGTH_SHORT
                    ).show()
                    activity?.supportFragmentManager?.popBackStack()
                }
                DialogInterface.BUTTON_NEGATIVE -> dialog.dismiss()
            }
        }

        builder.setPositiveButton("Có", dialogClickListener)
        builder.setNegativeButton("Không", dialogClickListener)
        dialog = builder.create()
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun confirmOrder(currentUser: FirebaseUser?) {
        orderRef = FirebaseFirestore.getInstance()
                .collection("requests")
        val guestRequestsRef = orderRef.document(currentUser?.uid.toString())
        val userEmail = hashMapOf("email" to currentUser?.email.toString())
        guestRequestsRef.set(userEmail)

        val cart = CartRepository.fetchCart(databaseHelper)
        val priceString = txvTotalPrice.text.toString()
                .split(",")
        var totalPrice = ""
        for (each in priceString) totalPrice += each

        val guestOrdersRef = guestRequestsRef.collection("orders")
                .document()
        val now = LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
        val order = Order(
                userID = currentUser?.uid.toString(),
                total = totalPrice.toInt(),
                quantity = cart.size,
                address = eTAddress.text.toString(),
                orderedDate = now
        )
        guestOrdersRef.set(order)
        order.orderID = guestOrdersRef.id
        guestOrdersRef.update("orderID", guestOrdersRef.id)
        val guestOrderDetailsRef = guestOrdersRef.collection("orderDetails")
        cart.forEach {
            guestOrderDetailsRef.document(it.id).set(it)
            CartRepository.removeProductInCart(databaseHelper, it.id)
        }
    }

    private fun setUpUserInformation(username: TextView, phoneNumber: TextView,
                                     email: TextView, currentUser: FirebaseUser?) {

        val db = Firebase.firestore.collection("users")
                .whereEqualTo("email", currentUser?.email)
        db.get().addOnSuccessListener { result ->
            for (document in result) {
                username.text = document.data["username"].toString()
                phoneNumber.text = document.data["phone"].toString()
                email.text = document.data["email"].toString()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateTotalPrice(totalPrice: TextView) {
        val cart = CartRepository.fetchCart(databaseHelper)

        var total = 0
        for (product in cart) {
            total += product.total
        }

        totalPrice.text =  DecimalFormat("#,###.##").format(total)
    }

    private fun setUpRecyclerCartView(cartRecyclerView: RecyclerView?,
                                      cart: ArrayList<ProductInCart>,
                                      totalPrice: TextView?) {
        val adapter = context?.let { CartAdapter(it, cart, totalPrice) }
        cartRecyclerView?.adapter = adapter
    }
}