package com.k1dave6412.kotlintaichung

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_element.view.*


class RecyclerViewAdapter(val listener: OnElementClickListener) :
    RecyclerView.Adapter<BaseViewHolder>() {
    private val mList: MutableList<ListElement> = mutableListOf()
    private var isVisible = false

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        when (viewType) {
            // 如果在 loading 會新增一個 laoding view, loading 完後再刪除換回正常的 view
            VIEW_NORMAL -> ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.list_element, parent, false)
            )
            else -> ProgressHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.list_loading, parent, false)
            )
        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position, listener)
    }

    override fun getItemViewType(position: Int): Int {
        if (isVisible && position == mList.size - 1) {
            return VIEW_LOADING
        }
        return VIEW_NORMAL
    }

    fun clear() {
        mList.clear()
        notifyDataSetChanged()
    }

    fun addElements(newList: MutableList<ListElement>) {
        mList.addAll(newList)
    }

    fun addLoading() {
        // Loading
        isVisible = true
        mList.add(ListElement())
        notifyItemInserted(mList.size - 1)
    }

    fun removeLoading() {
        // Loading 完成後移除
        isVisible = false
        val position = mList.size - 1
        val element = getItem(position)
        if (element != null) {
            mList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun getItem(position: Int): ListElement? {
        return mList[position]
    }


    inner class ViewHolder internal constructor(itemView: View) : BaseViewHolder(itemView) {
        private var tvStatus: TextView = itemView.tvStatus
        private var tvOrderID: TextView = itemView.tvOrderID
        private var tvOrderCreated: TextView = itemView.tvOrderCreated
        private var tvPackageNo: TextView = itemView.tvPackageNo
        private var tvReceiverName: TextView = itemView.tvReceiverName
        private var tvPhone: TextView = itemView.tvPhone

        override fun onBind(position: Int, listener: OnElementClickListener?) {
            super.onBind(position, listener)
            val element = mList[position]
            tvStatus.text = element.status
            tvOrderID.text = element.id
            tvOrderCreated.text = element.orderCreateAt
            tvPackageNo.text = element.packageNo
            tvPhone.text = element.receiverPhone

            if (element.receiverName == "") tvReceiverName.text = element.receiverID
            else tvReceiverName.text = element.receiverName

            if (listener != null) {
                itemView.setOnClickListener {
                    listener.onElementClick(element)
                }
            }
        }

        override fun clear() {}
    }

    inner class ProgressHolder internal constructor(itemView: View) : BaseViewHolder(itemView) {
        override fun clear() {}
    }


    companion object {
        const val VIEW_LOADING = 0
        const val VIEW_NORMAL = 1
    }
}

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var currentPosition: Int = 0

    open fun onBind(position: Int, listener: OnElementClickListener? = null) {
        currentPosition = position
        clear()
    }

    protected abstract fun clear()
}

interface OnElementClickListener {
    fun onElementClick(element: ListElement)
}