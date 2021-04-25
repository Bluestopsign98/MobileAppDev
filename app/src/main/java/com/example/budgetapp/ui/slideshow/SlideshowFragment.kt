package com.example.budgetapp.ui.slideshow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.budgetapp.R
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.ArrayList
import android.widget.Toast
import com.example.budgetapp.DatabaseHelper
import com.squareup.picasso.Picasso

class SlideshowFragment : Fragment(), AdapterView.OnItemSelectedListener {

  private lateinit var slideshowViewModel: SlideshowViewModel


  private var incomeCategoriesList = ArrayList<String>()
  private var expenseCategoriesList = ArrayList<String>()
  private var income : Boolean = false
  private var currentList = ArrayList<String>()
  lateinit var myAdapter: ArrayAdapter<String>
  private var graphList = ArrayList<String>()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    retrieveCategories()
    val root = inflater.inflate(R.layout.fragment_slideshow, container, false)
    retrieveCategories()

    // -- Setup listView ---
    val mySpinner = root.findViewById<Spinner>(R.id.categorySpinner)
    myAdapter = ArrayAdapter<String>(this.requireContext(), android.R.layout.simple_list_item_1, currentList)

    // -- Initialize list contents ---
    updateList()

    mySpinner.adapter = myAdapter



    var button = root.findViewById<Button>(R.id.generateButton)
    button.setOnClickListener { view ->
        generateGraph(root)
    }


    val ts = root.findViewById<Switch>(R.id.typeSwitch)
    ts.setOnClickListener() {
      categorySwitcher()
    }
    mySpinner.setSelection(Adapter.NO_SELECTION, true);
    mySpinner.onItemSelectedListener = this
    return root
  }

  private fun retrieveCategories() {


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

  // --- Switch list contents based on income vs expense switcher ---
  private fun categorySwitcher(){
    // --- Set income boolean to match the switch contents ---
    income = typeSwitch.isChecked
    updateList()
    Log.d("HELLO","Previous list: " + graphList.toString())
    graphList.clear()
    Log.d("HELLO","New list: " + graphList.toString())
    Toast.makeText(context, "Selection has been cleared", Toast.LENGTH_SHORT).show()

  }

  // --- display new list in listview ---
  private fun updateList(){
    if(income)
    {
      currentList = incomeCategoriesList
    }
    else
    {
      currentList = expenseCategoriesList
    }
    refreshSpinner()
  }

  private fun refreshSpinner(){
    myAdapter.clear()
    myAdapter.addAll(currentList)
    val mySpinner = view?.findViewById<Spinner>(R.id.categorySpinner)
    mySpinner?.setSelection(Adapter.NO_SELECTION, true);
    myAdapter.notifyDataSetChanged()
  }


  fun generateGraph(view: View)
  {
    val categoryCostSummary =  ArrayList<Float>()

    val dbHelper = DatabaseHelper(requireContext())
    Log.d("HELLO","${graphList.toString()}")
    Log.d("HELLO","$graphList.size")
    for(i in 0..graphList.size-1){
      categoryCostSummary.add(0F)
      // --- Cursor is used to iterate though the result of the database get call
      val cursor = dbHelper.getDataByCategory(graphList[i], income)
      while (cursor.moveToNext()) {
          categoryCostSummary[i] += cursor.getFloat(2)
      }
    }

  var labels = ""
  var data = ""
  for(i in 0 until graphList.size) {
      Log.d("HELLO",graphList[i])
      var label = graphList[i]
      var d = categoryCostSummary[i].toString()
      labels += "'$label', "
      data += "$d, "
  }
  var color = "rgba(255, 99, 132, 0.5)"
  var bcolor = "rgb(255, 99, 132)"
  if(income)
  {
    color = "rgba(34,139,34 0.5)"
    bcolor = "rgb(0,100,0)"
  }



    var chartLink = "https://quickchart.io/chart?w=303&h=303&bkg=%23ffffff&c={\n" +
            "  \"type\": 'bar',\n" +
            "  \"data\": {\n" +
            "    \"labels\": [$labels],\n" +
            "    \"datasets\": [\n" +
            "      {\n" +
            "        \"label\": 'Income',\n" +
            "        \"backgroundColor\": '$color',\n" +
            "        \"borderColor\": '$bcolor',\n" +
            "        \"borderWidth\": 1,\n" +
            "        \"data\": [$data],\n" +
            "      }\n" +
            "    ],\n" +
            "  },\n" +
            "  \"options\": {\n" +
            "\n" +
            "    \"plugins\": {\n" +
            "      \"datalabels\": {\n" +
            "        \"anchor\": 'center',\n" +
            "        \"align\": 'center',\n" +
            "        \"color\": '%23666',\n" +
            "        \"font\": {\n" +
            "          \"weight\": 'normal',\n" +
            "        },\n" +
            "      },\n" +
            "    },\n" +
            "  },\n" +
            "}&format=.png"



    val chart= view.findViewById<ImageView>(R.id.graphHolder)
    Picasso.get().load(chartLink).into(chart)
    Log.d("HELLO",chartLink)
  }

  override fun onNothingSelected(parent: AdapterView<*>?) {
    Log.d("HELLO", "nothing is selected!!!")
  }

  override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    // Determine which item in the dropdown list is selected
    val selectedItem = parent?.getItemAtPosition(position).toString()
    Log.d("HELLO",selectedItem + " "  + position)
    if(graphList.contains(selectedItem))
    {
      graphList.remove(selectedItem)
      Toast.makeText(context, "$selectedItem has been removed", Toast.LENGTH_SHORT).show()
    }
    else
    {
        graphList.add(selectedItem)
        Toast.makeText(context, "$selectedItem has been added", Toast.LENGTH_SHORT).show()
    }

  }


}

