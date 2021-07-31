package com.example.android.v2.activities.personal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.android.v2.R
import com.example.android.v2.adapters.RequestDetailsAdapter
import com.example.android.v2.data.requests.RequestRepository
import com.example.android.v2.models.product.ProductInCart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RequestDetailsActivity : AppCompatActivity() {

    private val requestRepository = RequestRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_details)
        title = "Chi tiết đơn hàng"

        val orderID = intent.getStringExtra("orderID")
        val requestDetailsRecyclerView = findViewById<RecyclerView>(R.id.requestDetailsRecyclerView)
        CoroutineScope(Dispatchers.Main).launch {
            val requestDetails = loadRequestDetails(orderID!!)
            val adapter = RequestDetailsAdapter(requestDetails)
            requestDetailsRecyclerView.adapter = adapter
        }
    }

    private suspend fun loadRequestDetails(orderID: String) : ArrayList<ProductInCart> {
        val requestDetails = ArrayList<ProductInCart>()

        val cart = requestRepository.getRequestDetails(orderID)
                .get().await()
        cart.forEach { product ->
            requestDetails.add(ProductInCart(
                    id = product.get("id").toString(),
                    imageURL = product.get("imageURL").toString(),
                    name = product.get("name").toString(),
                    price = product.get("price").toString().toInt(),
                    quantity = product.get("quantity").toString().toInt(),
                    total = product.get("total").toString().toInt()
            ))
        }

        return requestDetails
    }
}