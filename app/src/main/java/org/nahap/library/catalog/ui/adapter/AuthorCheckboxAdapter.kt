package org.nahap.library.catalog.ui.adapter

import android. view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview. widget.RecyclerView
import org.nahap.library.catalog.model.AuthorResponse
import org.nahap.library.databinding.ItemAuthorCheckboxBinding

class AuthorCheckboxAdapter(
    private val onAuthorToggle: (Int, Boolean) -> Unit,
    private val selectedAuthorIds: () -> List<Int>
) : ListAdapter<AuthorResponse, AuthorCheckboxAdapter.AuthorViewHolder>(AuthorDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthorViewHolder {
        val binding = ItemAuthorCheckboxBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AuthorViewHolder(binding, onAuthorToggle, selectedAuthorIds)
    }

    override fun onBindViewHolder(holder: AuthorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AuthorViewHolder(
        private val binding: ItemAuthorCheckboxBinding,
        private val onAuthorToggle: (Int, Boolean) -> Unit,
        private val selectedAuthorIds: () -> List<Int>
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(author: AuthorResponse) {
            binding.tvAuthorName.text = author. fullName
            binding.checkbox.setOnCheckedChangeListener(null)
            binding. checkbox.isChecked = selectedAuthorIds().contains(author. id)

            binding.checkbox. setOnCheckedChangeListener { _, isChecked ->
                onAuthorToggle(author.id, isChecked)
            }

            binding.root. setOnClickListener {
                binding.checkbox.isChecked = ! binding.checkbox.isChecked
            }
        }
    }

    class AuthorDiffCallback : DiffUtil.ItemCallback<AuthorResponse>() {
        override fun areItemsTheSame(oldItem: AuthorResponse, newItem: AuthorResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AuthorResponse, newItem: AuthorResponse): Boolean {
            return oldItem == newItem
        }
    }
}