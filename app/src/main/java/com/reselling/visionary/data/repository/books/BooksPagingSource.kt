package com.reselling.visionary.data.repository.books

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.reselling.visionary.data.models.books.Books
import com.reselling.visionary.data.network.apis.BooksApi
import retrofit2.HttpException
import java.io.IOException

private const val Book_STARTING_PAGE_INDEX = 0

class BooksPagingSource(
        private val userId: String,
        private val query: String,
        private val district: String,
        private val booksApi: BooksApi
        ) : PagingSource<Int, Books>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Books> {

        val start = params.key ?: Book_STARTING_PAGE_INDEX
        val perPage = params.loadSize

        return try {
            val response = booksApi.getBooksUsingPaging(perPage, start, district, userId, query)
            val books = response.books

            LoadResult.Page(
                    data = books,
                    prevKey = if (start == Book_STARTING_PAGE_INDEX) null else start - perPage,
                    nextKey = if (books.isEmpty()) null else start + perPage
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }


    }

    override fun getRefreshKey(state: PagingState<Int, Books>): Int? {
        TODO("Not yet implemented")
    }
}