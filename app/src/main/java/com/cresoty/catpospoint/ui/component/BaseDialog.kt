package com.cresoty.catpospoint.ui.component

import android.annotation.SuppressLint
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.cresoty.catpospoint.R


@SuppressLint("RememberReturnType")
@Composable
fun BaseDialog(
    onCreate: () -> Unit = {},
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = {
            onDismiss()
        },
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false,
            usePlatformDefaultWidth = false
        )
    ) {
        val view = LocalView.current
        val dialogWindow = (view.parent as? DialogWindowProvider)?.window

        remember(dialogWindow) {
            dialogWindow?.setWindowAnimations(R.style.NoWindowAnimation)
        }
        SideEffect {
            dialogWindow?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            dialogWindow?.let { win ->
                WindowCompat.setDecorFitsSystemWindows(win, false)
                WindowInsetsControllerCompat(win, win.decorView).apply {
                    systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    hide(WindowInsetsCompat.Type.systemBars())
                }
            }
        }

        LaunchedEffect(Unit) {
            onCreate()
        }

        val context = LocalContext.current
        val focusManager = LocalFocusManager.current
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        val imm = ContextCompat.getSystemService(
                            context,
                            InputMethodManager::class.java
                        )
                        imm?.hideSoftInputFromWindow(view.windowToken, 0)
                        focusManager.clearFocus()
                    })
                }
        ) {
            content()
        }
    }
}
