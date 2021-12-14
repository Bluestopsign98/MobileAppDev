package com.example.budgetapp.ui.list

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetapp.DatabaseHelper
import com.example.budgetapp.MyRecyclerAdapter
import com.example.budgetapp.R
import com.example.budgetapp.ui.Transaction
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_entry.*
import kotlinx.android.synthetic.main.fragment_list.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "ListFragment"


class ListFragment : Fragment(), DatePickerDialog.OnDateSetListener {

  private lateinit var listViewModel: ListViewModel
  lateinit var dbHelper: DatabaseHelper
  lateinit var recyclerView: RecyclerView
  private var ascending = false; // --- Used to determine which direction to sort entries ---
  // --- TODO: Find an alternative to isStartorEnd below ---
  private var isStartOrEnd = ""; // --- Used to determine which field the date picker should populate.

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

    // specify an viewAdapter for the dataset
    recyclerView.adapter = MyRecyclerAdapter(retrieveDatabaseData())
    // use a linear layout manager, you can use different layouts as well
    recyclerView.layoutManager = LinearLayoutManager(activity)


    // --- Add a divider between rows ---
    val dividerItemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
    recyclerView.addItemDecoration(dividerItemDecoration)

    val calendar: Calendar = Calendar.getInstance()

    // --- Set up on click listener for end date text input. ---
    // --- This is used to open the date picker ---
    var endDateInput = root.findViewById<TextView>(R.id.endDateInput)
    endDateInput.setOnClickListener(View.OnClickListener {
      isStartOrEnd = "end"
      // --- Open date picker ---
      val datePickerDialog = DatePickerDialog(this.requireContext(), this@ListFragment, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
      datePickerDialog.show()
    })

    // --- Set up on click listener for start date text input. ---
    // --- This is used to open the date picker ---
    var startDateInput = root.findViewById<TextView>(R.id.startDateInput)
    startDateInput.setOnClickListener(View.OnClickListener {
      isStartOrEnd = "start"
      // --- Open date picker ---
      val datePickerDialog = DatePickerDialog(this.requireContext(), this@ListFragment, calendar.get(Calendar.YEAR) - 1, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
      datePickerDialog.show()
    })



    // --- Get current date and initialize date input with it
    //Source = https://www.tutorialspoint.com/get-current-time-and-date-on-android#:~:text=Get%20current%20time%20and%20date%20on%20Android%201,code%20to%20src%2FMainActivity.java%20...%20...%20More%20items...%20
    val sdf = SimpleDateFormat("MM/dd/yyyy")
    val currentDateandTime = sdf.format(Date())
    //calculate start date to be 1 year before current date
    var startDate =  currentDateandTime.substring(0, 6) + (currentDateandTime.substring(6).toInt() - 1).toString()
    //initialize date inputs
    var endInput = root.findViewById<TextView>(R.id.endDateInput)
    var startInput = root.findViewById<TextView>(R.id.startDateInput)
    endInput.setText(currentDateandTime)
    startInput.setText(startDate)


    // --- refresh button onClick, filter entries by date ---
    var refreshutton = root.findViewById<Button>(R.id.refreshButton)
    refreshutton.setOnClickListener { view ->

      // --- Refresh list with filtered data ---
      recyclerView.adapter = MyRecyclerAdapter(filterDatabaseData())
    }



    // --- Order swap button on click ---
    var orderButton = root.findViewById<ImageButton>(R.id.orderButton)
    orderButton.setOnClickListener { view ->

      ascending = !ascending
      // --- Rotate button icon 180 degrees
      orderButton.rotation = orderButton.rotation + 180F

    }

    return root
  }

  // --- returns all data within specified dates ---
  private fun filterDatabaseData():  ArrayList<Transaction>{
    var startDateString = startDateInput.text.toString()
    var endDateString = endDateInput.text.toString()
    val regPattern = "^\\d{2}/\\d{2}/\\d{4}$"

    Log.d(TAG, "filterDatabaseData: $startDateString")
    Log.d(TAG, "filterDatabaseData: $endDateString")

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

    val cursor = dbHelper.getDataByDate(startDateString, endDateString, ascending)
    while (cursor.moveToNext()) {
      var currentTransaction = Transaction(cursor.getInt(0), cursor.getString(1), cursor.getFloat(2), cursor.getString(4), cursor.getString(3), cursor.getString(5), cursor.getInt(6) > 0, R.drawable.dollar_sign_symbol)
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
      var currentTransaction = Transaction(cursor.getInt(0), cursor.getString(1), cursor.getFloat(2), cursor.getString(4), cursor.getString(3), cursor.getString(5), cursor.getInt(6) > 0, R.drawable.dollar_sign_symbol)
      transactions.add(currentTransaction)
    }

    cursor.close()

    // return the list of contacts
    return transactions

  }

  @Override
  override fun onDestroyView() {
    super.onDestroyView()
    dbHelper.close()
    super.onDestroy()
  }

  // --- This is called when the date picker is closed
  override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

    // --- For single digit days and months, concatenate a 0 to make then 2 digits ---
    var monthString = month.toString()
    if (monthString.length == 1){
      monthString = "0" + monthString }
    var dayString = dayOfMonth.toString()
    if (dayString.length == 1){
      dayString = "0" + dayString }

    // --- Check which button was clicked and update text respective text field ---
    if (isStartOrEnd == "start"){
      startDateInput.text = "$monthString/$dayString/$year"
    }else if (isStartOrEnd == "end"){
      endDateInput.text = "$monthString/$dayString/$year"
    }else{
      Log.d(TAG, "onDateSet: Unknown date input selected!")
    }
  }
}
