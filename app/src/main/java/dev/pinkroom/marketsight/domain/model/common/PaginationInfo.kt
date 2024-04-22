package dev.pinkroom.marketsight.domain.model.common

data class PaginationInfo(
    val isLoading: Boolean = false,
    val endReached: Boolean = false,
    val page: String? = null
)