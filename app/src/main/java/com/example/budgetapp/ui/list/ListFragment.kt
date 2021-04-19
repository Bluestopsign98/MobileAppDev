package com.example.budgetapp.ui.list

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class ListFragment : Fragment() {

  private lateinit var listViewModel: ListViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    listViewModel = ViewModelProvider(this).get(ListViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_list, container, false)
    //val textView: TextView = root.findViewById(R.id.text_gallery)

    // Store the the recyclerView widget in a variable
    val recyclerView = root.findViewById<RecyclerView>(R.id.my_recyler_view)


    // specify an viewAdapter for the dataset (we use dummy data containing 20 contacts)
    recyclerView.adapter = MyRecyclerAdapter(retrieveDatabaseData())

    // use a linear layout manager, you can use different layouts as well
    recyclerView.layoutManager = LinearLayoutManager(activity)


    // if you want, you can make the layout of the recyclerview horizontal as follows
    //recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


    // Add a divider between rows -- Optional
    val dividerItemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
    recyclerView.addItemDecoration(dividerItemDecoration)

    // use this setting to improve performance if you know the size of the RecyclerView is fixed
    //recyclerView.setHasFixedSize(true)


    listViewModel.text.observe(viewLifecycleOwner, Observer {
      //textView.text = it
    })

    return root

  }



  // A helper function to create specified amount of dummy contact data
  private fun retrieveDatabaseData() : ArrayList<Transaction>{

    val transactions = ArrayList<Transaction>()

    //To access your database, instantiate your subclass of SQLiteOpenHelper
    val dbHelper = DatabaseHelper(requireContext())
    // --- Cursor is used to iterate though the result of the database get call
    val cursor = dbHelper.viewAllData
    while (cursor.moveToNext()) {
      var currentTransaction = Transaction(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(4), cursor.getString(3),R.drawable.dollar_sign_symbol)
      transactions.add(currentTransaction)
    }

    // return the list of contacts
    return transactions

  }
//  fun Nav(){
//    val navID = R.id.action_nav_gallery_to_entryDetailsActivity
//    var navController = this.findNavController()
//    navController.navigate(navID)
//  }


}

