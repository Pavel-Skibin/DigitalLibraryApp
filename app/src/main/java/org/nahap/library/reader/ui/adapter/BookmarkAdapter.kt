package org.nahap.library.reader.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.nahap.library.databinding.ItemBookmarkBinding
import org.nahap.library.reader.model.BookmarkResponse

class BookmarkAdapter(
    private val onBookmarkClick: (BookmarkResponse) -> Unit,
    private val onBookmarkEdit: (BookmarkResponse) -> Unit
) : ListAdapter<BookmarkResponse, BookmarkAdapter.BookmarkViewHolder>(BookmarkDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val binding = ItemBookmarkBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookmarkViewHolder(binding, onBookmarkClick, onBookmarkEdit)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BookmarkViewHolder(
        private val binding: ItemBookmarkBinding,
        private val onBookmarkClick: (BookmarkResponse) -> Unit,
        private val onBookmarkEdit: (BookmarkResponse) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(bookmark: BookmarkResponse) {
            binding.tvBookmarkName.text = bookmark.name
            binding.tvBookmarkPosition.text = "Позиция: ${(bookmark.position * 100).toInt()}%"


            if (!bookmark.notes.isNullOrEmpty()) {
                binding.tvBookmarkNotes.text = bookmark.notes
                binding.tvBookmarkNotes.visibility = View.VISIBLE
            } else {
                binding.tvBookmarkNotes.visibility = View.GONE
            }


            binding.root.setOnClickListener {
                onBookmarkClick(bookmark)
            }


            binding.btnEditBookmark.setOnClickListener {
                onBookmarkEdit(bookmark)
            }
        }
    }

    class BookmarkDiffCallback : DiffUtil.ItemCallback<BookmarkResponse>() {
        override fun areItemsTheSame(oldItem: BookmarkResponse, newItem: BookmarkResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BookmarkResponse, newItem: BookmarkResponse): Boolean {
            return oldItem == newItem
        }
    }
}