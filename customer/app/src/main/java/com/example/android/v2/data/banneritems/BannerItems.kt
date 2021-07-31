package com.example.android.v2.data.banneritems

import com.example.android.v2.R

// Define a custom data class of banner's items
data class BannerItem(var image: Int)

object BannerItems {

    // Get all the images from the drawable resource
    private val images = intArrayOf(
        R.drawable.banner1,
        R.drawable.banner2,
        R.drawable.banner3,
        R.drawable.banner4
    )

    // Publish these images via a list of images
    var list: ArrayList<BannerItem>? = null
        get() {

            if (field != null)
                return field

            field = ArrayList()
            for (i in images.indices) {

                val image = images[i]
                val item = BannerItem(image)
                field!!.add(item)
            }

            return field
        }
}