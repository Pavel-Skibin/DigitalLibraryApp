package org.nahap.library. catalog.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle. lifecycleScope
import androidx. recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org. nahap.library.R
import org.nahap.library.catalog.model.SortOption
import org.nahap.library.catalog.ui.adapter.AuthorCheckboxAdapter
import org.nahap.library.catalog.ui.adapter.BookAdapter
import org.nahap.library.catalog.ui. adapter.GenreCheckboxAdapter
import org.nahap.library. catalog.viewmodel.SearchViewModel
import org.nahap.library. databinding.FragmentSearchBinding

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding?  = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()

    private lateinit var authorAdapter: AuthorCheckboxAdapter
    private lateinit var genreAdapter: GenreCheckboxAdapter
    private lateinit var resultsAdapter: BookAdapter

    private var allAuthors = listOf<org.nahap.library.catalog. model.AuthorResponse>()
    private var allGenres = listOf<org.nahap.library.catalog.model.GenreResponse>()

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super. onViewCreated(view, savedInstanceState)
        setupAdapters()
        setupListeners()
        setupObservers()
    }

    private fun setupAdapters() {
        authorAdapter = AuthorCheckboxAdapter(
            onAuthorToggle = { authorId, _ -> viewModel. toggleAuthor(authorId) },
            selectedAuthorIds = { viewModel.state. value.filters.selectedAuthors }
        )
        binding.recyclerViewAuthors.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewAuthors.adapter = authorAdapter

        genreAdapter = GenreCheckboxAdapter(
            onGenreToggle = { genreId, _ -> viewModel.toggleGenre(genreId) },
            selectedGenreIds = { viewModel.state.value.filters.selectedGenres }
        )
        binding. recyclerViewGenres.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewGenres.adapter = genreAdapter

        resultsAdapter = BookAdapter(
            onBookClick = { book ->
                val intent = Intent(requireContext(), BookDetailActivity::class.java). apply {
                    putExtra(BookDetailActivity.EXTRA_BOOK_ID, book.id)
                }
                startActivity(intent)
            },
            getCoverUrl = { bookId -> viewModel.getBookCoverUrl(bookId) }
        )
        binding.recyclerViewResults. layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerViewResults.adapter = resultsAdapter
    }

    private fun setupListeners() {
        binding.editTextTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateTitle(s?. toString() ?: "")
            }
        })

        binding. editTextAuthorSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterAuthors(s?.toString() ?: "")
            }
        })

        binding.editTextGenreSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterGenres(s?.toString() ?: "")
            }
        })

        binding.editTextMinRating.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateRatingRange()
            }
        })

        binding.editTextMaxRating.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateRatingRange()
            }
        })

        binding.radioGroupSort.setOnCheckedChangeListener { _, checkedId ->
            val sortOption = when (checkedId) {
                R.id.radioTitleAsc -> SortOption. TITLE_ASC
                R.id.radioRatingDesc -> SortOption.RATING_DESC
                else -> SortOption. TITLE_ASC
            }
            viewModel.updateSortOption(sortOption)
        }

        binding.btnSearch.setOnClickListener {
            hideKeyboard()
            viewModel.performSearch(loadMore = false)
        }

        binding.btnLoadMore.setOnClickListener {
            viewModel.loadMoreResults()
        }

        binding.btnReset.setOnClickListener {
            resetFilters()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                Log.d("SearchFragment", "State: hasSearched=${state.hasSearched}, results=${state.searchResults.size}, page=${state.currentPage}, loading=${state.isLoading}")

                allAuthors = state. authors
                allGenres = state.genres

                authorAdapter.submitList(state.authors)
                genreAdapter.submitList(state.genres)


                binding.tvAuthorsSelected.visibility = if (state.filters.selectedAuthors.isNotEmpty()) {
                    binding.tvAuthorsSelected.text = "Выбрано: ${state.filters. selectedAuthors.size}"
                    View.VISIBLE
                } else {
                    View. GONE
                }


                binding.tvGenresSelected. visibility = if (state.filters. selectedGenres.isNotEmpty()) {
                    binding.tvGenresSelected.text = "Выбрано: ${state.filters.selectedGenres.size}"
                    View.VISIBLE
                } else {
                    View. GONE
                }


                binding.progressLoading.visibility = if (state.isLoading) {
                    Log.d("SearchFragment", "Showing loading...")
                    View. VISIBLE
                } else {
                    View.GONE
                }


                if (state.hasSearched) {
                    Log.d("SearchFragment", "hasSearched=true, showing results section")

                    if (state.searchResults.isEmpty()) {
                        Log.d("SearchFragment", "No results found")
                        binding. cardResults.visibility = View. GONE
                        binding.tvEmptyState.visibility = View. VISIBLE
                    } else {
                        Log.d("SearchFragment", "Showing ${state.searchResults.size} results")
                        binding.cardResults.visibility = View.VISIBLE
                        binding.tvEmptyState.visibility = View.GONE
                        resultsAdapter.submitList(state.searchResults)


                        binding.btnLoadMore.visibility = if (state.hasMoreResults && !state.isLoading) {
                            Log.d("SearchFragment", "Showing 'Load More' button")
                            View.VISIBLE
                        } else {
                            Log.d("SearchFragment", "Hiding 'Load More' button (hasMore=${state.hasMoreResults}, loading=${state.isLoading})")
                            View.GONE
                        }
                    }
                } else {
                    Log.d("SearchFragment", "hasSearched=false, hiding results")
                    binding. cardResults.visibility = View. GONE
                    binding.tvEmptyState.visibility = View. GONE
                }

                state. error?.let {
                    Log.e("SearchFragment", "Error: $it")
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun filterAuthors(query: String) {
        val filtered = if (query.isEmpty()) {
            allAuthors
        } else {
            allAuthors.filter { it.fullName.contains(query, ignoreCase = true) }
        }
        authorAdapter.submitList(filtered)
    }

    private fun filterGenres(query: String) {
        val filtered = if (query.isEmpty()) {
            allGenres
        } else {
            allGenres.filter { it.name.contains(query, ignoreCase = true) }
        }
        genreAdapter.submitList(filtered)
    }

    private fun updateRatingRange() {
        val minRating = binding.editTextMinRating. text.toString(). toDoubleOrNull()
        val maxRating = binding.editTextMaxRating.text.toString().toDoubleOrNull()
        viewModel.updateRatingRange(minRating, maxRating)
    }

    private fun resetFilters() {
        Log.d("SearchFragment", " Resetting filters...")

        hideKeyboard()

        binding.editTextTitle.setText("")
        binding.editTextAuthorSearch.setText("")
        binding. editTextGenreSearch.setText("")
        binding.editTextMinRating.setText("")
        binding. editTextMaxRating.setText("")
        binding.radioTitleAsc.isChecked = true

        viewModel. resetFilters()

        binding. cardResults.visibility = View. GONE
        binding.tvEmptyState.visibility = View.GONE
        binding.btnLoadMore. visibility = View.GONE

        authorAdapter.notifyDataSetChanged()
        genreAdapter.notifyDataSetChanged()

        binding.root.post {
            binding.root.smoothScrollTo(0, 0)
        }

        Log.d("SearchFragment", "✅ Filters reset complete")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}