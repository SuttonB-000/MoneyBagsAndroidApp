package com.example.moneybags

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import java.math.BigDecimal
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moneybags.ui.theme.DeepBlue
import com.example.moneybags.ui.theme.MoneyBagsTheme
import com.example.moneybags.ui.theme.SoftNeutral

class MainActivity : ComponentActivity() {
    private var darkModeEnabled by mutableStateOf(false)
    private var textScale by mutableStateOf(AppPreferences.DEFAULT_TEXT_SCALE)
    private var selectedCurrency by mutableStateOf(AppCurrency.USD)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        darkModeEnabled = AppPreferences.isDarkModeEnabled(this)
        textScale = AppPreferences.getTextScale(this)
        selectedCurrency = AppPreferences.getCurrency(this)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT))
        setContent {
            MoneyBagsTheme(darkTheme = darkModeEnabled, textScale = textScale) {
                HomeScreen(selectedCurrency = selectedCurrency)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        darkModeEnabled = AppPreferences.isDarkModeEnabled(this)
        textScale = AppPreferences.getTextScale(this)
        selectedCurrency = AppPreferences.getCurrency(this)
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier, selectedCurrency: AppCurrency = AppCurrency.USD) {
    val context = LocalContext.current
    val groceries = stringResource(R.string.example_groceries)
    val savings = stringResource(R.string.example_savings)
    var bagNames by rememberSaveable { mutableStateOf(arrayListOf(groceries, savings)) }
    var bagAmounts by rememberSaveable { mutableStateOf(arrayListOf("0.00", "0.00")) }
    var unallocatedAmount by rememberSaveable { mutableStateOf("0.00") }
    var editingBagIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    var confirmDelete by rememberSaveable { mutableStateOf(false) }
    var showNewBag by rememberSaveable { mutableStateOf(false) }
    var newName by rememberSaveable { mutableStateOf("") }
    var newAmount by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }
    var amountFocusRequests by remember { mutableIntStateOf(0) }
    var transactionBagIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    var waitingForBag by rememberSaveable { mutableStateOf(false) }
    var showMoneyAction by rememberSaveable { mutableStateOf(false) }
    var subtractingMoney by rememberSaveable { mutableStateOf(false) }
    var transactionAmount by rememberSaveable { mutableStateOf("") }
    val parsedAmount = newAmount.text.toBigDecimalOrNull()
    val exceedsAvailableFunds = editingBagIndex?.let { index ->
        parsedAmount != null &&
            parsedAmount > bagAmounts[index].toBigDecimal() + unallocatedAmount.toBigDecimal()
    } ?: false
    val validAmount = parsedAmount != null && parsedAmount.signum() >= 0 &&
        parsedAmount.scale() <= 2 && parsedAmount <= BigDecimal("999999999.99") &&
        !exceedsAvailableFunds
    val projectedUnallocated = editingBagIndex?.let { index ->
        parsedAmount?.takeIf {
            it.signum() >= 0 && it.scale() <= 2 && it <= BigDecimal("999999999.99")
        }?.let { amount ->
            unallocatedAmount.toBigDecimal() + bagAmounts[index].toBigDecimal() - amount
        }
    }
    val currency = selectedCurrency.formatter()
    val currencySymbol = selectedCurrency.symbol
    val bagGridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(amountFocusRequests) {
        if (amountFocusRequests > 0) {
            // Let the tap place the cursor before selecting the full amount.
            withFrameNanos { }
            if (showNewBag && editingBagIndex != null) {
                newAmount = newAmount.copy(selection = TextRange(0, newAmount.text.length))
            }
        }
    }

    if (showNewBag && !confirmDelete) {
        AlertDialog(
            onDismissRequest = { showNewBag = false },
            title = {
                Text(stringResource(if (editingBagIndex == null) R.string.new_bag_title else R.string.edit_bag_title))
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(R.string.bag_name),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        placeholder = {
                            Text(
                                stringResource(R.string.bag_name_placeholder),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        },
                        singleLine = true
                    )
                    if (editingBagIndex != null) {
                        Text(
                            text = stringResource(R.string.bag_amount),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedTextField(
                                value = newAmount,
                                onValueChange = { newAmount = it },
                                modifier = Modifier.onFocusChanged { focusState ->
                                    if (focusState.isFocused) {
                                        amountFocusRequests++
                                    }
                                },
                                placeholder = {
                                    Text(
                                        stringResource(R.string.balance_placeholder, currencySymbol),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                isError = newAmount.text.isNotEmpty() && !validAmount
                            )
                            Text(
                                text = stringResource(
                                    R.string.money_left_to_budget,
                                    currency.format(projectedUnallocated ?: unallocatedAmount.toBigDecimal())
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = stringResource(
                                    if (exceedsAvailableFunds) R.string.insufficient_unallocated
                                    else R.string.amount_hint
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = newName.isNotBlank() && (editingBagIndex == null || validAmount),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ),
                    onClick = {
                        val editIndex = editingBagIndex
                        val amount = if (editIndex == null) "0.00" else parsedAmount?.setScale(2)?.toPlainString()
                        if (newName.isNotBlank() && amount != null && (editIndex == null || validAmount)) {
                            if (editIndex == null) {
                                bagNames = ArrayList(bagNames + newName.trim())
                                bagAmounts = ArrayList(bagAmounts + amount)
                            } else {
                                unallocatedAmount = (
                                    unallocatedAmount.toBigDecimal() +
                                        bagAmounts[editIndex].toBigDecimal() - amount.toBigDecimal()
                                ).setScale(2).toPlainString()
                                bagNames = ArrayList(bagNames).apply { set(editIndex, newName.trim()) }
                                bagAmounts = ArrayList(bagAmounts).apply { set(editIndex, amount) }
                            }
                            val newBagIndex = bagNames.lastIndex
                            newName = ""
                            newAmount = TextFieldValue()
                            showNewBag = false
                            editingBagIndex = null
                            if (editIndex == null) {
                                coroutineScope.launch {
                                    bagGridState.animateScrollToItem(newBagIndex)
                                }
                            }
                        }
                    }
                ) {
                    Text(stringResource(if (editingBagIndex == null) R.string.create_bag else R.string.save_changes))
                }
            },
            dismissButton = {
                Row {
                    if (editingBagIndex != null) {
                        TextButton(onClick = { confirmDelete = true }) {
                            Text(stringResource(R.string.delete_bag), color = MaterialTheme.colorScheme.error)
                        }
                    }
                    TextButton(onClick = { showNewBag = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }
        )
    }
    if (confirmDelete && editingBagIndex != null) {
        val index = editingBagIndex!!
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(stringResource(R.string.delete_bag_title)) },
            text = { Text(stringResource(R.string.delete_bag_message, bagNames[index])) },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    onClick = {
                        unallocatedAmount = (
                            unallocatedAmount.toBigDecimal() + bagAmounts[index].toBigDecimal()
                        ).setScale(2).toPlainString()
                        bagNames = ArrayList(bagNames).apply { removeAt(index) }
                        bagAmounts = ArrayList(bagAmounts).apply { removeAt(index) }
                        transactionBagIndex = when {
                            transactionBagIndex == index -> null
                            transactionBagIndex != null && transactionBagIndex!! > index -> transactionBagIndex!! - 1
                            else -> transactionBagIndex
                        }
                        confirmDelete = false
                        showNewBag = false
                        editingBagIndex = null
                    }
                ) { Text(stringResource(R.string.delete_bag)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    if (showMoneyAction && (!subtractingMoney || transactionBagIndex != null)) {
        val index = transactionBagIndex
        val amountToMove = transactionAmount.toBigDecimalOrNull()
        val currentAmount = if (subtractingMoney) {
            bagAmounts[requireNotNull(index)].toBigDecimal()
        } else {
            unallocatedAmount.toBigDecimal()
        }
        val maxAmount = BigDecimal("999999999.99")
        val amountIsValid = amountToMove != null &&
            amountToMove.signum() > 0 &&
            amountToMove.scale() <= 2 &&
            amountToMove <= maxAmount &&
            (if (subtractingMoney) {
                amountToMove <= currentAmount
            } else {
                currentAmount + amountToMove <= maxAmount
            })

        AlertDialog(
            onDismissRequest = {
                showMoneyAction = false
                transactionBagIndex = null
                transactionAmount = ""
            },
            title = {
                Text(
                    if (subtractingMoney) {
                        stringResource(R.string.subtract_from_bag_title, bagNames[requireNotNull(index)])
                    } else {
                        stringResource(R.string.add_to_unallocated_title)
                    }
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.transaction_amount))
                    OutlinedTextField(
                        value = transactionAmount,
                        onValueChange = { transactionAmount = it },
                        placeholder = {
                            Text(stringResource(R.string.balance_placeholder, currencySymbol))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        isError = transactionAmount.isNotEmpty() && !amountIsValid,
                        supportingText = {
                            Text(
                                if (subtractingMoney) {
                                    stringResource(R.string.subtract_amount_hint)
                                } else {
                                    stringResource(R.string.add_amount_hint, currencySymbol)
                                }
                            )
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    enabled = amountIsValid,
                    onClick = {
                        if (amountIsValid && amountToMove != null) {
                            if (subtractingMoney) {
                                bagAmounts = ArrayList(bagAmounts).apply {
                                    set(requireNotNull(index), (currentAmount - amountToMove).setScale(2).toPlainString())
                                }
                            } else {
                                unallocatedAmount = (currentAmount + amountToMove).setScale(2).toPlainString()
                            }
                            showMoneyAction = false
                            transactionBagIndex = null
                            transactionAmount = ""
                        }
                    }
                ) {
                    Text(stringResource(if (subtractingMoney) R.string.subtract else R.string.add))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showMoneyAction = false
                        transactionBagIndex = null
                        transactionAmount = ""
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Surface(
                color = DeepBlue,
                contentColor = SoftNeutral
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = SoftNeutral
                        )
                        Text(
                            text = stringResource(R.string.app_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = SoftNeutral
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            context.startActivity(Intent(context, SettingsActivity::class.java))
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = SoftNeutral)
                    ) {
                        Text(stringResource(R.string.settings))
                    }
                }
            }
        },
        bottomBar = {
            BottomAppBar(
                containerColor = DeepBlue,
                contentColor = SoftNeutral
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            subtractingMoney = false
                            transactionAmount = ""
                            transactionBagIndex = null
                            waitingForBag = false
                            showMoneyAction = true
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(stringResource(R.string.add_income))
                    }
                    Button(
                        onClick = {
                            subtractingMoney = true
                            transactionAmount = ""
                            transactionBagIndex = null
                            waitingForBag = true
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(stringResource(R.string.track_spending))
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 24.dp, vertical = 16.dp).fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.balance),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = currency.format(bagAmounts.fold(unallocatedAmount.toBigDecimal()) { total, amount ->
                        total + amount.toBigDecimal()
                    }),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = stringResource(
                        R.string.unallocated_balance,
                        currency.format(unallocatedAmount.toBigDecimal())
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = bagGridState,
                modifier = Modifier.fillMaxWidth().weight(1f).pointerInput(Unit) {
                    detectTapGestures(onLongPress = {
                        waitingForBag = false
                        editingBagIndex = null
                        confirmDelete = false
                        newName = ""
                        newAmount = TextFieldValue()
                        showNewBag = true
                    })
                },
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(bagNames.indices.toList()) { index ->
                    ExampleMoneyBag(
                        name = bagNames[index],
                        amount = currency.format(bagAmounts[index].toBigDecimal()),
                        modifier = Modifier.pointerInput(index, bagNames[index], bagAmounts[index]) {
                            detectTapGestures(onTap = {
                                if (waitingForBag) {
                                    transactionBagIndex = index
                                    waitingForBag = false
                                    transactionAmount = ""
                                    showMoneyAction = true
                                }
                            }, onLongPress = {
                                waitingForBag = false
                                editingBagIndex = index
                                newName = bagNames[index]
                                newAmount = TextFieldValue(bagAmounts[index])
                                confirmDelete = false
                                showNewBag = true
                            })
                        }
                    )
                }
            }
            if (waitingForBag) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(
                            if (subtractingMoney) R.string.select_bag_to_subtract
                            else R.string.select_bag_to_add
                        ),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    TextButton(onClick = { waitingForBag = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }
        }
    }
}

@Composable
private fun ExampleMoneyBag(
    name: String,
    amount: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.money_bag),
            contentDescription = null,
            modifier = Modifier.size(140.dp)
        )
        Text(text = name, style = MaterialTheme.typography.titleMedium)
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MoneyBagsTheme {
        HomeScreen()
    }
}
