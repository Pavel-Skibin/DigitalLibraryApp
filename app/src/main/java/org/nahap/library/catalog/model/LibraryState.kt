package org.nahap. library.catalog.model


data class LibraryState(
    val categories: List<CategoryState> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<BookResponse> = emptyList(),
    val isSearching: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)


data class CategoryState(
    val id: Int,
    val name: String,
    val books: List<BookResponse> = emptyList(),
    val currentPage: Int = 0,
    val hasMore: Boolean = true,
    val isLoading: Boolean = false,
    val isTopRated: Boolean = false
)


data class BookDetailState(
    val book: BookDetailResponse?  = null,
    val isLoading: Boolean = false,
    val error: String? = null
)