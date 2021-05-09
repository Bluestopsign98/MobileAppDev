package com.example.budgetapp.ui.list

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetapp.DatabaseHelper
import com.example.budgetapp.MainActivity
import com.example.budgetapp.MyRecyclerAdapter
import com.example.budgetapp.R
import com.example.budgetapp.ui.Transaction
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_entry.*
import kotlinx.android.synthetic.main.fragment_list.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

class ListFragment : Fragment() {

  private lateinit var listViewModel: ListViewModel
  lateinit var dbHelper: DatabaseHelper
  lateinit var recyclerView: RecyclerView
  private var ascending = false;

  override fun onCreateView(
          inflater: LayoutInflater,
          container: ViewGroup?,
          savedInstanceState: Bundle?
  ): View? {
    listViewModel = ViewModelProvider(this).get(ListViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_list, container, false)
    //val textView: TextView = root.findViewById(R.id.text_gallery)
    // Store the the recyclerView widget in a variable
    recyclerView = root.findViewById<RecyclerView>(R.id.my_recyler_view)

    dbHelper = DatabaseHelper(requireContext())

    // specify an viewAdapter for the dataset (we use dummy data containing 20 contacts)
    recyclerView.adapter = MyRecyclerAdapter(retrieveDatabaseData())
    // use a linear layout manager, you can use different layouts as well
    recyclerView.layoutManager = LinearLayoutManager(activity)


    // Add a divider between rows -- Optional
    val dividerItemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
    recyclerView.addItemDecoration(dividerItemDecoration)


    // --- Get current date and initialize date input with it
    //Source = https://www.tutorialspoint.com/get-current-time-and-date-on-android#:~:text=Get%20current%20time%20and%20date%20on%20Android%201,code%20to%20src%2FMainActivity.java%20...%20...%20More%20items...%20
    val sdf = SimpleDateFormat("MM/dd/yyyy")
    val currentDateandTime = sdf.format(Date())
    //calculate start date to be 1 year before current date
    var startDate =  currentDateandTime.substring(0,6) + (currentDateandTime.substring(6).toInt() - 1).toString()
    //initialize date inputs
    var endInput = root.findViewById<EditText>(R.id.endDateInput)
    var startInput = root.findViewById<EditText>(R.id.startDateInput)
    endInput.setText(currentDateandTime)
    startInput.setText(startDate)


    // --- refresh button onClick, filter entries by date ---
    var refreshutton = root.findViewById<Button>(R.id.refreshButton)
    refreshutton.setOnClickListener { view ->

      recyclerView.adapter = MyRecyclerAdapter(filterDatabaseData())

    }

    // --- order swap button on click ---
    var orderButton = root.findViewById<ImageButton>(R.id.orderButton)
    orderButton.setOnClickListener { view ->

      ascending = !ascending
      orderButton.rotation = orderButton.rotation + 180F

    }

    return root

  }

  // --- returns all data withing specified dates ---
  private fun filterDatabaseData():  ArrayList<Transaction>{
    var startDateString = startDateInput.text.toString()
    var endDateString = endDateInput.text.toString()
    val regPattern = "^\\d{2}/\\d{2}/\\d{4}$"

    //--- validate date input ---
    if(startDateString.matches(regPattern.toRegex()) && endDateString.matches(regPattern.toRegex()) ) {
      // --- Convert datestring into yyyyMMdd format from mm/dd/yyyy
      var strs = startDateString.split("/").toTypedArray()
      startDateString = strs[2] + strs[0] + strs[1]

      strs = endDateString.split("/").toTypedArray()
      endDateString = strs[2] + strs[0] + strs[1]
    } else {
      view?.let {
        Snackbar.make(it, "Invalid date format", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
      }
    }

    dbHelper = DatabaseHelper(requireContext())
    val transactions = ArrayList<Transaction>()

    val cursor = dbHelper.getDataByDate(startDateString,endDateString,ascending)
    while (cursor.moveToNext()) {
      var currentTransaction = Transaction(cursor.getInt(0),cursor.getString(1),cursor.getFloat(2),cursor.getString(4), cursor.getString(3), cursor.getString(5), cursor.getInt(6) > 0, R.drawable.dollar_sign_symbol)
      transactions.add(currentTransaction)
    }

    cursor.close()
    return transactions
  }


  // --- Returns all data from database ---
  private fun retrieveDatabaseData() : ArrayList<Transaction>{

    val dbHelper = DatabaseHelper(requireContext())
    val transactions = ArrayList<Transaction>()

    //To access your database, instantiate your subclass of SQLiteOpenHelper
    // --- Cursor is used to iterate though the result of the database get call
    val cursor = dbHelper.viewAllData
    while (cursor.moveToNext()) {
      var currentTransaction = Transaction(cursor.getInt(0),cursor.getString(1),cursor.getFloat(2),cursor.getString(4), cursor.getString(3), cursor.getString(5), cursor.getInt(6) > 0, R.drawable.dollar_sign_symbol)
      transactions.add(currentTransaction)
    }

    cursor.close()

    // return the list of contacts
    return transactions

  }

  @Override
  override fun onDestroyView() {
    super.onDestroyView()
    Log.d(TAG, "onDestroyview: ")
    dbHelper.close()
    super.onDestroy()
    Log.d(TAG, "onDestroy: 2")
  }




}
