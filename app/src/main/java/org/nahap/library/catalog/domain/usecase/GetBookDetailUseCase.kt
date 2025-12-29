package org.nahap.library.catalog.domain.usecase

import org.nahap.library.catalog.domain.model.BookDetail
import org.nahap.library.catalog.domain.repository.CatalogRepository
import javax.inject.Inject

/**
 * Use case для получения детальной информации о книге
 */
class GetBookDetailUseCase @Inject constructor(
    private val catalogRepository: CatalogRepository
) {
    suspend operator fun invoke(bookId: Int): Result<BookDetail> {
        return catalogRepository.getBookDetail(bookId)
    }
}
