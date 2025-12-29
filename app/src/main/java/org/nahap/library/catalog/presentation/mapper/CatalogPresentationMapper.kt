package org.nahap.library.catalog.presentation.mapper

import org.nahap.library.catalog.domain.model.Author
import org.nahap.library.catalog.domain.model.BookComment
import org.nahap.library.catalog.domain.model.BookDetail
import org.nahap.library.catalog.domain.model.CatalogBook
import org.nahap.library.catalog.domain.model.Genre
import org.nahap.library.catalog.domain.model.SearchFilter
import org.nahap.library.catalog.domain.model.SortBy
import org.nahap.library.catalog.model.AuthorResponse
import org.nahap.library.catalog.model.BookDetailResponse
import org.nahap.library.catalog.model.BookResponse
import org.nahap.library.catalog.model.CommentResponse
import org.nahap.library.catalog.model.GenreResponse
import org.nahap.library.catalog.model.SearchFilters
import org.nahap.library.catalog.model.SortOption

/**
 * Mapper для преобразования Domain моделей каталога в UI модели (Presentation слой)
 */
object CatalogPresentationMapper {
    
    fun catalogBookToUi(domain: CatalogBook): BookResponse {
        return BookResponse(
            id = domain.id,
            title = domain.title,
            description = domain.description,
            authors = domain.authors,
            genres = domain.genres,
            averageRating = domain.averageRating,
            totalRatings = domain.totalRatings,
            coverUrl = domain.coverUrl
        )
    }
    
    fun bookDetailToUi(domain: BookDetail): BookDetailResponse {
        return BookDetailResponse(
            id = domain.id,
            title = domain.title,
            description = domain.description,
            authors = domain.authors,
            genres = domain.genres,
            averageRating = domain.averageRating,
            totalRatings = domain.totalRatings,
            comments = domain.comments.map { commentToUi(it) },
            coverUrl = null
        )
    }
    
    fun commentToUi(domain: BookComment): CommentResponse {
        return CommentResponse(
            id = domain.id,
            userId = domain.userId,
            userName = domain.userName,
            bookId = domain.bookId,
            text = domain.text,
            createdAt = domain.createdAt,
            deletedAt = null
        )
    }
    
    fun genreToUi(domain: Genre): GenreResponse {
        return GenreResponse(
            id = domain.id,
            name = domain.name,
            description = null
        )
    }
    
    fun authorToUi(domain: Author): AuthorResponse {
        return AuthorResponse(
            id = domain.id,
            firstName = domain.firstName,
            lastName = domain.lastName,
            fullName = domain.fullName
        )
    }
    
    fun searchFiltersToDomain(ui: SearchFilters): SearchFilter {
        return SearchFilter(
            title = ui.title,
            authorIds = ui.selectedAuthors,
            genreIds = ui.selectedGenres,
            minRating = ui.minRating,
            maxRating = ui.maxRating,
            sortBy = when (ui.sortBy) {
                SortOption.TITLE_ASC -> SortBy.TITLE_ASC
                SortOption.RATING_DESC -> SortBy.RATING_DESC
            }
        )
    }
}
