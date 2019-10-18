package com.k1dave6412.kotlintaichung

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


abstract class PaginationListener(private val layoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {
    /*
    RecyclerView 分頁
    當可視的數量加上第一筆 >= 總筆數的時候
    就刷新頁
     */
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleElementCount = layoutManager.childCount
        val firstVisibleElementPosition = layoutManager.findFirstVisibleItemPosition()
        val totalElementCount = layoutManager.itemCount

        if (!isLoading() && !isLastPage()) {
            if (visibleElementCount + firstVisibleElementPosition >= totalElementCount
                && firstVisibleElementPosition >= 0
                && totalElementCount > PAGE_SIZE
            ) {
                loadMore()
            }
        }
    }

    protected abstract fun loadMore()
    protected abstract fun isLastPage(): Boolean
    protected abstract fun isLoading(): Boolean

    companion object {
        const val PAGE_START = 1
        const val PAGE_SIZE = 30
    }
}