package com.example.testapplt.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.testapplt.R
import com.example.testapplt.databinding.ItemBookBinding
import com.example.testapplt.domain.model.domain.BooksInfo
import com.example.testapplt.ui.utils.load


class BooksListAdapter : PagingDataAdapter<BooksInfo,
        BooksListAdapter.ViewHolder>(BooksInfoDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        item?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)


    class ViewHolder private constructor(private val binding: ItemBookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BooksInfo) {
            binding.itemBookMainTextView.text = item.tittle
            binding.itemBookDescriptionTextView.text = item.authors
            with(binding.itemBookImageView) {
                item.image?.let { load(it) } ?: run {
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.placeholder_book
                    )?.let {
                        load(it)
                    } ?: run {
                        setImageDrawable(null)
                    }
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBookBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }


    class BooksInfoDiffCallback : DiffUtil.ItemCallback<BooksInfo>() {
        override fun areItemsTheSame(oldItem: BooksInfo, newItem: BooksInfo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BooksInfo, newItem: BooksInfo): Boolean {
            return oldItem == newItem
        }
    }
}
