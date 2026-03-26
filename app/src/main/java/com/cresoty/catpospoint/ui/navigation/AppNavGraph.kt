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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cresoty.catpospoint.model.event.AppEvent
import com.cresoty.catpospoint.model.interfaces.PadAction
import com.cresoty.catpospoint.model.interfaces.ViewController
import com.cresoty.catpospoint.model.enums.PointUseSource
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
    val controller = remember(mainVm) {
        object : ViewController {
            override val mainState = mainVm.mainState
            override val configState = mainVm.configState
            override val previewState = mainVm.previewState
            override val settingState = mainVm.settingState
            override val pointState = mainVm.pointState
            override val customerState = mainVm.customerState
            override val customThemeImageUriState = mainVm.customThemeImageUriState
            override val appEvents = mainVm.appEvents
            override fun dispatch(action: PadAction) = mainVm.dispatch(action)
        }
    }

    CompositionLocalProvider(LocalController provides controller) {

        // AppEvent 처리 (APK 설치 등) — MainViewModel 제거 시 함께 삭제
        LaunchedEffect(Unit) {
            controller.appEvents.collect { event ->
                when (event) {
                    is AppEvent.InstallApk -> context.startActivity(
                        Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(event.uri, "application/vnd.android.package-archive")
                            flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                        }
                    )
                }
            }
        }

        // ── 소켓 이벤트 → 화면 전환 ──────────────────────────────────
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

                    AppContract.Effect.NavigateToCatRequestNum ->
                        navController.navigate(AppDestination.CatRequestNum.route)

                    AppContract.Effect.NavigateToCatRequestCustomer ->
                        navController.navigate(AppDestination.CatRequestCustomer.route)
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

//                // ── 포인트 적립 ───────────────────────────────────
//                composable(AppDestination.EarnPoint.route) {
//                    val state by appVm.uiState.collectAsStateWithLifecycle()
//                    val args = state.earnPointArgs ?: return@composable
//                    // TODO: EarnPointRoute(
//                    //   args        = args,
//                    //   onGoToResult  = { resultModel -> navController.navigate(AppDestination.Result.route) },
//                    //   onGoToWaiting = { navController.popBackStack(AppDestination.Idle.route, inclusive = false) }
//                    // )
//                    Box(Modifier.fillMaxSize())
//                }

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

                // ── CATPOS CAT NUM (휴대폰 번호 요청) ────────────
                composable(AppDestination.CatRequestNum.route) {
                    val state by appVm.uiState.collectAsStateWithLifecycle()
                    val args = state.phoneNumberInputArgs ?: return@composable
                    PhoneNumberInputRoute(
                        args = args,
                        onNavigateResult = { resultState ->
                            appVm.setResultArgs(resultState)
                            navController.navigate(AppDestination.Result.route)
                        },
                        onNavigateUsePoint = {},
                        onNavigateBack = {
                            navController.popBackStack(AppDestination.Idle.route, inclusive = false)
                        },
                    )
                }

                // ── CATPOS CAT CST (휴대폰+고객 번호 요청) ───────
                composable(AppDestination.CatRequestCustomer.route) {
                    val state by appVm.uiState.collectAsStateWithLifecycle()
                    val args = state.phoneNumberInputArgs ?: return@composable
                    PhoneNumberInputRoute(
                        args = args,
                        onNavigateUsePoint = {},
                        onNavigateResult = {},  // CAT 002는 결과 화면 없음
                        onNavigateBack = {
                            navController.popBackStack(AppDestination.Idle.route, inclusive = false)
                        },
                    )
                }
            }
        }
    }
}
