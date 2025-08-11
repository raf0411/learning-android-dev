package android.app.healthconnectpoc

import android.annotation.SuppressLint
import android.app.healthconnectpoc.ui.theme.HealthConnectPOCTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@SuppressLint("DefaultLocale")
@Composable
fun MainAppContent(viewModel: HealthViewModel) {
    val context = LocalContext.current
    val stepCount by viewModel.todaySteps
    val caloriesBurned by viewModel.todayActiveCalories

    LaunchedEffect(Unit) {
        viewModel.readTodaySteps()
        viewModel.readTodayActiveCalories()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Health Connect Ready",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(32.dp))

        // Display the data
        Text(
            text = "Today's Steps: $stepCount",
            style = MaterialTheme.typography.bodyLarge
        )
        // Add this to display calories, formatted to one decimal place
        Text(
            text = "Active Calories: ${String.format("%.1f", caloriesBurned)} kcal",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(16.dp))

        Button(onClick = { viewModel.logActiveCalories(context = context) }) {
            Text("Log 200 Active Calories")
        }

        // TODO: Add your dashboard, charts, and logging buttons here.
        // TODO: Add UI for reading and displaying data here
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun MainAppContentPreview() {
    HealthConnectPOCTheme {
        MainAppContent(viewModel = HealthViewModel())
    }
}