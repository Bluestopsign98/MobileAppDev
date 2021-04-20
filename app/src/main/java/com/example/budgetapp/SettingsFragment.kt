package com.example.budgetapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
<<<<<<< Updated upstream
import java.util.ArrayList


class SettingsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

=======
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.budgetapp.ui.home.HomeViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_add_entry.*
import java.util.ArrayList
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Switch
import kotlinx.android.synthetic.main.fragment_settings.*


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
        retrieveCategories(root)
        val myLV = root.findViewById<ListView>(R.id.categoryListView)
        currentList.add("hello")
        myAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, currentList)
        myLV.adapter = myAdapter
        updateList()
        val ts = root.findViewById<Switch>(R.id.typeSwitch)
        ts.setOnClickListener()
        {
            categorySwitcher(root)
        }

        myLV.setOnItemClickListener { list, item, position, id ->
            //remove category
        }
        return root
>>>>>>> Stashed changes
    }
    private fun retrieveCategories(view: View) {
        val sharedPreferences = this.getActivity()?.getSharedPreferences("BudgetApp", AppCompatActivity.MODE_PRIVATE)
        if (sharedPreferences!!.contains("IncomeCategories")) {
            // --- Retrieve the income category list ---
            val incomeCategories = sharedPreferences?.getString("IncomeCategories", "").toString()

<<<<<<< Updated upstream
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // --- variable to determine if the new entry is and income or an expense
        var income = false
        var categorySelected = ""

        var incomeCategoriesList = ArrayList<String>()
        var expenseCategoriesList = ArrayList<String>()


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

=======
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
        Log.d("ASDF",expenseCategoriesList.toString())

    }

    fun categorySwitcher(view: View){
        // --- Set income boolean to match the switch contents ---
        income = typeSwitch.isChecked
        updateList()
    }

    fun updateList()
    {
        if(income)
        {
            currentList = incomeCategoriesList
        }
        else
        {
            currentList = expenseCategoriesList
        }
        Log.d("TAG", currentList.toString())
        myAdapter.notifyDataSetChanged()
    }

>>>>>>> Stashed changes

}