package com.example.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyBroadcastReceiver(private val onDataReceived: (List<String>) ->Unit) : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action == "com.example.services.DATA_DOWNLOADED"){
            Log.d("MyBroadcastReceiver", "Data downloaded")
            val dataList = p1.getStringArrayListExtra("DATA_LIST")
            if (dataList != null){
                onDataReceived(dataList)
            }
        }
    }

}