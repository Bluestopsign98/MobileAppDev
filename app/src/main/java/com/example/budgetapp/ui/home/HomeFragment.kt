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
    totalExpenses *= -1
    var diff = totalIncome-totalExpenses
    var moneyPref = ""
    if(diff > 0.0)
    {
      moneyPref = "%2B"
    }
    else {
      if (diff < 0.0)
        moneyPref = ""
    }
    var TIShort = totalIncome
    var TEShort = totalExpenses
    while(TEShort > 100 || TIShort > 100)
    {
      TEShort /= 10
      TIShort /= 10
    }

    var labels = ""
    var data = ""
    labels += "', "
    data += "$, "


    var chartLink =" https://quickchart.io/chart?c={ " +
            "   type: 'doughnut', " +
            "   data: { " +
            "     datasets: [ " +
            "       { " +
            "         data: [$totalIncome, $totalExpenses, $totalInvestments], " +
            "         backgroundColor: [ " +
            "           'rgb(72, 245, 44)', " +
            "           'rgb(236, 64, 45)', " +
            "           'rgb(255, 205, 86)', " +
            "         ], " +
            "         borderColor: '#5c5c5c', " +
            "         label: 'Dataset 1', " +
            "       }, " +
            "     ], " +
            "     labels: ['Income', 'Expenses', 'Inventments',], " +
            "   }, " +
            "   options: { " +
            "     title: { " +
            "       fontSize: 15, " +
            "       display: true, " +
            "       text: 'Financial Summary', " +
            "     }, " +
            "   }, " +
            " } " +
            "  "



    val chart= view.findViewById<ImageView>(R.id.incomeSummaryChart)
    Picasso.get().load("https://image-charts.com/chart?chco=95f991%2CF99191&chd=t%3A$TIShort%2C$TEShort&chl=$totalIncome%7C$totalExpenses&chli=$moneyPref$diff&chlps=font.size%2C64&chs=800x800&cht=pd&chof=.png").into(chart)

  }


}