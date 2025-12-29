package org.nahap.library.catalog.domain.model

/**
 * Domain модель фильтров поиска
 */
data class SearchFilter(
    val title: String = "",
    val authorIds: List<Int> = emptyList(),
    val genreIds: List<Int> = emptyList(),
    val minRating: Double? = null,
    val maxRating: Double? = null,
    val sortBy: SortBy = SortBy.TITLE_ASC
)

enum class SortBy(val value: String) {
    TITLE_ASC("title,asc"),
    RATING_DESC("averageRating,desc")
}
