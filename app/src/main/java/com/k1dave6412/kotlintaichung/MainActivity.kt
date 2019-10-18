package com.k1dave6412.kotlintaichung

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.k1dave6412.kotlintaichung.PaginationListener.Companion.PAGE_START
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private val sharedPreferences by lazy {
        getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    private val api: API by lazy {
        API(sharedPreferences.getString("server", "")!!)
    }

    // recycler view
    private val layoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(this)
    }
    private val recyclerViewAdapter: RecyclerViewAdapter by lazy {
        RecyclerViewAdapter(object : OnElementClickListener {
            override fun onElementClick(element: ListElement) {
                GlobalScope.launch(Dispatchers.Main) {
                    val receiver = api.getReceiver(element.receiverID)
                    element.receiverName = receiver.name
                    element.receiverPhone = receiver.phone

                    val dialog = AlertDialog.Builder(this@MainActivity)
                        .setTitle("收件人資料")
                        .setMessage("收件人姓名：${receiver.name}\n收件人電話：${receiver.phone}")
                        .setPositiveButton("OK", null)
                        .create()
                    dialog.show()
                    recyclerViewAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun initListener() {
        fabMain.setOnClickListener {
            toggleFabMenu()
        }

        fabSetting.setOnClickListener {
            // set server address
            val editText = EditText(this@MainActivity)
            editText.inputType = InputType.TYPE_TEXT_VARIATION_URI
            editText.text = Editable.Factory.getInstance()
                .newEditable(sharedPreferences.getString("server", ""))

            // layout 版面設計 (左右縮排)
            val layout = LinearLayout(this@MainActivity)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(64, 16, 64, 0)
            layout.orientation = LinearLayout.VERTICAL
            layout.addView(editText, layoutParams)

            val dialog = AlertDialog.Builder(this@MainActivity)
                .setTitle("伺服器網址")
                .setView(layout)
                .setPositiveButton("完成") { _, _ ->
                    val editor = sharedPreferences.edit()
                    editor.putString("server", editText.text.toString())
                    editor.apply()
                }
                .setNegativeButton("取消", null)
                .create()
            dialog.show()
        }

        fabDownloadReceiver.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                recyclerViewAdapter.mList.map {
                    async {
                        if (it.receiverID != "" && it.receiverName.length > 10) {
                            val receiver = api.getReceiver(it.receiverID)
                            it.receiverName = receiver.name
                            it.receiverPhone = receiver.phone
                        }
                    }
                }.awaitAll()
                Toast.makeText(this@MainActivity, "更新完成", Toast.LENGTH_SHORT).show()
                recyclerViewAdapter.notifyDataSetChanged()
            }
        }

        recyclerView.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMore() {
                isLoading = true
                currentPage++
                getOrders()
            }

            override fun isLastPage(): Boolean = isLastPage
            override fun isLoading(): Boolean = isLoading
        })

        swipeRefresh.setOnRefreshListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initListener()
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recyclerViewAdapter
    }

    // swipe refresh
    override fun onRefresh() {
        currentPage = PAGE_START
        isLastPage = false
        elementCount = 0
        recyclerViewAdapter.clear()
        getOrders()
    }

    private fun getOrders() {
        val elements: MutableList<ListElement> = mutableListOf()
        GlobalScope.launch {
            val data = api.getOrders(currentPage)
            val orders = data.data
            orders.forEach {
                elements.add(
                    ListElement(
                        id = it.order_id,
                        orderCreateAt = it.order_created_at,
                        packageNo = it.package_no,
                        status = it.status,
                        receiverName = it.receiver_id,
                        receiverPhone = "",
                        receiverID = it.receiver_id
                    )
                )
            }
            val page = data.page
            elementCount = page.total
            totalPage = page.pages
            isLastPage = !page.has_next

            if (currentPage != PAGE_START) recyclerViewAdapter.removeLoading()
            recyclerViewAdapter.addElements(elements)
            swipeRefresh.isRefreshing = false

            if (currentPage < totalPage) {
                recyclerViewAdapter.addLoading()
            } else {
                isLastPage = true
            }
            isLoading = false
        }
    }

    private fun toggleFabMenu() {
        // 開關 fab menu
        val rotate: Animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.rotate_clockwise)
        fabMain.startAnimation(rotate)

        if (isFabOpen) {
            linearDownload.animate().translationY(0F)
            linearSetting.animate().translationY(0F)

            tvDownload.animate().alpha(0f)
            tvSetting.animate().alpha(0f)
        } else {
            linearDownload.animate().translationY(-resources.getDimension(R.dimen._56))
            linearSetting.animate().translationY(-resources.getDimension(R.dimen._104))

            tvDownload.animate().alpha(1.0f)
            tvSetting.animate().alpha(1.0f)
        }
        isFabOpen = !isFabOpen
    }

    companion object {
        var isFabOpen = false
        var isLastPage = false
        var isLoading = false
        var elementCount = 0
        var currentPage = PAGE_START
        var totalPage = 3
        const val PREF_NAME = "setting"
    }
}
