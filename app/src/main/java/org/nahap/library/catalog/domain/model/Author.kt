package org.nahap.library.catalog.domain.model

/**
 * Domain модель автора
 */
data class Author(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val middleName: String?
) {
    val fullName: String
        get() = listOfNotNull(firstName, middleName, lastName)
            .filter { it.isNotEmpty() }
            .joinToString(" ")
}
