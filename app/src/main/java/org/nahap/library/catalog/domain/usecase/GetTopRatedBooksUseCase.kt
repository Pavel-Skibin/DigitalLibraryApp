package org.nahap.library.catalog.domain.usecase

import org.nahap.library.catalog.domain.model.CatalogBook
import org.nahap.library.catalog.domain.repository.CatalogRepository
import javax.inject.Inject

/**
 * Use case для получения топ книг по рейтингу
 */
class GetTopRatedBooksUseCase @Inject constructor(
    private val catalogRepository: CatalogRepository
) {
    suspend operator fun invoke(page: Int = 0): Result<List<CatalogBook>> {
        return catalogRepository.getTopRatedBooks(page)
    }
}
