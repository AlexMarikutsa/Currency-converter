package com.currencyexchanger.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import com.currencyexchanger.presentation.screens.home.dvo.CurrencyBalanceDvo
import com.currencyexchanger.presentation.screens.home.dvo.CurrencyExchangeDvo
import com.currencyexchanger.presentation.screens.home.state.ScreenState

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    HomeScreenContent(
        screenState = screenState
    )
}

@Composable
private fun HomeScreenContent(screenState: ScreenState) {
    when (screenState) {
        ScreenState.Initial -> Unit
        is ScreenState.Prepared -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MyBalancesSection(myBalances = screenState.myBalances)
                CurrencyExchangeSection(currencyExchange = screenState.currencyExchange)
                Spacer(modifier = Modifier.height(60.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(fraction = 0.8f),
                    onClick = {

                    }
                ) {
                    Text(text = stringResource(id = R.string.submit))
                }
            }
        }
    }

}

@Composable
private fun MyBalancesSection(myBalances: List<CurrencyBalanceDvo>) {
    Text(
        modifier = Modifier.padding(8.dp),
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
private fun CurrencyExchangeSection(currencyExchange: CurrencyExchangeDvo) {
    Text(
        modifier = Modifier.padding(8.dp),
        text = stringResource(id = R.string.currency_exchange).uppercase(),
        fontSize = 20.sp,
        color = Color.Black.copy(alpha = 0.5f)
    )
    CurrencyExchangeItem(
        currencyExchangeType = CurrencyExchangeType.Sell,
        currencyBalance = currencyExchange.sell
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
        currencyBalance = currencyExchange.receive
    )
}

@Composable
private fun CurrencyExchangeItem(
    currencyExchangeType: CurrencyExchangeType,
    currencyBalance: CurrencyBalanceDvo
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
            modifier = Modifier.padding(8.dp),
            text = currencyBalance.balance,
            fontSize = 16.sp,
            color = when (currencyExchangeType) {
                CurrencyExchangeType.Sell -> Color.Black
                CurrencyExchangeType.Receive -> Color.Green
            }
        )

        Text(
            modifier = Modifier,
            text = currencyBalance.currency,
            fontSize = 16.sp,
            color = Color.Black
        )

        Image(
            modifier = Modifier,
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
        )
    )
}

@Preview
@Composable
private fun CurrencyExchangeItem_Preview() {
    Box(modifier = Modifier.background(Color.White)) {
        CurrencyExchangeItem(
            currencyExchangeType = CurrencyExchangeType.Receive,
            currencyBalance = CurrencyBalanceDvo(balance = "101.0", currency = "USD")
        )
    }
}