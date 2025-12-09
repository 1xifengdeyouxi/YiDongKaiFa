package com.swu.experiment03

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class SecondeActivity : AppCompatActivity() {
    private lateinit var et1: EditText
    private lateinit var et2: EditText
    private lateinit var bt: Button
    private lateinit var tv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seconde)

        et1 = findViewById(R.id.et1)
        et2 = findViewById(R.id.et2)
        bt = findViewById(R.id.bt)
        tv = findViewById(R.id.info)

        // 获取传递的信息并显示
        val info = intent.getStringExtra("info")
        tv.text = info

        // 注册按钮点击事件
        bt.setOnClickListener {
            val account = et1.text.toString()
            val password = et2.text.toString()
            // 回传数据
            val intent = Intent()
            intent.putExtra("账号", account)
            intent.putExtra("密码", password)
            Toast.makeText(this, "注册成功，请重新登录", Toast.LENGTH_LONG).show()
            setResult(1, intent)
            finish()
        }
    }
}