package com.example.budgetapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.budgetapp.DatabaseHelper
import com.example.budgetapp.MainActivity
import com.example.budgetapp.R
import kotlinx.android.synthetic.main.activity_add_entry.*

private const val TAG = "DetailsActivity"

class EntryDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_details)
        
        val transactionID = intent.getIntExtra("TransactionID", 0)
        Log.d(TAG, "onCreate: $transactionID")

        val dbHelper = DatabaseHelper(this)
        // --- Cursor is used to iterate though the result of the database get call
        val cursor = dbHelper.getDataByID(transactionID)

        cursor.moveToNext()
        var currentTransaction = Transaction(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(4), cursor.getString(3),R.drawable.dollar_sign_symbol)
        Log.d(TAG, "onCreate: $currentTransaction")

        name_details_id.setText(currentTransaction.name)
        amount_details_id.setText(currentTransaction.amount)
        desc_details_id.setText(currentTransaction.description)
        date_details_id.setText(currentTransaction.date)



    }
    




}