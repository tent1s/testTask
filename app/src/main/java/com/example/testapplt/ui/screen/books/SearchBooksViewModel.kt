package com.example.testapplt.ui.screen.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapplt.domain.repository.GoogleBookRepository
import com.example.testapplt.domain.usecases.ListOfBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchBooksViewModel @Inject constructor(
    private val listOfBooksUseCase: ListOfBooksUseCase
): ViewModel() {

    fun getBooksByParameter(parameter: String){
        viewModelScope.launch {
            listOfBooksUseCase.getBooks(parameter).process(
                {

                },
                {

                }
            )
        }
    }

}