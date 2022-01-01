package com.example.testapplt.ui.screen.filters

import androidx.lifecycle.ViewModel
import com.example.testapplt.ui.screen.SearchBooksScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@HiltViewModel
class SearchFiltersViewModel @Inject constructor(
    private val router : Router
): ViewModel() {

    @FlowPreview
    fun returnToBooksScreen(){
        router.backTo(SearchBooksScreen("None"))
    }
}