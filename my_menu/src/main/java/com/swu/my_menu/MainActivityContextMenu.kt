package com.swu.my_menu

import android.graphics.Color
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivityContextMenu : AppCompatActivity() {

    private lateinit var mTextView: TextView
    private lateinit var mTextView1: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_context)
        mTextView = findViewById(R.id.text)
        mTextView1 = findViewById(R.id.text2)
        registerForContextMenu(mTextView1) // 注册上下文菜单
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_context, menu)
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
        return super.onMenuOpened(featureId, menu)
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
        inflator.inflate(R.menu.menu_context, menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.blue -> mTextView.setTextColor(Color.BLUE)
            R.id.green -> mTextView.setTextColor(Color.GREEN)
            R.id.red -> mTextView.setTextColor(Color.RED)
            R.id.yellow -> mTextView.setTextColor(Color.YELLOW)
        }
        return super.onContextItemSelected(item)
    }
}
