package com.example.testapplt.ui.screen.filters

import androidx.lifecycle.ViewModel
import com.example.testapplt.NAVIGATION_RESULT_KEY
import com.example.testapplt.ui.screen.books.SearchType
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchFiltersViewModel @Inject constructor(
    private val router: Router
) : ViewModel() {


    fun sendNewSearchType(searchType: SearchType) {
        router.sendResult(NAVIGATION_RESULT_KEY, searchType)
        router.exit()
    }

    fun onBackClicked() {
        router.exit()
    }
}