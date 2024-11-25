package com.example.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import java.util.ArrayList


class MyService : Service() {
    private val handler = Handler()
    private var currentFile = 0
    private val total = 5


    private val data = mutableListOf<String>()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyService ----->","started")
        download()
        return START_STICKY
    }
    private fun download(){
        if (currentFile<total){
            Log.d("MyService ----->","downloading file ...$currentFile")
            handler.postDelayed({download()},1000)
            data.add("File $currentFile")
            currentFile++
        }else{
            Log.d("MyService ----->","end")
            val broadcastIntent = Intent("com.example.services.DATA_DOWNLOADED")
            broadcastIntent.putStringArrayListExtra("DATA_LIST", ArrayList(data))
            sendBroadcast(Intent(broadcastIntent))
            stopSelf()
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d("MyService ----->","onBind()")
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        Log.d("MyService ----->","onDestroy()")
    }

}