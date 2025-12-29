package org.nahap.library.reader.data.mapper

import org.nahap.library.reader.domain.model.Book
import org.nahap.library.reader.domain.model.TocItem as DomainTocItem
import org.nahap.library.reader.model.TocItem as DataTocItem
import org.nahap.library.reader.parser.FB2Parser

/**
 * Mapper для преобразования результатов парсера в Domain модели
 */
object BookMapper {
    
    fun toDomain(parseResult: FB2Parser.ParseResult, bookId: Int): Book {
        return Book(
            id = bookId,
            title = parseResult.title,
            author = parseResult.author,
            htmlContent = parseResult.htmlContent,
            toc = parseResult.toc.map { toDomainTocItem(it) }
        )
    }
    
    private fun toDomainTocItem(dataTocItem: DataTocItem): DomainTocItem {
        return DomainTocItem(
            label = dataTocItem.label,
            href = dataTocItem.href,
            subitems = dataTocItem.subitems?.map { toDomainTocItem(it) }
        )
    }
}
