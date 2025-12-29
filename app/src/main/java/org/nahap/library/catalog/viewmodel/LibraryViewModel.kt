package org.nahap.library.catalog.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.nahap.library.catalog.domain.model.SearchFilter
import org.nahap.library.catalog.domain.model.SortBy
import org.nahap.library.catalog.domain.repository.CatalogRepository
import org.nahap.library.catalog.domain.usecase.GetBooksByGenreUseCase
import org.nahap.library.catalog.domain.usecase.GetTopRatedBooksUseCase
import org.nahap.library.catalog.domain.usecase.SearchBooksUseCase
import org.nahap.library.catalog.model.CategoryState
import org.nahap.library.catalog.model.LibraryState
import org.nahap.library.catalog.presentation.mapper.CatalogPresentationMapper
import javax.inject.Inject

/**
 * ViewModel для экрана библиотеки
 */
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getTopRatedBooksUseCase: GetTopRatedBooksUseCase,
    private val getBooksByGenreUseCase: GetBooksByGenreUseCase,
    private val searchBooksUseCase: SearchBooksUseCase,
    private val catalogRepository: CatalogRepository
) : ViewModel() {

    companion object {
        private const val TAG = "LibraryViewModel"
        private const val GENRE_PHILOSOPHY = 7
        private const val GENRE_DRAMA = 2
        private const val GENRE_CLASSICS = 6
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

                categories.add(
                    CategoryState(
                        id = GENRE_PHILOSOPHY,
                        name = "Философия"
                    )
                )

                categories.add(
                    CategoryState(
                        id = GENRE_DRAMA,
                        name = "Драма"
                    )
                )

                categories.add(
                    CategoryState(
                        id = GENRE_CLASSICS,
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
        val categories = _state.value.categories.toMutableList()
        if (categoryIndex >= categories.size) return

        val category = categories[categoryIndex]
        if (category.isLoading) return

        viewModelScope.launch {
            categories[categoryIndex] = category.copy(isLoading = true)
            _state.value = _state.value.copy(categories = categories)

            val result = if (category.isTopRated) {
                getTopRatedBooksUseCase(category.currentPage)
            } else {
                getBooksByGenreUseCase(category.id, category.currentPage)
                    .map { page -> page.content }
            }

            result.onSuccess { domainBooks ->
                val updatedCategories = _state.value.categories.toMutableList()
                val currentCategory = updatedCategories[categoryIndex]

                val uiBooks = domainBooks.map { CatalogPresentationMapper.catalogBookToUi(it) }

                updatedCategories[categoryIndex] = currentCategory.copy(
                    books = currentCategory.books + uiBooks,
                    currentPage = currentCategory.currentPage + 1,
                    hasMore = uiBooks.size >= 10,
                    isLoading = false
                )

                _state.value = _state.value.copy(categories = updatedCategories)
                Log.d(TAG, "Loaded ${uiBooks.size} books for category '${category.name}'")
            }.onFailure { e ->
                val updatedCategories = _state.value.categories.toMutableList()
                updatedCategories[categoryIndex] = category.copy(isLoading = false)
                _state.value = _state.value.copy(categories = updatedCategories)
                Log.e(TAG, "Error loading books for category", e)
            }
        }
    }

    fun loadMoreBooks(categoryIndex: Int) {
        val category = _state.value.categories.getOrNull(categoryIndex) ?: return
        if (!category.hasMore || category.isLoading) return

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
            val filter = SearchFilter(
                title = query,
                sortBy = SortBy.TITLE_ASC
            )

            searchBooksUseCase(filter).onSuccess { page ->
                val uiBooks = page.content.map { CatalogPresentationMapper.catalogBookToUi(it) }
                _state.value = _state.value.copy(searchResults = uiBooks)
            }.onFailure {
                _state.value = _state.value.copy(searchResults = emptyList())
            }
        }
    }


    fun clearSearch() {
        _state.value = _state.value.copy(
            searchQuery = "",
            searchResults = emptyList(),
            isSearching = false
        )
    }


    fun getBookCoverUrl(bookId: Int): String {
        return catalogRepository.getBookCoverUrl(bookId)
    }
}
