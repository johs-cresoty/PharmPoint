package com.cresoty.catpospoint.ui.navigation

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cresoty.catpospoint.model.interfaces.Dialogs
import com.cresoty.catpospoint.model.interfaces.PadAction
import com.cresoty.catpospoint.model.interfaces.ViewController
import com.cresoty.catpospoint.model.enums.PointUseSource
import com.cresoty.catpospoint.model.state.SettingState
import com.cresoty.catpospoint.presentation.app.AppContract
import com.cresoty.catpospoint.presentation.app.AppViewModel
import com.cresoty.catpospoint.presentation.phoneNumberInput.PhoneNumberInputContract
import com.cresoty.catpospoint.presentation.viewmodel.MainViewModel
import com.cresoty.catpospoint.ui.idle.IdleRoute
import com.cresoty.catpospoint.ui.phoneNumberInput.PhoneNumberInputRoute
import com.cresoty.catpospoint.ui.result.ResultRoute
import com.cresoty.catpospoint.ui.use.UseRoute
import com.cresoty.catpospoint.view.controller.DialogController
import com.cresoty.catpospoint.view.controller.LocalController
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * 앱 전역 NavGraph.
 *
 * - [AppViewModel] 이 소켓 이벤트 → [AppContract.Effect] 발행 → navController 화면 전환
 * - [MainViewModel] 은 전환 기간 동안 [LocalController] 브리지로 제공되어
 *   구 아키텍처 Screen 들(IdleScreen 등)이 정상 동작한다.
 *   [MainViewModel] 제거 완료 시 브리지 파라미터도 함께 삭제한다.
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
    // TODO: MainViewModel 제거 완료 시 아래 두 파라미터 삭제
    mainVm: MainViewModel = hiltViewModel(),
) {
    // ── LocalController 브리지 ─────────────────────────────────────────
    // IdleScreen 등 구 아키텍처 화면이 MainViewModel 의 상태/이벤트에 접근할 수 있도록
    // CompositionLocal 로 제공한다. 신 아키텍처 화면은 LocalController 를 사용하지 않는다.
    //
    // settingState: AppViewModel 의 다이얼로그 상태를 우선 반영한다.
    //   (업데이트 관련 Dialogs 는 AppViewModel 이 관리)
    // dispatch:     PadAction.OnAcceptUpdate 는 AppViewModel 로 라우팅한다.
    val scope = rememberCoroutineScope()
    val mergedSettingState = remember(mainVm, appVm, scope) {
        combine(mainVm.settingState, appVm.uiState) { setting, appState ->
            if (appState.dialog != Dialogs.None) setting.copy(dialog = appState.dialog) else setting
        }.stateIn(scope, SharingStarted.WhileSubscribed(5_000), SettingState())
    }
    val controller = remember(mainVm, appVm, mergedSettingState) {
        object : ViewController {
            override val mainState = mainVm.mainState
            override val configState = mainVm.configState
            override val previewState = mainVm.previewState
            override val settingState = mergedSettingState
            override val pointState = mainVm.pointState
            override val customerState = mainVm.customerState
            override val customThemeImageUriState = mainVm.customThemeImageUriState
            override val appEvents = mainVm.appEvents
            override fun dispatch(action: PadAction) {
                if (action == PadAction.OnAcceptUpdate) appVm.dispatch(AppContract.Event.OnAcceptUpdate)
                else mainVm.dispatch(action)
            }
        }
    }

    CompositionLocalProvider(LocalController provides controller) {

        // ── AppViewModel effect 처리 ──────────────────────────────────
        LaunchedEffect(Unit) {
            appVm.effect.collect { effect ->
                when (effect) {
                    AppContract.Effect.NavigateToIdle ->
                        navController.popBackStack(AppDestination.Idle.route, inclusive = false)

//                    AppContract.Effect.NavigateToEarnPoint ->
//                        navController.navigate(AppDestination.EarnPoint.route)

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
                    AppContract.Effect.ShowSettingDialog -> mainVm.openSettingDialog()
                }
            }
        }

        // TODO: MainViewModel 제거 완료 시 DialogController 도 함께 정리
        DialogController()

        Scaffold(
            containerColor = Color.White,
            contentWindowInsets = WindowInsets(0),
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = AppDestination.Idle.route,
                modifier = Modifier.padding(padding),
            ) {

                // ── Idle (대기 화면) ──────────────────────────────
                composable(AppDestination.Idle.route) {
                    IdleRoute(
                        // TODO: MainViewModel 제거 시 직접 다이얼로그를 Compose로 관리
                        onNavigatePointBalance = {
                            appVm.setPhoneNumberInputArgs(
                                AppContract.PhoneNumberInputArgs(
                                    mode = PhoneNumberInputContract.Mode.Lookup(PointUseSource.MANUAL)
                                )
                            )
                            navController.navigate(AppDestination.PhoneNumberInput.route)
                        },
                        onShowSettingDialog = { mainVm.openSettingDialog() },
                        onShowPasswordDialog = { mainVm.openPasswordDialog() },
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
}
