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
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val sharedPreferences by lazy {
        getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }


    private fun initListener() {
        fabMain.setOnClickListener {
            toggleFabMenu()
        }

        // set server address
        fabSetting.setOnClickListener {
            val editText = EditText(this@MainActivity)
            editText.inputType = InputType.TYPE_TEXT_VARIATION_URI
            editText.text = Editable.Factory.getInstance()
                .newEditable(sharedPreferences.getString("server", ""))

            val layout = LinearLayout(this@MainActivity)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            layoutParams.setMargins(64, 0, 64, 0)
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initListener()
    }


    private fun toggleFabMenu() {
        val rotate: Animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.rotate_clockwise)
        fabMain.startAnimation(rotate)

        if (isFabOpen) {
            linearDownload.animate().translationY(0F)
            linearSetting.animate().translationY(0F)
            fabSetting.animate().translationY(0F)
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
        const val PREF_NAME = "setting"
    }
}
