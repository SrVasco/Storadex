package com.example.storadex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import com.example.storadex.navigation.AppNavigation
import com.example.storadex.ui.theme.StoradexTheme
import com.example.storadex.viewmodel.LoginScreenViewModel
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File

class MainActivity : ComponentActivity() {
    private val viewModel: LoginScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val cacheSize = 50L * 1024 * 1024
        val httpCache = Cache(File(cacheDir, "http_cache"), cacheSize)
        val okHttpClient = OkHttpClient.Builder()
            .cache(httpCache)
            .build()

        val imageLoader = ImageLoader.Builder(this)
            .okHttpClient { okHttpClient }
            .diskCache {
                DiskCache.Builder()
                    .directory(File(cacheDir, "image_cache"))
                    .maxSizeBytes(cacheSize)
                    .build()
            }
            .build()

        Coil.setImageLoader(imageLoader)

        val startDestination = if (FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()) {
            "login"
        } else {
            "home"
        }

        setContent {
            StoradexTheme {
                AppNavigation(viewModel, startDestination)
            }
        }
    }
}

