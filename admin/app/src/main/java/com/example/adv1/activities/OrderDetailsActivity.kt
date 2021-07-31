package com.example.adv1.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.adv1.R
import com.example.adv1.adapters.OrderDetailsAdapter
import com.example.adv1.models.ProductInOrder
import com.example.adv1.repositories.Repositories
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OrderDetailsActivity : AppCompatActivity() {

    private val repository = Repositories()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Adv1_2)
        setContentView(R.layout.activity_order_details)
        title = "Chi tiết đơn hàng"

        val orderID = intent.getStringExtra("orderID")
        val guestID = intent.getStringExtra("guestID")
        val orderDetailsRecyclerView = findViewById<RecyclerView>(R.id.orderDetailsRecyclerView)
        CoroutineScope(Dispatchers.Main).launch {
            val cart = loadOrderDetails(orderID, guestID)
            setUpRecyclerCartView(orderDetailsRecyclerView, cart)
        }
    }

    suspend fun loadOrderDetails(orderID: String?, guestID: String?):
            ArrayList<ProductInOrder> {
        val orderDetails = ArrayList<ProductInOrder>()
        val productsInOrder = repository
            .fetchOrderDetails(orderID = orderID!!, guestID = guestID!!)
            .get().await().documents
        
        productsInOrder.forEach { product ->
            orderDetails.add(
                ProductInOrder(
                    id = product.id,
                    name = product.get("name").toString(),
                    price = product.get("price").toString().toInt(),
                    quantity = product.get("quantity").toString().toInt(),
                    total = product.get("total").toString().toInt(),
                    category = product.get("category").toString(),
                    imageURL = product.get("imageURL").toString()
            ))
        }

        return orderDetails
    }

    private fun setUpRecyclerCartView(orderDetailsRecyclerView: RecyclerView,
                                      cart: ArrayList<ProductInOrder>) {
        val adapter = OrderDetailsAdapter(cart)
        orderDetailsRecyclerView.adapter = adapter
    }
}