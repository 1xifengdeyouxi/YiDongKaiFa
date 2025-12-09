package com.swu.my_menu

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivitySubMenu : AppCompatActivity() {

    private lateinit var mTextView: TextView
    private lateinit var mTextView1: TextView
    private lateinit var mTextView2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_sub)
        mTextView = findViewById(R.id.text)
        mTextView1 = findViewById(R.id.text2)
        mTextView2 = findViewById(R.id.text_sub)
        registerForContextMenu(mTextView2) // 注册上下文菜单
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_sub, menu)
        return true
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        if (menu != null) {
            if (menu.javaClass.simpleName.equals("MenuBuilder", ignoreCase = true)) {
                try {
                    val method = menu.javaClass.getDeclaredMethod("setOptionalIconsVisible", Boolean::class.javaPrimitiveType)
                    method.isAccessible = true
                    method.invoke(menu, true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return menu.let { super.onMenuOpened(featureId, it) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        mTextView.text = item.title
        when (item.itemId) {
            R.id.menu1 -> Toast.makeText(this, "点击了第1个", Toast.LENGTH_SHORT).show()
            R.id.menu2 -> Toast.makeText(this, "点击了第2个", Toast.LENGTH_SHORT).show()
            R.id.menu3 -> Toast.makeText(this, "点击了第3个", Toast.LENGTH_SHORT).show()
            R.id.menu4 -> Toast.makeText(this, "点击了第4个", Toast.LENGTH_SHORT).show()
            R.id.menu5 -> Toast.makeText(this, "点击了第5个", Toast.LENGTH_SHORT).show()
            R.id.menu6 -> Toast.makeText(this, "点击了第6个", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        val inflator = MenuInflater(this)
        inflator.inflate(R.menu.menu_sub, menu) // 绑定子菜单资源
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.colour_blue -> mTextView.setTextColor(Color.BLUE)
            R.id.colour_green -> mTextView.setTextColor(Color.GREEN)
            R.id.colour_red -> mTextView.setTextColor(Color.RED)
            R.id.colour_yellow -> mTextView.setTextColor(Color.YELLOW)

            R.id.font_size_10sp -> mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            R.id.font_size_30sp -> mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
            R.id.font_size_50sp -> mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f)
            R.id.font_size_70sp -> mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 70f)

            R.id.text_hello, R.id.text_menu, R.id.text_MQ -> mTextView.text = item.title

            R.id.text_div -> {
                val editText = EditText(this)
                AlertDialog.Builder(this)
                    .setTitle("请输入要自定义的文字")
                    .setView(editText)
                    .setPositiveButton("确定") { _, _ ->
                        mTextView.text = editText.text.toString()
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }
        }
        return super.onContextItemSelected(item)
    }
}
