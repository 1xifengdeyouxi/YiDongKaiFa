package com.swu.handlertest

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var mTextView: TextView
    private lateinit var mHandler: Handler

    // 自定义Handler子类(继承Handler类) & 复写handleMessage()方法
    inner class MHandler : Handler(Looper.getMainLooper()) {
        // 通过复写handlerMessage() 从而确定更新UI的操作
        override fun handleMessage(msg: Message) {
            // 根据不同线程发送过来的消息，执行不同的UI操作
            // 根据Message对象的what属性 标识不同的消息
            when (msg.what) {
                1 -> mTextView.text = "执行了线程1的UI操作: ${msg.obj}"
                2 -> mTextView.text = "执行了线程2的UI操作"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        mTextView = findViewById(R.id.text)
        // 在主线程中创建Handler实例
        mHandler = MHandler()

        // 第1个工作线程
        Thread {
            try {
                Thread.sleep(3000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            // 创建所需的消息对象
            val msg = Message.obtain().apply {
                what = 1 // 消息标识
                obj = "A" // 消息内容存放
            }
            // 在工作线程中 通过Handler发送消息到消息队列中
            mHandler.sendMessage(msg)
        }.start()

        // 第2个工作线程
        Thread {
            try {
                Thread.sleep(6000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            // 定义要发送的消息
            val msg = Message.obtain().apply {
                what = 2 // 消息标识
                obj = "B" // 消息内容存放
            }
            // 通过Handler发送消息到其绑定的消息队列
            mHandler.sendMessage(msg)
        }.start()
    }
}