package com.swu.contentproviderone

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), View.OnClickListener {
    
    private lateinit var btnSimulate: Button
    private lateinit var btnReal: Button
    private lateinit var contactView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val contactsList = mutableListOf<String>()
    
    companion object {
        private const val REQUEST_CODE_READ_CONTACTS = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化视图
        initViews()
        
        // ListView数据加载
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, contactsList)
        contactView.adapter = adapter
        
        // 设置按钮点击监听
        btnSimulate.setOnClickListener(this)
        btnReal.setOnClickListener(this)
    }
    
    /**
     * 初始化视图组件
     */
    private fun initViews() {
        btnSimulate = findViewById(R.id.btn_simulate)
        btnReal = findViewById(R.id.btn_real)
        contactView = findViewById(R.id.contacts_view)
    }
    
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_simulate -> {
                loadSimulateData()
            }
            R.id.btn_real -> {
                checkAndReadRealContacts()
            }
        }
    }
    
    /**
     * 加载模拟数据（模拟 ContentProvider 读取数据）
     */
    private fun loadSimulateData() {
        contactsList.clear()
        
        // 模拟从 ContentProvider 读取的数据
        val simulateData = listOf(
            "陈一鸣\t13800138000",
            "老七\t13900139000",
            "孙八\t15000150000",
            "郑十一\t15100151000",
            "钱七\t15200152000",
            "老二\t15300153000",
            "周九\t15400154000",
            "吴十\t15500155000",
            "十八\t15600156000",
            "李四\t15700157000"
        )
        
        contactsList.addAll(simulateData)
        adapter.notifyDataSetChanged()
        
        Toast.makeText(this, "已加载 ${contactsList.size} 条模拟数据", Toast.LENGTH_SHORT).show()
    }
    
    /**
     * 检查权限并读取真实联系人数据
     */
    private fun checkAndReadRealContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_CODE_READ_CONTACTS
            )
        } else {
            // 权限已授予，直接读取
            readRealContacts()
        }
    }
    
    /**
     * 读取真实联系人数据
     */
    @SuppressLint("Range")
    private fun readRealContacts() {
        contactsList.clear()
        
        var cursor: android.database.Cursor? = null
        try {
            cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null
            )
            cursor?.let {
                while (it.moveToNext()) {
                    val displayName = it.getString(
                        it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    )
                    val number = it.getString(
                        it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    )
                    contactsList.add("$displayName\t$number")
                }
            }
            adapter.notifyDataSetChanged()
            
            if (contactsList.isEmpty()) {
                Toast.makeText(this, "未找到联系人数据", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "已加载 ${contactsList.size} 条真实联系人数据", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "读取联系人失败: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_READ_CONTACTS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readRealContacts()
                } else {
                    Toast.makeText(this, "需要读取联系人权限才能查看真实数据", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}