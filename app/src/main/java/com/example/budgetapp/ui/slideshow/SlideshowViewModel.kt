package com.example.budgetapp.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SlideshowViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Mini todo list app inside here? notes for upcoming bills? stuff like that?"
    }
    val text: LiveData<String> = _text
}