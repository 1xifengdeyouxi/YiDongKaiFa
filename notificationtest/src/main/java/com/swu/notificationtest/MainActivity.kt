package com.swu.notificationtest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import java.io.File

class MainActivity : AppCompatActivity(), View.OnClickListener {
    
    companion object {
        private const val CHANNEL_ID = "notification_channel"
        private const val CHANNEL_NAME = "通知频道"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val sendNotice = findViewById<Button>(R.id.send_notice)
        sendNotice.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.send_notice -> {
                // 给通知添加点击跳转的Activity
                val intent = Intent(this, NotificationActivity::class.java)
                // PendingIntent对象---通知点击跳转功能
                val pendingIntent = PendingIntent.getActivity(
                    this, 0, intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                
                // 通知管理对象
                val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                
                // Android 8.0及以上需要创建通知渠道
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        enableLights(true)
                        lightColor = Color.GREEN
                        enableVibration(true)
                        vibrationPattern = longArrayOf(0, 1000, 1000, 1000)
                    }
                    manager.createNotificationChannel(channel)
                }
                
                // 创建通知对象
                val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("通知") // 设置标题
                    .setContentText("测试通知内容：您收到一条消息") // 设置内容
                    .setWhen(System.currentTimeMillis()) // 设置时间
                    .setSmallIcon(R.mipmap.ic_launcher) // 设置小图标
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)) // 设置大图标
                    .setContentIntent(pendingIntent)
                    .setDefaults(NotificationCompat.DEFAULT_ALL) // 使用默认效果
                    // 设置长文本
                    .setStyle(NotificationCompat.BigTextStyle().bigText(
                        "这是一段很长的文字，这是一段很长的文字，这是一段很长的文字，这是一段很长的文字，这是一段很长的文字，这是一段很长的文字"
                    ))
                    .setPriority(NotificationCompat.PRIORITY_MAX) // 设置通知重要程度
                    .setVibrate(longArrayOf(0, 1000, 1000, 1000)) // 设置振动
                    .setLights(Color.GREEN, 1000, 1000) // 设置LED灯闪烁
                    .setAutoCancel(true) // 设置为自动取消
                    .build()
                    
                manager.notify(1, notification) // 通知的id号为1
            }
        }
    }
}