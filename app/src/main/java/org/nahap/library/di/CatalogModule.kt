package org.nahap.library.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.nahap.library.catalog.data.repository.CatalogRepositoryImpl
import org.nahap.library.catalog.domain.repository.CatalogRepository
import javax.inject.Singleton

/**
 * Dagger Hilt модуль для предоставления зависимостей Catalog модуля
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CatalogModule {

    @Binds
    @Singleton
    abstract fun bindCatalogRepository(
        catalogRepositoryImpl: CatalogRepositoryImpl
    ): CatalogRepository
}
