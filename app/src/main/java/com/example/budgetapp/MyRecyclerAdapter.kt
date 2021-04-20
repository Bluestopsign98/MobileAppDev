package com.example.budgetapp

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetapp.ui.Transaction
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.navigateUp
import com.example.budgetapp.ui.AddEntryActivity
import com.example.budgetapp.ui.EntryDetailsActivity
import com.example.budgetapp.R
import com.example.budgetapp.ui.list.ListFragment


class MyRecyclerAdapter(private val transactionList: ArrayList<Transaction>): RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder>() {


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        // This class will represent a single row in our recyclerView list
        // This class also allows caching views and reuse them
        // Each MyViewHolder object keeps a reference to 3 view items in our row_item.xml file
        val transactionName = itemView.findViewById<TextView>(R.id.transaction_name)
        val transactionAmount = itemView.findViewById<TextView>(R.id.transaction_amount)
        val transactionImage = itemView.findViewById<ImageView>(R.id.dollar_image)
        val transactionDate = itemView.findViewById<TextView>(R.id.transaction_date)

        init {
            itemView.setOnClickListener {
                //val selectedItem = adapterPosition
                val transactionID = transactionList[adapterPosition].name
                //Toast.makeText(itemView.context, "You clicked on $transactionID", Toast.LENGTH_SHORT).show()

                //val nav = ListFragment()
                //nav.Nav(this)

                val context = itemView.context

                val myIntent = Intent(context, EntryDetailsActivity::class.java)
                myIntent.putExtra("TransactionID", transactionList[adapterPosition].id)
                context.startActivity(myIntent)

            }


            // Set onLongClickListener to show a toast message and remove the selected row item from the list
            // Make sure to change the  MyViewHolder class to inner class to get a reference to an object of outer class
            itemView.setOnLongClickListener {

                val selectedItem = adapterPosition
                transactionList.removeAt(selectedItem)
                notifyItemRemoved(selectedItem)
                Toast.makeText(itemView.context, "Long press, deleting $selectedItem", Toast.LENGTH_SHORT).show()

                return@setOnLongClickListener true
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Inflate a layout from our XML (row_item.XML) and return the holder


        // create a new view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_row_layout, parent, false)
        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //Log.d(TAG, "onBindViewHolder: ")
        val currentItem = transactionList[position]
        //.d(TAG, "onBindViewHolder: $currentItem")
        //Log.d(TAG, "onBindViewHolder: ${currentItem.amount}")
        //Log.d(TAG, "onBindViewHolder: ${currentItem.name}")
        //Log.d(TAG, "onBindViewHolder: ${currentItem.dollarImage}")


        holder.transactionName.text = currentItem.name
        holder.transactionDate.text = currentItem.date

        if(currentItem.amount < 0.0 ){
            holder.transactionAmount.setTextColor(Color.RED)
            holder.transactionImage.setColorFilter(Color.RED)
        }else{
            //holder.transactionAmount.setTextColor(Color.GREEN)
            holder.transactionImage.setColorFilter(Color.GREEN)
        }
        holder.transactionAmount.text = "$ ${currentItem.amount}"
        holder.transactionImage.setImageResource(currentItem.dollarImage)

        //Log.d(TAG, "onBindViewHolder: $position")
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return transactionList.size
    }

}