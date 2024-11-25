package com.example.services

import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.services.ui.theme.ServicesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        var someData by remember { mutableStateOf(emptyList<String>()) }
        val myServiceIntent = Intent(context,MyService::class.java)

        DisposableEffect(Unit) {
            val receiver = MyBroadcastReceiver{ newData ->
                someData = newData
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

        Column (modifier = Modifier.fillMaxSize().padding(5.dp,32.dp)){
            Button(
                onClick = {context.startService(myServiceIntent)}
            ) {
                Text("Start download service")
            }
            LazyColumn (modifier = Modifier.fillMaxSize()){
                items(someData) { item ->
                    Text(item, modifier = Modifier.padding(16.dp))
                }
            }
        }

    }
}