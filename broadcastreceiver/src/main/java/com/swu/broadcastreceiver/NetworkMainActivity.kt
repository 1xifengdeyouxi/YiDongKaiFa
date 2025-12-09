//package com.swu.broadcastreceiver
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.net.ConnectivityManager
//import android.net.NetworkInfo
//import android.os.Bundle
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//
///**
// * 动态注册监听网络变化示例
// * 运行后切换网络（开/关 Wi-Fi、飞行模式等）可以看到 Toast 与 TextView 更新
// */
//class NetworkMainActivity : AppCompatActivity() {
//
//    private lateinit var textStatus: TextView
//    private lateinit var intentFilter: IntentFilter
//    private lateinit var networkChangeReceiver: NetworkChangeReceiver
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_network_main)
//
//        textStatus = findViewById(R.id.textStatus)
//
//        // 初始化 IntentFilter，监听系统广播 "android.net.conn.CONNECTIVITY_CHANGE"
//        intentFilter = IntentFilter().apply {
//            addAction("android.net.conn.CONNECTIVITY_CHANGE")
//        }
//        networkChangeReceiver = NetworkChangeReceiver()
//
//        // 动态注册广播接收器
//        registerReceiver(networkChangeReceiver, intentFilter)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        // 动态注册的广播必须取消注册
//        unregisterReceiver(networkChangeReceiver)
//    }
//
//    /**
//     * 内部广播接收器类，接收网络变化
//     * 注意：onReceive 的执行时间很短，如果需要耗时处理要开启服务或其他线程
//     */
//    inner class NetworkChangeReceiver : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            // 获取 ConnectivityManager 并检测当前网络状态（教育/实验示例）
//            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//            val networkInfo: NetworkInfo? = cm.activeNetworkInfo
//            val available = networkInfo != null && networkInfo.isAvailable
//            if (available) {
//                Toast.makeText(context, "网络可用！", Toast.LENGTH_SHORT).show()
//                textStatus.text = "网络可用（${networkInfo?.typeName}）"
//            } else {
//                Toast.makeText(context, "网络不可用", Toast.LENGTH_SHORT).show()
//                textStatus.text = "网络不可用"
//            }
//        }
//    }
//}
