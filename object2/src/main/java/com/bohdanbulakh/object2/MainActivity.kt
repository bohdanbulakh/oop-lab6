package com.bohdanbulakh.object2

import android.content.*
import android.os.Bundle
import androidx.activity.*
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.bohdanbulakh.object2.ui.theme.Lab6Theme
import com.google.gson.Gson
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    var n by mutableStateOf<Int?>(null)
    var minX by mutableStateOf<Double?>(null)
    var maxX by mutableStateOf<Double?>(null)
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
                Obj2Content()
            }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        getDataFromIntent(intent)
        vector = generateVector()

        copyArrayToClipboard(this, vector)
        val resultIntent = Intent("OBJECT_2").apply {
            putExtra("completed", true)
        }

        sendBroadcast(resultIntent)
    }

    private fun generateVector(): Array<Double> {
        return Array(n ?: 0) {
            val random = Random.nextDouble(from = minX ?: 0.0, until = maxX ?: 0.0)
            BigDecimal(random).setScale(2, RoundingMode.HALF_UP).toDouble()
        }
    }

    private fun copyArrayToClipboard(
        context: Context?, array: Array<Double>?, label: String = "Array"
    ) {
        if (!array.isNullOrEmpty()) {
            val jsonArray = Gson().toJson(array)

            val clipboard = context?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
            val clip = ClipData.newPlainText(label, jsonArray)
            clipboard?.setPrimaryClip(clip)
        }
    }

    @Composable
    fun Obj2Content() {
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
                        Text("Object 2", fontSize = 30.sp)
                    }

                    DisplayList(vector)
                }
            }
        }
    }

    @Composable
    fun DisplayList(vector: Array<Double>?) {
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

    fun getDataFromIntent(intent: Intent?) {
        n = intent?.getIntExtra("n", 0)
        minX = intent?.getDoubleExtra("minX", 0.0)
        maxX = intent?.getDoubleExtra("maxX", 1.0)
    }

}