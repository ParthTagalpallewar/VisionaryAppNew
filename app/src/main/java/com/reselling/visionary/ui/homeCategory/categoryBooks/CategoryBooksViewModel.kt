package com.reselling.visionary.ui.homeCategory.categoryBooks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.reselling.visionary.data.models.userModel.User
import com.reselling.visionary.data.repository.AuthRepository
import com.reselling.visionary.data.repository.BookRepository
import com.reselling.visionary.utils.Choose
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class CategoryBooksViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val userRepository: AuthRepository
): ViewModel() {

    private val query = MutableLiveData<String>("")
    private val category = MutableLiveData<String>(Choose)
    private val userFlow: Flow<User> = userRepository.getUserFlow()

   private val categoryBooksFlow = combine(
        query.asFlow(),
        category.asFlow(),
        userFlow
    ) { query, category, user ->
        Triple(query, category, user)
    }.flatMapLatest { (query, category, user) ->
        flow {
            emit(bookRepository.getBooksByCategory(user.district, user.id, category, query))
        }
    }

    val catgoryBooks = categoryBooksFlow.asLiveData()


    fun searchBooks(queryPara: String) {
        query.postValue(queryPara)
    }

    fun setCategory(categoryPara: String) {
        category.postValue(categoryPara)
    }
}