package com.example.testapplt.ui.screen.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapplt.domain.model.ErrorReason
import com.example.testapplt.domain.model.domain.BooksInfo
import com.example.testapplt.domain.usecases.ListOfBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchBooksViewModel @Inject constructor(
    private val listOfBooksUseCase: ListOfBooksUseCase
): ViewModel() {


    private val mutableBookList = MutableStateFlow<BooksListState>(BooksListState.Empty)
    val bookList: StateFlow<BooksListState> = mutableBookList.asStateFlow()


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


    fun getBooksByParameter(parameter: String){
        viewModelScope.launch {
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

}