package dev.pinkroom.marketsight.common.paginator

import dev.pinkroom.marketsight.common.Resource

class DefaultPagination<Key, T>(
    private val initialKey: Key?,
    private val onLoadUpdated: (Boolean) -> Unit,
    private val onRequest: suspend (nextPageToken: Key?, nextPageNumber: Int) -> Resource<T>,
    private val getNextKey: suspend (T) -> Key?,
    private val onError: suspend (message: String?) -> Unit,
    private val onSuccess: suspend (data: T, newKey: Key?) -> Unit
): Pagination<Key, T> {

    private var currentKey = initialKey
    private var currentPage = 0
    private var isMakingRequest = false

    override suspend fun loadNextItems() {
        if(isMakingRequest) {
            return
        }
        setLoading(isLoading = true)
        when(val response = onRequest(currentKey, currentPage)){
            is Resource.Error -> {
                onError(response.message)
                setLoading(isLoading = false)
            }
            is Resource.Success -> {
                currentKey = getNextKey(response.data)
                currentPage++
                onSuccess(response.data, currentKey)
                setLoading(isLoading = false)
            }
        }
    }

    private fun setLoading(isLoading: Boolean){
        onLoadUpdated(isLoading)
        isMakingRequest = isLoading
    }

    override fun reset(key: Key?) {
        currentKey = key ?: initialKey
        currentPage = if (key != null) 1 else currentPage
    }
}