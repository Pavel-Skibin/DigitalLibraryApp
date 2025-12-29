package org.nahap.library.reader.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.nahap.library.reader.domain.usecase.CreateBookmarkUseCase
import org.nahap.library.reader.domain.usecase.DeleteBookmarkUseCase
import org.nahap.library.reader.domain.usecase.GetBookmarksUseCase
import org.nahap.library.reader.domain.usecase.LoadBookUseCase
import org.nahap.library.reader.domain.usecase.UpdateBookmarkUseCase
import org.nahap.library.reader.model.ReaderState
import org.nahap.library.reader.model.ReadingSettings
import org.nahap.library.reader.presentation.mapper.ReaderPresentationMapper
import javax.inject.Inject

/**
 * ViewModel для экрана чтения книги
 */
@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val loadBookUseCase: LoadBookUseCase,
    private val getBookmarksUseCase: GetBookmarksUseCase,
    private val createBookmarkUseCase: CreateBookmarkUseCase,
    private val updateBookmarkUseCase: UpdateBookmarkUseCase,
    private val deleteBookmarkUseCase: DeleteBookmarkUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ReaderState(bookId = -1))
    val state: StateFlow<ReaderState> = _state.asStateFlow()


    fun loadBook(bookId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, bookId = bookId)

            try {
                val bookResult = loadBookUseCase(bookId)
                
                if (bookResult.isFailure) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Ошибка загрузки: ${bookResult.exceptionOrNull()?.message}"
                    )
                    return@launch
                }
                
                val book = bookResult.getOrNull()!!
                val bookmarksResult = getBookmarksUseCase(bookId)
                val bookmarks = bookmarksResult.getOrNull() ?: emptyList()

                _state.value = _state.value.copy(
                    title = book.title,
                    author = book.author,
                    htmlContent = book.htmlContent,
                    toc = book.toc.map { ReaderPresentationMapper.tocItemToUi(it) },
                    bookmarks = bookmarks.map { ReaderPresentationMapper.bookmarkToUi(it) },
                    isLoading = false
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Ошибка загрузки: ${e.message}"
                )
            }
        }
    }


    fun createBookmark(position: Double, name: String, notes: String = "") {
        viewModelScope.launch {
            try {
                val result = createBookmarkUseCase(
                    bookId = _state.value.bookId,
                    position = position,
                    name = name,
                    notes = notes
                )

                result.onSuccess { createdBookmark ->
                    val updated = _state.value.bookmarks + ReaderPresentationMapper.bookmarkToUi(createdBookmark)
                    _state.value = _state.value.copy(bookmarks = updated.sortedBy { it.position })
                }

            } catch (e: Exception) {

            }
        }
    }


    fun updateBookmark(bookmarkId: Int, position: Double, name: String, notes: String) {
        viewModelScope.launch {
            try {
                val result = updateBookmarkUseCase(
                    bookmarkId = bookmarkId,
                    position = position,
                    name = name,
                    notes = notes
                )

                result.onSuccess { updatedBookmark ->
                    val list = _state.value.bookmarks.toMutableList()
                    val index = list.indexOfFirst { it.id == bookmarkId }
                    if (index != -1) {
                        list[index] = ReaderPresentationMapper.bookmarkToUi(updatedBookmark)
                        _state.value = _state.value.copy(bookmarks = list.sortedBy { it.position })
                    }
                }

            } catch (e: Exception) {

            }
        }
    }


    fun deleteBookmark(bookmarkId: Int) {
        viewModelScope.launch {
            try {
                val result = deleteBookmarkUseCase(bookmarkId)

                result.onSuccess {
                    val updated = _state.value.bookmarks.filterNot { it.id == bookmarkId }
                    _state.value = _state.value.copy(bookmarks = updated)
                }

            } catch (e: Exception) {

            }
        }
    }


    fun updatePosition(position: Double) {
        _state.value = _state.value.copy(currentPosition = position)
    }


    fun updatePageInfo(pageInfo: String) {
        _state.value = _state.value.copy(currentPage = pageInfo)
    }


    fun updateReadingSettings(settings: ReadingSettings) {
        _state.value = _state.value.copy(readingSettings = settings)
    }


    fun setReadingFlow(flow: ReadingSettings.ReadingFlow) {
        val current = _state.value.readingSettings
        _state.value = _state.value.copy(
            readingSettings = current.copy(flow = flow)
        )
    }


    fun setFontSize(fontSize: Float) {
        val current = _state.value.readingSettings
        _state.value = _state.value.copy(
            readingSettings = current.copy(fontSize = fontSize.coerceIn(8f, 32f))
        )
    }


    fun setLineHeight(lineHeight: Float) {
        val current = _state.value.readingSettings
        _state.value = _state.value.copy(
            readingSettings = current.copy(lineHeight = lineHeight.coerceIn(1.0f, 2.5f))
        )
    }


    fun setWordSpacing(wordSpacing: Float) {
        val current = _state.value.readingSettings
        _state.value = _state.value.copy(
            readingSettings = current.copy(wordSpacing = wordSpacing.coerceIn(0f, 0.5f))
        )
    }


    fun setTheme(theme: ReadingSettings.ReadingTheme) {
        val current = _state.value.readingSettings
        _state.value = _state.value.copy(
            readingSettings = current.copy(theme = theme)
        )
    }
}