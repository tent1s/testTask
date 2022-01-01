package com.example.testapplt.ui.screen.books

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle

import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.testapplt.R
import com.example.testapplt.databinding.FragmentSearchBooksBinding
import com.example.testapplt.ui.adapter.BooksListAdapter
import com.example.testapplt.ui.utils.hide
import com.example.testapplt.ui.utils.show
import com.example.testapplt.ui.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.RecyclerView
import com.example.testapplt.domain.model.domain.BooksInfo
import com.example.testapplt.ui.adapter.BooksListDataItem
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.random.Random

@FlowPreview
@AndroidEntryPoint
class SearchBooksFragment : Fragment(R.layout.fragment_search_books){

    companion object {
        private const val EXTRA_NAME = "tcf_extra_name"

        fun getNewInstance(name: String?) =
            SearchBooksFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_NAME, name)
                }
            }
    }

    private val binding: FragmentSearchBooksBinding by viewBinding()

    private val viewModel: SearchBooksViewModel by viewModels()

    private var adapter = BooksListAdapter()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initRecyclerView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bookList
                    .collect { books ->
                        when (books) {
                            is SearchBooksViewModel.BooksListState.BooksListSuccess -> showSuccessState(books.booksInfo)
                            is SearchBooksViewModel.BooksListState.Error -> showErrorState(books.errorReason.message)
                            SearchBooksViewModel.BooksListState.Empty -> showEmptyState()
                            SearchBooksViewModel.BooksListState.NoConnection -> showNoConnectionState()
                        }
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.liveSearch
                    .debounce { state->
                        if (state.length != 1)
                            700
                        else
                            0
                    }
                    .distinctUntilChanged()
                    .collectLatest { parameter ->
                        viewModel.startSearch(parameter)
                    }
            }
        }
    }


    private fun showNoConnectionState(){
        adapter.submitList(arrayListOf())
        binding.searchBooksNotAvailableTextView.hide()
        binding.searchBooksNoConnectionImageView.show()
    }

    private fun showEmptyState(){
        adapter.submitList(arrayListOf())
        binding.searchBooksNotAvailableTextView.show()
        binding.searchBooksNoConnectionImageView.hide()
    }

    private fun showSuccessState(booksInfo: List<BooksInfo>) {
        adapter.submitList(
            booksInfo.map { BooksListDataItem.BooksInfoItem(it) } +
                    listOf(BooksListDataItem.Loader(Random.nextLong().toString()))
        )
        binding.searchBooksNotAvailableTextView.hide()
        binding.searchBooksNoConnectionImageView.hide()
    }

    private fun showErrorState(message: String) {
        requireContext().showToast(message)
        viewModel.setEmptyState()
    }



    private fun initToolbar(){
        with(binding.searchBooksToolbar.searchBooksToolbarEditText) {
            setOnEditorActionListener { _, action, _ ->
                if (action == EditorInfo.IME_ACTION_SEARCH) {
                    viewModel.getBooksByParameter(
                        binding.searchBooksToolbar.searchBooksToolbarEditText.text.toString()
                    )
                    true
                }else
                    false
            }

            addTextChangedListener {
                viewModel.textFieldBeChanged(it.toString())
            }
            hint = getString(R.string.search)
        }

        binding.searchBooksToolbar.searchBooksToolbarFilterAppCompatImageButton.setOnClickListener {
            viewModel.navigateToFilters()
        }


    }


    private fun initRecyclerView(){
        with(binding.searchBooksRecyclerView) {
            adapter = this@SearchBooksFragment.adapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1)) {
                       viewModel.validateLoadMoreBook(
                           binding.searchBooksToolbar.searchBooksToolbarEditText.text.toString()
                       )
                    }
                }
            })
        }

    }

}