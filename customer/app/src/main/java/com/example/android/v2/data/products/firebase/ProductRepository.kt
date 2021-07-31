package com.example.android.v2.data.products.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ProductRepository() {

    private val firebaseFirestore = FirebaseFirestore.getInstance()

    fun getProductsList(categoryName: String): Query {
        return firebaseFirestore
                .collection("products")
                .document(categoryName)
                .collection(categoryName)
                .orderBy("quantity", Query.Direction.DESCENDING)
                .whereGreaterThan("quantity", 0)
    }
}