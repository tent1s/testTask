package com.example.testapplt.ui.screen.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapplt.domain.model.ErrorReason
import com.example.testapplt.domain.model.domain.BooksInfo
import com.example.testapplt.domain.usecases.ListOfBooksUseCase
import com.example.testapplt.ui.screen.SearchFiltersScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class SearchBooksViewModel @Inject constructor(
    private val listOfBooksUseCase: ListOfBooksUseCase,
    private val router : Router
): ViewModel() {

    private var getBooksRequest: Job? = null

    private val mutableBookList = MutableStateFlow<BooksListState>(BooksListState.Empty)
    val bookList: StateFlow<BooksListState> = mutableBookList.asStateFlow()

    private val mutableLiveSearch = MutableSharedFlow<String>()
    val liveSearch = mutableLiveSearch.asSharedFlow()

    private var isLoadingNextBooks = false
    private var isTextFiledEmpty = true


    sealed class BooksListState {
        object Empty : BooksListState()
        object NoConnection : BooksListState()
        class Error(val errorReason: ErrorReason) : BooksListState()
        class BooksListSuccess(val booksInfo: List<BooksInfo>) : BooksListState()
    }

    fun setEmptyState(){
        viewModelScope.launch {
            mutableBookList.emit(BooksListState.Empty)
        }
    }

    fun textFieldBeChanged(parameter: String){
        when{
            parameter.isBlank() -> viewModelScope.launch {
                mutableBookList.emit(BooksListState.Empty)
                getBooksRequest?.cancel()
                isTextFiledEmpty = true
            }
            else -> viewModelScope.launch {
                mutableLiveSearch.emit(parameter)
                isTextFiledEmpty = false
            }
        }
    }


    fun startSearch(parameter: String){
        getBooksByParameter(parameter)
    }


    fun getBooksByParameter(parameter: String){
        if (parameter.isBlank() || isTextFiledEmpty) return
        getBooksRequest?.cancel()
        getBooksRequest = viewModelScope.launch {
            listOfBooksUseCase.getBooks(parameter).process(
                {
                    when(it){
                        is ErrorReason.NetworkError -> launch {
                            mutableBookList.emit(BooksListState.NoConnection)
                        }
                        else -> launch {
                            mutableBookList.emit(BooksListState.Error(it))
                        }
                    }
                },
                {
                    it?.let { notNull ->
                        launch {
                            mutableBookList.emit(BooksListState.BooksListSuccess(notNull))
                        }
                    }
                    if (it.isNullOrEmpty()){
                        launch {
                            mutableBookList.emit(BooksListState.Empty)
                        }
                    }

                }
            )
        }
    }

    fun validateLoadMoreBook(parameter: String){
        when{
            parameter.isBlank() -> return
            isLoadingNextBooks -> return
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
            listOfBooksUseCase.getBooks(parameter, currentList.size).process(
                {
                    when(it){
                        is ErrorReason.NetworkError -> launch {
                            mutableBookList.emit(BooksListState.NoConnection)
                        }
                        else -> launch {
                            mutableBookList.emit(BooksListState.Error(it))
                        }
                    }
                },
                {
                    it?.let {
                        launch {
                            currentList.addAll(it)
                            mutableBookList.emit(BooksListState.BooksListSuccess(
                                ArrayList(LinkedHashSet(currentList))       // we must do it, because the backend has a bug, it sends duplicates
                            ))

                        }
                    }
                    isLoadingNextBooks = false
                    Timber.i("Loaded more books")
                }
            )
        }
    }

    fun navigateToFilters(){
        router.navigateTo(SearchFiltersScreen("None"))
    }

}