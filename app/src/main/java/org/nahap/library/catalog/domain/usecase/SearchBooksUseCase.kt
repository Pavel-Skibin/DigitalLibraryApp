package org.nahap.library.catalog.domain.usecase

import org.nahap.library.catalog.domain.model.CatalogBook
import org.nahap.library.catalog.domain.model.Page
import org.nahap.library.catalog.domain.model.SearchFilter
import org.nahap.library.catalog.domain.repository.CatalogRepository
import javax.inject.Inject

/**
 * Use case для поиска книг с фильтрами
 */
class SearchBooksUseCase @Inject constructor(
    private val catalogRepository: CatalogRepository
) {
    suspend operator fun invoke(filter: SearchFilter, page: Int = 0): Result<Page<CatalogBook>> {
        return catalogRepository.searchBooks(filter, page)
    }
}
