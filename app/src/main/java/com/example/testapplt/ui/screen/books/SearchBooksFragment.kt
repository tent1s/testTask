package com.example.testapplt.ui.screen.books

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.testapplt.R
import com.example.testapplt.databinding.FragmentSearchBooksBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchBooksFragment : Fragment(R.layout.fragment_search_books){

    companion object {
        fun newInstance() = SearchBooksFragment()
    }

    private val binding: FragmentSearchBooksBinding by viewBinding()

    private val viewModel: SearchBooksViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
    }



    private fun initToolbar(){
        with(binding.searchBooksToolbar.searchBooksToolbarEditText) {
            setOnEditorActionListener { _, action, _ ->
                if (action == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch()
                    true
                }else
                    false
            }
        }

        binding.searchBooksToolbar.searchBooksToolbarFilterAppCompatImageButton.setOnClickListener {

        }
    }

    private fun performSearch(){
        TODO()
    }

}