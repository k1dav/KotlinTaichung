package com.k1dave6412.kotlintaichung

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.k1dave6412.kotlintaichung.PaginationListener.Companion.PAGE_START
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private val sharedPreferences by lazy {
        getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // recycler view
    private val layoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(this)
    }
    private val recyclerViewAdapter: RecyclerViewAdapter by lazy {
        RecyclerViewAdapter()
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

        recyclerView.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMore() {
                isLoading = true
                currentPage++
                generateFakeData()
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

        generateFakeData()
    }

    // swipe refresh
    override fun onRefresh() {
        currentPage = PAGE_START
        isLastPage = false
        elementCount = 0
        recyclerViewAdapter.clear()
        generateFakeData()
    }

    private fun generateFakeData() {
        // 假資料生成
        // TODO:  API
        val elements: MutableList<ListElement> = mutableListOf()
        print(isLoading)
        for (i in 0..29) {
            elementCount++
            val element = ListElement(
                id = "id=$i",
                orderCreateAt = "c=$i",
                packageNo = "p=$i",
                status = "成功",
                receiverName = "name=$i",
                receiverPhone = "phone=$i",
                receiverID = "receive_id=$i"
            )
            elements.add(element)
        }

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
        const val totalPage = 3
        const val PREF_NAME = "setting"
    }
}
