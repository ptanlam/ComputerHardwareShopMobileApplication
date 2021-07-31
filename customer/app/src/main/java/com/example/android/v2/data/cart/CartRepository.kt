package com.example.android.v2.data.cart

import android.content.ContentValues
import com.example.android.v2.models.product.ProductInCart

object CartRepository {

    fun fetchCart(databaseHelper: DatabaseHelper): ArrayList<ProductInCart> {
        val cart = ArrayList<ProductInCart>()

        val db = databaseHelper.readableDatabase
        val columns = arrayOf(
                CartDB.ProductEntry.COLUMN_ID,
                CartDB.ProductEntry.COLUMN_NAME,
                CartDB.ProductEntry.COLUMN_PRICE,
                CartDB.ProductEntry.COLUMN_QUANTITY,
                CartDB.ProductEntry.COLUMN_TOTAL,
                CartDB.ProductEntry.COLUMN_CATEGORY,
                CartDB.ProductEntry.COLUMN_IMAGE_URL,
        )
        val cursor = db.query(CartDB.ProductEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
        )
        val idPos = cursor.getColumnIndex(CartDB.ProductEntry.COLUMN_ID)
        val namePos = cursor.getColumnIndex(CartDB.ProductEntry.COLUMN_NAME)
        val pricePos = cursor.getColumnIndex(CartDB.ProductEntry.COLUMN_PRICE)
        val quantityPos = cursor.getColumnIndex(CartDB.ProductEntry.COLUMN_QUANTITY)
        val totalPos = cursor.getColumnIndex(CartDB.ProductEntry.COLUMN_TOTAL)
        val categoryPos = cursor.getColumnIndex(CartDB.ProductEntry.COLUMN_CATEGORY)
        val imageURLPos = cursor.getColumnIndex(CartDB.ProductEntry.COLUMN_IMAGE_URL)

        while (cursor.moveToNext()) {
            val id = cursor.getString(idPos)
            val name = cursor.getString(namePos)
            val price = cursor.getString(pricePos).toInt()
            val quantity = cursor.getString(quantityPos).toInt()
            val total = cursor.getString(totalPos).toInt()
            val category = cursor.getString(categoryPos)
            val imageURL = cursor.getString(imageURLPos)

            cart.add(ProductInCart(id = id, category = category, name = name, price = price,
                    quantity = quantity, total = total, imageURL = imageURL))
        }

        cursor.close()
        return cart
    }

    fun fetchProductInCart(databaseHelper: DatabaseHelper, productID: String): ProductInCart? {
        val db = databaseHelper.readableDatabase
        var productInCart: ProductInCart? = null

        val columns = arrayOf(
                CartDB.ProductEntry.COLUMN_NAME,
                CartDB.ProductEntry.COLUMN_PRICE,
                CartDB.ProductEntry.COLUMN_QUANTITY,
                CartDB.ProductEntry.COLUMN_TOTAL,
                CartDB.ProductEntry.COLUMN_CATEGORY,
                CartDB.ProductEntry.COLUMN_IMAGE_URL,
        )

        val selection = "${CartDB.ProductEntry.COLUMN_ID} LIKE ? "
        val selectionArgs = arrayOf(productID)

        val cursor = db.query(CartDB.ProductEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        )
        val namePos = cursor.getColumnIndex(CartDB.ProductEntry.COLUMN_NAME)
        val pricePos = cursor.getColumnIndex(CartDB.ProductEntry.COLUMN_PRICE)
        val quantityPos = cursor.getColumnIndex(CartDB.ProductEntry.COLUMN_QUANTITY)
        val totalPos = cursor.getColumnIndex(CartDB.ProductEntry.COLUMN_TOTAL)
        val categoryPos = cursor.getColumnIndex(CartDB.ProductEntry.COLUMN_CATEGORY)
        val imageURLPos = cursor.getColumnIndex(CartDB.ProductEntry.COLUMN_IMAGE_URL)

        while (cursor.moveToNext()) {
            val name = cursor.getString(namePos)
            val price = cursor.getString(pricePos).toInt()
            val quantity = cursor.getString(quantityPos).toInt()
            val total = cursor.getString(totalPos).toInt()
            val category = cursor.getString(categoryPos)
            val imageURL = cursor.getString(imageURLPos)

            productInCart = ProductInCart(
                    id = productID,
                    category = category,
                    name = name,
                    price = price,
                    quantity = quantity,
                    total = total,
                    imageURL = imageURL
            )
        }
        cursor.close()
        return productInCart
    }

    fun updateProductInCart(databaseHelper: DatabaseHelper, product: ProductInCart) {
        val db = databaseHelper.writableDatabase

        val values = ContentValues()
        values.put(CartDB.ProductEntry.COLUMN_NAME, product.name)
        values.put(CartDB.ProductEntry.COLUMN_PRICE, product.price)
        values.put(CartDB.ProductEntry.COLUMN_QUANTITY, product.quantity)
        values.put(CartDB.ProductEntry.COLUMN_TOTAL, product.total)
        values.put(CartDB.ProductEntry.COLUMN_CATEGORY, product.category)
        values.put(CartDB.ProductEntry.COLUMN_IMAGE_URL, product.imageURL)

        val selection = "${CartDB.ProductEntry.COLUMN_ID} LIKE ? "
        val selectionArgs = arrayOf(product.id)

        db.update(
                CartDB.ProductEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        )
    }

    fun removeProductInCart(databaseHelper: DatabaseHelper, productID: String): Int {
        val db = databaseHelper.writableDatabase

        val selection = "${CartDB.ProductEntry.COLUMN_ID} LIKE ? "
        val selectionArgs = arrayOf(productID)

        return db.delete(CartDB.ProductEntry.TABLE_NAME, selection, selectionArgs)
    }
}