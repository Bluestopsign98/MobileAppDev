package com.example.budgetapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This fragment will have a couple pie charts that use the restful api call. It will also show your net income this month/year/week"
    }
    val text: LiveData<String> = _text
}