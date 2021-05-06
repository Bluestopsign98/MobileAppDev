package com.example.budgetapp.ui.slideshow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.budgetapp.R
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.ArrayList
import android.widget.Toast
import com.example.budgetapp.DatabaseHelper
import com.squareup.picasso.Picasso
import kotlin.math.abs

private const val TAG = "categoryFragment"

class SlideshowFragment : Fragment(){

  private lateinit var slideshowViewModel: SlideshowViewModel


  private var incomeCategoriesList = ArrayList<String>()
  private var expenseCategoriesList = ArrayList<String>()
  private var income : Boolean = false
  private var currentList = ArrayList<String>()
  lateinit var myAdapter: ArrayAdapter<String>
  private var expenseGraphList = ArrayList<String>()
  private var incomeGraphList = ArrayList<String>()
  private lateinit var root: View

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    retrieveCategories()
    root = inflater.inflate(R.layout.category_chart, container, false)
    retrieveCategories()


    // -- Setup listView ---
    val myLV = root.findViewById<ListView>(R.id.categoryListView)
    myAdapter = ArrayAdapter<String>(this.requireContext(), android.R.layout.simple_list_item_1, currentList)

    // -- Initialize list contents ---
    updateList()
    myLV.adapter = myAdapter


    //Set onClick to delete item from listview
    myLV.setOnItemClickListener { parent, view, position, id ->
      val selectedItem = parent.getItemAtPosition(position).toString()
    if(income){
      if(incomeGraphList.contains(selectedItem))
      {
        incomeGraphList.remove(selectedItem)
        Toast.makeText(context, "$selectedItem has been removed", Toast.LENGTH_SHORT).show()
      }
      else
      {
        incomeGraphList.add(selectedItem)
        Toast.makeText(context, "$selectedItem has been added", Toast.LENGTH_SHORT).show()
      }
    }else{
      if(expenseGraphList.contains(selectedItem))
      {
        expenseGraphList.remove(selectedItem)
        Toast.makeText(context, "$selectedItem has been removed", Toast.LENGTH_SHORT).show()
      }
      else
      {
        expenseGraphList.add(selectedItem)
        Toast.makeText(context, "$selectedItem has been added", Toast.LENGTH_SHORT).show()
      }
    }




      generateGraph(root)
      
    }



    // --- category switcher onClick, changes the displayed list ---
    val ts = root.findViewById<Switch>(R.id.typeSwitch)
    ts.setOnClickListener() {
      categorySwitcher()
      myLV.adapter = myAdapter
    }



    generateGraph(root)

    return root
  }

  // --- refresh the listview, for use after listview contents change ---
  private fun refreshListview(){
    myAdapter = ArrayAdapter<String>(this.requireContext(), android.R.layout.simple_list_item_1, currentList)
  }

  // --- Retrieve catergory lists from Shared Preferences ---
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

  // --- Switch list contents based on income vs expense switcher ---
  private fun categorySwitcher(){
    // --- Set income boolean to match the switch contents ---
    income = typeSwitch.isChecked

    generateGraph(root)
    updateList()
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
    refreshListview()
  }


  fun generateGraph(view: View)
  {
    val incomeCategoryCostSummary =  ArrayList<Float>()
    val expenseCategoryCostSummary =  ArrayList<Float>()

    val dbHelper = DatabaseHelper(requireContext())
    for(i in 0..incomeGraphList.size-1){
      incomeCategoryCostSummary.add(0F)
      // --- Cursor is used to iterate though the result of the database get call
      val cursor = dbHelper.getDataByCategory(incomeGraphList[i], true)
      while (cursor.moveToNext()) {
          incomeCategoryCostSummary[i] += cursor.getFloat(2)
      }
      incomeCategoryCostSummary[i] = abs(incomeCategoryCostSummary[i])
    }

    for(i in 0..expenseGraphList.size-1){
      expenseCategoryCostSummary.add(0F)
      // --- Cursor is used to iterate though the result of the database get call
      val cursor = dbHelper.getDataByCategory(expenseGraphList[i], false)
      while (cursor.moveToNext()) {
        expenseCategoryCostSummary[i] += cursor.getFloat(2)
      }
      expenseCategoryCostSummary[i] = abs(expenseCategoryCostSummary[i])
    }



  var labels = ""
  var data = ""
  for(i in 0 until expenseGraphList.size) {

      var label = expenseGraphList[i]
      Log.d(TAG, "generateGraph: $label")
      var d = expenseCategoryCostSummary[i].toString()
      labels += "'$label', "
      data += "$d, "
  }

    var incomeLabels = ""
    var incomeData = ""
    for(i in 0 until incomeGraphList.size) {
      var label = incomeGraphList[i]
      var d = incomeCategoryCostSummary[i].toString()
      incomeLabels += "'$label', "
      incomeData += "$d, "
    }

  var expenseColor = "rgba(255, 99, 132, 0.5)"
  var expenseBorderColor = "rgb(255, 99, 132)"
  var incomecolor = "rgba(34,139,34 0.5)"
  var incomeBordercolor = "rgb(0,100,0)"

    var chartLink = ""
    if(income){
      chartLink = "https://quickchart.io/chart?w=303&h=303&bkg=%23ffffff&c={\n" +
              "  \"type\": 'bar',\n" +
              "  \"data\": {\n" +
              "    \"labels\": [$incomeLabels],\n" +
              "    \"datasets\": [\n" +
              "      {\n" +
              "        \"label\": 'Income',\n" +
              "        \"backgroundColor\": '$incomecolor',\n" +
              "        \"borderColor\": '$incomeBordercolor',\n" +
              "        \"borderWidth\": 1,\n" +
              "        \"data\": [$incomeData],\n" +
              "      },\n" +
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
    }else{
      chartLink = "https://quickchart.io/chart?w=303&h=303&bkg=%23ffffff&c={\n" +
              "  \"type\": 'bar',\n" +
              "  \"data\": {\n" +
              "    \"labels\": [$labels],\n" +
              "    \"datasets\": [\n" +
              "      {\n" +
              "        \"label\": 'Expenses',\n" +
              "        \"backgroundColor\": '$expenseColor',\n" +
              "        \"borderColor\": '$expenseBorderColor',\n" +
              "        \"borderWidth\": 1,\n" +
              "        \"data\": [$data],\n" +
              "      },\n" +
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
    }

    val chart= view.findViewById<ImageView>(R.id.graphHolder)
    Picasso.get().load(chartLink).into(chart)
    //Log.d("HELLO",chartLink)
  }



}

