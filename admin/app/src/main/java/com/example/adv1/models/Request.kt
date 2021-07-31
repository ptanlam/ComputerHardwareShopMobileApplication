package com.example.adv1.models

data class Request(var orderID: String = "", var guestID: String = "", var username: String = "",
                   var email: String = "", var phoneNumber: String = "", var total: Int = 0,
                   var orderedDate: String = "", var quantity: Int = 0, var address: String,
                   var isDone: Boolean = false)

