package dev.pinkroom.marketsight.common

import dev.pinkroom.marketsight.domain.model.ErrorMessage

sealed class Resource<T>{
    data class Success<T>(val data: T): Resource<T>()
    data class Error<T>(
        val message: String? = null,
        val errorInfo: ErrorMessage? = null,
        val data: T? = null
    ): Resource<T>()
    data class Loading<T>(val isLoading: Boolean=true): Resource<T>()
}