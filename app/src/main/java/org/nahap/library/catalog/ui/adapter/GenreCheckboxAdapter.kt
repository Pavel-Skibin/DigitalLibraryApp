package org.nahap.library. catalog.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget. DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.nahap.library.catalog.model. GenreResponse
import org.nahap.library.databinding.ItemGenreCheckboxBinding

class GenreCheckboxAdapter(
    private val onGenreToggle: (Int, Boolean) -> Unit,
    private val selectedGenreIds: () -> List<Int>
) : ListAdapter<GenreResponse, GenreCheckboxAdapter.GenreViewHolder>(GenreDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val binding = ItemGenreCheckboxBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GenreViewHolder(binding, onGenreToggle, selectedGenreIds)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class GenreViewHolder(
        private val binding: ItemGenreCheckboxBinding,
        private val onGenreToggle: (Int, Boolean) -> Unit,
        private val selectedGenreIds: () -> List<Int>
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(genre: GenreResponse) {
            binding.tvGenreName.text = genre.name

            binding.checkbox.setOnCheckedChangeListener(null)

            binding.checkbox.isChecked = selectedGenreIds().contains(genre.id)

            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                onGenreToggle(genre.id, isChecked)
            }

            binding.root.setOnClickListener {
                binding.checkbox.isChecked = !binding.checkbox.isChecked
            }
        }
    }

    class GenreDiffCallback : DiffUtil.ItemCallback<GenreResponse>() {
        override fun areItemsTheSame(oldItem: GenreResponse, newItem: GenreResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GenreResponse, newItem: GenreResponse): Boolean {
            return oldItem == newItem
        }
    }
}