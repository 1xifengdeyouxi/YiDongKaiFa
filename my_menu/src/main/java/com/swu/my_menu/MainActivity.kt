package com.swu.my_menu

import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import java.lang.reflect.Method

class MainActivity : AppCompatActivity() {
    private lateinit var mTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mTextView = findViewById(R.id.text)
    }

    // 创建并显示下拉菜单
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_optionmenu, menu)
        return true
    }

    // 设置菜单图标显示（通过反射）
    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        if (menu.javaClass.simpleName.equals("MenuBuilder", ignoreCase = true)) {
            try {
                val method = menu.javaClass.getDeclaredMethod("setOptionalIconsVisible", Boolean::class.java)
                method.isAccessible = true
                method.invoke(menu, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return super.onMenuOpened(featureId, menu)
    }

    // 下拉菜单项点击事件
    override fun onOptionsItemSelected(@NonNull item: MenuItem): Boolean {
        mTextView.text = item.title
        when (item.itemId) {
            R.id.menu1 -> Toast.makeText(this, "点击了第1个", Toast.LENGTH_SHORT).show()
            R.id.menu2 -> Toast.makeText(this, "点击了第2个", Toast.LENGTH_SHORT).show()
            R.id.menu3 -> Toast.makeText(this, "点击了第3个", Toast.LENGTH_SHORT).show()
            R.id.menu4 -> Toast.makeText(this, "点击了第4个", Toast.LENGTH_SHORT).show()
            R.id.menu5 -> Toast.makeText(this, "点击了第5个", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }
}