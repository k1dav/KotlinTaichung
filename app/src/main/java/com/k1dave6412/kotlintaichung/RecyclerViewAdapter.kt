package com.k1dave6412.kotlintaichung

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_element.view.*


class RecyclerViewAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    private var isVisible = false
    private val mList: MutableList<ListElement> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        when (viewType) {
            VIEW_NORMAL -> ProgressHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.list_element, parent, false)
            )
            else -> ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.list_loading, parent, false)
            )
        }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun addElements(newList: MutableList<ListElement>) {
        // 新增元素
        mList.addAll(newList)
    }

    fun addLoading() {
        // LOADING
        isVisible = true
        mList.add(ListElement())
        notifyItemInserted(mList.size - 1)
    }

    fun removeLoading() {
        // LOADING 完成後移除
        isVisible = false
        val position = mList.size - 1
        val element = getItem(position)
        if (element != null) {
            mList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        mList.clear()
        notifyDataSetChanged()
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


        override fun clear() {}

        override fun onBind(position: Int) {
            super.onBind(position)
            val item = mList[position]
            tvStatus.text = item.status
            tvOrderID.text = item.id
            tvOrderCreated.text = item.orderCreateAt
            tvPackageNo.text = item.packageNo
            tvPhone.text = item.receiverPhone

            if (item.receiverName == "") tvReceiverName.text = item.receiverID
            else tvReceiverName.text = item.receiverName
        }
    }

    inner class ProgressHolder internal constructor(itemView: View) : BaseViewHolder(itemView) {
        override fun clear() {}
    }

    companion object {
        const val VIEW_NORMAL = 1
    }
}

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var currentPosition: Int = 0

    open fun onBind(position: Int) {
        currentPosition = position
        clear()
    }

    protected abstract fun clear()
}