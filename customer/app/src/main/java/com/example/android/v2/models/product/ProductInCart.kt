package com.example.android.v2.models.product

data class ProductInCart(var id: String = "", var name: String = "", var price: Int = 0,
                         val category: String = "", var quantity: Int = 0, var total: Int = 0,
                         var imageURL: String = "")