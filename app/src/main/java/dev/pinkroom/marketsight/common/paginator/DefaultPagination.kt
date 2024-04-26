package dev.pinkroom.marketsight.common.paginator

import dev.pinkroom.marketsight.common.Resource

class DefaultPagination<Key, T>(
    private val initialKey: Key?,
    private inline val onLoadUpdated: (Boolean) -> Unit,
    private inline val onRequest: suspend (nextKey: Key?) -> Resource<T>,
    private inline val getNextKey: suspend (T) -> Key?,
    private inline val onError: suspend (message: String?) -> Unit,
    private inline val onSuccess: suspend (data: T, newKey: Key?) -> Unit
): Pagination<Key, T> {

    private var currentKey = initialKey
    private var isMakingRequest = false

    override suspend fun loadNextItems() {
        if(isMakingRequest) {
            return
        }
        setLoading(isLoading = true)
        when(val response = onRequest(currentKey)){
            is Resource.Error -> {
                onError(response.message)
                setLoading(isLoading = false)
            }
            is Resource.Success -> {
                currentKey = getNextKey(response.data)
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
    }
}