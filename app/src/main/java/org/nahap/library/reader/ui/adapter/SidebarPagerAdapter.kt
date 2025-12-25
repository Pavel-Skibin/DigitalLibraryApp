package org.nahap.library.reader.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.nahap.library.databinding.FragmentBookmarksBinding
import org.nahap.library.databinding.FragmentTocBinding
import org.nahap.library.reader.viewmodel.ReaderViewModel


class SidebarPagerAdapter(
    private val context: Context,
    private val viewModel: ReaderViewModel,
    private val lifecycleOwner: LifecycleOwner?,
    private val onTocItemClick: (String) -> Unit,
    private val onBookmarkClick: (Double) -> Unit
) : RecyclerView.Adapter<SidebarPagerAdapter.PageViewHolder>() {

    override fun getItemCount(): Int = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        return when (viewType) {
            0 -> TocViewHolder(
                FragmentTocBinding.inflate(LayoutInflater.from(context), parent, false),
                viewModel,
                lifecycleOwner,
                onTocItemClick
            )
            1 -> BookmarksViewHolder(
                FragmentBookmarksBinding.inflate(LayoutInflater.from(context), parent, false),
                viewModel,
                lifecycleOwner,
                onBookmarkClick
            )
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemViewType(position: Int): Int = position

    abstract class PageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind()
    }


    class TocViewHolder(
        private val binding: FragmentTocBinding,
        private val viewModel: ReaderViewModel,
        private val lifecycleOwner: LifecycleOwner?,
        private val onTocItemClick: (String) -> Unit
    ) : PageViewHolder(binding.root) {

        override fun bind() {
            val adapter = TocAdapter { tocItem ->
                onTocItemClick(tocItem.href)
            }

            binding.recyclerViewToc.adapter = adapter

            if (lifecycleOwner != null) {
                lifecycleOwner.lifecycleScope.launch {
                    viewModel.state.collect { state ->
                        adapter.submitList(state.toc)
                    }
                }
            } else {
                adapter.submitList(viewModel.state.value.toc)
            }
        }
    }


    class BookmarksViewHolder(
        private val binding: FragmentBookmarksBinding,
        private val viewModel: ReaderViewModel,
        private val lifecycleOwner: LifecycleOwner?,
        private val onBookmarkClick: (Double) -> Unit
    ) : PageViewHolder(binding.root) {

        override fun bind() {
            val adapter = BookmarkAdapter(
                onBookmarkClick = { bookmark ->
                    onBookmarkClick(bookmark.position)
                },
                onBookmarkEdit = { bookmark ->
                    org.nahap.library.reader.ui.EditBookmarkDialog.show(
                        context = binding.root.context,
                        bookmark = bookmark,
                        onSave = { name, notes ->
                            viewModel.updateBookmark(
                                bookmarkId = bookmark.id,
                                position = bookmark.position,
                                name = name,
                                notes = notes
                            )
                        },
                        onDelete = {
                            viewModel.deleteBookmark(bookmark.id)
                        }
                    )
                }
            )

            binding.recyclerViewBookmarks.adapter = adapter

            if (lifecycleOwner != null) {
                lifecycleOwner.lifecycleScope.launch {
                    viewModel.state.collect { state ->
                        adapter.submitList(state.bookmarks)
                        if (state.bookmarks.isEmpty()) {
                            binding.emptyState.visibility = View.VISIBLE
                            binding.recyclerViewBookmarks.visibility = View.GONE
                        } else {
                            binding.emptyState.visibility = View.GONE
                            binding.recyclerViewBookmarks.visibility = View.VISIBLE
                        }
                    }
                }
            } else {
                val bookmarks = viewModel.state.value.bookmarks
                adapter.submitList(bookmarks)
                if (bookmarks.isEmpty()) {
                    binding.emptyState.visibility = View.VISIBLE
                    binding.recyclerViewBookmarks.visibility = View.GONE
                } else {
                    binding.emptyState.visibility = View.GONE
                    binding.recyclerViewBookmarks.visibility = View.VISIBLE
                }
            }
        }
    }
}