package com.example.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyBroadcastReceiver(private val onDataReceived: (BookInfo) ->Unit) : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action == "com.example.services.DATA_DOWNLOADED"){
            Log.d("MyBroadcastReceiver", "Data downloaded")

            val bookInfo = BookInfo(
                title = p1.getStringExtra("TITLE") ?: " ",
                wordCount = p1.getIntExtra("WORD_COUNT",0),
                charCount = p1.getIntExtra("CHAR_COUNT",0),
                mostCommonWord = p1.getStringExtra("COMMON_WORD") ?: " "
            )

            if (bookInfo != null){
                onDataReceived(bookInfo)
            }
        }
    }

}