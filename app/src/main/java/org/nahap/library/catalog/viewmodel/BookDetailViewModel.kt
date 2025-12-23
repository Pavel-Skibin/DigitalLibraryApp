package org.nahap.library.catalog.viewmodel

import android.util.Log
import androidx. lifecycle.ViewModel
import androidx. lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx. coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.nahap.library.catalog.model.BookDetailState
import org.nahap.library.catalog.repository.LibraryRepository
import javax.inject. Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val repository: LibraryRepository
) : ViewModel() {

    companion object {
        private const val TAG = "BookDetailViewModel"
    }

    private val _state = MutableStateFlow(BookDetailState())
    val state: StateFlow<BookDetailState> = _state.asStateFlow()

    fun loadBookDetails(bookId: Int) {
        viewModelScope.launch {
            _state. value = _state.value.copy(isLoading = true, error = null)

            repository.getBookDetails(bookId). onSuccess { book ->
                _state.value = _state.value.copy(
                    book = book,
                    isLoading = false
                )
                Log.d(TAG, "Loaded book: ${book.title}")
            }. onFailure { e ->
                _state.value = _state. value.copy(
                    isLoading = false,
                    error = "Ошибка загрузки: ${e. message}"
                )
                Log.e(TAG, "Error loading book details", e)
            }
        }
    }

    fun getBookCoverUrl(bookId: Int): String {
        return repository.getBookCoverUrl(bookId)
    }
}