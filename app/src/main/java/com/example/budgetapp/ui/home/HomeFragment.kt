package com.example.budgetapp.ui.home

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetapp.DatabaseHelper
import com.example.budgetapp.R
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONObject
import com.squareup.picasso.Picasso
import java.math.RoundingMode
import java.text.DecimalFormat

class HomeFragment : Fragment() {

  private lateinit var homeViewModel: HomeViewModel

  override fun onCreateView(
          inflater: LayoutInflater,
          container: ViewGroup?,
          savedInstanceState: Bundle?
  ): View? {
    homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_home, container, false)
    generateSummary(root)

    return root
  }

  private fun generateSummary(view: View)
  {
    var totalExpenses = 0.0
    var totalIncome = 0.0
    var totalInvestments = 0.0
    //To access your database, instantiate your subclass of SQLiteOpenHelper
    val dbHelper = DatabaseHelper(requireContext())
    // --- Cursor is used to iterate though the result of the database get call
    val cursor = dbHelper.viewAllData
    while (cursor.moveToNext()) {
      if(cursor.getFloat(2) >= 0.0) //A positive transaction, add to income total
      {
        totalIncome += cursor.getFloat(2)
      }
      else //A negative transaction
      {
        if(cursor.getString(5) == "Investments" || cursor.getString(5) == "Investment")
        {
          totalInvestments += cursor.getFloat(2)
        }
        else
        {
          totalExpenses += cursor.getFloat(2)
        }

      }
    }


    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING

    // --- Round totals to 2 decimal places ---
    totalExpenses = df.format(totalExpenses).toDouble()
    totalIncome = df.format(totalIncome).toDouble()
    totalInvestments = df.format(totalInvestments).toDouble()
    totalExpenses *= -1
    totalInvestments *= -1
    var diff = totalIncome-totalExpenses-totalInvestments
    var moneyPref = ""
    if(diff > 0.0)
    {
      moneyPref = "%2B"
    }
    else {
      if (diff < 0.0)
        moneyPref = ""
    }
    diff = df.format(diff).toDouble()
    var diffString = moneyPref + diff.toString()
    var labels = ""
    var data = ""
    labels += "', "
    data += "$, "


    var chartLink ="https://quickchart.io/chart?w=850&h=850&c=" +
            "{\n  \"type\": \"doughnut\",\n "+
            " \"data\": {\n    \"datasets\": [\n      {\n        \"data\": [\n          $totalIncome,\n          $totalExpenses,\n          $totalInvestments,\n        ],\n        \"backgroundColor\": [\n          \"rgb(72, 245, 44)\",\n          \"rgb(236, 64, 45)\",\n          \"rgb(255, 205, 86)\"\n        ],\n        \"borderColor\": \"%205c5c5c\",\n        \"label\": \"Dataset 1\"\n      }\n    ],\n    \"labels\": [\n      \"Income \",\n      \"Expenses \",\n      \"Inventments \"\n    ]\n  },\n  \"options\": {\n     " +
            "\"legend\": {\n" +
            "      \"position\": 'bottom',\n" +
            "      \"labels\": {\n" +
            "        \"fontSize\": 40,\n" +
            "        \"fontStyle\": 'bold',\n" +
            "      }\n" +
            "    },   \"plugins\": {\n      \"datalabels\": {\n        \"display\": true,\n  \"color\": 'black',\n     \"borderRadius\": 3,\n        \"font\": {\n          \"weight\": \"bold\",\n          \"size\": 60\n        }\n      },\n      \"doughnutlabel\": {\n        \"labels\": [\n          {\n            \"text\": \"$diffString\",\n            \"font\": {\n              \"size\": 65,\n              \"weight\": \"bold\"\n            }\n          },\n          {\n            \"text\": \"\"\n          }\n        ]\n      }\n    }\n  }\n}&format=.png"




    Log.d("YO",chartLink)
    val chart= view.findViewById<ImageView>(R.id.incomeSummaryChart)
    Picasso.get().load(chartLink).into(chart)

  }


}