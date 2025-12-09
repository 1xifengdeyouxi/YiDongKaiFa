package com.swu.notificationtest

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        // cancel传入的是1，它表示我们创建Notification中指定的通知的ID
        manager.cancel(1)
    }
}
