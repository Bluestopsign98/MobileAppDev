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

private const val TAG = "AddEntryActivity"

class AddEntryActivity : AppCompatActivity() {

    //To access your database, instantiate your subclass of SQLiteOpenHelper
    private val dbHelper = DatabaseHelper(this)
    
    // --- variable to determine if the new entry is and income or an expense
    private var income = false
    private var categorySelected = ""

    private var incomeCategoriesList = ArrayList<String>()
    private var expenseCategoriesList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_entry)

        // --- Get current date and initialize date input with it
        //Source = https://www.tutorialspoint.com/get-current-time-and-date-on-android#:~:text=Get%20current%20time%20and%20date%20on%20Android%201,code%20to%20src%2FMainActivity.java%20...%20...%20More%20items...%20
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val currentDateandTime = sdf.format(Date())
        date_details_id.setText(currentDateandTime)



        retrieveCategories()

        configureSpinner()


    }

    fun submitEntry(view: View){

        var amount = amount_details_id.text.toString().toFloat()
        if(income){
        }else{
            amount *= -1
        }

        // --- Add new entry to database ---
        try {
            dbHelper.insertData(name_details_id.text.toString(), amount, date_details_id.text.toString(), desc_details_id.text.toString(), categorySelected,income)
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

    // --- function for income vs expense switch ---
    fun incomeVSExpenseSwitch(view: View){
        // --- Set income boolean to match the switch contents ---
        income = income_vs_expense_id.isChecked
        Log.d(TAG, "incomeVSExpenseSwitch: $income")

        // --- Update category dropdown ---
        configureSpinner()
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

    fun configureSpinner(){
        //Configure Spinner
        val category_Spinner: Spinner = category_spinner_id;

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