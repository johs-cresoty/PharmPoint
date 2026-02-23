package com.cresoty.catpossignpad.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cresoty.catpossignpad.Application
import com.cresoty.catpossignpad.view.controller.MainController
import com.cresoty.catpossignpad.view.theme.CatposSignpadTheme
import com.cresoty.catpossignpad.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var viewModelFactory: MainViewModelFactory
    private lateinit var viewModel : MainViewModel

    fun getRealScreenSizePx(context: Context): Pair<Int, Int> {
        val metrics = context.resources.displayMetrics
        return metrics.widthPixels to metrics.heightPixels
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initViewModel()

        setConcentrateMode()

        val test = getRealScreenSizePx(this)
        Log.d("@#@#", "preview용 화면 실제 픽셀 : $test")
        Log.d("@#@#", "preview용 화면 실제 dpi : ${resources.displayMetrics.densityDpi}")

        setContent {
            CatposSignpadTheme {
                MainController(viewModel, applicationContext)
            }
        }
    }

    private fun initViewModel() {
        viewModelFactory = MainViewModelFactory(
            context = applicationContext
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private fun setConcentrateMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            controller.hide(WindowInsetsCompat.Type.systemBars())
        }
    }


    inner class MainViewModelFactory(
        private val context: Context,
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {

                val app = context.applicationContext as Application
                val repo = app.appContainer.configRepository
                val network = app.appContainer.networkManager
                val socket = app.appContainer.socketManager

                @Suppress("UNCHECKED_CAST")
                return MainViewModel(
                    configRepo = repo,
                    networkManager = network,
                    socketManager = socket
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
