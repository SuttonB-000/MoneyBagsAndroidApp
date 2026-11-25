package com.example.moneybags

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.moneybags.ui.theme.DeepBlue
import com.example.moneybags.ui.theme.MoneyBagsTheme
import com.example.moneybags.ui.theme.SoftNeutral
import kotlin.math.roundToInt

class SettingsActivity : ComponentActivity() {
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
                SettingsScreen(
                    darkModeEnabled = darkModeEnabled,
                    onDarkModeChange = { enabled ->
                        AppPreferences.setDarkModeEnabled(this, enabled)
                        darkModeEnabled = enabled
                    },
                    textScale = textScale,
                    onTextScaleChange = { textScale = it },
                    onTextScaleChangeFinished = {
                        AppPreferences.setTextScale(this, textScale)
                    },
                    selectedCurrency = selectedCurrency,
                    onCurrencySelected = { currency ->
                        AppPreferences.setCurrency(this, currency)
                        selectedCurrency = currency
                    },
                    onBack = { finish() }
                )
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    darkModeEnabled: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    textScale: Float,
    onTextScaleChange: (Float) -> Unit,
    onTextScaleChangeFinished: () -> Unit,
    selectedCurrency: AppCurrency,
    onCurrencySelected: (AppCurrency) -> Unit,
    onBack: () -> Unit
) {
    var showCurrencyDialog by rememberSaveable { mutableStateOf(false) }
    var showHelpDialog by rememberSaveable { mutableStateOf(false) }

    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurface,
            title = { Text(stringResource(R.string.select_currency)) },
            text = {
                Column {
                    AppCurrency.entries.forEach { currency ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = currency == selectedCurrency,
                                    role = Role.RadioButton,
                                    onClick = {
                                        onCurrencySelected(currency)
                                        showCurrencyDialog = false
                                    }
                                )
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currency == selectedCurrency,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    unselectedColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = stringResource(currency.labelRes),
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showCurrencyDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurface,
            title = { Text(stringResource(R.string.help)) },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("• ${stringResource(R.string.help_create_bag)}")
                    Text("• ${stringResource(R.string.help_edit_bag)}")
                    Text(
                        text = stringResource(R.string.track_spending),
                        modifier = Modifier.padding(top = 12.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("• ${stringResource(R.string.help_add_money, selectedCurrency.symbol)}")
                    Text("• ${stringResource(R.string.help_subtract_money, selectedCurrency.symbol)}")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showHelpDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Surface(
                color = DeepBlue,
                contentColor = SoftNeutral
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onBack,
                        colors = ButtonDefaults.textButtonColors(contentColor = SoftNeutral)
                    ) {
                        Text(stringResource(R.string.back))
                    }
                    Text(
                        text = stringResource(R.string.settings),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = darkModeEnabled,
                        role = Role.Switch,
                        onValueChange = onDarkModeChange
                    )
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.dark_mode),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(checked = darkModeEnabled, onCheckedChange = null)
            }
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.text_size),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(
                            R.string.text_size_percent,
                            (textScale * 100).roundToInt()
                        ),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Slider(
                    value = textScale,
                    onValueChange = onTextScaleChange,
                    onValueChangeFinished = onTextScaleChangeFinished,
                    valueRange = AppPreferences.MIN_TEXT_SCALE..AppPreferences.MAX_TEXT_SCALE,
                    steps = 5,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCurrencyDialog = true }
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.currency),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(selectedCurrency.labelRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                Text(
                    text = stringResource(R.string.about),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(
                        R.string.version_label,
                        stringResource(R.string.app_version_name)
                    ),
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            OutlinedButton(
                onClick = { showHelpDialog = true },
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
            ) {
                Text(stringResource(R.string.help))
            }
        }
    }
}
