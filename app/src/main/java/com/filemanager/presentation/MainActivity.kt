package com.filemanager.presentation

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.filemanager.core.navigation.AppNavigator
import com.filemanager.ui.theme.FileManagerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            TransparentSystemBars(isSystemInDarkTheme())
            checkPermission()
            FileManagerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigator(rememberNavController())
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun checkPermission(): Boolean {
    val notificationPermissionState = if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        rememberMultiplePermissionsState(permissions = listOf(Manifest.permission.READ_EXTERNAL_STORAGE))
    } else {
        rememberMultiplePermissionsState(permissions = listOf(
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
        ))
    }
    return if(!notificationPermissionState.allPermissionsGranted) {
        SideEffect {
            notificationPermissionState.launchMultiplePermissionRequest()
        }
        false
    } else true
}

@Composable
fun TransparentSystemBars(isDarkTheme: Boolean) {
    val systemUiController = rememberSystemUiController()
    val transparentColor = Color.Transparent

    SideEffect {
        systemUiController.setSystemBarsColor(transparentColor)
        systemUiController.setNavigationBarColor(
            darkIcons = !isDarkTheme,
            color = transparentColor,
            navigationBarContrastEnforced = false,
        )
        systemUiController.setStatusBarColor(
            color = transparentColor,
            darkIcons = !isDarkTheme
        )
    }
}