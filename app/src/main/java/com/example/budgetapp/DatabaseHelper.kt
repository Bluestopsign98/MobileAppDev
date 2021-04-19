package com.example.budgetapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.budgetapp.ui.list.ListFragment

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    /**
     * A companion object allows to hold static fields which are common to all instances of a given
     * class.
     */
    companion object {

        // Database name
        val DATABASE_NAME = "transactions.db"

        // Version number
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1

        // Table(s) name
        val TABLE_NAME = "transactions_table"

        // Column names
        val _ID = "id" // _(underscore indicates primary key), it is a convention
        val NAME = "name"
        val AMOUNT = "amount"
        val DATE = "date"
        val DESCRIPTION = "description"
        val CATEGORY = "category"

    }


    /**
     * onCreate() is called when the database is created for the first time.
     */
    override fun onCreate(db: SQLiteDatabase?) {

        // Create a table
        val SQL_CREATE_TABLE =
            "CREATE TABLE ${TABLE_NAME} (" +
                    "${_ID} INTEGER PRIMARY KEY," +
                    "${NAME} TEXT," +
                    "${AMOUNT} TEXT," +
                    "${DATE} TEXT," +
                    "${DESCRIPTION} TEXT," +
                    "${CATEGORY} TEXT)"
        db?.execSQL(SQL_CREATE_TABLE)
    }

    /**
     * onUpgrade() is called when the database needs to be upgraded.
     * This database is only a cache for online data, so its upgrade policy is
     * to simply to discard the data and start over
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS ${TABLE_NAME}"
        db?.execSQL(SQL_DELETE_TABLE)
        onCreate(db)
    }

    /**
     * our insertData() method allows to insert data to SQLIte database.
     */
    fun insertData(name: String, amount: String, date: String, desc: String = "No description provided", category:String) {

        // Gets the data repository in write mode
        val db = this.writableDatabase

        // Add new data using ContentValues which represents a series of key value pairs
        val contentValues = ContentValues()
        contentValues.put(NAME, name)
        contentValues.put(AMOUNT, amount)
        contentValues.put(DATE, date)
        contentValues.put(DESCRIPTION, desc)
        contentValues.put(CATEGORY, category)
        // Insert the new row
        db.insert(TABLE_NAME, null, contentValues)
    }

    /**
     * our updateData() methods allows to update a row with new field values.
     */
    fun updateData(id: String, name: String, amount: String, date: String, desc: String = "No description provided", category:String): Boolean {

        // Gets the data repository in write mode
        val db = this.writableDatabase

        // New value for one column
        val contentValues = ContentValues()
        contentValues.put(_ID, id)
        contentValues.put(NAME, name)
        contentValues.put(AMOUNT, amount)
        contentValues.put(DATE, date)
        contentValues.put(DESCRIPTION, desc)
        contentValues.put(CATEGORY, desc)
        // Which row to update, based on id
        db.update(TABLE_NAME, contentValues, "ID = ?", arrayOf(id))
        return true
    }

    /**
     * deleteData method allows to delete a given row based on the id
     */
    fun deleteData(id : String) : Int {

        // Gets the data repository in write mode
        val db = this.writableDatabase

        return db.delete(TABLE_NAME,"ID = ?", arrayOf(id))
    }

    /**
     * viewAllData() method queries the database to get all records in the database
     * The below getter property will return a Cursor containing our dataset.
     */
    val viewAllData : Cursor
        get() {
            // Gets the data repository in write mode
            val db = this.writableDatabase
            // Cursor iterates through one row at a time in the results
            val cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null)
            return cursor
        }


    fun getDataByID(id : Int) : Cursor {

        // Gets the data repository in write mode
        val db = this.writableDatabase

        val cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE `ID` = " + id, null)
        return cursor
    }

    fun getDataByCategory(category:String) : Cursor {

        // Gets the data repository in write mode
        val db = this.writableDatabase

        val cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE `CATEGORY` = " + category, null)
        return cursor
    }


}



