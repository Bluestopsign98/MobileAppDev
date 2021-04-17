package com.example.budgetapp.ui.list

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetapp.MyRecyclerAdapter
import com.example.budgetapp.R
import com.example.budgetapp.ui.Transaction
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment() {

  private lateinit var listViewModel: ListViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    listViewModel =
            ViewModelProvider(this).get(ListViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_list, container, false)
    //val textView: TextView = root.findViewById(R.id.text_gallery)

    Log.d(TAG, "onCreateView: 0")
    // Store the the recyclerView widget in a variable
    val recyclerView = root.findViewById<RecyclerView>(R.id.my_recyler_view)

    Log.d(TAG, "onCreateView: 1")
    // specify an viewAdapter for the dataset (we use dummy data containing 20 contacts)
    recyclerView.adapter = MyRecyclerAdapter(generateContact(20))

    Log.d(TAG, "onCreateView: 2")
    // use a linear layout manager, you can use different layouts as well
    recyclerView.layoutManager = LinearLayoutManager(activity)

    Log.d(TAG, "onCreateView: 3")
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
  private fun generateContact(size: Int) : ArrayList<Transaction>{

    val contacts = ArrayList<Transaction>()



    // The for loop will generate [size] amount of contact data and store in a list
    for (i in 1..size) {
      val person = Transaction("John Doe-$i", i, R.drawable.dollar_sign_symbol)
      contacts.add(person)
      Log.d(TAG, "generateContact:  $person")
    }
    // return the list of contacts
    //Log.d(TAG, "generateContact:  $contacts")
    return contacts

  }


}