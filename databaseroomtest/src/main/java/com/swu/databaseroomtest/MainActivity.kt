package com.swu.databaseroomtest

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), View.OnClickListener {
    
    private lateinit var etId: EditText
    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnInsert: Button
    private lateinit var btnQuery: Button
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button
    private lateinit var btnQueryAll: Button
    private lateinit var btnClear: Button
    private lateinit var tvDisplay: TextView
    
    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 初始化数据库
        database = AppDatabase.getDatabase(this)
        userDao = database.userDao()
        
        // 初始化视图
        initViews()
        
        // 设置点击监听
        btnInsert.setOnClickListener(this)
        btnQuery.setOnClickListener(this)
        btnUpdate.setOnClickListener(this)
        btnDelete.setOnClickListener(this)
        btnQueryAll.setOnClickListener(this)
        btnClear.setOnClickListener(this)
        
        // 自动查询所有数据并实时更新
        observeAllUsers()
    }
    
    /**
     * 初始化视图组件
     */
    private fun initViews() {
        etId = findViewById(R.id.et_id)
        etName = findViewById(R.id.et_name)
        etAge = findViewById(R.id.et_age)
        etEmail = findViewById(R.id.et_email)
        btnInsert = findViewById(R.id.btn_insert)
        btnQuery = findViewById(R.id.btn_query)
        btnUpdate = findViewById(R.id.btn_update)
        btnDelete = findViewById(R.id.btn_delete)
        btnQueryAll = findViewById(R.id.btn_query_all)
        btnClear = findViewById(R.id.btn_clear)
        tvDisplay = findViewById(R.id.tv_display)
    }
    
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_insert -> insertUser()
            R.id.btn_query -> queryUser()
            R.id.btn_update -> updateUser()
            R.id.btn_delete -> deleteUser()
            R.id.btn_query_all -> queryAllUsers()
            R.id.btn_clear -> clearInputs()
        }
    }
    
    /**
     * 插入用户
     */
    private fun insertUser() {
        val name = etName.text.toString().trim()
        val ageStr = etAge.text.toString().trim()
        val email = etEmail.text.toString().trim()
        
        if (name.isEmpty() || ageStr.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show()
            return
        }
        
        val age = try {
            ageStr.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "年龄必须是数字", Toast.LENGTH_SHORT).show()
            return
        }
        
        lifecycleScope.launch {
            try {
                val user = User(name = name, age = age, email = email)
                val id = userDao.insertUser(user)
                Toast.makeText(this@MainActivity, "添加成功，ID: $id", Toast.LENGTH_SHORT).show()
                clearInputs()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "添加失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    /**
     * 查询用户（根据ID）
     */
    private fun queryUser() {
        val idStr = etId.text.toString().trim()
        
        if (idStr.isEmpty()) {
            Toast.makeText(this, "请输入要查询的用户ID", Toast.LENGTH_SHORT).show()
            return
        }
        
        val id = try {
            idStr.toLong()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "ID必须是数字", Toast.LENGTH_SHORT).show()
            return
        }
        
        lifecycleScope.launch {
            try {
                val user = userDao.getUserById(id)
                if (user != null) {
                    val displayText = "ID: ${user.id}\n姓名: ${user.name}\n年龄: ${user.age}\n邮箱: ${user.email}"
                    Toast.makeText(this@MainActivity, displayText, Toast.LENGTH_LONG).show()
                    // 填充到输入框
                    etName.setText(user.name)
                    etAge.setText(user.age.toString())
                    etEmail.setText(user.email)
                } else {
                    Toast.makeText(this@MainActivity, "未找到ID为 $id 的用户", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "查询失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    /**
     * 更新用户
     */
    private fun updateUser() {
        val idStr = etId.text.toString().trim()
        val name = etName.text.toString().trim()
        val ageStr = etAge.text.toString().trim()
        val email = etEmail.text.toString().trim()
        
        if (idStr.isEmpty()) {
            Toast.makeText(this, "请输入要更新的用户ID", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (name.isEmpty() || ageStr.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show()
            return
        }
        
        val id = try {
            idStr.toLong()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "ID必须是数字", Toast.LENGTH_SHORT).show()
            return
        }
        
        val age = try {
            ageStr.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "年龄必须是数字", Toast.LENGTH_SHORT).show()
            return
        }
        
        lifecycleScope.launch {
            try {
                val user = User(id = id, name = name, age = age, email = email)
                userDao.updateUser(user)
                Toast.makeText(this@MainActivity, "更新成功", Toast.LENGTH_SHORT).show()
                clearInputs()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "更新失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    /**
     * 删除用户
     */
    private fun deleteUser() {
        val idStr = etId.text.toString().trim()
        
        if (idStr.isEmpty()) {
            Toast.makeText(this, "请输入要删除的用户ID", Toast.LENGTH_SHORT).show()
            return
        }
        
        val id = try {
            idStr.toLong()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "ID必须是数字", Toast.LENGTH_SHORT).show()
            return
        }
        
        lifecycleScope.launch {
            try {
                val user = userDao.getUserById(id)
                if (user != null) {
                    userDao.deleteUser(user)
                    Toast.makeText(this@MainActivity, "删除成功", Toast.LENGTH_SHORT).show()
                    clearInputs()
                } else {
                    Toast.makeText(this@MainActivity, "未找到ID为 $id 的用户", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "删除失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    /**
     * 查询所有用户
     * 注意：数据已经在实时更新，此方法仅用于刷新显示
     */
    private fun queryAllUsers() {
        Toast.makeText(this, "数据已实时更新，请查看下方列表", Toast.LENGTH_SHORT).show()
        // 数据会通过 observeAllUsers() 自动更新，无需重复查询
    }
    
    /**
     * 观察所有用户数据变化（实时更新）
     */
    private fun observeAllUsers() {
        lifecycleScope.launch {
            userDao.getAllUsers().collect { userList ->
                displayUsers(userList)
            }
        }
    }
    
    /**
     * 显示用户列表
     */
    private fun displayUsers(users: List<User>) {
        if (users.isEmpty()) {
            tvDisplay.text = "暂无数据"
        } else {
            val sb = StringBuilder()
            users.forEach { user ->
                sb.append("ID: ${user.id}\n")
                sb.append("姓名: ${user.name}\n")
                sb.append("年龄: ${user.age}\n")
                sb.append("邮箱: ${user.email}\n")
                sb.append("-------------------\n")
            }
            tvDisplay.text = sb.toString()
        }
    }
    
    /**
     * 清空输入框
     */
    private fun clearInputs() {
        etId.setText("")
        etName.setText("")
        etAge.setText("")
        etEmail.setText("")
    }
}