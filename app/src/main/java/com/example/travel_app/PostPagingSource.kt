package com.example.travel_app

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.travel_app.Spring.Bulletin.PostRequest
import com.example.travel_app.Spring.Bulletin.PaginatedResponse
import com.example.travel_app.Spring.Bulletin.PostInterface
import retrofit2.HttpException
import java.io.IOException

class PostPagingSource(private val postInterface: PostInterface) : PagingSource<Int, PostRequest>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostRequest> {
        try {
            val page = params.key ?: 0
            val response = postInterface.getPosts(page, params.loadSize)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    return LoadResult.Page(
                        data = body.content,
                        prevKey = if (page > 0) page - 1 else null,
                        nextKey = if (!body.last) page + 1 else null
                    )
                }
            }
            return LoadResult.Error(HttpException(response))
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PostRequest>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}