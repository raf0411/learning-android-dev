package android.app.healthconnectpoc

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HealthScreen(
    viewModel: HealthViewModel = viewModel()
) {
    val context = LocalContext.current
    val availability by viewModel.availability
    var hasPermissions by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.containsAll(viewModel.permissions)) {
            hasPermissions = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkAvailability(context)
    }

    LaunchedEffect(availability) {
        if (availability == HealthConnectAvailability.Installed) {
            hasPermissions = viewModel.hasAllPermissions()
        }
    }

    when (availability) {
        HealthConnectAvailability.NotChecked -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        HealthConnectAvailability.Installed -> {
            if (hasPermissions) {
                MainAppContent(viewModel)
            } else {
                PermissionRequestScreen {
                    permissionLauncher.launch(viewModel.permissions)
                }
            }
        }
        HealthConnectAvailability.NotSupported -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Health Connect is not supported on this device.")
            }
        }
        HealthConnectAvailability.NeedsUpdate -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Button(onClick = { viewModel.installHealthConnect(context) }) {
                    Text("Please update Health Connect")
                }
            }
        }
    }
}

@Composable
fun PermissionRequestScreen(onRequest: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Our app needs access to Health Connect to function.")
        Spacer(Modifier.height(8.dp))
        Button(onClick = onRequest) {
            Text("Request Permissions")
        }
    }
}

@Preview
@Composable
fun HealthScreenPreview() {
    HealthScreen()
}