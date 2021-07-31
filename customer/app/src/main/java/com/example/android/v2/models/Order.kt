package com.example.android.v2.models


data class Order(var orderID: String = "", var userID: String = "", var total: Int = 0,
                 var orderedDate: String = "", var quantity: Int = 0, var address: String,
                 var isDone: Boolean = false, var isRejected: Boolean = false)
