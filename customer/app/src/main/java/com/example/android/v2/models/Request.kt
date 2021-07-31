package com.example.android.v2.models

data class Request(var orderID: String = "", var total: Int = 0, var orderedDate: String = "",
                   var quantity: Int = 0, var address: String, var isDone: Boolean = false,
                   var isRejected: Boolean = false)