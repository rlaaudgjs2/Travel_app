package com.example.travel_app

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.travel_app.Spring.Bulletin.PostRequest
import com.example.travel_app.Spring.Bulletin.PaginatedResponse
import com.example.travel_app.Spring.Bulletin.PostInterface
import retrofit2.HttpException
import java.io.IOException

class PostPagingSource(
    private val postInterface: PostInterface
) : PagingSource<Int, PostRequest>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostRequest> {
        try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 1
            val response = postInterface.getPosts(nextPageNumber, params.loadSize)

            if (!response.isSuccessful) {
                return LoadResult.Error(HttpException(response))
            }

            val body = response.body()
            return if (body != null) {
                LoadResult.Page(
                    data = body.content,
                    prevKey = if (nextPageNumber > 1) nextPageNumber - 1 else null,
                    nextKey = if (body.last) null else nextPageNumber + 1
                )
            } else {
                LoadResult.Error(IOException("Empty response body"))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PostRequest>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}