package com.example.budgetapp
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.budgetapp.R.id.nav_host_fragment
import com.example.budgetapp.ui.AddEntryActivity
import com.google.gson.Gson

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        // --- Add new entry button ---
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->

            // --- Open the add Entry Activity ---
            val myIntent = Intent(this, AddEntryActivity::class.java)
            startActivity(myIntent)

            // ---- OLD CODE to pop up a message -----
            //Snackbar.make(view, "This will open the add new item activity", Snackbar.LENGTH_LONG)
            //     .setAction("Action", null).show()
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_settings), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // --- Retrieve category arrays or generate them if they did not exist ---
        loadCategories()

    }

    // --- old code to create a menu in top R corner
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    // --- Retrieve category arrays or generate them if they did not exist ---
    fun loadCategories(){
        val sharedPreferences = getSharedPreferences("BudgetApp", MODE_PRIVATE)

        if(sharedPreferences.contains("IncomeCategories")){
            // --- Retrieve the income category list ---
            val incomeCategories = sharedPreferences.getString("IncomeCategories", "")
            Log.d(TAG, "loadCategories: $incomeCategories")

        }else{
            // --- Initialize Shared preferences with an income category list ---
            val editor = sharedPreferences.edit()
            val gson = Gson()

            // --- Initial list of categories ---
            val incomeCatList = ArrayList<String>()
            incomeCatList.add("Paycheck")
            incomeCatList.add("Gift")
            incomeCatList.add("Other")
            //var incomeCatList = mutableListOf("Paycheck", "Gift", "Other")

            // --- convert list to Json and push to shared preferences ---
            val json = gson.toJson(incomeCatList)
            editor.putString("IncomeCategories",json)
            editor.commit()
        }

        if(sharedPreferences.contains("ExpenseCategories")){
            // --- Retrieve the expense category list ---
            val expenseCategories = sharedPreferences.getString("ExpenseCategories", "")
            Log.d(TAG, "loadCategories: $expenseCategories")
        }else{
            // --- Initialize Shared preferences with an income category list ---
            val editor = sharedPreferences.edit()
            val gson = Gson()

            // --- Initial list of categories
            val expenseCatList = ArrayList<String>()
            expenseCatList.add("Groceries")
            expenseCatList.add("Gift")
            expenseCatList.add("Subscription")
            expenseCatList.add("Payment")
            expenseCatList.add("Entertainment")
            expenseCatList.add("Other")
            //var incomeCatList = mutableListOf("Groceries", "Gifts", "Subscriptions", "Payments", "Entertainment", "Other" )

            // --- convert list to Json and push to shared preferences ---
            val json = gson.toJson(expenseCatList)
            editor.putString("ExpenseCategories",json)
            editor.commit()
        }
    }



}