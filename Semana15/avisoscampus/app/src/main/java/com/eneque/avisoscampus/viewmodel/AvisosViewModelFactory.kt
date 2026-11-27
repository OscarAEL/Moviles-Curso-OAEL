package com.eneque.avisoscampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eneque.avisoscampus.data.AvisosRepository

class AvisosViewModelFactory(private val repo: AvisosRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(AvisosViewModel::class.java))
        @Suppress("UNCHECKED_CAST")
        return AvisosViewModel(repo) as T
    }
}
