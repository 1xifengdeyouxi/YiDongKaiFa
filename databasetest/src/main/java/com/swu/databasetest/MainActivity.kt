package com.swu.databasetest

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {
    
    private lateinit var etId: EditText
    private lateinit var etName: EditText
    private lateinit var etAuthor: EditText
    private lateinit var etPages: EditText
    private lateinit var etPrice: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnQuery: Button
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button
    private lateinit var btnQueryAll: Button
    private lateinit var btnClear: Button
    private lateinit var tvDisplay: TextView
    
    private lateinit var dbHelper: MyDatabaseHelper
    private var db: SQLiteDatabase? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        initDatabase()
    }
    
    /**
     * 初始化视图组件
     */
    private fun initViews() {
        etId = findViewById(R.id.et_id)
        etName = findViewById(R.id.et_name)
        etAuthor = findViewById(R.id.et_author)
        etPages = findViewById(R.id.et_pages)
        etPrice = findViewById(R.id.et_price)
        btnAdd = findViewById(R.id.btn_add)
        btnQuery = findViewById(R.id.btn_query)
        btnUpdate = findViewById(R.id.btn_update)
        btnDelete = findViewById(R.id.btn_delete)
        btnQueryAll = findViewById(R.id.btn_query_all)
        btnClear = findViewById(R.id.btn_clear)
        tvDisplay = findViewById(R.id.tv_display)
        
        btnAdd.setOnClickListener(this)
        btnQuery.setOnClickListener(this)
        btnUpdate.setOnClickListener(this)
        btnDelete.setOnClickListener(this)
        btnQueryAll.setOnClickListener(this)
        btnClear.setOnClickListener(this)
    }
    
    /**
     * 初始化数据库
     */
    private fun initDatabase() {
        dbHelper = MyDatabaseHelper(this)
        db = dbHelper.writableDatabase
    }
    
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_add -> addBook()
            R.id.btn_query -> queryBook()
            R.id.btn_update -> updateBook()
            R.id.btn_delete -> deleteBook()
            R.id.btn_query_all -> queryAllBooks()
            R.id.btn_clear -> clearInputs()
        }
    }
    
    /**
     * 添加图书
     */
    private fun addBook() {
        val name = etName.text.toString().trim()
        val author = etAuthor.text.toString().trim()
        val pagesStr = etPages.text.toString().trim()
        val priceStr = etPrice.text.toString().trim()
        
        if (name.isEmpty() || author.isEmpty() || pagesStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show()
            return
        }
        
        val pages = try {
            pagesStr.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "页数必须是数字", Toast.LENGTH_SHORT).show()
            return
        }
        
        val price = try {
            priceStr.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "价格必须是数字", Toast.LENGTH_SHORT).show()
            return
        }
        
        val values = ContentValues().apply {
            put(MyDatabaseHelper.COLUMN_NAME, name)
            put(MyDatabaseHelper.COLUMN_AUTHOR, author)
            put(MyDatabaseHelper.COLUMN_PAGES, pages)
            put(MyDatabaseHelper.COLUMN_PRICE, price)
        }
        
        val result = db?.insert(MyDatabaseHelper.TABLE_NAME, null, values)
        
        if (result != null && result != -1L) {
            Toast.makeText(this, "添加成功，ID: $result", Toast.LENGTH_SHORT).show()
            clearInputs()
            queryAllBooks()
        } else {
            Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 查询图书（根据ID）
     */
    private fun queryBook() {
        val idStr = etId.text.toString().trim()
        
        if (idStr.isEmpty()) {
            Toast.makeText(this, "请输入要查询的图书ID", Toast.LENGTH_SHORT).show()
            return
        }
        
        val id = try {
            idStr.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "ID必须是数字", Toast.LENGTH_SHORT).show()
            return
        }
        
        val cursor = db?.query(
            MyDatabaseHelper.TABLE_NAME,
            null,
            "${MyDatabaseHelper.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null, null, null
        )
        
        cursor?.use {
            if (it.moveToFirst()) {
                val name = it.getString(it.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_NAME))
                val author = it.getString(it.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_AUTHOR))
                val pages = it.getInt(it.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_PAGES))
                val price = it.getDouble(it.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_PRICE))
                
                etName.setText(name)
                etAuthor.setText(author)
                etPages.setText(pages.toString())
                etPrice.setText(price.toString())
                
                Toast.makeText(this, "查询成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "未找到ID为 $id 的图书", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    /**
     * 更新图书
     */
    private fun updateBook() {
        val idStr = etId.text.toString().trim()
        val name = etName.text.toString().trim()
        val author = etAuthor.text.toString().trim()
        val pagesStr = etPages.text.toString().trim()
        val priceStr = etPrice.text.toString().trim()
        
        if (idStr.isEmpty()) {
            Toast.makeText(this, "请输入要更新的图书ID", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (name.isEmpty() || author.isEmpty() || pagesStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show()
            return
        }
        
        val id = try {
            idStr.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "ID必须是数字", Toast.LENGTH_SHORT).show()
            return
        }
        
        val pages = try {
            pagesStr.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "页数必须是数字", Toast.LENGTH_SHORT).show()
            return
        }
        
        val price = try {
            priceStr.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "价格必须是数字", Toast.LENGTH_SHORT).show()
            return
        }
        
        val values = ContentValues().apply {
            put(MyDatabaseHelper.COLUMN_NAME, name)
            put(MyDatabaseHelper.COLUMN_AUTHOR, author)
            put(MyDatabaseHelper.COLUMN_PAGES, pages)
            put(MyDatabaseHelper.COLUMN_PRICE, price)
        }
        
        val result = db?.update(
            MyDatabaseHelper.TABLE_NAME,
            values,
            "${MyDatabaseHelper.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
        
        if (result != null && result > 0) {
            Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show()
            clearInputs()
            queryAllBooks()
        } else {
            Toast.makeText(this, "更新失败或未找到该图书", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 删除图书
     */
    private fun deleteBook() {
        val idStr = etId.text.toString().trim()
        
        if (idStr.isEmpty()) {
            Toast.makeText(this, "请输入要删除的图书ID", Toast.LENGTH_SHORT).show()
            return
        }
        
        val id = try {
            idStr.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "ID必须是数字", Toast.LENGTH_SHORT).show()
            return
        }
        
        val result = db?.delete(
            MyDatabaseHelper.TABLE_NAME,
            "${MyDatabaseHelper.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
        
        if (result != null && result > 0) {
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
            clearInputs()
            queryAllBooks()
        } else {
            Toast.makeText(this, "删除失败或未找到该图书", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 查询所有图书
     */
    private fun queryAllBooks() {
        val cursor = db?.query(
            MyDatabaseHelper.TABLE_NAME,
            null, null, null, null, null,
            "${MyDatabaseHelper.COLUMN_ID} DESC"
        )
        
        val sb = StringBuilder()
        cursor?.use {
            if (it.count == 0) {
                tvDisplay.text = "暂无数据"
                return
            }
            
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_ID))
                val name = it.getString(it.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_NAME))
                val author = it.getString(it.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_AUTHOR))
                val pages = it.getInt(it.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_PAGES))
                val price = it.getDouble(it.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_PRICE))
                
                sb.append("ID: $id\n")
                sb.append("书名: $name\n")
                sb.append("作者: $author\n")
                sb.append("页数: $pages\n")
                sb.append("价格: $price\n")
                sb.append("-------------------\n")
            }
        }
        
        tvDisplay.text = sb.toString()
        Toast.makeText(this, "已加载所有数据", Toast.LENGTH_SHORT).show()
    }
    
    /**
     * 清空输入框
     */
    private fun clearInputs() {
        etId.setText("")
        etName.setText("")
        etAuthor.setText("")
        etPages.setText("")
        etPrice.setText("")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        db?.close()
        dbHelper.close()
    }
}
