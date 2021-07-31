package com.example.adv1.repositories

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class Repositories {

    private val firestore = FirebaseFirestore.getInstance()

    fun fetchProducts(): CollectionReference {
        return firestore
            .collection("products")
    }

    fun fetchRequests(): CollectionReference {
        return firestore
            .collection("requests")
    }

    fun fetchOrders(guestID: String): CollectionReference {
        return firestore
            .collection("requests")
            .document(guestID)
            .collection("orders")
    }

    fun fetchOrderDetails(orderID: String, guestID: String): CollectionReference {
        return fetchOrders(guestID)
            .document(orderID)
            .collection("orderDetails")
    }

    fun fetchUsers(): CollectionReference {
        return firestore
            .collection("users")
    }
}