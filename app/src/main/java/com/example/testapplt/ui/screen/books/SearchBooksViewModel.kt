package com.example.testapplt.ui.screen.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapplt.domain.model.ErrorReason
import com.example.testapplt.domain.model.domain.BooksInfo
import com.example.testapplt.domain.usecases.ListOfBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class SearchBooksViewModel @Inject constructor(
    private val listOfBooksUseCase: ListOfBooksUseCase
): ViewModel() {

    private var getBooksRequest: Job? = null

    private val mutableBookList = MutableStateFlow<BooksListState>(BooksListState.Empty)
    val bookList: StateFlow<BooksListState> = mutableBookList.asStateFlow()

    private val mutableLiveSearch = MutableSharedFlow<String>()
    val liveSearch = mutableLiveSearch.asSharedFlow().debounce(700).distinctUntilChanged()

    private var isLoadingNextBooks = false


    sealed class BooksListState {
        object Empty : BooksListState()
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
            }
            else -> viewModelScope.launch {
                mutableLiveSearch.emit(parameter)
            }
        }
    }


    fun startSearch(parameter: String){
        getBooksByParameter(parameter)
    }


    fun getBooksByParameter(parameter: String){
        if (parameter.isBlank()) return
        getBooksRequest?.cancel()
        getBooksRequest = viewModelScope.launch {
            listOfBooksUseCase.getBooks(parameter).process(
                {
                    launch {
                        mutableBookList.emit(BooksListState.Error(it))
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
                    launch {
                        mutableBookList.emit(BooksListState.Error(it))
                    }
                },
                {
                    it?.let {
                        launch {
                            currentList.addAll(it)
                            mutableBookList.emit(BooksListState.BooksListSuccess(currentList))
                        }
                    }
                    isLoadingNextBooks = false
                    Timber.i("Loaded more books")
                }
            )
        }
    }

}