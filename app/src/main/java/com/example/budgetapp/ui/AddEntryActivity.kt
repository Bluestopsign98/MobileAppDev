package com.example.budgetapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetapp.DatabaseHelper
import com.example.budgetapp.MainActivity
import com.example.budgetapp.R
import kotlinx.android.synthetic.main.activity_add_entry.*
import java.text.SimpleDateFormat
import java.util.*

const val TAG = "AddEntryActivity"

class AddEntryActivity : AppCompatActivity() {

    //To access your database, instantiate your subclass of SQLiteOpenHelper
    private val dbHelper = DatabaseHelper(this)
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_entry)

        // --- Get current date and initialize date input with it
        //Source = https://www.tutorialspoint.com/get-current-time-and-date-on-android#:~:text=Get%20current%20time%20and%20date%20on%20Android%201,code%20to%20src%2FMainActivity.java%20...%20...%20More%20items...%20
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val currentDateandTime = sdf.format(Date())
        date_input_id.setText(currentDateandTime)
        
        
    }

    fun submitEntry(view: View){

        // --- Add new entry to database ---
        try {
            dbHelper.insertData(name_input_id.text.toString(), amount_input_id.text.toString(), date_input_id.text.toString(), desc_input_id.text.toString())
            Log.d(TAG, "submitEntry: success?")
        } catch (e: Exception) {
            Log.e(TAG, "error: $e")
        }


        // --- Return to Overview ---
        val myIntent = Intent(this, MainActivity::class.java)
        startActivity(myIntent)
    }

    fun cancel(view: View){
        // --- Return to Overview ---
        val myIntent = Intent(this, MainActivity::class.java)
        startActivity(myIntent)
    }
}