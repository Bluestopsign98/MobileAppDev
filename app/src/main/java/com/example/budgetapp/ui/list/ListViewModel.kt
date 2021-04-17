package com.example.budgetapp.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This Fragment will have the recycle list thing that pulls data from the database to populate a list of all recent interactions. (incoming / outgoing moneys)"
    }
    val text: LiveData<String> = _text
}