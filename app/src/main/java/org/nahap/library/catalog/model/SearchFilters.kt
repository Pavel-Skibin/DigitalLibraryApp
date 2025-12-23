package org.nahap.library.catalog.model

data class SearchFilters(
    val title: String = "",
    val selectedAuthors: List<Int> = emptyList(),
    val selectedGenres: List<Int> = emptyList(),
    val minRating: Double?  = null,
    val maxRating: Double? = null,
    val sortBy: SortOption = SortOption. TITLE_ASC
)

enum class SortOption(val value: String) {
    TITLE_ASC("title,asc"),
    RATING_DESC("averageRating,desc")
}