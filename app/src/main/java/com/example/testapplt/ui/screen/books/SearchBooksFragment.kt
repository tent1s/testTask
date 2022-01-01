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

@FlowPreview
@AndroidEntryPoint
class SearchBooksFragment : Fragment(R.layout.fragment_search_books){

    companion object {
        fun newInstance() = SearchBooksFragment()
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
                            is SearchBooksViewModel.BooksListState.BooksListSuccess -> {
                                adapter.addLoaderAndSubmitList(books.booksInfo)
                                binding.searchBooksNotAvailableTextView.hide()
                            }
                            is SearchBooksViewModel.BooksListState.Error -> {
                                requireContext().showToast(books.errorReason.message)
                                viewModel.setEmptyState()
                            }
                            SearchBooksViewModel.BooksListState.Empty -> {
                                adapter.submitList(arrayListOf())
                                binding.searchBooksNotAvailableTextView.show()
                            }
                        }
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.liveSearch
                    .collectLatest { parameter ->
                        viewModel.startSearch(parameter)
                    }
            }
        }
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