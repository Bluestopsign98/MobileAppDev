package com.example.budgetapp

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.ArrayList
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.budgetapp.ui.home.HomeViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_add_entry.*
import kotlinx.android.synthetic.main.fragment_settings.*

private const val TAG = "SettingsFragment"

class SettingsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var incomeCategoriesList = ArrayList<String>()
    private var expenseCategoriesList = ArrayList<String>()
    private var income : Boolean = false
    private var currentList = ArrayList<String>()
    lateinit var myAdapter: ArrayAdapter<String>
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        retrieveCategories()

        val myLV = root.findViewById<ListView>(R.id.categoryListView)
        myAdapter = ArrayAdapter<String>(this.requireContext(), android.R.layout.simple_list_item_1, currentList)

        updateList()
        myLV.adapter = myAdapter

        var button = root.findViewById<Button>(R.id.addButton)
        button.setOnClickListener { view ->
            if (!categoryInput.text.isNullOrEmpty()){
                if(income){
                    incomeCategoriesList.add(categoryInput.text.toString())
                }else{
                    expenseCategoriesList.add(categoryInput.text.toString())
                }

                refreshListview()
                myLV.adapter = myAdapter

            }else{
                Toast.makeText(this.requireContext(), "Cannot add an empty category string!", Toast.LENGTH_SHORT).show()
            }
        }

        // --- Confirm button onClick, Save Shared preferences and return to home ---
        var confirmButton = root.findViewById<Button>(R.id.confirm_button_id)
        confirmButton.setOnClickListener { view ->

            saveData()

            // --- Return to Overview ---
            val myIntent = Intent(this.requireContext(), MainActivity::class.java)
            startActivity(myIntent)
        }


        val ts = root.findViewById<Switch>(R.id.typeSwitch)
        ts.setOnClickListener()
        {
            categorySwitcher(root)
            myLV.adapter = myAdapter
        }


        myLV.setOnItemLongClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            if(income){
                incomeCategoriesList.removeAt(position)
            }else{
                expenseCategoriesList.removeAt(position)
            }

            Toast.makeText(this.requireContext(), "Deleting $selectedItem", Toast.LENGTH_SHORT).show()

            // update listview
            myAdapter.notifyDataSetChanged()

            return@setOnItemLongClickListener true
        }


        return root
    }

    // refresh the listview, for use after listview contents change
    private fun refreshListview(){
        myAdapter = ArrayAdapter<String>(this.requireContext(), android.R.layout.simple_list_item_1, currentList)
    }


    private fun retrieveCategories() {
        Log.d(TAG, "retrieveCategories: ")
        
        val sharedPreferences = this.getActivity()?.getSharedPreferences("BudgetApp", AppCompatActivity.MODE_PRIVATE)
        if (sharedPreferences!!.contains("IncomeCategories")) {
            // --- Retrieve the income category list ---
            val incomeCategories = sharedPreferences?.getString("IncomeCategories", "").toString()
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

    fun categorySwitcher(view: View){
        // --- Set income boolean to match the switch contents ---
        income = typeSwitch.isChecked
        updateList()
    }

    private fun updateList()
    {
        if(income)
        {
            currentList = incomeCategoriesList
        }
        else
        {
            currentList = expenseCategoriesList
        }
        refreshListview()
    }

    // save current state
    private fun saveData(){
        val sharedPreferences = this.getActivity()?.getSharedPreferences("BudgetApp", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()

        // Create an instance of Gson (make sure to include its dependency first to be able use gson)
        val gson = Gson()
        // toJson() method serializes the specified object into its equivalent Json representation.
        val updatedIncomeList = gson.toJson(incomeCategoriesList)
        val updatedExpenseList = gson.toJson(incomeCategoriesList)
        // Put the  Json representation, which is a string, into sharedPreferences
        editor?.putString("IncomeCategories", updatedIncomeList )
        editor?.putString("ExpenseCategories", updatedIncomeList )
        // Apply the changes
        editor?.apply()
    }

}