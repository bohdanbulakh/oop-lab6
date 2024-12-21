package com.bohdanbulakh.lab6

import android.content.*
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bohdanbulakh.lab6.ui.theme.Lab6Theme

class MainActivity : AppCompatActivity() {
    var n: Int? = null
    var minX: Double? = null
    var maxX: Double? = null
    var minError by mutableStateOf(false)
    lateinit var receiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        receiver = createReceiver("OBJECT_2")
        { context: Context?, intent: Intent? ->
            if (intent?.getBooleanExtra("completed", false) == true) {
                startActivity(
                    "com.bohdanbulakh.object3",
                    "MainActivity",
                    Intent(),
                )
            }
        }

        setContent {
            Lab6Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    App()
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent("CLOSE_TARGET_ACTIVITIES")
        sendBroadcast(intent)
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
    fun App() {
        var showDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = { Toolbar { showDialog = true } },
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {

                if (showDialog) {
                    InputDialog(
                        n,
                        minX,
                        maxX,
                        onConfirmCallback = {
                                newN: Int?,
                                newMinX: Double?,
                                newMaxX: Double?,
                            ->
                            n = newN
                            minX = newMinX
                            maxX = newMaxX
                            showDialog = false

                            val intent = Intent()
                            intent.putExtra("n", n)
                            intent.putExtra("minX", minX)
                            intent.putExtra("maxX", maxX)

                            if (!minError) {
                                startActivity(
                                    "com.bohdanbulakh.object2",
                                    "MainActivity",
                                    intent,
                                )
                            } else minError = false
                        },
                        onDismissCallback = { showDialog = false })
                }
            }
        }
    }

    private fun startActivity(
        packageName: String,
        activityName: String,
        intent: Intent,
    ) {
        intent.setComponent(ComponentName(packageName, "$packageName.$activityName"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    @Composable
    fun DialogTextField(
        name: String,
        value: String,
        updateValueCallback: (newValue: String) -> Unit
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = updateValueCallback,
            label = { Text(name) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Decimal
            )
        )
    }

    @Composable
    fun InputDialog(
        n: Int?,
        minX: Double?,
        maxX: Double?,
        onConfirmCallback: (
            n: Int?,
            minX: Double?,
            maxX: Double?,
        ) -> Unit,
        onDismissCallback: () -> Unit,
    ) {
        var newN by remember { mutableStateOf(n?.toString() ?: "") }
        var newMinX by remember { mutableStateOf(minX?.toString() ?: "") }
        var newMaxX by remember { mutableStateOf(maxX?.toString() ?: "") }

        AlertDialog(
            title = { Text("Введіть параметри") },
            confirmButton = {
                Button(onClick = {
                    if (
                        (newMinX.toDoubleOrNull() ?: 0.0) >=
                        (newMaxX.toDoubleOrNull() ?: 1.0)
                    ) {
                        minError = true
                        Toast.makeText(applicationContext, "Min X повинен бути менше max X!", Toast.LENGTH_SHORT).show()
                    }

                    onConfirmCallback(
                        newN.toIntOrNull(),
                        newMinX.toDoubleOrNull(),
                        newMaxX.toDoubleOrNull(),
                    )
                }) {
                    Text("Підтвердити")
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    DialogTextField("N", newN) { newN = it }
                    Spacer(modifier = Modifier.height(8.dp))

                    DialogTextField("Min X", newMinX) { newMinX = it }
                    Spacer(modifier = Modifier.height(8.dp))

                    DialogTextField("Max X", newMaxX) { newMaxX = it }
                }
            },
            dismissButton = {
                Button(onClick = onDismissCallback) {
                    Text("Відхилити")
                }
            },
            onDismissRequest = onDismissCallback,
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Toolbar(showDialog: () -> Unit) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ), actions = {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Button(onClick = showDialog) {
                        Text("Ввести параметри")
                    }
                }
            }, title = {})
    }
}
