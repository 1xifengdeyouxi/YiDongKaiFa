package com.swu.broadcastreceiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var mDivReceiver: MyDivBroadcastReceiver  // 原Java中声明但未使用的变量，保留定义

    // 三个广播接收者实例
    private lateinit var mReceiverMessage: ReceiverMessage
    private lateinit var mReceiverMessageTwo: ReceiverMessageTwo
    private lateinit var mReceiverMessageThree: ReceiverMessageThree

    // 发送按钮和输入框
    private lateinit var buttonSendMessage: Button
    private lateinit var editTextMessage: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // 加载布局
        initListener()  // 初始化发送按钮监听
        initSubscriber()  // 初始化广播接收者注册
    }

    // 初始化控件监听事件（发送广播）
    private fun initListener() {
        buttonSendMessage = findViewById(R.id.buttonSendMessage)
        editTextMessage = findViewById(R.id.editText)

        buttonSendMessage.setOnClickListener {
            // 获取输入的消息内容
            val message = editTextMessage.text.toString()
            // 创建广播Intent
            val intent = Intent().apply {
                action = "message"  // 设置广播动作
                putExtra("myMessage", message)  // 携带消息数据
            }
            // 发送有序广播
//            sendOrderedBroadcast(intent, null)
            sendBroadcast(intent, null)
        }
    }

    // 初始化广播接收者注册
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun initSubscriber() {
        val action = "message"  // 广播动作标识

        // 实例化三个广播接收者
        mReceiverMessage = ReceiverMessage()
        mReceiverMessageTwo = ReceiverMessageTwo()
        mReceiverMessageThree = ReceiverMessageThree()

        // 为每个接收者创建并配置过滤器
        val filterOne = IntentFilter(action).apply {
            addAction(action)
        }
        val filterTwo = IntentFilter(action).apply {
            addAction(action)
        }
        val filterThree = IntentFilter(action).apply {
            addAction(action)
        }
        filterOne.priority = 100
        filterTwo.priority = 1000
        filterThree.priority = 500

        // 注册广播接收者
// ... 在 initSubscriber() 中
        if (Build.VERSION.SDK_INT >= 34) {
            // 如果希望该接收器可以接收来自其他应用的广播，使用 RECEIVER_EXPORTED
            registerReceiver(mReceiverMessage, filterOne, Context.RECEIVER_EXPORTED)
            registerReceiver(mReceiverMessageTwo, filterTwo, Context.RECEIVER_EXPORTED)
            registerReceiver(mReceiverMessageThree, filterThree, Context.RECEIVER_EXPORTED)
        } else {
            // 老版本仍然使用旧签名
            registerReceiver(mReceiverMessage, filterOne)
            registerReceiver(mReceiverMessageTwo, filterTwo)
            registerReceiver(mReceiverMessageThree, filterThree)
        }
    }

    // 页面销毁时取消广播注册
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiverMessage)
        unregisterReceiver(mReceiverMessageTwo)
        unregisterReceiver(mReceiverMessageThree)
    }

    // 延迟1000毫秒
    private fun delay() {
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    // 第一个广播接收者（内部类）
    inner class ReceiverMessage : BroadcastReceiver() {
        private lateinit var firstTextView: TextView

        override fun onReceive(context: Context, intent: Intent) {
            val message = intent.getStringExtra("myMessage") ?: ""  // 处理空值
            Log.i("ReceiverMessageOne", "收到广播信息$message")
            firstTextView = findViewById(R.id.firstTextView)
            firstTextView.text = "张三接受到消息:$message"
            delay()
        }
    }

    // 第二个广播接收者（内部类）
    inner class ReceiverMessageTwo : BroadcastReceiver() {
        private lateinit var secondTextView: TextView

        override fun onReceive(context: Context, intent: Intent) {
            val message = intent.getStringExtra("myMessage") ?: ""
            Log.i("ReceiverMessageTwo", "收到广播信息$message")
            secondTextView = findViewById(R.id.secondTextView)
            secondTextView.text = "小王接受到消息:$message"
            delay()
        }
    }

    // 第三个广播接收者（内部类）
    inner class ReceiverMessageThree : BroadcastReceiver() {
        private lateinit var thirdTextView: TextView

        override fun onReceive(context: Context, intent: Intent) {
            val message = intent.getStringExtra("myMessage") ?: ""
            Log.i("ReceiverMessageThree", "收到广播信息$message")
            thirdTextView = findViewById(R.id.thirdTextView)
            thirdTextView.text = "李晓晓接受到消息:$message"
            abortBroadcast();
            delay()
        }
    }
}