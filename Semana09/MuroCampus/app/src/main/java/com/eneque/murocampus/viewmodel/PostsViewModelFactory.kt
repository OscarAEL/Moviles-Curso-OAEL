package com.eneque.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eneque.murocampus.data.PostRepository

class PostsViewModelFactory(private val repo: PostRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(PostsViewModel::class.java)) {
            "ViewModel no compatible: ${modelClass.name}"
        }
        @Suppress("UNCHECKED_CAST")
        return PostsViewModel(repo) as T
    }
}
