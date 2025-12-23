package org. nahap.library.catalog.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines. flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org. nahap.library.catalog.model.BookResponse
import org.nahap.library.catalog.model.CategoryState
import org.nahap. library.catalog.model.LibraryState
import org.nahap.library.catalog. repository.LibraryRepository
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: LibraryRepository
) : ViewModel() {

    companion object {
        private const val TAG = "LibraryViewModel"
    }

    private val _state = MutableStateFlow(LibraryState())
    val state: StateFlow<LibraryState> = _state.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                val categories = mutableListOf<CategoryState>()


                categories.add(
                    CategoryState(
                        id = -1,
                        name = "Топ по рейтингу ",
                        isTopRated = true
                    )
                )


                categories. add(
                    CategoryState(
                        id = LibraryRepository.GENRE_PHILOSOPHY,
                        name = "Философия"
                    )
                )


                categories.add(
                    CategoryState(
                        id = LibraryRepository.GENRE_DRAMA,
                        name = "Драма"
                    )
                )


                categories.add(
                    CategoryState(
                        id = LibraryRepository.GENRE_CLASSICS,
                        name = "Классическая литература"
                    )
                )

                _state.value = _state.value.copy(
                    categories = categories,
                    isLoading = false
                )

                categories.forEachIndexed { index, _ ->
                    loadBooksForCategory(index)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error loading categories", e)
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Ошибка загрузки: ${e.message}"
                )
            }
        }
    }


    fun loadBooksForCategory(categoryIndex: Int) {
        val categories = _state.value.categories. toMutableList()
        if (categoryIndex >= categories.size) return

        val category = categories[categoryIndex]
        if (category.isLoading) return

        viewModelScope.launch {
            categories[categoryIndex] = category.copy(isLoading = true)
            _state.value = _state.value. copy(categories = categories)

            val result = if (category.isTopRated) {
                repository.getTopRatedBooks(category.currentPage)
            } else {
                repository.getBooksByGenre(category.id, category.currentPage)
                    . map { it.content }
            }

            result.onSuccess { books ->
                val updatedCategories = _state.value.categories.toMutableList()
                val currentCategory = updatedCategories[categoryIndex]

                updatedCategories[categoryIndex] = currentCategory.copy(
                    books = currentCategory.books + books,
                    currentPage = currentCategory.currentPage + 1,
                    hasMore = books.size >= 10,
                    isLoading = false
                )

                _state.value = _state.value. copy(categories = updatedCategories)
                Log.d(TAG, "Loaded ${books.size} books for category '${category.name}'")
            }. onFailure { e ->
                val updatedCategories = _state.value.categories.toMutableList()
                updatedCategories[categoryIndex] = category. copy(isLoading = false)
                _state.value = _state.value.copy(categories = updatedCategories)
                Log.e(TAG, "Error loading books for category", e)
            }
        }
    }


    fun loadMoreBooks(categoryIndex: Int) {
        val category = _state.value.categories.getOrNull(categoryIndex) ?: return
        if (! category.hasMore || category.isLoading) return

        loadBooksForCategory(categoryIndex)
    }

    fun searchBooks(query: String) {
        _state.value = _state.value.copy(
            searchQuery = query,
            isSearching = query.isNotEmpty()
        )

        if (query.isEmpty()) {
            _state.value = _state.value.copy(searchResults = emptyList())
            return
        }

        viewModelScope.launch {
            repository.searchBooks(query). onSuccess { response ->
                _state.value = _state.value.copy(searchResults = response. content)
            }.onFailure {
                _state.value = _state.value.copy(searchResults = emptyList())
            }
        }
    }


    fun clearSearch() {
        _state.value = _state. value.copy(
            searchQuery = "",
            searchResults = emptyList(),
            isSearching = false
        )
    }

    fun getBookCoverUrl(bookId: Int): String {
        return repository.getBookCoverUrl(bookId)
    }
}