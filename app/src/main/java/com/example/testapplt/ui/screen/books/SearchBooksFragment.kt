package com.example.testapplt.ui.screen.books

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.testapplt.R
import com.example.testapplt.databinding.FragmentSearchBooksBinding
import com.example.testapplt.ui.adapter.BooksListAdapter
import com.example.testapplt.ui.adapter.BooksListLoadStateAdapter
import com.example.testapplt.ui.utils.hide
import com.example.testapplt.ui.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.testapplt.ui.utils.hideKeyboard
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged


@AndroidEntryPoint
class SearchBooksFragment : Fragment(R.layout.fragment_search_books) {

    companion object {
        fun getNewInstance() = SearchBooksFragment()

        private const val ONE_SYMBOL = 1
        private const val INSTANT_SEARCH = 0L
        private const val DELAYED_SEARCH = 700L
        private const val PAGE_SIZE = 10
    }

    private val binding: FragmentSearchBooksBinding by viewBinding()

    private val viewModel: SearchBooksViewModel by viewModels()

    private var adapter: BooksListAdapter? = null


    @FlowPreview
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        lifecycleScope.launchWhenStarted {
            adapter?.onPagesUpdatedFlow?.collect {
                if (adapter?.itemCount == PAGE_SIZE) {
                    binding.searchBooksRecyclerView.layoutManager?.scrollToPosition(0)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bookListState
                    .collect { books ->
                        when (books) {
                            is SearchBooksViewModel.BooksListState.BooksListSuccess ->
                                adapter?.submitData(
                                    viewLifecycleOwner.lifecycle,
                                    books.booksInfo
                                )
                            SearchBooksViewModel.BooksListState.Empty ->
                                adapter?.submitData(
                                    lifecycle,
                                    PagingData.empty()
                                )
                        }
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.liveSearch
                    .debounce { (searchParam, isInstant) ->
                        when {
                            searchParam.length == ONE_SYMBOL || isInstant -> INSTANT_SEARCH
                            else -> DELAYED_SEARCH
                        }
                    }
                    .distinctUntilChanged()
                    .collectLatest { (searchParam, _) ->
                        viewModel.searchPhotos(searchParam)
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchTypeState
                    .collect { searchType ->
                        when (searchType) {
                            SearchType.ALL ->
                                binding.searchBooksToolbar.searchBooksToolbarFilterActiveView.hide()
                            else ->
                                binding.searchBooksToolbar.searchBooksToolbarFilterActiveView.show()
                        }
                    }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initRecyclerView()
    }

    private fun initToolbar() {
        with(binding.searchBooksToolbar.searchBooksToolbarEditText) {
            hint = getString(R.string.search)

            setOnEditorActionListener { _, action, _ ->
                when {
                    (action == EditorInfo.IME_ACTION_SEARCH) -> {
                        viewModel.textFieldBeChanged(
                            text.toString(),
                            true
                        )
                        requireActivity().hideKeyboard()
                        true
                    }
                    else -> false
                }
            }

            addTextChangedListener {
                viewModel.textFieldBeChanged(
                    it.toString(),
                    false
                )
            }

        }

        binding.searchBooksToolbar.searchBooksToolbarFilterAppCompatImageButton.setOnClickListener {
            viewModel.navigateToFilters()
        }

    }


    private fun initRecyclerView() {
        with(binding) {

            adapter = BooksListAdapter()

            searchBooksRecyclerView.adapter = adapter?.withLoadStateHeaderAndFooter(
                header = BooksListLoadStateAdapter { adapter?.retry() },
                footer = BooksListLoadStateAdapter { adapter?.retry() },
            )

            adapter?.addLoadStateListener { loadState ->
                searchBooksNoConnectionImageView.isVisible =
                    loadState.source.refresh is LoadState.Error

                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter!!.itemCount < 1
                ) {
                    searchBooksNotAvailableTextView.show()
                } else {
                    searchBooksNotAvailableTextView.hide()
                }
            }
        }
    }
}