package com.example.budgetapp.ui

data class Transaction(val id: Int, val name: String, var amount: String, val description: String = "No desc", val date: String, val dollarImage: Int)