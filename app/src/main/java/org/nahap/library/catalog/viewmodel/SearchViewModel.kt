package org.nahap.library.catalog.viewmodel

import android.util.Log
import androidx. lifecycle.ViewModel
import androidx. lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow. MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines. launch
import org.nahap. library.catalog.model.AuthorResponse
import org.nahap.library.catalog.model.BookResponse
import org.nahap. library.catalog.model.GenreResponse
import org.nahap. library.catalog.model.SearchFilters
import org.nahap. library.catalog.model.SortOption
import org. nahap.library.catalog.repository.LibraryRepository
import javax.inject.Inject

data class SearchState(
    val filters: SearchFilters = SearchFilters(),
    val authors: List<AuthorResponse> = emptyList(),
    val genres: List<GenreResponse> = emptyList(),
    val searchResults: List<BookResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMetadata: Boolean = false,
    val error: String? = null,
    val hasSearched: Boolean = false,
    val currentPage: Int = 0,
    val hasMoreResults: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: LibraryRepository
) : ViewModel() {

    companion object {
        private const val TAG = "SearchViewModel"
    }

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    init {
        loadMetadata()
    }

    private fun loadMetadata() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingMetadata = true)

            try {
                val authorsResult = repository.getAllAuthors()
                val genresResult = repository.getAllGenres()

                _state.value = _state.value.copy(
                    authors = authorsResult.getOrNull() ?: emptyList(),
                    genres = genresResult.getOrNull() ?: emptyList(),
                    isLoadingMetadata = false
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error loading metadata", e)
                _state.value = _state.value.copy(
                    isLoadingMetadata = false,
                    error = "Ошибка загрузки данных"
                )
            }
        }
    }

    fun updateFilters(newFilters: SearchFilters) {
        _state.value = _state.value.copy(filters = newFilters)
    }

    fun updateTitle(title: String) {
        _state.value = _state.value. copy(
            filters = _state.value. filters.copy(title = title)
        )
    }

    fun toggleAuthor(authorId: Int) {
        val currentAuthors = _state.value.filters.selectedAuthors. toMutableList()
        if (currentAuthors.contains(authorId)) {
            currentAuthors.remove(authorId)
        } else {
            currentAuthors.add(authorId)
        }
        _state.value = _state.value.copy(
            filters = _state.value. filters.copy(selectedAuthors = currentAuthors)
        )
    }

    fun toggleGenre(genreId: Int) {
        val currentGenres = _state.value.filters.selectedGenres.toMutableList()
        if (currentGenres.contains(genreId)) {
            currentGenres.remove(genreId)
        } else {
            currentGenres.add(genreId)
        }
        _state.value = _state. value.copy(
            filters = _state.value.filters.copy(selectedGenres = currentGenres)
        )
    }

    fun updateRatingRange(minRating: Double?, maxRating: Double?) {
        _state.value = _state.value.copy(
            filters = _state.value.filters. copy(
                minRating = minRating,
                maxRating = maxRating
            )
        )
    }

    fun updateSortOption(sortOption: SortOption) {
        _state.value = _state. value.copy(
            filters = _state.value.filters.copy(sortBy = sortOption)
        )
    }

    fun performSearch(loadMore: Boolean = false) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val page = if (loadMore) _state.value.currentPage + 1 else 0

            repository.searchBooksWithFilters(_state.value.filters, page = page)
                .onSuccess { response ->
                    val updatedResults = if (loadMore) {
                        _state.value.searchResults + response.content
                    } else {
                        response.content
                    }

                    _state.value = _state.value. copy(
                        searchResults = updatedResults,
                        isLoading = false,
                        hasSearched = true,
                        currentPage = page,
                        hasMoreResults = ! response.last
                    )
                    Log.d(TAG, "Found ${response.content.size} books (page $page, total ${updatedResults.size})")
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Ошибка поиска: ${e.message}",
                        hasSearched = true
                    )
                    Log.e(TAG, "Error searching", e)
                }
        }
    }

    fun loadMoreResults() {
        if (! _state.value.isLoading && _state.value.hasMoreResults) {
            performSearch(loadMore = true)
        }
    }

    fun resetFilters() {
        _state.value = SearchState(
            authors = _state.value.authors,
            genres = _state.value.genres
        )
    }

    fun getBookCoverUrl(bookId: Int): String {
        return repository.getBookCoverUrl(bookId)
    }
}