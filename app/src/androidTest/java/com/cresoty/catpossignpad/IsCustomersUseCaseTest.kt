package com.cresoty.catpossignpad

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cresoty.catpossignpad.domain.usecase.IsCustomersUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject



@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class IsCustomersUseCaseTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var useCase: IsCustomersUseCase

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun 등록된_회원인지_확인_테스트() = runBlocking {
        Log.d("API_TEST", "=== 등록된_회원인지_확인_테스트 시작===")

        val results = useCase.invoke(
            taxNo = "2018182695",
            computerName = "JOHS",
            posVersion = "3.3.0",
            customerHp = "01034541234",
        ).toList()


        Log.d("API_TEST", "테스트 결과 : $results")

    }
}