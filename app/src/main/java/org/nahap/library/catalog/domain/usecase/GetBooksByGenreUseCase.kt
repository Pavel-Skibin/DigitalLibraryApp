package org.nahap.library.catalog.domain.usecase

import org.nahap.library.catalog.domain.model.CatalogBook
import org.nahap.library.catalog.domain.model.Page
import org.nahap.library.catalog.domain.repository.CatalogRepository
import javax.inject.Inject

/**
 * Use case для получения книг по жанру
 */
class GetBooksByGenreUseCase @Inject constructor(
    private val catalogRepository: CatalogRepository
) {
    suspend operator fun invoke(genreId: Int, page: Int = 0): Result<Page<CatalogBook>> {
        return catalogRepository.getBooksByGenre(genreId, page)
    }
}
