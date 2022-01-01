package com.example.testapplt.ui.screen.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapplt.domain.model.ErrorReason
import com.example.testapplt.domain.model.domain.BooksInfo
import com.example.testapplt.domain.usecases.ListOfBooksUseCase
import com.example.testapplt.ui.screen.Screens.searchFiltersScreen
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class SearchBooksViewModel @Inject constructor(
    private val listOfBooksUseCase: ListOfBooksUseCase,
    private val router : Router
): ViewModel() {

    companion object{
        private const val COUNT_ITEMS_FROM_BACKAND = 20
    }

    private var getBooksRequest: Job? = null

    private val mutableBookList = MutableStateFlow<BooksListState>(BooksListState.Empty)
    val bookList: StateFlow<BooksListState> = mutableBookList.asStateFlow()

    private val mutableLiveSearch = MutableSharedFlow<String>()
    val liveSearch = mutableLiveSearch.asSharedFlow()

    private var isLoadingNextBooks = false

    private var textFieldContent = ""

    private val mutableSearchType = MutableStateFlow(SearchType.ALL)
    val searchType: StateFlow<SearchType> = mutableSearchType.asStateFlow()


    sealed class BooksListState {
        object Empty : BooksListState()
        object NoConnection : BooksListState()
        class Error(val errorReason: ErrorReason) : BooksListState()
        class BooksListSuccess(val booksInfo: List<BooksInfo>, val isShowLoader: Boolean) : BooksListState()
    }

    fun setEmptyState(){
        viewModelScope.launch {
            mutableBookList.emit(BooksListState.Empty)
        }
    }

    fun textFieldBeChanged(parameter: String){
        textFieldContent = parameter
        when{
            parameter.isBlank() -> viewModelScope.launch {
                mutableBookList.emit(BooksListState.Empty)
                getBooksRequest?.cancel()
            }
            else -> viewModelScope.launch {
                mutableLiveSearch.emit(parameter)
            }
        }
    }


    fun getBooksByParameter(parameter: String){
        if (parameter.isBlank() || textFieldContent.isBlank()) return
        getBooksRequest?.cancel()
        getBooksRequest = viewModelScope.launch {
            listOfBooksUseCase.getBooks(searchType.value.typeSignature,
                parameter,
                COUNT_ITEMS_FROM_BACKAND).process(
                {
                    setRequestErrorState(it)
                },
                {
                    launch {
                        it?.let { notNull ->
                            if (notNull.size < COUNT_ITEMS_FROM_BACKAND) {
                                mutableBookList.emit(
                                    BooksListState.BooksListSuccess(
                                        notNull,
                                        false
                                    )
                                )
                            } else {
                                mutableBookList.emit(
                                    BooksListState.BooksListSuccess(
                                        notNull,
                                        true
                                    )
                                )
                            }
                        }
                        if (it.isNullOrEmpty()) {
                            mutableBookList.emit(BooksListState.Empty)
                        }
                    }
                }
            )
        }
    }

    private fun setRequestErrorState(errorReason: ErrorReason) {
        viewModelScope.launch {
            when (errorReason) {
                is ErrorReason.NetworkError -> mutableBookList.emit(BooksListState.NoConnection)
                else -> mutableBookList.emit(BooksListState.Error(errorReason))
            }
        }
    }

    fun validateLoadMoreBook(parameter: String){
        when{
            parameter.isBlank() || isLoadingNextBooks -> return
            bookList.value is BooksListState.BooksListSuccess -> {
                isLoadingNextBooks = true

                val currentList = (bookList.value as BooksListState.BooksListSuccess)
                    .booksInfo
                    .toMutableList()

                getMoreBooks(parameter, currentList)
            }
        }
    }

    private fun getMoreBooks(parameter: String, currentList: MutableList<BooksInfo>){
        getBooksRequest = viewModelScope.launch {
            listOfBooksUseCase.getBooks(searchType.value.typeSignature,
                parameter,
                COUNT_ITEMS_FROM_BACKAND,
                currentList.size).process(
                {
                    setRequestErrorState(it)
                },
                {
                    launch {
                        it?.let {
                            currentList.addAll(it)
                            val listWithOutDuplicates =
                                ArrayList(LinkedHashSet(currentList)) // we must do it, because the backend has a bug, it sends duplicates

                            if (listWithOutDuplicates.size == currentList.size - it.size) {
                                mutableBookList.emit(
                                    BooksListState.BooksListSuccess(
                                        listWithOutDuplicates,
                                        false
                                    )
                                )
                            } else {
                                mutableBookList.emit(
                                    BooksListState.BooksListSuccess(
                                        listWithOutDuplicates,
                                        it.isNotEmpty()
                                    )
                                )
                            }
                        } ?: run {
                            mutableBookList.emit(
                                BooksListState.BooksListSuccess(
                                    ArrayList(LinkedHashSet(currentList)), // we must do it, because the backend has a bug, it sends duplicates
                                    false
                                )
                            )
                        }
                        isLoadingNextBooks = false
                        Timber.i("Loaded more books")
                    }
                }
            )
        }
    }

    fun navigateToFilters(){
        router.setResultListener(RESULT_KEY) { data ->
            viewModelScope.launch {
                mutableSearchType.emit(data as SearchType)
            }
            getBooksByParameter(textFieldContent)
        }
        router.navigateTo(searchFiltersScreen(searchType.value))
    }

}