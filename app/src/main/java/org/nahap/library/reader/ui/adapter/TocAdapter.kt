package org.nahap.library.reader.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.nahap.library.databinding.ItemTocBinding
import org.nahap.library.reader.model.TocItem

class TocAdapter(
    private val onItemClick: (TocItem) -> Unit
) : ListAdapter<TocItem, TocAdapter.TocViewHolder>(TocDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TocViewHolder {
        val binding = ItemTocBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TocViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: TocViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TocViewHolder(
        private val binding: ItemTocBinding,
        private val onItemClick: (TocItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TocItem) {
            binding.tvTocTitle.text = item.label

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    class TocDiffCallback : DiffUtil.ItemCallback<TocItem>() {
        override fun areItemsTheSame(oldItem: TocItem, newItem: TocItem): Boolean {
            return oldItem.href == newItem.href
        }

        override fun areContentsTheSame(oldItem: TocItem, newItem: TocItem): Boolean {
            return oldItem == newItem
        }
    }
}