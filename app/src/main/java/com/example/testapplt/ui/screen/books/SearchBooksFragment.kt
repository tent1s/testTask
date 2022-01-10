package com.example.testapplt.ui.screen.books

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.paging.PagingData
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.testapplt.LOADING_GOOGLE_BOOKS_PAGE_SIZE
import com.example.testapplt.R
import com.example.testapplt.databinding.FragmentSearchBooksBinding
import com.example.testapplt.ui.adapter.BooksListAdapter
import com.example.testapplt.ui.adapter.BooksListLoadStateAdapter
import com.example.testapplt.ui.utils.launchWhenStart
import com.example.testapplt.ui.utils.hide
import com.example.testapplt.ui.utils.show
import dagger.hilt.android.AndroidEntryPoint
import com.example.testapplt.ui.utils.hideKeyboard
import kotlinx.coroutines.flow.onEach
import timber.log.Timber


@AndroidEntryPoint
class SearchBooksFragment : Fragment(R.layout.fragment_search_books) {

    companion object {
        fun getNewInstance() = SearchBooksFragment()
    }

    private val binding: FragmentSearchBooksBinding by viewBinding()

    private val viewModel: SearchBooksViewModel by viewModels()

    private var adapter: BooksListAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = BooksListAdapter()
        adapter?.onPagesUpdatedFlow?.onEach {
            if (adapter?.itemCount == LOADING_GOOGLE_BOOKS_PAGE_SIZE) {
                binding.searchBooksRecyclerView.layoutManager?.scrollToPosition(0)
            }
        }?.launchWhenStart(lifecycle)

        viewModel.bookListState
            .onEach { books ->
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
            }.launchWhenStart(lifecycle)

        viewModel.searchTypeState
            .onEach { searchType ->
                when (searchType) {
                    SearchType.ALL ->
                        binding.searchBooksToolbar.searchBooksToolbarFilterActiveView.hide()
                    else ->
                        binding.searchBooksToolbar.searchBooksToolbarFilterActiveView.show()
                }
            }.launchWhenStart(lifecycle)
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
                        viewModel.validateQueryForSearchBooks(
                            text.toString(),
                            isInstantSearch = true
                        )
                        requireActivity().hideKeyboard()
                        true
                    }
                    else -> false
                }
            }

            addTextChangedListener {
                viewModel.validateQueryForSearchBooks(
                    it.toString(),
                    isInstantSearch = false
                )
            }

        }

        binding.searchBooksToolbar.searchBooksToolbarFilterAppCompatImageButton.setOnClickListener {
            viewModel.navigateToFilters()
        }

    }


    private fun initRecyclerView() {
        with(binding) {

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