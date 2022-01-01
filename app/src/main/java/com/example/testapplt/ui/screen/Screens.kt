package com.example.testapplt.ui.screen

import androidx.fragment.app.Fragment
import com.example.testapplt.ui.screen.books.SearchBooksFragment
import com.example.testapplt.ui.screen.filters.SearchFiltersFragment

import kotlinx.coroutines.FlowPreview
import ru.terrakok.cicerone.android.support.SupportAppScreen


@FlowPreview
class SearchBooksScreen(
    private val filter: String
) : SupportAppScreen() {

    override fun getFragment(): Fragment =
        SearchBooksFragment.getNewInstance(filter)
}

@FlowPreview
class SearchFiltersScreen(
    private val filter: String
) : SupportAppScreen() {

    override fun getFragment(): Fragment =
        SearchFiltersFragment.getNewInstance(filter)
}