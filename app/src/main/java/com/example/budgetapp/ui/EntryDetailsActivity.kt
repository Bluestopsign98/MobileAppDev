package com.example.budgetapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.budgetapp.DatabaseHelper
import com.example.budgetapp.MainActivity
import com.example.budgetapp.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_add_entry.*
import kotlinx.android.synthetic.main.activity_add_entry.amount_details_id
import kotlinx.android.synthetic.main.activity_add_entry.date_details_id
import kotlinx.android.synthetic.main.activity_add_entry.desc_details_id
import kotlinx.android.synthetic.main.activity_add_entry.name_details_id
import kotlinx.android.synthetic.main.activity_entry_details.*
import java.util.ArrayList

private const val TAG = "DetailsActivity"

class EntryDetailsActivity : AppCompatActivity() {

    // val dbHelper = DatabaseHelper(this)
    var categorySelected = ""
    var income = false
    private var incomeCategoriesList = ArrayList<String>()
    private var expenseCategoriesList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_details)

        val transactionID = intent.getIntExtra("TransactionID", 0)
        Log.d(TAG, "onCreate: $transactionID")


        // --- Cursor is used to iterate though the result of the database get call
//        val cursor = dbHelper.getDataByID(transactionID)
//
//        cursor.moveToNext()
//        var currentTransaction = Transaction(
//            cursor.getInt(0),
//            cursor.getString(1),
//            cursor.getFloat(2),
//            cursor.getString(4),
//            cursor.getString(3),
//            cursor.getString(5),
//            cursor.getInt(6) > 0,
//            R.drawable.dollar_sign_symbol
//        )
//
//        cursor.close()
//        dbHelper.close()


//        Log.d(TAG, "onCreate: $currentTransaction")
//
//        name_details_id.setText(currentTransaction.name)
//        amount_details_id.setText(currentTransaction.amount.toString())
//        desc_details_id.setText(currentTransaction.description)
//        date_details_id.setText(currentTransaction.date)
//
//        categorySelected = currentTransaction.category
//        income = currentTransaction.income

        retrieveCategories()

        configureSpinner()

        var i = 0
        if(income){
            while (i < incomeCategoriesList.size){
                if(incomeCategoriesList[i] == categorySelected){
                    category_details_id.setSelection(i)
                }
            }
        }else{
            while (i < expenseCategoriesList.size){
                if(expenseCategoriesList[i] == categorySelected){
                    category_details_id.setSelection(i)
                }
            }
        }

        //category_details_id.setSelection() = categorySelected

    }

    fun submitEntry(view: View) {

        var amount = amount_details_id.text.toString().toFloat()
        if(income){
        }else{
            amount *= -1
        }

        // --- Add new entry to database ---
        try {
//            dbHelper.insertData(
//                name_details_id.text.toString(),
//                amount_details_id.text.toString().toFloat(),
//                date_details_id.text.toString(),
//                desc_details_id.text.toString(),
//                categorySelected,
//                income
//            )
        } catch (e: Exception) {
            Log.e(TAG, "error: $e")
        }

       // dbHelper.close()

        // --- Return to Overview ---
        val myIntent = Intent(this, MainActivity::class.java)
        startActivity(myIntent)
    }

    fun cancel(view: View) {

       // dbHelper.close()

        // --- Return to Overview ---
        val myIntent = Intent(this, MainActivity::class.java)
        startActivity(myIntent)
    }


    private fun retrieveCategories() {
        val sharedPreferences = getSharedPreferences("BudgetApp", MODE_PRIVATE)

        if (sharedPreferences.contains("IncomeCategories")) {
            // --- Retrieve the income category list ---
            val incomeCategories = sharedPreferences.getString("IncomeCategories", "").toString()

            // Create an instance of Gson
            val gson = Gson()
            // create an object expression that descends from TypeToken
            // and then get the Java Type from that
            val sType = object : TypeToken<ArrayList<String>>() {}.type
            // provide the type specified above to fromJson() method
            // this will deserialize the previously saved Json into an object of the specified type (e.g., list)
            incomeCategoriesList = gson.fromJson<ArrayList<String>>(incomeCategories, sType)
        }

        if (sharedPreferences.contains("ExpenseCategories")) {

            // --- Retrieve the expense category list ---
            val expenseCategories = sharedPreferences.getString("ExpenseCategories", "") ?: ""

            // Create an instance of Gson
            val gson = Gson()
            // create an object expression that descends from TypeToken
            // and then get the Java Type from that
            val sType = object : TypeToken<ArrayList<String>>() {}.type
            // provide the type specified above to fromJson() method
            // this will deserialize the previously saved Json into an object of the specified type (e.g., list)
            expenseCategoriesList = gson.fromJson<ArrayList<String>>(expenseCategories, sType)
        }
    }

    fun configureSpinner(){
        //Configure Spinner
        val category_Spinner: Spinner = category_details_id;

        if(income){
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, incomeCategoriesList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            category_Spinner.setAdapter(adapter)
        }else{
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, expenseCategoriesList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            category_Spinner.setAdapter(adapter)
        }

        category_Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(income){
                    categorySelected = incomeCategoriesList[position]
                }else{
                    categorySelected = expenseCategoriesList[position]
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

}