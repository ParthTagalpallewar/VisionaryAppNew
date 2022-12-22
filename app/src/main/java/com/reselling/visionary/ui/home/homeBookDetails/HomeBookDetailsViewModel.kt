package com.reselling.visionary.ui.home.homeBookDetails

import androidx.lifecycle.*
import com.reselling.visionary.data.models.books.Books
import com.reselling.visionary.data.models.books.BooksResponseModel
import com.reselling.visionary.data.models.userModel.User
import com.reselling.visionary.data.network.networkResponseType.Resource
import com.reselling.visionary.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomeBookDetailsViewMode"

@HiltViewModel
class HomeBookDetailsViewModel @Inject constructor(
        private val repository: BookRepository
) : ViewModel() {

    val books = MutableLiveData<Books>()
    val seller = MutableLiveData<Resource<User>>()
    val sellerBooks = MutableLiveData<Resource<BooksResponseModel>>()


    fun getSellerBooks(sellerId: String) = viewModelScope.launch {
        sellerBooks.postValue(repository.getBooksAddedByUser(sellerId))
    }

    fun getSeller(sellerId: String) = viewModelScope.launch {
        seller.postValue(repository.getSellerById(sellerId))
    }

}
