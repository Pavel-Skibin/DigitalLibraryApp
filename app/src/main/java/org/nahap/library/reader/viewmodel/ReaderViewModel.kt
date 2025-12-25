package org.nahap.library.reader.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.nahap.library.reader.api.BookApi
import org.nahap.library.reader.api.BookmarkApi
import org.nahap.library.reader.model.BookmarkCreateRequest
import org.nahap.library.reader.model.BookmarkUpdateRequest
import org.nahap.library.reader.model.ReaderState
import org.nahap.library.reader.model.ReadingSettings
import org.nahap.library.reader.parser.FB2Parser
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val bookApi: BookApi,
    private val bookmarkApi: BookmarkApi,
    private val fb2Parser: FB2Parser
) : ViewModel() {

    private val _state = MutableStateFlow(ReaderState(bookId = -1))
    val state: StateFlow<ReaderState> = _state.asStateFlow()


    fun loadBook(bookId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, bookId = bookId)

            try {

                val fb2Content = bookApi.getBookFb2(bookId)


                val parsed = fb2Parser.parse(fb2Content)


                val bookmarks = try {
                    bookmarkApi.getBookmarks(bookId).content
                } catch (e: Exception) {
                    emptyList()
                }

                _state.value = _state.value.copy(
                    title = parsed.title,
                    author = parsed.author,
                    htmlContent = parsed.htmlContent,
                    toc = parsed.toc,
                    bookmarks = bookmarks,
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
                val request = BookmarkCreateRequest(
                    bookId = _state.value.bookId,
                    position = position,
                    name = name,
                    notes = notes
                )

                val created = bookmarkApi.createBookmark(request)


                val updated = _state.value.bookmarks + created
                _state.value = _state.value.copy(bookmarks = updated.sortedBy { it.position })

            } catch (e: Exception) {

            }
        }
    }


    fun updateBookmark(bookmarkId: Int, position: Double, name: String, notes: String) {
        viewModelScope.launch {
            try {
                val request = BookmarkUpdateRequest(position, name, notes)
                val updated = bookmarkApi.updateBookmark(bookmarkId, request)

                val list = _state.value.bookmarks.toMutableList()
                val index = list.indexOfFirst { it.id == bookmarkId }
                if (index != -1) {
                    list[index] = updated
                    _state.value = _state.value.copy(bookmarks = list.sortedBy { it.position })
                }

            } catch (e: Exception) {

            }
        }
    }


    fun deleteBookmark(bookmarkId: Int) {
        viewModelScope.launch {
            try {
                bookmarkApi.deleteBookmark(bookmarkId)

                val updated = _state.value.bookmarks.filterNot { it.id == bookmarkId }
                _state.value = _state.value.copy(bookmarks = updated)

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


    fun setMaxInlineSize(maxInlineSize: Int) {
        val current = _state.value.readingSettings
        _state.value = _state.value.copy(
            readingSettings = current.copy(maxInlineSize = maxInlineSize.coerceIn(600, 1400))
        )
    }


    fun setTheme(theme: ReadingSettings.ReadingTheme) {
        val current = _state.value.readingSettings
        _state.value = _state.value.copy(
            readingSettings = current.copy(theme = theme)
        )
    }
}