package com.example.android.v2.data.cart

import android.provider.BaseColumns

object CartDB {

    object ProductEntry : BaseColumns {
        const val TABLE_NAME = "cart"
        const val COLUMN_ID = "_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_PRICE = "price"
        const val COLUMN_QUANTITY = "quantity"
        const val COLUMN_TOTAL = "total"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_IMAGE_URL = "image_url"

        const val CMD_CREATE_TABLE_PRODUCT =
            "CREATE TABLE $TABLE_NAME (" +
                    "$COLUMN_ID NVARCHAR(50) NOT NULL, " +
                    "$COLUMN_NAME TEXT NOT NULL, " +
                    "$COLUMN_PRICE INT NOT NULL, " +
                    "$COLUMN_QUANTITY INT NOT NULL, " +
                    "$COLUMN_TOTAL INT NOT NULL, " +
                    "$COLUMN_CATEGORY NVARCHAR(50) NOT NULL, " +
                    "$COLUMN_IMAGE_URL TEXT NOT NULL)"

        const val CMD_DROP_TABLE_PRODUCT =
            "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}