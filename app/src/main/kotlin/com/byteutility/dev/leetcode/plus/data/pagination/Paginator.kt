package com.byteutility.dev.leetcode.plus.data.pagination

interface Paginator<Key, Item> {
    suspend fun loadNextItems()
    fun reset()
}
