package org.nahap.library.catalog.ui. adapter

import android.view. LayoutInflater
import android.view.View
import android.view. ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview. widget.RecyclerView
import org.nahap.library.catalog.model.BookResponse
import org.nahap. library.catalog.model.CategoryState
import org.nahap.library.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val onBookClick: (BookResponse) -> Unit,
    private val onLoadMore: (Int) -> Unit,
    private val getCoverUrl: (Int) -> String
) : ListAdapter<CategoryState, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding, onBookClick, onLoadMore, getCoverUrl)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    class CategoryViewHolder(
        private val binding: ItemCategoryBinding,
        private val onBookClick: (BookResponse) -> Unit,
        private val onLoadMore: (Int) -> Unit,
        private val getCoverUrl: (Int) -> String
    ) : RecyclerView. ViewHolder(binding.root) {

        private val bookAdapter = BookAdapter(onBookClick, getCoverUrl)

        init {
            binding.recyclerViewBooks.adapter = bookAdapter
        }

        fun bind(category: CategoryState, position: Int) {
            binding.tvCategoryName.text = category.name

            binding.tvSeeAll.visibility = if (category.hasMore && !category.isLoading) {
                View.VISIBLE
            } else {
                View. GONE
            }

            binding. progressCategory.visibility = if (category. isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }

            binding.tvEmpty.visibility = if (category.books.isEmpty() && ! category.isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }

            bookAdapter.submitList(category.books)

            binding.tvSeeAll.setOnClickListener {
                onLoadMore(position)
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryState>() {
        override fun areItemsTheSame(oldItem: CategoryState, newItem: CategoryState): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoryState, newItem: CategoryState): Boolean {
            return oldItem == newItem
        }
    }
}