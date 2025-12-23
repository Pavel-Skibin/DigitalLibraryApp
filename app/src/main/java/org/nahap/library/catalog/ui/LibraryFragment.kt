package org.nahap.library.catalog.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.nahap.library.catalog.model.BookResponse
import org.nahap.library.catalog.ui.BookDetailActivity
import org.nahap.library.catalog.ui.adapter.BookAdapter
import org.nahap.library.catalog.ui.adapter.CategoryAdapter
import org.nahap.library.catalog.viewmodel.LibraryViewModel
import org.nahap.library.databinding.FragmentLibraryBinding

@AndroidEntryPoint
class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LibraryViewModel by viewModels()

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var searchAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        setupSearch()
        setupObservers()
    }

    private fun setupAdapters() {
        categoryAdapter = CategoryAdapter(
            onBookClick = { book -> openBookDetail(book) },
            onLoadMore = { categoryIndex -> viewModel.loadMoreBooks(categoryIndex) },
            getCoverUrl = { bookId -> viewModel.getBookCoverUrl(bookId) }
        )
        binding.recyclerViewCategories.adapter = categoryAdapter

        searchAdapter = BookAdapter(
            onBookClick = { book -> openBookDetail(book) },
            getCoverUrl = { bookId -> viewModel.getBookCoverUrl(bookId) }
        )
        binding.recyclerViewSearch.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerViewSearch.adapter = searchAdapter
    }

    private fun setupSearch() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                viewModel.searchBooks(query)
            }
        })

        binding.editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.editTextSearch.text.toString()
                viewModel.searchBooks(query)
                true
            } else {
                false
            }
        }

        binding.btnSearch.setOnClickListener {
            val query = binding.editTextSearch.text.toString()
            viewModel.searchBooks(query)
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.progressLoading.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                if (state.isSearching) {
                    binding.recyclerViewCategories.visibility = View.GONE
                    binding.recyclerViewSearch.visibility = View.VISIBLE
                    searchAdapter.submitList(state.searchResults)
                } else {
                    binding.recyclerViewCategories.visibility = View.VISIBLE
                    binding.recyclerViewSearch.visibility = View.GONE
                    categoryAdapter.submitList(state.categories)
                }
            }
        }
    }

    private fun openBookDetail(book: BookResponse) {
        val intent = Intent(requireContext(), BookDetailActivity::class.java).apply {
            putExtra(BookDetailActivity.EXTRA_BOOK_ID, book.id)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}