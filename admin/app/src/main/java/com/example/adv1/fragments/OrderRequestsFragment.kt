package com.example.adv1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.adv1.R
import com.example.adv1.adapters.OrderRequestsAdapter
import com.example.adv1.models.Request
import com.example.adv1.repositories.Repositories
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class OrderRequestsFragment : Fragment() {

    private val repositories = Repositories()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root =  inflater.inflate(R.layout.fragment_order_requests, container, false)
        val orderRecyclerView = root.findViewById<RecyclerView>(R.id.ordersRecyclerView)
        CoroutineScope(Dispatchers.Main).launch {
            val requests = loadRequests()
            setUpRecyclerView(orderRecyclerView, requests)
        }
        return root
    }

    private suspend fun loadRequests(): ArrayList<Request> {
        val requests = ArrayList<Request>()
        val requestsFromDB =  repositories.fetchRequests()
            .get()
            .await().documents
        val guests = ArrayList<HashMap<String, String>>()
        requestsFromDB.forEach {
            guests.add(loadGuestInformation(guestID = it.id))
        }
        guests.forEach { guest ->
            val userID = guest["id"]
            val username = guest["username"]
            val phoneNumber = guest["phoneNumber"]
            val email = guest["email"]

            val orders = repositories.fetchOrders(userID!!)
                    .whereEqualTo("done", false)
                    .get()
                    .await()
            orders.forEach { order ->
                val orderID = order.id
                val address = order.get("address").toString()
                val totalPrice = order.get("total").toString().toInt()
                val orderedDate = order.get("orderedDate").toString()
                val quantity = order.get("quantity").toString().toInt()
                requests.add(Request(
                        orderID = orderID,
                        guestID = userID,
                        username = username!!,
                        phoneNumber = phoneNumber!!,
                        email = email!!,
                        address = address,
                        total = totalPrice,
                        orderedDate = orderedDate,
                        quantity = quantity
                ))
            }
        }
        return requests
    }

    private suspend fun loadGuestInformation(guestID: String): HashMap<String, String> {
        val info = repositories.fetchUsers().document(guestID).get().await()
        val guestInfo = HashMap<String, String>()
        guestInfo["id"] = guestID
        guestInfo["username"] = info.get("username").toString()
        guestInfo["email"] = info.get("email").toString()
        guestInfo["phoneNumber"] = info.get("phone").toString()
        return guestInfo
    }

    private fun setUpRecyclerView(orderRecyclerView: RecyclerView?,
                                  requests: ArrayList<Request>) {
        val adapter = context?.let { OrderRequestsAdapter(requests, it) }
        orderRecyclerView?.adapter = adapter
    }
}