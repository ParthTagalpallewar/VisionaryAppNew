package com.reselling.visionary.data.repository.books

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.reselling.visionary.data.models.books.Books
import com.reselling.visionary.data.models.books.BooksResponseModel
import com.reselling.visionary.data.network.apis.BooksApi
import com.reselling.visionary.data.network.networkResponseType.MySafeApiRequest
import com.reselling.visionary.data.network.networkResponseType.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

val NETWORK_PAGE_SIZE = 10

class BooksRepository @Inject constructor(
    private val booksApi: BooksApi
) : MySafeApiRequest() {

    suspend fun getBooksInHome(
        limit: String,
        query: String,
        userId: String,
        district: String
    ): Resource<BooksResponseModel> =

        apiRequest {
            booksApi.getBooksInHome(
                per_page = limit.toInt(),
                query = query,
                userId = userId,
                district = district
            )
        }


    fun getSearchResults(
        query: String,
        userId: String,
        district: String
    ): LiveData<PagingData<Books>> {

     //   Log.e("Searcing", "query $query")
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                maxSize = 30,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { BooksPagingSource(userId, query, district, booksApi) }
        ).liveData
    }
}