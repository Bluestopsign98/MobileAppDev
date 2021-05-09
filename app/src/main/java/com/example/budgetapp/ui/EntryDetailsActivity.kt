package com.example.budgetapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetapp.DatabaseHelper
import com.example.budgetapp.MainActivity
import com.example.budgetapp.R
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_add_entry.amount_details_id
import kotlinx.android.synthetic.main.activity_add_entry.date_details_id
import kotlinx.android.synthetic.main.activity_add_entry.desc_details_id
import kotlinx.android.synthetic.main.activity_add_entry.name_details_id
import kotlinx.android.synthetic.main.activity_entry_details.*
import java.util.*

private const val TAG = "DetailsActivity"

class EntryDetailsActivity : AppCompatActivity() {

    val dbHelper = DatabaseHelper(this)
    var categorySelected = ""
    var income = false
    private var transactionID = -1

    private var incomeCategoriesList = ArrayList<String>()
    private var expenseCategoriesList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_details)
        transactionID = intent.getIntExtra("TransactionID", 0)
        Log.d(TAG, "onCreate HELLO: $transactionID")


//         --- Cursor is used to iterate though the result of the database get call
        val cursor = dbHelper.getDataByID(transactionID)

        cursor.moveToNext()
        var currentTransaction = Transaction(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getFloat(2),
                cursor.getString(4),
                cursor.getString(3),
                cursor.getString(5),
                cursor.getInt(6) > 0,
                R.drawable.dollar_sign_symbol
        )

        cursor.close()
        dbHelper.close()


        Log.d(TAG, "onCreate: $currentTransaction")

        name_details_id.setText(currentTransaction.name)
        amount_details_id.setText(currentTransaction.amount.toString())
        desc_details_id.setText(currentTransaction.description)


        // --- Convert currentItem date to MM/dd/yyyy format
        var entryDate = currentTransaction.date
        entryDate = entryDate.substring(4, 6) + "/" + entryDate.substring(6, 8) + "/" + entryDate.substring(0, 4)
        date_details_id.setText(entryDate)

        categorySelected = currentTransaction.category
        income = currentTransaction.income

        retrieveCategories()
        configureSpinner()

        var i = 0
        if(income){
            while (i < incomeCategoriesList.size){
                Log.d("HSDFSHPDO", "i is " + i)
                if(incomeCategoriesList[i] == categorySelected){
                    category_details_id.setSelection(i)
                }
                i++
            }
        }else{
            while (i < expenseCategoriesList.size){
                Log.d("HSDFSHPDO", "i is " + i)
                if(expenseCategoriesList[i] == categorySelected){
                    category_details_id.setSelection(i)
                }
                i++
            }

        }


    }

    
    fun deleteEntry(view: View) {
        // --- Add new entry to database ---
        try {
            dbHelper.deleteData(
                    transactionID.toString())

        } catch (e: Exception) {
            Log.e(TAG, "error: $e")
        }

        dbHelper.close()

        // --- Return to Overview ---
        val myIntent = Intent(this, MainActivity::class.java)
        startActivity(myIntent)
    }

    fun submitEntry(view: View) {

        view.hideKeyboard()

        if (name_details_id.text.isNullOrBlank() || date_details_id.text.isNullOrBlank() || amount_details_id.text.isNullOrBlank()){
            Snackbar.make(view, "Missing required fields!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            return
        }

        var dateString = date_details_id.text.toString()
        val regPattern = "^\\d{2}/\\d{2}/\\d{4}$"

        //--- validate date input ---
        if(dateString.matches(regPattern.toRegex())) {
            // --- Convert datestring into yyyyMMdd format from mm/dd/yyyy
            val strs = dateString.split("/").toTypedArray()
            dateString = strs[2] + strs[0] + strs[1]
        } else {
            Snackbar.make(view, "Invalid date format", Snackbar.LENGTH_LONG)
                 .setAction("Action", null).show()
            return
        }

        var amount = amount_details_id.text.toString().toFloat()
        if(income){
        }else{
            amount *= -1
        }

        // --- Add new entry to database ---
        try {
            dbHelper.updateData(
                    transactionID.toString(),
                    name_details_id.text.toString(),
                    amount_details_id.text.toString().toFloat(),
                    dateString,
                    desc_details_id.text.toString(),
                    categorySelected,
                    income
            )
        } catch (e: Exception) {
            Log.e(TAG, "error: $e")
        }

       dbHelper.close()

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
        Log.d("ASDF", expenseCategoriesList.toString())

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


    fun cancel(view: View) {

        // dbHelper.close()

        // --- Return to Overview ---
        val myIntent = Intent(this, MainActivity::class.java)
        startActivity(myIntent)
    }

    fun View.hideKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }



}