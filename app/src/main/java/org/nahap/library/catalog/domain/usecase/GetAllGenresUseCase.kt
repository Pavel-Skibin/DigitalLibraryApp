package org.nahap.library.catalog.domain.usecase

import org.nahap.library.catalog.domain.model.Genre
import org.nahap.library.catalog.domain.repository.CatalogRepository
import javax.inject.Inject

/**
 * Use case для получения всех жанров
 */
class GetAllGenresUseCase @Inject constructor(
    private val catalogRepository: CatalogRepository
) {
    suspend operator fun invoke(): Result<List<Genre>> {
        return catalogRepository.getAllGenres()
    }
}
