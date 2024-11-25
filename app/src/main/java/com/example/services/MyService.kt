package com.example.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.key
import java.util.ArrayList
import kotlin.random.Random


class MyService : Service() {
    private val handler = Handler()

    private val downloadBooks = DATA_DOWNLOADED()
    p


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


            val bookInfo : BookInfo = BookInfo(
                    pair.key,
                    pair.value.count { it == ' ' },
                    pair.value.count(),
                    mostCommon)



            Log.d("MyService ----->","end")
            val broadcastIntent = Intent("com.example.services.DATA_DOWNLOADED")
            //broadcastIntent.putExtra("DATA_LIST", ArrayList(data))


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