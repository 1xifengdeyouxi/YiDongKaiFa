package com.swu.sharedpreferencestest

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {
    
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var cbRememberPassword: CheckBox
    private lateinit var btnLogin: Button
    private lateinit var tvLoginStatus: TextView
    
    private lateinit var sharedPreferences: SharedPreferences
    
    companion object {
        private const val PREF_NAME = "login_data"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_REMEMBER_PASSWORD = "remember_password"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 初始化SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        
        // 初始化视图
        initViews()
        
        // 设置点击监听
        btnLogin.setOnClickListener(this)
        
        // 应用启动时自动加载保存的账号和密码（如果之前勾选了记住密码）
        loadSavedCredentials()
    }
    
    /**
     * 初始化视图组件
     */
    private fun initViews() {
        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        cbRememberPassword = findViewById(R.id.cb_remember_password)
        btnLogin = findViewById(R.id.btn_login)
        tvLoginStatus = findViewById(R.id.tv_login_status)
    }
    
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_login -> {
                login()
            }
        }
    }
    
    /**
     * 登录功能
     */
    private fun login() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val rememberPassword = cbRememberPassword.isChecked
        
        // 验证输入
        if (username.isEmpty()) {
            Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (password.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
            return
        }
        
        // 这里可以添加实际的登录验证逻辑
        // 示例：简单的账号密码验证（实际应用中应该连接服务器验证）
        if (username == "吴明涛" && password == "123456") {
            // 登录成功
            tvLoginStatus.text = "登录成功！"
            tvLoginStatus.setTextColor(getColor(android.R.color.holo_green_dark))
            
            // 如果勾选了"记住密码"，则保存账号和密码
            if (rememberPassword) {
                saveCredentials(username, password)
                Toast.makeText(this, "账号和密码已保存", Toast.LENGTH_SHORT).show()
            } else {
                // 如果没有勾选，清除之前保存的数据
                clearSavedCredentials()
            }
            
            Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show()
        } else {
            // 登录失败
            tvLoginStatus.text = "账号或密码错误"
            tvLoginStatus.setTextColor(getColor(android.R.color.holo_red_dark))
            Toast.makeText(this, "账号或密码错误，请重新输入", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 保存账号和密码到SharedPreferences
     */
    private fun saveCredentials(username: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_PASSWORD, password)
        editor.putBoolean(KEY_REMEMBER_PASSWORD, true)
        editor.apply() // 使用apply()异步保存，性能更好
    }
    
    /**
     * 清除保存的账号和密码
     */
    private fun clearSavedCredentials() {
        val editor = sharedPreferences.edit()
        editor.remove(KEY_USERNAME)
        editor.remove(KEY_PASSWORD)
        editor.putBoolean(KEY_REMEMBER_PASSWORD, false)
        editor.apply()
    }
    
    /**
     * 加载保存的账号和密码（应用启动时自动调用）
     */
    private fun loadSavedCredentials() {
        val rememberPassword = sharedPreferences.getBoolean(KEY_REMEMBER_PASSWORD, false)
        
        if (rememberPassword) {
            val savedUsername = sharedPreferences.getString(KEY_USERNAME, "")
            val savedPassword = sharedPreferences.getString(KEY_PASSWORD, "")
            
            if (!savedUsername.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
                // 自动填充账号和密码
                etUsername.setText(savedUsername)
                etPassword.setText(savedPassword)
                cbRememberPassword.isChecked = true
                
                Toast.makeText(this, "已自动填充保存的账号和密码", Toast.LENGTH_SHORT).show()
            }
        } else {
            // 如果没有保存，清空输入框
            etUsername.setText("")
            etPassword.setText("")
            cbRememberPassword.isChecked = false
        }
    }
}