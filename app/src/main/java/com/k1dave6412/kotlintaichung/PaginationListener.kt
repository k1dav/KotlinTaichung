package com.k1dave6412.kotlintaichung

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


abstract class PaginationListener(private val layoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleElementCount = layoutManager.childCount
        val totalElementCount = layoutManager.itemCount
        val firstVisibleElementPosition = layoutManager.findFirstVisibleItemPosition()

        if (!isLoading() && !isLastPage()) {
            if (visibleElementCount + firstVisibleElementPosition >= totalElementCount
                && firstVisibleElementPosition >= 0
                && totalElementCount > PAGE_SIZE
            ) {
                loadMoreItems()
            }
        }
    }

    protected abstract fun loadMoreItems()

    abstract fun isLastPage(): Boolean

    abstract fun isLoading(): Boolean

    companion object {
        const val PAGE_START = 1
        const val PAGE_SIZE = 30
    }
}