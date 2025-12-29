package org.nahap.library.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.nahap.library.reader.data.repository.BookRepositoryImpl
import org.nahap.library.reader.data.repository.BookmarkRepositoryImpl
import org.nahap.library.reader.domain.repository.BookRepository
import org.nahap.library.reader.domain.repository.BookmarkRepository
import javax.inject.Singleton

/**
 * Dagger Hilt модуль для предоставления зависимостей Reader модуля
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ReaderModule {

    @Binds
    @Singleton
    abstract fun bindBookRepository(
        bookRepositoryImpl: BookRepositoryImpl
    ): BookRepository

    @Binds
    @Singleton
    abstract fun bindBookmarkRepository(
        bookmarkRepositoryImpl: BookmarkRepositoryImpl
    ): BookmarkRepository
}
