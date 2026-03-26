package com.cresoty.catpospoint.view.controller

import android.content.Context
import android.graphics.PixelFormat
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.cresoty.catpospoint.model.interfaces.Dialogs
import com.cresoty.catpospoint.model.interfaces.ViewController
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme
import com.cresoty.catpospoint.view.composable.AdminLoginDialog
import com.cresoty.catpospoint.view.composable.MainIdleScreen
import com.cresoty.catpospoint.view.composable.SettingDialog
import com.cresoty.catpospoint.view.composable.UpdateBlockedDialog
import com.cresoty.catpospoint.view.composable.UpdateRequiredDialog

@Composable
fun DialogController() {
    val controller = LocalController.current
    val preview by controller.previewState.collectAsStateWithLifecycle()
    val setting by controller.settingState.collectAsStateWithLifecycle()
    val dialog = setting.dialog

    when (dialog) {
        Dialogs.None -> Unit
        Dialogs.Setting -> SettingDialog()
        Dialogs.InputPassword -> AdminLoginDialog()
        Dialogs.UpdateRequired -> UpdateRequiredDialog()
        is Dialogs.UpdateBlocked -> UpdateBlockedDialog(messageTitle = dialog.messageTitle, message = dialog.message)
    }

    if (preview.isHideDialog) {
        PreviewOverlay(controller = controller)
    }
}

@Composable
private fun PreviewOverlay(controller: ViewController) {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        var composeView: ComposeView? = null

        if (Settings.canDrawOverlays(context)) {
            val activity = context as ComponentActivity

            composeView = ComposeView(context).apply {
                setViewTreeLifecycleOwner(activity)
                setViewTreeViewModelStoreOwner(activity)
                setViewTreeSavedStateRegistryOwner(activity)

                addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                    override fun onViewAttachedToWindow(v: View) {
                        ViewCompat.getWindowInsetsController(v)?.apply {
                            systemBarsBehavior =
                                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                            hide(WindowInsetsCompat.Type.systemBars())
                        }
                    }
                    override fun onViewDetachedFromWindow(v: View) {}
                })

                setContent {
                    CatposPointTheme {
                        CompositionLocalProvider(LocalController provides controller) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                MainIdleScreen(isPreview = true)
                            }
                        }
                    }
                }
            }

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            )

            windowManager.addView(composeView, params)
        }

        onDispose {
            composeView?.let { windowManager.removeView(it) }
        }
    }
}
