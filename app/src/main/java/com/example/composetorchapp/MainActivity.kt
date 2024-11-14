package com.example.composetorchapp

import android.content.Context
import android.hardware.camera2.CameraManager
import android.health.connect.datatypes.AppInfo
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composetorchapp.ui.theme.ComposeTorchAppTheme
import com.example.composetorchapp.ui.theme.greenColor

class MainActivity : ComponentActivity() {
    lateinit var cameraManager: CameraManager
    lateinit var cameraId: String

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = try {
            cameraManager.cameraIdList[0]
        } catch (e: Exception) {
            e.printStackTrace()
        }.toString()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                cameraId.let {
                    try {
                        cameraManager.setTorchMode(it, false)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                finish()
            }
        })
        setContent {
            ComposeTorchAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        containerColor = Color.Black,
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "Torch App",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        color = Color.White
                                    )
                                }
                            )
                        },
                        content = { paddingValues ->
                            Box(
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .fillMaxSize()
                            ) {
                                TorchApp(LocalContext.current)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TorchApp(context: Context) {
    val torchStatus = remember {
        mutableStateOf(false)
    }
    val torchMsg = remember {
        mutableStateOf("Off")
    }
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val cameraId = cameraManager.cameraIdList[0]


    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        Text(
            text = "Torch is ${torchMsg.value}",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Default,
            fontSize = 20.sp,
            modifier = Modifier.padding(5.dp)
        )
        Switch(checked = torchStatus.value,
            onCheckedChange = {
                torchStatus.value = it
                try {
                    cameraManager.setTorchMode(cameraId, it)
                    torchMsg.value = if (it ) "On" else "Off"
                    Toast.makeText(
                        context,
                        if (it) "Torch turned on" else "Torch turned off",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        )

    }
}