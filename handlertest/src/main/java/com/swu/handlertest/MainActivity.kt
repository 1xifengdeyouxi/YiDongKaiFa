package com.swu.handlertest

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(), View.OnClickListener {
    
    private lateinit var mTextView: TextView
    private lateinit var btnSendMessage: Button
    private lateinit var btnSendDelayed: Button
    private lateinit var btnStartTimer: Button
    private lateinit var btnStopTimer: Button
    private lateinit var btnClear: Button
    
    private lateinit var mHandler: MHandler
    private var timerRunnable: Runnable? = null
    private var timerCount = 0
    
    companion object {
        private const val MSG_FROM_WORK_THREAD = 1
        private const val MSG_DELAYED = 2
        private const val MSG_TIMER = 3
    }
    
    // 自定义Handler子类(继承Handler类) & 复写handleMessage()方法
    inner class MHandler : Handler(Looper.getMainLooper()) {
        // 通过复写handleMessage() 从而确定更新UI的操作
        override fun handleMessage(msg: Message) {
            // 根据不同线程发送过来的消息，执行不同的UI操作
            // 根据Message对象的what属性 标识不同的消息
            val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            val existingText = mTextView.text.toString()
            
            when (msg.what) {
                MSG_FROM_WORK_THREAD -> {
                    val newText = "$existingText\n[$currentTime] 工作线程消息: ${msg.obj}"
                    mTextView.text = newText
                    Toast.makeText(this@MainActivity, "收到工作线程消息", Toast.LENGTH_SHORT).show()
                }
                MSG_DELAYED -> {
                    val newText = "$existingText\n[$currentTime] 延迟消息已执行: ${msg.obj}"
                    mTextView.text = newText
                    Toast.makeText(this@MainActivity, "延迟消息已执行", Toast.LENGTH_SHORT).show()
                }
                MSG_TIMER -> {
                    timerCount++
                    val newText = "$existingText\n[$currentTime] 定时任务 #$timerCount"
                    mTextView.text = newText
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        
        // 在主线程中创建Handler实例
        mHandler = MHandler()
    }
    
    /**
     * 初始化视图组件
     */
    private fun initViews() {
        mTextView = findViewById(R.id.text)
        btnSendMessage = findViewById(R.id.btn_send_message)
        btnSendDelayed = findViewById(R.id.btn_send_delayed)
        btnStartTimer = findViewById(R.id.btn_start_timer)
        btnStopTimer = findViewById(R.id.btn_stop_timer)
        btnClear = findViewById(R.id.btn_clear)
        
        btnSendMessage.setOnClickListener(this)
        btnSendDelayed.setOnClickListener(this)
        btnStartTimer.setOnClickListener(this)
        btnStopTimer.setOnClickListener(this)
        btnClear.setOnClickListener(this)
    }
    
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_send_message -> {
                sendMessageFromWorkThread()
            }
            R.id.btn_send_delayed -> {
                sendDelayedMessage()
            }
            R.id.btn_start_timer -> {
                startTimer()
            }
            R.id.btn_stop_timer -> {
                stopTimer()
            }
            R.id.btn_clear -> {
                clearDisplay()
            }
        }
    }
    
    /**
     * 从工作线程发送消息
     */
    private fun sendMessageFromWorkThread() {
        // 在工作线程中发送消息
        Thread {
            try {
                // 模拟耗时操作
                Thread.sleep(1000)
                
                // 创建所需的消息对象
                val msg = Message.obtain().apply {
                    what = MSG_FROM_WORK_THREAD // 消息标识
                    obj = "这是来自工作线程的消息" // 消息内容存放
                }
                
                // 在工作线程中 通过Handler发送消息到消息队列中
                mHandler.sendMessage(msg)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }.start()
        
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val existingText = mTextView.text.toString()
        mTextView.text = "$existingText\n[$currentTime] 已启动工作线程..."
    }
    
    /**
     * 发送延迟消息
     */
    private fun sendDelayedMessage() {
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val existingText = mTextView.text.toString()
        mTextView.text = "$existingText\n[$currentTime] 已发送延迟消息，将在3秒后执行..."
        
        // 创建延迟消息
        val msg = Message.obtain().apply {
            what = MSG_DELAYED
            obj = "延迟3秒后执行的消息"
        }
        
        // 发送延迟消息（3秒后执行）
        mHandler.sendMessageDelayed(msg, 3000)
    }
    
    /**
     * 开始定时任务
     */
    private fun startTimer() {
        if (timerRunnable != null) {
            Toast.makeText(this, "定时任务已在运行", Toast.LENGTH_SHORT).show()
            return
        }
        
        timerCount = 0
        timerRunnable = object : Runnable {
            override fun run() {
                // 创建定时消息
                val msg = Message.obtain().apply {
                    what = MSG_TIMER
                }
                mHandler.sendMessage(msg)
                
                // 每秒执行一次
                mHandler.postDelayed(this, 1000)
            }
        }
        
        // 立即开始，然后每秒执行
        mHandler.post(timerRunnable!!)
        
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val existingText = mTextView.text.toString()
        mTextView.text = "$existingText\n[$currentTime] 定时任务已启动（每秒更新）"
        Toast.makeText(this, "定时任务已启动", Toast.LENGTH_SHORT).show()
    }
    
    /**
     * 停止定时任务
     */
    private fun stopTimer() {
        timerRunnable?.let {
            mHandler.removeCallbacks(it)
            timerRunnable = null
            timerCount = 0
            
            val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            val existingText = mTextView.text.toString()
            mTextView.text = "$existingText\n[$currentTime] 定时任务已停止"
            Toast.makeText(this, "定时任务已停止", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(this, "定时任务未运行", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 清空显示
     */
    private fun clearDisplay() {
        mTextView.text = "等待消息..."
        timerCount = 0
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // 停止定时任务，避免内存泄漏
        stopTimer()
    }
}