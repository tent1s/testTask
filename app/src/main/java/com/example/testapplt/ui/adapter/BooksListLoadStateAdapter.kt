package com.example.testapplt.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.testapplt.databinding.ItemLoaderBinding

class BooksListLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<BooksListLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {

        return LoadStateViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) =
        holder.bind(loadState, retry)


    class LoadStateViewHolder private constructor(private val binding: ItemLoaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): LoadStateViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemLoaderBinding.inflate(layoutInflater, parent, false)

                return LoadStateViewHolder(binding)
            }
        }

        fun bind(loadState: LoadState, retry: () -> Unit) {
            with(binding) {

                itemLoaderErrorButton.setOnClickListener {
                    retry.invoke()
                }

                itemLoaderProgressBar.isVisible = loadState is LoadState.Loading
                itemLoaderErrorButton.isVisible = loadState !is LoadState.Loading
                itemLoaderErrorTextView.isVisible = loadState !is LoadState.Loading
            }
        }
    }
}