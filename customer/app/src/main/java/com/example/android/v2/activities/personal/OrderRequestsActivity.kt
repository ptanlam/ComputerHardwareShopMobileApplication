package com.example.android.v2.activities.personal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.android.v2.R
import com.example.android.v2.adapters.OrderRequestsAdapter
import com.example.android.v2.adapters.ProductsAdapter
import com.example.android.v2.data.requests.RequestRepository
import com.example.android.v2.models.Request
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OrderRequestsActivity : AppCompatActivity() {

    private val requestsRepository = RequestRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_requests)
        title = "Đơn hàng của bạn"

        val orderRequestsRecyclerView = findViewById<RecyclerView>(R.id.orderRequestsRecyclerView)
        CoroutineScope(Dispatchers.Main).launch {
            val requests = loadRequests()
            setUpRecyclerProductView(orderRequestsRecyclerView, requests)
        }

    }

    private suspend fun loadRequests(): ArrayList<Request> {
        val requests = ArrayList<Request>()
        val requestsFromDB = requestsRepository.getRequests().get().await()
        requestsFromDB.forEach { request ->
            requests.add(Request(
                    orderID = request.id,
                    total = request.get("total").toString().toInt(),
                    address = request.get("address").toString(),
                    orderedDate = request.get("orderedDate").toString(),
                    quantity = request.get("quantity").toString().toInt(),
                    isDone = request.get("done").toString().toBoolean(),
                    isRejected = request.get("rejected").toString().toBoolean()
            ))
        }
        return requests
    }

    private fun setUpRecyclerProductView(recyclerView: RecyclerView, requests: ArrayList<Request>) {
        val adapter = OrderRequestsAdapter(requests, this)
        recyclerView.adapter = adapter
    }
}