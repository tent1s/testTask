package com.example.testapplt.ui.screen.books

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager

import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.testapplt.R
import com.example.testapplt.databinding.FragmentSearchBooksBinding
import com.example.testapplt.ui.adapter.BooksListAdapter
import com.example.testapplt.ui.utils.hide
import com.example.testapplt.ui.utils.show
import com.example.testapplt.ui.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
        viewModel.getBooksByParameter(binding.searchBooksToolbar.searchBooksToolbarEditText.text.toString())
    }


    private fun initRecyclerView(){
        binding.searchBooksRecyclerView.adapter = adapter
        binding.searchBooksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bookList
                    .collect { books ->
                        when (books) {
                            is SearchBooksViewModel.BooksListState.BooksListSuccess -> {
                                adapter.submitList(books.booksInfo)
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
    }

}