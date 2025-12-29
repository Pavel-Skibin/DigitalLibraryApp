package org.nahap.library.catalog.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.nahap.library.catalog.domain.repository.CatalogRepository
import org.nahap.library.catalog.domain.usecase.GetBookDetailUseCase
import org.nahap.library.catalog.model.BookDetailState
import org.nahap.library.catalog.presentation.mapper.CatalogPresentationMapper
import javax.inject.Inject

/**
 * ViewModel для экрана деталей книги
 */
@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val getBookDetailUseCase: GetBookDetailUseCase,
    private val catalogRepository: CatalogRepository
) : ViewModel() {

    companion object {
        private const val TAG = "BookDetailViewModel"
    }

    private val _state = MutableStateFlow(BookDetailState())
    val state: StateFlow<BookDetailState> = _state.asStateFlow()


    fun loadBookDetails(bookId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            getBookDetailUseCase(bookId).onSuccess { domainBook ->
                val uiBook = CatalogPresentationMapper.bookDetailToUi(domainBook)
                _state.value = _state.value.copy(
                    book = uiBook,
                    isLoading = false
                )
                Log.d(TAG, "Loaded book: ${domainBook.title}")
            }.onFailure { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Ошибка загрузки: ${e.message}"
                )
                Log.e(TAG, "Error loading book details", e)
            }
        }
    }


    fun getBookCoverUrl(bookId: Int): String {
        return catalogRepository.getBookCoverUrl(bookId)
    }
}
