package dev.pinkroom.marketsight.common.paginator

interface Pagination<Key, T> {
    suspend fun loadNextItems()
    fun reset(key: Key?)
}