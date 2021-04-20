package com.example.budgetapp.ui

data class Transaction(val id: Int, val name: String, var amount: Float, val description: String = "No desc", val date: String, val category: String, val income: Boolean, val dollarImage: Int)
