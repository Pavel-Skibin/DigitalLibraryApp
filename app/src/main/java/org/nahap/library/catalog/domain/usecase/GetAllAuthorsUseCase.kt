package org.nahap.library.catalog.domain.usecase

import org.nahap.library.catalog.domain.model.Author
import org.nahap.library.catalog.domain.repository.CatalogRepository
import javax.inject.Inject

/**
 * Use case для получения всех авторов
 */
class GetAllAuthorsUseCase @Inject constructor(
    private val catalogRepository: CatalogRepository
) {
    suspend operator fun invoke(): Result<List<Author>> {
        return catalogRepository.getAllAuthors()
    }
}
