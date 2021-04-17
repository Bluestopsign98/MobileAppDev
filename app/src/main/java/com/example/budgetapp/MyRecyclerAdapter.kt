package com.example.budgetapp

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetapp.ui.Transaction

class MyRecyclerAdapter(private val contacts: ArrayList<Transaction>): RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder>() {


    var count = 1 //This variable is used for just testing purpose to understand how RecyclerView works

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        // This class will represent a single row in our recyclerView list
        // This class also allows caching views and reuse them
        // Each MyViewHolder object keeps a reference to 3 view items in our row_item.xml file
        val personName = itemView.findViewById<TextView>(R.id.transaction_name)
        val personAge = itemView.findViewById<TextView>(R.id.transaction_amount)
        val personImage = itemView.findViewById<ImageView>(R.id.dollar_image)
        

        init {

            Log.d(TAG, "MyRecycle: ")
            itemView.setOnClickListener {
                val selectedItem = adapterPosition
                Toast.makeText(itemView.context, "You clicked on $selectedItem", Toast.LENGTH_SHORT).show()
            }


            // Set onLongClickListener to show a toast message and remove the selected row item from the list
            // Make sure to change the  MyViewHolder class to inner class to get a reference to an object of outer class
            itemView.setOnLongClickListener {

                val selectedItem = adapterPosition
                contacts.removeAt(selectedItem)
                notifyItemRemoved(selectedItem)
                Toast.makeText(itemView.context, "Long press, deleting $selectedItem", Toast.LENGTH_SHORT).show()

                return@setOnLongClickListener true
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Inflate a layout from our XML (row_item.XML) and return the holder

        Log.d(TAG, "onCreateViewHolder: ${count++}")

        // create a new view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_row_layout, parent, false)
        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Log.d(TAG, "onBindViewHolder: ")
        val currentItem = contacts[position]
        Log.d(TAG, "onBindViewHolder: $currentItem")
        Log.d(TAG, "onBindViewHolder: ${currentItem.amount}")
        Log.d(TAG, "onBindViewHolder: ${currentItem.name}")
        Log.d(TAG, "onBindViewHolder: ${currentItem.dollarImage}")
        holder.personName.text = currentItem.name
        holder.personAge.text = "Age = ${currentItem.amount}"
        holder.personImage.setImageResource(currentItem.dollarImage)

        //Log.d(TAG, "onBindViewHolder: $position")
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return contacts.size
    }

}