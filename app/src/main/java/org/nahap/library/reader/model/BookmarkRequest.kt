package org.nahap.library.reader.model


data class BookmarkCreateRequest(
    val bookId: Int,
    val position: Double,
    val name: String,
    val notes: String = ""
)


data class BookmarkUpdateRequest(
    val position: Double,
    val name: String,
    val notes: String
)


data class BookmarkResponse(
    val id: Int,
    val bookId: Int,
    val position: Double,
    val name: String,
    val notes: String?,
    val createdAt: String?
)