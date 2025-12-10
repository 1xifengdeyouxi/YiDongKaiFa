package com.swu.databasetest

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    
    companion object {
        private const val DATABASE_NAME = "BookStore.db"
        private const val DATABASE_VERSION = 1
        
        const val TABLE_NAME = "Book"
        const val COLUMN_ID = "id"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_PRICE = "price"
        const val COLUMN_PAGES = "pages"
        const val COLUMN_NAME = "name"
    }
    
    private val CREATE_BOOK = """
        create table $TABLE_NAME (
            $COLUMN_ID integer primary key autoincrement,
            $COLUMN_AUTHOR text,
            $COLUMN_PRICE real,
            $COLUMN_PAGES integer,
            $COLUMN_NAME text
        )
    """.trimIndent()
    
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_BOOK)
    }
    
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table if exists $TABLE_NAME")
        onCreate(db)
    }
}

