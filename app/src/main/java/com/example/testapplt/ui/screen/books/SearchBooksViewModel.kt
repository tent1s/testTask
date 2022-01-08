package com.example.testapplt.ui.screen.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.testapplt.domain.model.domain.BooksInfo
import com.example.testapplt.domain.usecases.ListOfBooksUseCase
import com.example.testapplt.ui.screen.Screens.searchFiltersScreen
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject


@HiltViewModel
class SearchBooksViewModel @Inject constructor(
    private val listOfBooksUseCase: ListOfBooksUseCase,
    private val router: Router
) : ViewModel() {


    private val mutableBookListState = MutableStateFlow<BooksListState>(BooksListState.Empty)
    val bookListState: StateFlow<BooksListState> = mutableBookListState.asStateFlow()

    private val mutableLiveSearch = MutableSharedFlow<Pair<String, Boolean>>()
    val liveSearch = mutableLiveSearch.asSharedFlow()

    private var textFieldContent = ""

    private val mutableSearchTypeState = MutableStateFlow(SearchType.ALL)
    val searchTypeState: StateFlow<SearchType> = mutableSearchTypeState.asStateFlow()

    sealed class BooksListState {
        object Empty : BooksListState()
        class BooksListSuccess(val booksInfo: PagingData<BooksInfo>) : BooksListState()
    }

    fun textFieldBeChanged(parameter: String, isInstant: Boolean) {
        viewModelScope.launch {
            textFieldContent = parameter
            when {
                parameter.isBlank() -> mutableBookListState.emit(BooksListState.Empty)
                else -> {
                    mutableLiveSearch.emit(Pair(parameter, isInstant))
                }
            }
        }
    }

    fun searchPhotos(query: String) {
        viewModelScope.launch {
            listOfBooksUseCase.getBooksFlow(query, searchTypeState.value.typeSignature)
                .cachedIn(viewModelScope)
                .onEach { result ->
                    mutableBookListState.value = BooksListState.BooksListSuccess(result)
                }.launchIn(viewModelScope)
        }
    }

    fun navigateToFilters() {
        router.setResultListener(RESULT_KEY) { data ->
            viewModelScope.launch {
                mutableSearchTypeState.emit(data as SearchType)
            }
            searchPhotos(textFieldContent)
        }
        router.navigateTo(searchFiltersScreen(searchTypeState.value))
    }

}