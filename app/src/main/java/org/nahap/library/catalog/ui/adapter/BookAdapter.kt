package org.nahap.library.catalog.ui.adapter

import android.view.LayoutInflater
import android.view. View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview. widget.ListAdapter
import androidx. recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import org.nahap.library.R
import org.nahap.library.catalog.model.BookResponse
import org.nahap.library.databinding.ItemBookBinding

class BookAdapter(
    private val onBookClick: (BookResponse) -> Unit,
    private val getCoverUrl: (Int) -> String
) : ListAdapter<BookResponse, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookViewHolder(binding, onBookClick, getCoverUrl)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BookViewHolder(
        private val binding: ItemBookBinding,
        private val onBookClick: (BookResponse) -> Unit,
        private val getCoverUrl: (Int) -> String
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(book: BookResponse) {
            val coverUrl = getCoverUrl(book.id)
            binding.ivBookCover.load(coverUrl) {
                crossfade(true)
                placeholder(R. drawable.ic_book_placeholder)
                error(R.drawable. ic_book_placeholder)
                transformations(RoundedCornersTransformation(8f))
            }

            if (book.averageRating != null && book. averageRating > 0) {
                binding.ratingLayout.visibility = View.VISIBLE
                binding.tvRating.text = String.format("%.1f", book.averageRating)
            } else {
                binding.ratingLayout.visibility = View. GONE
            }

            binding.root.setOnClickListener {
                onBookClick(book)
            }
        }
    }

    class BookDiffCallback : DiffUtil.ItemCallback<BookResponse>() {
        override fun areItemsTheSame(oldItem: BookResponse, newItem: BookResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BookResponse, newItem: BookResponse): Boolean {
            return oldItem == newItem
        }
    }
}