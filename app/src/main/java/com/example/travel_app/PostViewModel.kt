package com.example.travel_app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.api.core.ApiService

class PostViewModel(private val apiService: ApiService) : ViewModel() {
    val posts = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { PostPagingSource(apiService) }
    ).flow.cachedIn(viewModelScope)
}