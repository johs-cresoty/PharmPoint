package com.cresoty.catpossignpad.view.composable.common

import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cresoty.catpossignpad.presentation.theme.common01
import com.cresoty.catpossignpad.presentation.theme.common02
import com.cresoty.catpossignpad.presentation.theme.dpx
import com.cresoty.catpossignpad.presentation.theme.spx
import com.cresoty.catpossignpad.presentation.theme.white
import com.cresoty.catpossignpad.toDecimalString

enum class FilterTextType {
    IP,
    PRICE,
    NUMBER,
    NONE
}

@Composable
fun FilterTextField(
    modifier: Modifier = Modifier,
    initText: String = "",
    placeholder: String,
    textAlign: TextAlign = TextAlign.Start,
    filterType: FilterTextType = FilterTextType.NONE,
    onTextChange: (String) -> Unit = {},
    onClickDone: (String) -> Unit = {}
) {
    val keyboardType = when (filterType) {
        FilterTextType.IP, FilterTextType.PRICE, FilterTextType.NUMBER -> KeyboardType.Number
        FilterTextType.NONE -> KeyboardType.Text
    }

    val focusRequester = remember { FocusRequester() }

    var textFieldValue by remember {
        val initialText = when (filterType) {
            FilterTextType.PRICE -> initText.replace(",", "").toDecimalString()
            FilterTextType.NUMBER -> initText.filter { it.isDigit() }.take(10)
            else -> initText
        }

        mutableStateOf(
            TextFieldValue(
                text = initialText,
                selection = TextRange(initialText.length)
            )
        )
    }
    val boarderColor = if (textFieldValue.text.isEmpty()) common01 else common02
    var isFocused by remember { mutableStateOf(false) }
    var isInitialFocus by remember { mutableStateOf(true) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val view = LocalView.current

    fun dismissKeyboard() {
        focusManager.clearFocus()
        keyboardController?.hide()
    }

    fun playClickSound() {
        view.playSoundEffect(SoundEffectConstants.CLICK)
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            playClickSound()
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(isFocused) {
        if (isFocused && isInitialFocus) {
            textFieldValue = textFieldValue.copy(
                selection = TextRange(textFieldValue.text.length)
            )
            isInitialFocus = false
        }
    }

    BasicTextField(
        value = textFieldValue,
        textStyle = TextStyle(
            fontSize = 20.spx,
            textAlign = textAlign,
            fontWeight = FontWeight.Normal,
            color = common02
        ),
        onValueChange = { newValue ->
            var inputText = newValue.text
            playClickSound() // 현금영수증, 현금ic, 제로페이 승인번호 수정 api

            when (filterType) {
                FilterTextType.PRICE,
                FilterTextType.NUMBER -> {
                    if (inputText.isNotEmpty()) {
                        inputText = inputText.replace(Regex("[^0-9]"), "")
                    }
                    if (filterType == FilterTextType.NUMBER) {
                        if (inputText.length > 10) inputText = inputText.dropLast(1)
                    } else {
                        if (inputText.isNotEmpty()) inputText = inputText.toDecimalString()
                    }
                }

                else -> {}
            }

            textFieldValue = newValue.copy(text = inputText)
            onTextChange(inputText)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                playClickSound()
                dismissKeyboard()
                onClickDone(textFieldValue.text)
            },
            onSearch = {
                playClickSound()
                dismissKeyboard()
            }
        ),
        visualTransformation = if (keyboardType == KeyboardType.Password) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        enabled = true,
        decorationBox = { innerTextField ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    propagateMinConstraints = true
                ) {
                    if (textFieldValue.text.isEmpty()) {
                        BasicText(
                            text = placeholder,
                            style = TextStyle(
                                fontSize = 20.spx,
                                fontWeight = FontWeight.Normal,
                                color = common01,
                                textAlign = textAlign
                            )
                        )
                    }
                    innerTextField()
                }
            }
        },
        interactionSource = interactionSource,
        modifier = modifier
            .background(color = white, shape = RoundedCornerShape(6.dp))
            .border(width = 1.dp, color = boarderColor, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 20.dpx, vertical = 14.dpx)
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
            .wrapContentSize()
    )
}


@Preview(name = "NONE")
@Composable
private fun FilterTextFieldNonePreview() {
    FilterTextField(
        placeholder = "약국명을 입력해 주세요.",
        filterType = FilterTextType.NONE
    )
}

@Preview(name = "NUMBER")
@Composable
private fun FilterTextFieldBizNoPreview() {
    FilterTextField(
        placeholder = "사업자번호 10자리를 입력해 주세요.",
        filterType = FilterTextType.NUMBER
    )
}

@Preview(name = "PRICE")
@Composable
private fun FilterTextFieldNumberPreview() {
    FilterTextField(
        placeholder = "숫자 입력",
        initText = "1000",
        textAlign = TextAlign.End,
        filterType = FilterTextType.PRICE,
    )
}


@Preview(name = "IP")
@Composable
private fun FilterTextFieldIpPreview() {
    FilterTextField(
        placeholder = "IP 주소",
        initText = "192.168.0.1",
        filterType = FilterTextType.IP
    )
}
