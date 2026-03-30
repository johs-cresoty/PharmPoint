package com.cresoty.catpospoint.ui.idle

import android.content.Context
import android.graphics.PixelFormat
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.cresoty.catpospoint.presentation.idle.IdleContract
import com.cresoty.catpospoint.presentation.idle.IdleViewModel
import com.cresoty.catpospoint.presentation.theme.CatposPointTheme

@Composable
fun IdleRoute(
    onShowSettingDialog: () -> Unit = {},
    onShowPasswordDialog: () -> Unit = {},
    onNavigatePointBalance: () -> Unit = {},
    vm: IdleViewModel = hiltViewModel(),
) {
    val state = vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is IdleContract.Effect.ShowSettingDialog ->
                    if (effect.requiresPassword) onShowPasswordDialog() else onShowSettingDialog()

                IdleContract.Effect.NavigateToPointBalance -> onNavigatePointBalance()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        IdleScreen(
            state = state.value,
            sendEvent = vm::dispatch,
        )
    }

    // 프리뷰 오버레이: isPreview = true일 때 시스템 오버레이 창으로 표시
    // IdleViewModel이 직접 관리하므로 MainViewModel 의존 없음
    if (state.value.isPreview) {
        PreviewOverlay(state = state.value, sendEvent = vm::dispatch)
    }
}

/**
 * 설정 다이얼로그 위에 프리뷰를 오버레이로 표시하는 시스템 창.
 * isPreview = true일 때 컴포지션에 진입 → 창 생성,
 * isPreview = false가 되면 컴포지션에서 제거 → onDispose에서 창 제거.
 */
@Composable
private fun PreviewOverlay(
    state: IdleContract.State,
    sendEvent: (IdleContract.Event) -> Unit,
) {
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
                        Box(modifier = Modifier.fillMaxSize()) {
                            IdleScreen(state = state, sendEvent = sendEvent)
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
