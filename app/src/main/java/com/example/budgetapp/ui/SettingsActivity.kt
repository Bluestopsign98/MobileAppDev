package com.example.budgetapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetapp.DatabaseHelper
import com.example.budgetapp.MainActivity
import com.example.budgetapp.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_add_entry.*
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "SettingsActivity"

class SettingsActivity : AppCompatActivity() {


    // --- variable to determine if the new entry is and income or an expense
    private var income = false
    private var categorySelected = ""

    private var incomeCategoriesList = ArrayList<String>()
    private var expenseCategoriesList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_entry)

        retrieveCategories()
    }

    private fun retrieveCategories(){
        val sharedPreferences = getSharedPreferences("BudgetApp",MODE_PRIVATE)

        if(sharedPreferences.contains("IncomeCategories")){
            // --- Retrieve the income category list ---
            val incomeCategories = sharedPreferences.getString("IncomeCategories", "").toString()

            // Create an instance of Gson
            val gson = Gson()
            // create an object expression that descends from TypeToken
            // and then get the Java Type from that
            val sType = object : TypeToken<ArrayList<String>>() { }.type
            // provide the type specified above to fromJson() method
            // this will deserialize the previously saved Json into an object of the specified type (e.g., list)
            incomeCategoriesList = gson.fromJson<ArrayList<String>>(incomeCategories, sType)
        }

        if(sharedPreferences.contains("ExpenseCategories")){

            // --- Retrieve the expense category list ---
            val expenseCategories = sharedPreferences.getString("ExpenseCategories", "") ?: ""

            // Create an instance of Gson
            val gson = Gson()
            // create an object expression that descends from TypeToken
            // and then get the Java Type from that
            val sType = object : TypeToken<ArrayList<String>>() { }.type
            // provide the type specified above to fromJson() method
            // this will deserialize the previously saved Json into an object of the specified type (e.g., list)
            expenseCategoriesList = gson.fromJson<ArrayList<String>>(expenseCategories, sType)

        }
    }
}