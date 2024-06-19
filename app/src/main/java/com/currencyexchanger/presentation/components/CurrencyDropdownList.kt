package com.currencyexchanger.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun CurrencyDropdownList(
    modifier: Modifier,
    selectedIndex: Int,
    currencies: Set<String>,
    onCurrencySelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {

    val scrollState = rememberScrollState()


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

                Column(
                    modifier = modifier
                        .verticalScroll(state = scrollState)
                        .border(width = 1.dp, color = Color.Gray),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    currencies.onEachIndexed { index, item ->
                        if (index != 0) {
                            Divider(thickness = 1.dp, color = Color.LightGray)
                        }
                        Box(
                            modifier = Modifier
                                .background(if (index == selectedIndex) Color.LightGray else Color.White)
                                .fillMaxWidth()
                                .clickable {
                                    onCurrencySelected(item)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = item)
                        }
                    }

                }
            }
        }
    }
}