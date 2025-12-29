package org.nahap.library.catalog.domain.model

/**
 * Domain модель книги в каталоге
 */
data class CatalogBook(
    val id: Int,
    val title: String,
    val description: String?,
    val authors: List<String>,
    val genres: List<String>,
    val averageRating: Double?,
    val totalRatings: Long?,
    val coverUrl: String?
)

/**
 * Domain модель детальной информации о книге
 */
data class BookDetail(
    val id: Int,
    val title: String,
    val description: String?,
    val authors: List<String>,
    val genres: List<String>,
    val averageRating: Double?,
    val totalRatings: Long?,
    val comments: List<BookComment>
)

/**
 * Domain модель комментария к книге
 */
data class BookComment(
    val id: Int,
    val userId: Int,
    val userName: String?,
    val bookId: Int,
    val text: String,
    val createdAt: String?
)

/**
 * Domain модель страницы с результатами
 */
data class Page<T>(
    val content: List<T>,
    val totalPages: Int,
    val totalElements: Long,
    val number: Int,
    val hasMore: Boolean
)
