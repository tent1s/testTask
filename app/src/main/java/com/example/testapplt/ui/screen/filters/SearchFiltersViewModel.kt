package com.example.testapplt.ui.screen.filters

import androidx.lifecycle.ViewModel
import com.example.testapplt.ui.screen.books.RESULT_KEY
import com.example.testapplt.ui.screen.books.SearchType
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@HiltViewModel
class SearchFiltersViewModel @Inject constructor(
    private val router : Router
): ViewModel() {

    @FlowPreview
    fun sendNewType(searchType: SearchType){
        router.sendResult(RESULT_KEY, searchType)
        router.exit()
    }

    fun onBackClicked(){
        router.exit()
    }
}