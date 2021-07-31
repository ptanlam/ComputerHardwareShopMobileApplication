package com.example.android.v2.data.requests

import com.example.android.v2.AUTH
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class RequestRepository {

    private val firebaseFirestore = FirebaseFirestore.getInstance()

    fun getRequests(): CollectionReference {
        return firebaseFirestore
            .collection("requests")
            .document(AUTH.currentUser!!.uid)
            .collection("orders")
    }

    fun getRequestDetails(orderID: String): CollectionReference {
        return firebaseFirestore
            .collection("requests")
            .document(AUTH.currentUser!!.uid)
            .collection("orders")
            .document(orderID)
            .collection("orderDetails")
    }
}