package com.example.testapplt.ui.screen.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.testapplt.domain.model.domain.BooksInfo
import com.example.testapplt.domain.usecases.BooksSearchUseCase
import com.example.testapplt.ui.screen.Screens.searchFiltersScreen
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject



@HiltViewModel
class SearchBooksViewModel @Inject constructor(
    private val listOfBooksUseCase: BooksSearchUseCase,
    private val router: Router
) : ViewModel() {

    companion object{
        private const val ONE_SYMBOL = 1
    }

    private val mutableBookListState = MutableStateFlow<BooksListState>(BooksListState.Empty)
    val bookListState: StateFlow<BooksListState> = mutableBookListState.asStateFlow()

    private var textFieldContent = ""

    private val mutableSearchTypeState = MutableStateFlow(SearchType.ALL)
    val searchTypeState: StateFlow<SearchType> = mutableSearchTypeState.asStateFlow()

    sealed class BooksListState {
        object Empty : BooksListState()
        class BooksListSuccess(val booksInfo: PagingData<BooksInfo>) : BooksListState()
    }

    init {
        viewModelScope.launch {
            listOfBooksUseCase.getSearchChange()
                .cachedIn(viewModelScope)
                .onEach { result ->
                    mutableBookListState.value = BooksListState.BooksListSuccess(result)
                }.launchIn(viewModelScope)
        }
    }

    fun validateQueryForSearchBooks(query: String, isInstantSearch: Boolean) {
        viewModelScope.launch {
            textFieldContent = query
            when {
                query.isBlank() -> mutableBookListState.emit(BooksListState.Empty)
                else -> {
                    onBooksSubmit(
                        query,
                        query.length == ONE_SYMBOL || isInstantSearch
                    )
                }
            }
        }
    }


    fun navigateToFilters() {
        router.setResultListener(RESULT_KEY) { data ->
            viewModelScope.launch {
                mutableSearchTypeState.emit(data as SearchType)
            }
            validateQueryForSearchBooks(textFieldContent, isInstantSearch = true)
        }
        router.navigateTo(searchFiltersScreen(searchTypeState.value))
    }

    private fun onBooksSubmit(query: String, isInstantSearch: Boolean) {
        viewModelScope.launch {
            listOfBooksUseCase.onBooksSubmit(
                convertTextEditQueryToSearchFormat(query),
                isInstantSearch
            )
        }
    }


    private fun convertTextEditQueryToSearchFormat(query: String) = "${searchTypeState.value}$query"


}