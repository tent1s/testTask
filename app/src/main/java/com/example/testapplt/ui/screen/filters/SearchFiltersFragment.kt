package com.example.testapplt.ui.screen.filters

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.testapplt.R
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.testapplt.databinding.FragmentSearchFiltersBinding
import com.example.testapplt.ui.screen.books.SearchBooksViewModel
import kotlinx.coroutines.FlowPreview

@FlowPreview
@AndroidEntryPoint
class SearchFiltersFragment : Fragment(R.layout.fragment_search_filters){

    companion object {
        private const val EXTRA_NAME = "tcf_extra_name"

        fun getNewInstance(name: String?) =
            SearchFiltersFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_NAME, name)
                }
            }
    }

    private val binding: FragmentSearchFiltersBinding by viewBinding()

    private val viewModel: SearchFiltersViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            viewModel.returnToBooksScreen()
        }
        binding.searchFiltersMaterialToolbar.setNavigationOnClickListener {
            viewModel.returnToBooksScreen()
        }
    }
}