package com.example.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.GridView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.services.ui.theme.ServicesTheme
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        enableEdgeToEdge()
        setContent {
            ServicesTheme {
                ReceiverUI()
            }
        }
    }

    @Composable
    fun ReceiverUI(){

        val context = this
        var someData by remember { mutableStateOf(emptyList<BookInfo>()) }
        val myServiceIntent = Intent(context,MyService::class.java)

        var permission by remember { mutableStateOf(checkNotificationPermission()) }
        val requestPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            permission  = isGranted;
            if(!isGranted){
                Toast.makeText(
                    this,
                    "Brak zezwolenia na powiadomienia",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        @RequiresPermission
        fun sendNotification(bookInfo: BookInfo){
            val builder = NotificationCompat.Builder(this,"default_channel")
                .setContentTitle("Downloaded")
                .setContentText("${bookInfo.title}")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.downloads)
            with(NotificationManagerCompat.from(this)){
                notify(1,builder.build())
            }
        }

        DisposableEffect(Unit) {
            val receiver = MyBroadcastReceiver{ bookInfo ->
                if (!(someData.contains(bookInfo))){
                    someData = someData.plus(bookInfo)
                    if (permission){
                        sendNotification(bookInfo)
                    }else{
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
            val  intentFilter = IntentFilter("com.example.services.DATA_DOWNLOADED")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                context.registerReceiver(receiver,intentFilter, RECEIVER_EXPORTED)
            } else{
                @Suppress("UnspecifiedRegisterReceiverFlag")
                context.registerReceiver(receiver,intentFilter)
            }

            onDispose {
                context.unregisterReceiver(receiver)
            }
        }

        Column (modifier = Modifier.fillMaxSize().padding(5.dp,40.dp)){
            Button(
                onClick = {
                    context.startService(myServiceIntent)

                }
            ) {
                Text("Start download service")
            }
            Row (Modifier.fillMaxWidth()){
                Text("title", modifier = Modifier.weight(0.5f),textAlign = TextAlign.Center)
                Text("Word Count",modifier = Modifier.weight(0.2f),textAlign = TextAlign.Center)
                Text("Char count",modifier = Modifier.weight(0.2f),textAlign = TextAlign.Center)
                Text("Most common",modifier = Modifier.weight(0.2f),textAlign = TextAlign.Center)
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {

                items(someData) { item ->
                    Row (Modifier.fillMaxWidth()){
                        Text(item.title, modifier = Modifier.weight(0.5f),textAlign = TextAlign.Center)
                        Text("${item.wordCount}", modifier = Modifier.weight(0.2f),textAlign = TextAlign.Center)
                        Text("${item.charCount}", modifier = Modifier.weight(0.2f),textAlign = TextAlign.Center)
                        Text(item.mostCommonWord, modifier = Modifier.weight(0.2f),textAlign = TextAlign.Center)
                    }
                }
            }
        }

    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Download Notification"
            val descriptionText = "Download Notification Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("default_channel",name,importance).apply {
                description = descriptionText
            }
            val notificationManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun checkNotificationPermission() : Boolean{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

        }else{
            true
        }
    }
}