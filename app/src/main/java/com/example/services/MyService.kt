package com.example.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.util.ArrayList
import java.util.jar.Manifest
import kotlin.random.Random


class MyService : Service() {
    private val handler = Handler()

    private val downloadBooks = DATA_DOWNLOADED()


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyService ----->","started")
        download()
        return START_STICKY
    }

    private fun download(){
            val pair = downloadBooks.books.entries.elementAt(Random.nextInt(downloadBooks.books.size))

            Log.d("MyService ----->","downloading file ...${pair.key}")
            handler.postDelayed({download()},1000)

            val words = pair.value.split("\\s+".toRegex())
            val numByWord = words.groupingBy { it }.eachCount()
            val mostCommon = numByWord.maxByOrNull { it.value }?.key ?: ""


            val bookInfo  = BookInfo(
                    pair.key,
                    pair.value.count { it == ' ' },
                    pair.value.count(),
                    mostCommon)
            Log.d("MyService ----->","${bookInfo.title}, ${bookInfo.wordCount}, ${bookInfo.charCount}, ${bookInfo.mostCommonWord} ")


            Log.d("MyService ----->","end")
            val broadcastIntent = Intent("com.example.services.DATA_DOWNLOADED")



            broadcastIntent.putExtra("TITLE", bookInfo.title)
            broadcastIntent.putExtra("WORD_COUNT", bookInfo.wordCount)
            broadcastIntent.putExtra("CHAR_COUNT", bookInfo.charCount)
            broadcastIntent.putExtra("COMMON_WORD", bookInfo.mostCommonWord)
            sendBroadcast(Intent(broadcastIntent))
            stopSelf()

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