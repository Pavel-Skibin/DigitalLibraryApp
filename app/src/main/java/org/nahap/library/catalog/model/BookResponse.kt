package org.nahap.library.catalog.model

data class BookResponse(
    val id: Int,
    val title: String,
    val description: String?,
    val authors: List<String>?,
    val genres: List<String>?,
    val averageRating: Double?,
    val totalRatings: Long?,
    val coverUrl: String?
)

data class BookDetailResponse(
    val id: Int,
    val title: String,
    val description: String?,
    val authors: List<String>?,
    val genres: List<String>?,
    val averageRating: Double?,
    val totalRatings: Long?,
    val comments: List<CommentResponse>?,
    val coverUrl: String?
)

data class CommentResponse(
    val id: Int,
    val userId: Int,
    val userName: String?,
    val bookId: Int,
    val text: String,
    val createdAt: String?,
    val deletedAt: String?
)

data class BookStatResponse(
    val bookId: Int,
    val title: String,
    val averageRating: Double?,
    val ratingCount: Long?,
    val commentCount: Long?,
    val bookmarkCount: Long?
)

data class PageResponse<T>(
    val content: List<T>,
    val totalPages: Int,
    val totalElements: Long,
    val number: Int,
    val size: Int,
    val first: Boolean,
    val last: Boolean
)