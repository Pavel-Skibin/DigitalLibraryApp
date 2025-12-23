package org.nahap.library.catalog. model

data class AuthorResponse(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val fullName: String
)

data class PagedAuthorsResponse(
    val content: List<AuthorResponse>,
    val totalPages: Int,
    val totalElements: Long,
    val number: Int,
    val size: Int
)