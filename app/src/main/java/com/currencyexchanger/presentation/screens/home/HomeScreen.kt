package com.currencyexchanger.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.currencyexchanger.R
import com.currencyexchanger.presentation.components.CurrencyDropdownList
import com.currencyexchanger.presentation.components.InputAmountDialog
import com.currencyexchanger.presentation.screens.home.dvo.CurrencyBalanceDvo
import com.currencyexchanger.presentation.screens.home.dvo.CurrencyExchangeDvo
import com.currencyexchanger.presentation.screens.home.event.Event
import com.currencyexchanger.presentation.screens.home.state.ScreenState
import com.currencyexchanger.presentation.screens.home.state.UiState

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreenContent(
        uiState = uiState,
        screenState = screenState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun HomeScreenContent(
    uiState: UiState,
    screenState: ScreenState,
    onEvent: (Event) -> Unit
) {
    when (screenState) {
        ScreenState.Initial -> Unit
        is ScreenState.Prepared -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MyBalancesSection(myBalances = screenState.myBalances)
                    CurrencyExchangeSection(
                        currencyExchange = screenState.currencyExchange,
                        onEvent = onEvent
                    )
                    Spacer(modifier = Modifier.height(60.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(fraction = 0.8f),
                        onClick = {
                            onEvent(Event.OnSubmit)
                        }
                    ) {
                        Text(text = stringResource(id = R.string.submit))
                    }
                }
                UiStateSection(uiState = uiState, onEvent = onEvent)
                            }
        }
    }
}

@Composable
private fun UiStateSection(uiState: UiState, onEvent: (Event) -> Unit) {
    when (uiState) {
        UiState.Default -> Unit
        is UiState.InputAmountForSale -> InputAmountDialog(
            defaultAmount = uiState.amount,
            onConfirm = { onEvent(Event.OnAmountForSaleEntered(it)) },
            onDismissRequest = { onEvent(Event.OnCloseDialog) }
        )
        UiState.Loading -> Unit
        is UiState.SelectCurrencyForSale -> CurrencyDropdownList(
            modifier = Modifier,
            selectedIndex = uiState.selectedIndex,
            currencies = uiState.currencies,
            onCurrencySelected = { onEvent(Event.OnCurrencyForSaleSelected(currency = it)) },
            onDismissRequest = { onEvent(Event.OnCloseDialog) }
        )
        is UiState.SelectCurrencyForReceive -> CurrencyDropdownList(
            modifier = Modifier,
            selectedIndex = uiState.selectedIndex,
            currencies = uiState.currencies,
            onCurrencySelected = { onEvent(Event.OnCurrencyForReceiveSelected(currency = it)) },
            onDismissRequest = { onEvent(Event.OnCloseDialog) }
        )

        is UiState.CurrencyConverted -> AlertDialog(
            title = {
                Text(text = stringResource(id = R.string.currency_converted_dialog_title))
            },
            text = {
                Text(text = uiState.message)
            },
            onDismissRequest = {
                onEvent(Event.OnCloseDialog)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEvent(Event.OnCloseDialog)
                    }
                ) {
                    Text(text = stringResource(id = R.string.done))
                }
            }
        )
    }
}

@Composable
private fun MyBalancesSection(myBalances: List<CurrencyBalanceDvo>) {
    Text(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        text = stringResource(id = R.string.my_balances).uppercase(),
        fontSize = 20.sp,
        color = Color.Black.copy(alpha = 0.5f)
    )

    val listState = rememberLazyListState()
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(items = myBalances, key = { it.currency }) { balance ->
            Text(
                text = "${balance.balance} ${balance.currency}",
                fontSize = 20.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun CurrencyExchangeSection(
    currencyExchange: CurrencyExchangeDvo,
    onEvent: (Event) -> Unit
) {
    Text(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        text = stringResource(id = R.string.currency_exchange).uppercase(),
        fontSize = 20.sp,
        color = Color.Black.copy(alpha = 0.5f)
    )
    CurrencyExchangeItem(
        currencyExchangeType = CurrencyExchangeType.Sell,
        currencyBalance = currencyExchange.sell,
        onNumberClicked = { onEvent(Event.OnEnterAmountForSale) },
        onCurrencyClicked = { onEvent(Event.OnChooseCurrencyForSale) }
    )
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = 8.dp)
            .background(Color.Black.copy(alpha = 0.4f))
    )
    CurrencyExchangeItem(
        currencyExchangeType = CurrencyExchangeType.Receive,
        currencyBalance = currencyExchange.receive,
        onCurrencyClicked = { onEvent(Event.OnChooseCurrencyForReceive) }
    )
}

@Composable
private fun CurrencyExchangeItem(
    currencyExchangeType: CurrencyExchangeType,
    currencyBalance: CurrencyBalanceDvo,
    onNumberClicked: (() -> Unit)? = null,
    onCurrencyClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .background(
                    color = when (currencyExchangeType) {
                        CurrencyExchangeType.Sell -> Color.Red
                        CurrencyExchangeType.Receive -> Color.Green
                    }, shape = CircleShape
                )
                .padding(8.dp)
                .rotate(
                    when (currencyExchangeType) {
                        CurrencyExchangeType.Sell -> 90f
                        CurrencyExchangeType.Receive -> -90f
                    }
                ),
            colorFilter = ColorFilter.tint(color = Color.White),
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = null
        )

        Text(
            modifier = Modifier.padding(8.dp),
            text = stringResource(
                id = when (currencyExchangeType) {
                    CurrencyExchangeType.Sell -> R.string.sell
                    CurrencyExchangeType.Receive -> R.string.receive
                }
            ),
            fontSize = 16.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            modifier = Modifier
                .padding(8.dp)
                .clickable(currencyExchangeType == CurrencyExchangeType.Sell) {
                    onNumberClicked?.invoke()
                },
            text = currencyBalance.balance,
            fontSize = 16.sp,
            color = when (currencyExchangeType) {
                CurrencyExchangeType.Sell -> Color.Black
                CurrencyExchangeType.Receive -> Color.Green
            }
        )

        Text(
            modifier = Modifier
                .clickable {
                    onCurrencyClicked()
                },
            text = currencyBalance.currency,
            fontSize = 16.sp,
            color = Color.Black
        )

        Image(
            modifier = Modifier
                .clickable {
                    onCurrencyClicked()
                },
            imageVector = Icons.Filled.KeyboardArrowDown,
            contentDescription = null
        )
    }
}

@Preview
@Composable
private fun HomeScreenContent_Preview() {
    HomeScreenContent(
        screenState = ScreenState.Prepared(
            myBalances = listOf(
                CurrencyBalanceDvo(
                    balance = "1000.0",
                    currency = "EUR"
                ),
                CurrencyBalanceDvo(
                    balance = "0.0",
                    currency = "USD"
                ),
                CurrencyBalanceDvo(
                    balance = "20.0",
                    currency = "GRN"
                ),
                CurrencyBalanceDvo(
                    balance = "73.41",
                    currency = "AED"
                ),
            ),
            currencyExchange = CurrencyExchangeDvo(
                sell = CurrencyBalanceDvo(
                    balance = "100.0",
                    currency = "EUR"
                ),
                receive = CurrencyBalanceDvo(
                    balance = "110.30",
                    currency = "USD"
                )
            )
        ),
        onEvent = {},
        uiState = UiState.Default
    )
}

@Preview
@Composable
private fun CurrencyExchangeItem_Preview() {
    Box(modifier = Modifier.background(Color.White)) {
        CurrencyExchangeItem(
            currencyExchangeType = CurrencyExchangeType.Receive,
            currencyBalance = CurrencyBalanceDvo(balance = "101.0", currency = "USD"),
            onCurrencyClicked = {},
            onNumberClicked = {}
        )
    }
}