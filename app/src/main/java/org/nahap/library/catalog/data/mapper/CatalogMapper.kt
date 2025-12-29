package org.nahap.library.catalog.data.mapper

import org.nahap.library.catalog.domain.model.Author
import org.nahap.library.catalog.domain.model.BookComment
import org.nahap.library.catalog.domain.model.BookDetail
import org.nahap.library.catalog.domain.model.CatalogBook
import org.nahap.library.catalog.domain.model.Genre
import org.nahap.library.catalog.domain.model.Page
import org.nahap.library.catalog.model.AuthorResponse
import org.nahap.library.catalog.model.BookDetailResponse
import org.nahap.library.catalog.model.BookResponse
import org.nahap.library.catalog.model.BookStatResponse
import org.nahap.library.catalog.model.CommentResponse
import org.nahap.library.catalog.model.GenreResponse
import org.nahap.library.catalog.model.PageResponse

/**
 * Mapper для преобразования API моделей каталога в Domain модели
 */
object CatalogMapper {
    
    fun bookToDomain(response: BookResponse): CatalogBook {
        return CatalogBook(
            id = response.id,
            title = response.title,
            description = response.description,
            authors = response.authors ?: emptyList(),
            genres = response.genres ?: emptyList(),
            averageRating = response.averageRating,
            totalRatings = response.totalRatings,
            coverUrl = response.coverUrl
        )
    }
    
    fun bookStatToDomain(response: BookStatResponse): CatalogBook {
        return CatalogBook(
            id = response.bookId,
            title = response.title,
            description = null,
            authors = emptyList(),
            genres = emptyList(),
            averageRating = response.averageRating,
            totalRatings = response.ratingCount,
            coverUrl = null
        )
    }
    
    fun bookDetailToDomain(response: BookDetailResponse): BookDetail {
        return BookDetail(
            id = response.id,
            title = response.title,
            description = response.description,
            authors = response.authors ?: emptyList(),
            genres = response.genres ?: emptyList(),
            averageRating = response.averageRating,
            totalRatings = response.totalRatings,
            comments = response.comments?.map { commentToDomain(it) } ?: emptyList()
        )
    }
    
    fun commentToDomain(response: CommentResponse): BookComment {
        return BookComment(
            id = response.id,
            userId = response.userId,
            userName = response.userName,
            bookId = response.bookId,
            text = response.text,
            createdAt = response.createdAt
        )
    }
    
    fun genreToDomain(response: GenreResponse): Genre {
        return Genre(
            id = response.id,
            name = response.name
        )
    }
    
    fun authorToDomain(response: AuthorResponse): Author {
        return Author(
            id = response.id,
            firstName = response.firstName,
            lastName = response.lastName,
            middleName = null
        )
    }
    
    fun <T, R> pageToDomain(response: PageResponse<T>, mapper: (T) -> R): Page<R> {
        return Page(
            content = response.content.map(mapper),
            totalPages = response.totalPages,
            totalElements = response.totalElements,
            number = response.number,
            hasMore = !response.last
        )
    }
}
