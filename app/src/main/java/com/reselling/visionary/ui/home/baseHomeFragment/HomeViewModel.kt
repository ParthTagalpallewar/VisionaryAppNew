package com.reselling.visionary.ui.home.baseHomeFragment

import androidx.lifecycle.*
import com.reselling.visionary.data.models.userModel.User
import com.reselling.visionary.data.preferences.PreferencesManager
import com.reselling.visionary.data.repository.AuthRepository
import com.reselling.visionary.data.repository.books.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(
        private val bookRepo: BooksRepository,
        private val userRepository: AuthRepository,
        private val state: SavedStateHandle,
) : ViewModel() {


    val searchQuery = state.getLiveData("searchQuery", "")

    fun searchBook(query: String) {
        searchQuery.value = query
    }

    val user: Flow<User> = userRepository.getUserFlow()

    private val booksFlow = combine(
            searchQuery.asFlow(),
            user
    ) { query, user ->
        Pair(query, user)
    }.flatMapLatest { (query, user) ->

        flow {
                val flow = bookRepo.getBooksInHome("10", query, user.id, user.district)
                emit(flow)

        }
    }

    val booksLiveData = booksFlow.asLiveData()


}




