package android.app.healthconnectpoc.ui.theme

import android.app.healthconnectpoc.HealthConnectAvailability
import android.app.healthconnectpoc.HealthViewModel
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HealthScreen() {
    val viewModel: HealthViewModel = viewModel()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.checkAvailability(context)
    }

    when (viewModel.availability.value) {
        HealthConnectAvailability.NotChecked -> {
            CircularProgressIndicator()
        }

        HealthConnectAvailability.Installed -> {
            // Health Connect is ready, show your main app content
            // You can now use viewModel.healthConnectClient to request permissions
            // and read/write data.
            MainAppContent(viewModel)
        }

        HealthConnectAvailability.NotSupported -> {
            Text("Health Connect is not supported on this device.")
        }

        HealthConnectAvailability.NeedsUpdate -> {
            Button(onClick = { viewModel.installHealthConnect(context) }) {
                Text("Please update Health Connect")
            }
        }
    }
}

@Preview
@Composable
fun HealthScreenPreview() {
    HealthScreen()
}