package com.example.android.v2.data.cart

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context?, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CartDB.ProductEntry.CMD_CREATE_TABLE_PRODUCT)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(CartDB.ProductEntry.CMD_DROP_TABLE_PRODUCT)
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "cart.db"
        const val DATABASE_VERSION = 2
    }

}