package com.reselling.visionary.ui.home.homeAllBooks

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.reselling.visionary.data.repository.AuthRepository
import com.reselling.visionary.data.repository.books.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class HomeAllBooksViewModel @Inject constructor(
    private val bookRepo: BooksRepository,
    private val userRepository: AuthRepository
): ViewModel() {

    private val searchQuery = MutableLiveData<String>("")

    private val currentUser = userRepository.getUserFlow()

    private  val booksFlow = combine(
        searchQuery.asFlow(),
        currentUser
    ) { query, user ->
        Pair(query, user)
    }.flatMapLatest { (query, user) ->


        flow {
            val flow = bookRepo.getSearchResults( query, user.id, user.district).cachedIn(viewModelScope)
            emit(flow)
        }
    }

    val books = booksFlow.asLiveData()



    fun searchPhotos(query: String) {
        searchQuery.value = query
    }
}