package com.example.adv1.models

data class Product(var id: String = "", var name: String = "", var category: String = "",
                   var price: Int = 0, var quantity: Int = 0, var imageURL: String? = "")

data class ProductInOrder(var id: String = "", var name: String = "", var price: Int = 0,
                          var category: String = "", var quantity: Int = 0, var total: Int = 0,
                          var imageURL: String = "")
