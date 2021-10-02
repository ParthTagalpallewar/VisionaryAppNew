package com.reselling.visionary.ui.userAddedBooks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reselling.visionary.data.models.books.Books
import com.reselling.visionary.data.network.networkResponseType.Resource
import com.reselling.visionary.data.preferences.PreferencesManager
import com.reselling.visionary.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserBooksViewModel @Inject constructor(
        private val repository: BookRepository,
        private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _addedBooksEvents = Channel<UserAddedBooksEvent>()
    val bookEvents = _addedBooksEvents.receiveAsFlow()

    val books = MutableLiveData<ArrayList<Books>>()

    val bottomSheetBook = MutableLiveData<Books>()

    init {
        getBooks()
    }

    fun getBooks() {
        viewModelScope.launch {
            preferencesManager.preferenceFlow.collect {
                getUserBooks(it.id)
            }
        }
    }

    private fun getUserBooks(userId: String) = viewModelScope.launch {
        _addedBooksEvents.send(UserAddedBooksEvent.LoadingEvent)
        val userAddedBookResponse = repository.getBooksAddedByUser(userId)

        when (userAddedBookResponse) {
            is Resource.NoInterException ->
                _addedBooksEvents.send(UserAddedBooksEvent.InternetProblem)
            is Resource.Failure -> {
                val errorMsg = "Error : ${userAddedBookResponse.errorBody}"
                showInvalidInputMessage(errorMsg)
            }

            is Resource.Success -> {
                withContext(Dispatchers.IO) {

                    books.postValue(userAddedBookResponse.value.books)

                    _addedBooksEvents.send(UserAddedBooksEvent.ShowDataInRecyclerView(userAddedBookResponse.value.books))

                }

            }
        }
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        _addedBooksEvents.send(UserAddedBooksEvent.ShowInvalidInputMessage(text))
    }

    fun deleteBook(id: String) = viewModelScope.launch {
        _addedBooksEvents.send(UserAddedBooksEvent.LoadingEvent)

        val bookDeletedResponse = repository.deleteBook(id)
        when (bookDeletedResponse) {
            is Resource.NoInterException ->
                _addedBooksEvents.send(UserAddedBooksEvent.InternetProblem)
            is Resource.Failure -> {
                val errorMsg = "Error : ${bookDeletedResponse.errorBody}"
                showInvalidInputMessage(errorMsg)
            }

            is Resource.Success -> {
                withContext(Dispatchers.IO) {

                    _addedBooksEvents.send(UserAddedBooksEvent.BookDeletedSuccessEvent)

                }

            }
        }
    }

    sealed class UserAddedBooksEvent {
        data class ShowInvalidInputMessage(val msg: String) : UserAddedBooksEvent()
        data class ShowDataInRecyclerView(val books: ArrayList<Books>) : UserAddedBooksEvent()
        object InternetProblem : UserAddedBooksEvent()
        object LoadingEvent : UserAddedBooksEvent()
        object BookDeletedSuccessEvent : UserAddedBooksEvent()


    }
}