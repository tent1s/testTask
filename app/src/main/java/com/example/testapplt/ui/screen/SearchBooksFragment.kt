package com.example.testapplt.ui.screen

import androidx.fragment.app.Fragment
import com.example.testapplt.R
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.testapplt.databinding.FragmentSearchBooksBinding

class SearchBooksFragment : Fragment(R.layout.fragment_search_books){

    companion object {
        fun newInstance() = SearchBooksFragment()
    }

    private val viewBinding: FragmentSearchBooksBinding by viewBinding()
}