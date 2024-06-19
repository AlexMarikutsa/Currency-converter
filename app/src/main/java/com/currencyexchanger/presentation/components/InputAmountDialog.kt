package com.currencyexchanger.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.currencyexchanger.R

@Composable
fun InputAmountDialog(
    defaultAmount: Double,
    onDismissRequest: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var enteredValue by remember {
        mutableStateOf(if (defaultAmount == 0.0) null else defaultAmount)
    }
    Dialog(onDismissRequest = {
        onDismissRequest()
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.enter_amount),
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    value = enteredValue?.toString() ?: "",
                    onValueChange = { newText ->
                        val filteredText = newText.filter { it.isDigit() || it == '.' }
                        if (filteredText.count { it == '.' } <= 1) {
                            val result = filteredText.toDouble()
                            enteredValue = if (result > 0) {
                                result
                            } else {
                                null
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = MaterialTheme.shapes.small,
                    placeholder =
                    {
                        Text(text = "0.0")
                    },
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                    TextButton(
                        onClick = { onConfirm(enteredValue ?: 0.0) },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun InputAmountDialog_Preview() {
    InputAmountDialog(
        defaultAmount = 0.0,
        onConfirm = {},
        onDismissRequest = {}
    )
}