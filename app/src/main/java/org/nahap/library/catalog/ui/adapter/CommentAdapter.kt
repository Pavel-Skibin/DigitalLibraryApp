package org.nahap.library.catalog. ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview. widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.nahap.library.catalog. model.CommentResponse
import org.nahap.library.databinding.ItemCommentBinding
import java.text.SimpleDateFormat
import java. util. Locale

class CommentAdapter : ListAdapter<CommentResponse, CommentAdapter.CommentViewHolder>(CommentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder. bind(getItem(position))
    }

    class CommentViewHolder(
        private val binding: ItemCommentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            private val serverFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            private val displayFormat = SimpleDateFormat("dd. MM.yyyy HH:mm", Locale.getDefault())
        }

        fun bind(comment: CommentResponse) {
            binding.tvUserName.text = comment.userName ?: "Пользователь"
            binding.tvCommentText.text = comment.text

            comment.createdAt?.let { dateStr ->
                try {
                    val cleanDate = dateStr
                        .substringBefore(".")
                        .substringBefore("+")
                        .substringBefore("Z")

                    val date = serverFormat. parse(cleanDate)
                    binding.tvDate.text = if (date != null) {
                        displayFormat.format(date)
                    } else {
                        dateStr. substringBefore("T")
                    }
                } catch (e: Exception) {
                    binding.tvDate.text = dateStr.substringBefore("T")
                }
            } ?: run {
                binding.tvDate. text = ""
            }

            val initial = (comment.userName?. firstOrNull() ?: 'U'). uppercase()
            binding.tvUserAvatar.text = initial
        }
    }

    class CommentDiffCallback : DiffUtil.ItemCallback<CommentResponse>() {
        override fun areItemsTheSame(oldItem: CommentResponse, newItem: CommentResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CommentResponse, newItem: CommentResponse): Boolean {
            return oldItem == newItem
        }
    }
}