package com.swu.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyDivBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 此方法在BroadcastReceiver接收Intent广播时调用
        throw UnsupportedOperationException("Not yet implemented")
    }
}