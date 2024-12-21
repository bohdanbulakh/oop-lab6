package com.bohdanbulakh.object3

import android.content.*
import android.os.*
import androidx.activity.*
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.bohdanbulakh.object3.ui.theme.Lab6Theme
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    var vector by mutableStateOf<Array<Double>?>(null)
    lateinit var receiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        receiver = createReceiver("CLOSE_TARGET_ACTIVITIES") { context: Context?, intent: Intent? ->
            finish()
        }

        onNewIntent(intent)

        enableEdgeToEdge()
        setContent {
            Lab6Theme {
                Obj3Content()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        vector = getDoubleArrayFromClipboard()?.sortedArray()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun createReceiver(
        action: String,
        onReceive: (context: Context?, intent: Intent?) -> Unit
    ): BroadcastReceiver {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                onReceive(context, intent)
            }
        }

        registerReceiver(receiver, IntentFilter(action))
        return receiver
    }

    @Composable
    fun Obj3Content() {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Object 3", fontSize = 30.sp)
                    }

                    DisplayList()
                }
            }
        }
    }

    @Composable
    fun DisplayList() {
        if (vector != null) {
            for (random in vector) {
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(40.dp)
                ) {
                    Text("$random", fontSize = 25.sp)
                }
            }
        }
    }

    fun getDoubleArrayFromClipboard(): Array<Double>? {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboard.primaryClip

        if (clipData != null && clipData.itemCount > 0) {
            val jsonArray = clipData.getItemAt(0)?.coerceToText(this)?.toString()
            if (!jsonArray.isNullOrEmpty()) {
                try {
                    return Gson().fromJson(jsonArray, Array<Double>::class.java)
                } catch (_: Exception) {}
            }
        }
        return null
    }
}