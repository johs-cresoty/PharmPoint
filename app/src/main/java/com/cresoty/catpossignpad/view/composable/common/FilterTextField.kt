package com.cresoty.PharmpayPos.view.composable

import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cresoty.catpossignpad.px2dp
import com.cresoty.catpossignpad.toDecimalString
import com.cresoty.catpossignpad.presentation.theme.black
import com.cresoty.catpossignpad.presentation.theme.common01
import com.cresoty.catpossignpad.presentation.theme.white

enum class FilterTextType() {
    FILTER_TYPE_IP,
    FILTER_TYPE_NUMBER,
    FILTER_TYPE_BIZNO,
    NOT_FILTER
}

@Composable
fun FilterTextField(
    modifier : Modifier = Modifier,
    initText: String = "",
    shape : RoundedCornerShape = RoundedCornerShape(6.dp),
    placeholder: String,
    placeholderColor : Color = common01,
    placeHolderAlpha : Float = 1f,  // placeholder 투명도 : 0.0f 완전투명 ~ 1f 완전불투명
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    isSettingField: Boolean = false,
    enabled: Boolean = true,
    hPadding: Dp = 8.dp,
    vPadding: Dp = (5.5).dp,
    fontSize: TextUnit = 20.sp,
    fontWeight : FontWeight = FontWeight.Normal,
    fontColor : Color = black,
    textAlign: TextAlign = TextAlign.Start,
    filterType: FilterTextType = FilterTextType.NOT_FILTER,
    backgroundColor : Color = white,
    border : Color = black,
    borderWidth : Dp = 1.dp,
    focusRequester : FocusRequester = remember { FocusRequester() },
    onTextChange: (String) -> Unit = {},
    onSearch: (String) -> Unit = {},
    onClickTextField : () -> Unit = {},
    onSuccessReturn: (Boolean) -> Unit = {},
    onFocusChange: (Boolean) -> Unit = {},
    onClickDone:(String) -> Unit = {}
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = initText, selection = TextRange(initText.length)))
    }
    var isFocused by remember { mutableStateOf(false) }
    var isInitialFocus by remember { mutableStateOf(true) }

    //동작 감지
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val view = LocalView.current

    val shape = remember(isSettingField) { shape }

    fun dismissKeyboard() {
        focusManager.clearFocus()
        keyboardController?.hide()
    }

    fun playClickSound() {
        view.playSoundEffect(SoundEffectConstants.CLICK)
    }

    LaunchedEffect(isPressed) {
        if(isPressed) {
            playClickSound()
            focusRequester.requestFocus()
            onClickTextField()
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
        textStyle = TextStyle(fontSize = fontSize, textAlign = textAlign, fontWeight = fontWeight, color = fontColor),
        onValueChange = { newValue ->
            var inputText = newValue.text
            playClickSound()    // 현금영수증, 현금ic, 제로페이 승인번호 수정 api

            if (filterType != FilterTextType.NOT_FILTER) {
                when(filterType) {
                    FilterTextType.FILTER_TYPE_NUMBER,
                    FilterTextType.FILTER_TYPE_BIZNO -> {
                        if (inputText.isNotEmpty()) {
                            inputText = inputText.replace(Regex("[^0-9]"), "")
                        }
                        if(filterType == FilterTextType.FILTER_TYPE_BIZNO) {
                            if(inputText.length > 10) inputText = inputText.dropLast(1)
                        }
                        else {
                            inputText = inputText.toDecimalString()
                        }
                    }
                    else -> {}
                }
            }

            textFieldValue = newValue.copy(text = inputText)

            onTextChange(inputText)
            onSuccessReturn(true)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onDone = {
                playClickSound()
                dismissKeyboard()
                onClickDone(textFieldValue.text)
            },
            onSearch = {
                playClickSound()
                dismissKeyboard()
                onSearch(textFieldValue.text)
            }
        ),
        visualTransformation = if (keyboardType == KeyboardType.Password) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        enabled = enabled,
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
                        Text(
                            text = placeholder,
                            fontSize = fontSize,
                            fontWeight = fontWeight,
                            color = placeholderColor.copy(alpha = placeHolderAlpha)
                        )
                    }

                    innerTextField()
                }
            }
        },
        interactionSource = interactionSource,
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = shape
            )
            .border(
                width = if (isSettingField) 0.dp else borderWidth,
                color = border,
                shape = shape
            )
            .padding(
                horizontal = hPadding,
                vertical = vPadding
            )
            .focusRequester(focusRequester)
            .onFocusChanged {
                isFocused = it.isFocused
            }
            .wrapContentSize()
    )
}

@Preview(device = "spec:width=800px,height=1319px,dpi=213")
@Composable
fun FilterTextFieldPreview() {
    FilterTextField(
        modifier = Modifier.size(width = 275f.px2dp(), height = 55f.px2dp()),
        textAlign = TextAlign.End,
        initText = "1,000",
        placeholder = "",
        keyboardType = KeyboardType.Number,
        filterType = FilterTextType.FILTER_TYPE_NUMBER,
        hPadding = 20f.px2dp(),
        vPadding = 14f.px2dp(),
        fontColor = common01
    )
}
