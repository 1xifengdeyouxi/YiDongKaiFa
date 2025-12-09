package com.swu.experiment03

import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var tv1: TextView
    private lateinit var tv2: TextView
    private lateinit var bt1: Button
    private lateinit var bt2: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv1 = findViewById(R.id.tv1) // 账号
        tv2 = findViewById(R.id.tv2) // 密码
        bt1 = findViewById(R.id.bt1) // 登录按钮
        bt2 = findViewById(R.id.bt2) // 注册按钮

        // 登录按钮点击事件
        bt1.setOnClickListener {
            val account = tv1.text.toString()
            val password = tv2.text.toString()
            if (account.length <= 3 && password.length <= 3) {
                Toast.makeText(this, "账号不存在，请先注册", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "登录成功", Toast.LENGTH_LONG).show()
            }
        }

        // 处理回传数据
        val launcher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                val data = result.data
                val account = data?.getStringExtra("账号")
                val password = data?.getStringExtra("密码")
                if (account != null && password != null) {
                    tv1.text = tv1.text.toString() + account
                    tv2.text = tv2.text.toString() + password
                }
            }
        )

        // 注册按钮点击事件（跳转页面）
        bt2.setOnClickListener {
            val intent = Intent(this@MainActivity, SecondeActivity::class.java)
            intent.putExtra("info", "用户名和密码的长度要超过3")
            launcher.launch(intent)
        }
    }
}