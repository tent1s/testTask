package com.example.testapplt.ui.screen.filters

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.testapplt.R
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.testapplt.databinding.FragmentSearchFiltersBinding
import com.example.testapplt.ui.screen.books.SearchType
import com.example.testapplt.ui.view.FilterView


@AndroidEntryPoint
class SearchFiltersFragment : Fragment(R.layout.fragment_search_filters) {

    companion object {
        private const val SEARCH_TYPE = "SearchType"

        fun getNewInstance(params: SearchType?) =
            SearchFiltersFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(SEARCH_TYPE, params)
                }
            }
    }

    private val binding: FragmentSearchFiltersBinding by viewBinding()

    private val viewModel: SearchFiltersViewModel by viewModels()

    private val filterViewToSearchType: List<Pair<FilterView, SearchType>>
        get() = listOf(
            binding.allSearchFiltersFilterView to SearchType.ALL,
            binding.authorSearchFiltersFilterView to SearchType.AUTHOR,
            binding.nameSearchFiltersFilterView to SearchType.NAME_VOLUME,
            binding.subjectSearchFiltersFilterView to SearchType.SUBJECT,
            binding.publisherSearchFiltersFilterView to SearchType.PUBLISHER,
        )


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchFiltersMaterialToolbar.setNavigationOnClickListener {
            viewModel.onBackClicked()
        }

        filterViewToSearchType.forEach { (view, searchType) ->
            if (arguments?.getParcelable<SearchType>(SEARCH_TYPE) == searchType)
                view.isActive = true
            else
                view.setOnClickListener {
                    viewModel.sendNewSearchType(searchType)
                }
        }
    }
}