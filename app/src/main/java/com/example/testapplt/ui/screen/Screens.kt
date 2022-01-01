package com.example.testapplt.ui.screen

import androidx.fragment.app.Fragment
import com.example.testapplt.ui.screen.books.SearchBooksFragment
import com.example.testapplt.ui.screen.books.SearchType
import com.example.testapplt.ui.screen.filters.SearchFiltersFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen
import kotlinx.coroutines.FlowPreview



@FlowPreview
object Screens {
    fun searchFiltersScreen(filter: SearchType) = FragmentScreen {
        SearchFiltersFragment.getNewInstance(filter)
    }
    fun searchBooksScreen() = FragmentScreen {
        SearchBooksFragment.getNewInstance()
    }
}