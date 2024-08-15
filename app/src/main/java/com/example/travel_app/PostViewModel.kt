package com.example.travel_app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.travel_app.Spring.Bulletin.PostInterface
import com.example.travel_app.Spring.Bulletin.PostRequest
import com.google.api.core.ApiService
import kotlinx.coroutines.flow.Flow

class PostViewModel(private val postInterface: PostInterface) : ViewModel() {
    val posts: Flow<PagingData<PostRequest>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
            maxSize = 100
        ),
        pagingSourceFactory = { PostPagingSource(postInterface) }
    ).flow.cachedIn(viewModelScope)
}