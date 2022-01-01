package com.example.testapplt.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.testapplt.R
import com.example.testapplt.databinding.ItemBookBinding
import com.example.testapplt.domain.model.domain.BooksInfo
import com.example.testapplt.ui.utils.load
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class BooksListAdapter : ListAdapter<BooksListDataItem,
        RecyclerView.ViewHolder>(BooksInfoDiffCallback()) {

    companion object{
        private const val ITEM_VIEW_TYPE_LOADER = 0
        private const val ITEM_VIEW_TYPE_ITEM = 1
    }

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addLoaderAndSubmitList(list: List<BooksInfo>) {
        adapterScope.launch {
            val items =
                 list.map { BooksListDataItem.BooksInfoItem(it) } + listOf(BooksListDataItem.Loader)

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is BooksListDataItem.Loader -> ITEM_VIEW_TYPE_LOADER
            is BooksListDataItem.BooksInfoItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = getItem(position) as BooksListDataItem.BooksInfoItem
                holder.bind( item.booksInfo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_LOADER -> LoaderHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    class ViewHolder private constructor(private val binding: ItemBookBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind( item: BooksInfo) {
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

    class LoaderHolder(view: View): RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): LoaderHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.item_loader, parent, false)
                return LoaderHolder(view)
            }
        }
    }
}


class BooksInfoDiffCallback : DiffUtil.ItemCallback<BooksListDataItem>() {
    override fun areItemsTheSame(oldItem: BooksListDataItem, newItem: BooksListDataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BooksListDataItem, newItem: BooksListDataItem): Boolean {
        return oldItem == newItem
    }
}


sealed class BooksListDataItem {
    data class BooksInfoItem(val booksInfo: BooksInfo): BooksListDataItem() {
        override val id = booksInfo.id
    }

    object Loader: BooksListDataItem() {
        override val id = Long.MIN_VALUE.toString()
    }

    abstract val id: String
}
