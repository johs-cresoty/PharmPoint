package com.cresoty.catpospoint.ui.navigation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cresoty.catpospoint.model.enums.PointUseSource
import com.cresoty.catpospoint.presentation.app.AppContract
import com.cresoty.catpospoint.presentation.app.AppViewModel
import com.cresoty.catpospoint.presentation.idle.IdleContract
import com.cresoty.catpospoint.presentation.idle.IdleViewModel
import com.cresoty.catpospoint.presentation.phoneNumberInput.PhoneNumberInputContract
import com.cresoty.catpospoint.presentation.setting.SettingContract
import com.cresoty.catpospoint.presentation.setting.SettingViewModel
import com.cresoty.catpospoint.ui.idle.IdleRoute
import com.cresoty.catpospoint.ui.phoneNumberInput.PhoneNumberInputRoute
import com.cresoty.catpospoint.ui.result.ResultRoute
import com.cresoty.catpospoint.ui.use.UseRoute
import com.cresoty.catpospoint.ui.component.FcmUpdateBanner
import com.cresoty.catpospoint.ui.component.MessageDialog
import com.cresoty.catpospoint.ui.component.NotificationHelper
import com.cresoty.catpospoint.ui.dialog.DialogController

/**
 * 앱 전역 NavGraph.
 *
 * - [AppViewModel] 이 소켓 이벤트 → [AppContract.Effect] 발행 → navController 화면 전환
 * - [SettingViewModel] 이 설정 다이얼로그 / 비밀번호 / 테마 프리뷰를 담당
 *
 * 화면 추가 방법:
 * 1. [AppDestination] 에 새 목적지 추가
 * 2. [AppContract] 에 navArgs / Effect 추가
 * 3. [AppViewModel.handleSocketEvent] 에서 Effect 발행
 * 4. 아래 [NavHost] 블록에 composable { } 추가
 */
@Composable
fun AppNavGraph(
    context: Context = LocalContext.current,
    navController: NavHostController = rememberNavController(),
    appVm: AppViewModel = hiltViewModel(),
    settingVm: SettingViewModel = hiltViewModel(),
) {
    // IdleViewModel dispatch 홀더 —
    // composable(Idle.route)의 DisposableEffect가 IdleViewModel 인스턴스를 등록/해제한다.
    val idleDispatchRef = remember { object { var value: (IdleContract.Event) -> Unit = {} } }

    // ── 알림 권한 요청 ────────────────────────────────────────────────
    var showNotifDeniedDialog by remember { mutableStateOf(false) }

    val notifPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            val canAskAgain = ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,
                Manifest.permission.POST_NOTIFICATIONS,
            )
            // canAskAgain == false : "다시 묻지 않음" 선택 → 설정 유도 다이얼로그 표시
            if (!canAskAgain) showNotifDeniedDialog = true
        }
    }

    LaunchedEffect(Unit) {
        NotificationHelper.createChannel(context)
        val isGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
        if (!isGranted) {
            notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    if (showNotifDeniedDialog) {
        MessageDialog(
            message = "알림 수신을 위해\n알림 권한이 필요합니다.",
            confirmText = "설정으로 이동",
            dismissText = "닫기",
            onConfirm = {
                showNotifDeniedDialog = false
                context.startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                )
            },
            onDismiss = { showNotifDeniedDialog = false },
        )
    }

    // ── 앱 설치 권한 사전 체크 (Android 8+, 최초 1회) ─────────────
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            !context.packageManager.canRequestPackageInstalls()
        ) {
            context.startActivity(
                Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:${context.packageName}")
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    // ── SettingViewModel effect 처리
    LaunchedEffect(Unit) {
        settingVm.effect.collect { effect ->
            when (effect) {
                is SettingContract.Effect.OpenPreview ->
                    idleDispatchRef.value(
                        IdleContract.Event.ShowPreview(
                            theme = effect.theme,
                            subTitle = effect.subTitle,
                            customImageUri = effect.customImageUri,
                        )
                    )
                is SettingContract.Effect.StartUpdate ->
                    appVm.dispatch(AppContract.Event.OnAcceptSettingUpdate(effect.installUrl))
            }
        }
    }

    // ── AppViewModel effect 처리 ──────────────────────────────────
    LaunchedEffect(Unit) {
        appVm.effect.collect { effect ->
            when (effect) {
                AppContract.Effect.NavigateToIdle ->
                    navController.popBackStack(AppDestination.Idle.route, inclusive = false)

                AppContract.Effect.NavigateToPhoneNumberInput ->
                    navController.navigate(AppDestination.PhoneNumberInput.route)

                AppContract.Effect.NavigateToUsePoint ->
                    navController.navigate(AppDestination.UsePoint.route)

                AppContract.Effect.NavigateToCatRequestCustomer ->
                    navController.navigate(AppDestination.CatRequestCustomer.route)

                // 업데이트 다운로드 완료 → APK 설치
                is AppContract.Effect.InstallApk -> context.startActivity(
                    Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(effect.uri, "application/vnd.android.package-archive")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                )

                // 업데이트 불필요 & 사업자번호 미설정 → 설정 다이얼로그
                AppContract.Effect.ShowSettingDialog ->
                    settingVm.dispatch(SettingContract.Event.OpenSettingDialog)

                // FCM 메시지 수신 (포그라운드) → 시스템 알림 표시
                is AppContract.Effect.ShowFcmMessage ->
                    NotificationHelper.show(context, effect.title, effect.body)
            }
        }
    }

    DialogController(settingVm = settingVm, appVm = appVm)

    val appState by appVm.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            if (appState.showUpdateBanner) {
                FcmUpdateBanner(
                    onAccept = { appVm.dispatch(AppContract.Event.OnAcceptFcmUpdate) },
                    onDismiss = { appVm.dispatch(AppContract.Event.OnDismissFcmUpdate) },
                )
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Idle.route,
            modifier = Modifier.padding(padding),
        ) {

            // ── Idle (대기 화면) ──────────────────────────────
            composable(AppDestination.Idle.route) {
                val idleVm: IdleViewModel = hiltViewModel()

                // 컨트롤러 브리지에 dispatch 함수 등록 — Idle 화면이 활성일 때만 유효
                DisposableEffect(idleVm) {
                    idleDispatchRef.value = idleVm::dispatch
                    onDispose { idleDispatchRef.value = {} }
                }

                IdleRoute(
                    onNavigatePointBalance = {
                        appVm.setPhoneNumberInputArgs(
                            AppContract.PhoneNumberInputArgs(
                                mode = PhoneNumberInputContract.Mode.Lookup(PointUseSource.MANUAL)
                            )
                        )
                        navController.navigate(AppDestination.PhoneNumberInput.route)
                    },
                    onShowSettingDialog = { settingVm.dispatch(SettingContract.Event.OpenSettingDialog) },
                    onShowPasswordDialog = { settingVm.dispatch(SettingContract.Event.OpenPasswordDialog) },
                )
            }


            // ── 휴대폰 번호 입력 (고객 조회 / 적립) ─────────────
            composable(AppDestination.PhoneNumberInput.route) {
                val state by appVm.uiState.collectAsStateWithLifecycle()
                val args = state.phoneNumberInputArgs ?: return@composable
                PhoneNumberInputRoute(
                    args = args,
                    onNavigateResult = { resultState ->
                        appVm.setResultArgs(resultState)
                        navController.navigate(AppDestination.Result.route)
                    },
                    onNavigateUsePoint = { usePointState ->
                        appVm.setUsePointScreenArgs(usePointState)
                        navController.navigate(AppDestination.UsePoint.route)
                    },
                    onNavigateBack = {
                        navController.popBackStack(AppDestination.Idle.route, inclusive = false)
                    },
                )
            }

            // ── 포인트 사용 ───────────────────────────────────
            composable(AppDestination.UsePoint.route) {
                val state by appVm.uiState.collectAsStateWithLifecycle()
                val args = state.usePointScreenArgs ?: return@composable
                UseRoute(
                    args = args,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateIdle = {
                        navController.popBackStack(AppDestination.Idle.route, inclusive = false)
                    },
                    onNavigateResult = { resultState ->
                        appVm.setResultArgs(resultState)
                        navController.navigate(AppDestination.Result.route)
                    },
                )

                Box(Modifier.fillMaxSize())
            }

            // ── 처리 결과 ─────────────────────────────────────
            composable(AppDestination.Result.route) {
                val state by appVm.uiState.collectAsStateWithLifecycle()
                val args = state.resultArgs ?: return@composable
                ResultRoute(
                    args = args,
                    onNavigateIdle = {
                        navController.popBackStack(AppDestination.Idle.route, inclusive = false)
                    },
                )
            }

            // ── CATPOS CAT NUM / CAT CST (휴대폰 번호 요청 공용) ─
            composable(AppDestination.CatRequestCustomer.route) {
                val state by appVm.uiState.collectAsStateWithLifecycle()
                val args = state.phoneNumberInputArgs ?: return@composable
                PhoneNumberInputRoute(
                    args = args,
                    onNavigateUsePoint = {},
                    onNavigateResult = {},
                    onNavigateBack = {
                        navController.popBackStack(AppDestination.Idle.route, inclusive = false)
                    },
                )
            }
        }
    }
}
