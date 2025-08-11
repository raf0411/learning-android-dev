package android.app.healthconnectpoc

import android.app.healthconnectpoc.ui.theme.HealthConnectPOCTheme
import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier

class PermissionsRationaleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthConnectPOCTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RationaleScreen(
                        onDoneClick = { finish() }
                    )
                }
            }
        }
    }
}

@Composable
fun RationaleScreen(onDoneClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Data Usage and Privacy",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "To provide you with personalized insights, our app needs to access " +
                    "certain data from Health Connect. Here’s how we use it:",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        RationaleItem(
            permission = "Active Calories Burned",
            explanation = "We read and write your calories burned to track your activity " +
                    "and display your progress in a weekly chart. This data is only stored " +
                    "on your device within Health Connect."
        )
        RationaleItem(
            permission = "Exercise Sessions",
            explanation = "When you log an activity like 'Running' or 'Cycling', we create " +
                    "an Exercise Session to keep a detailed record of your workouts."
        )
        RationaleItem(
            permission = "Steps",
            explanation = "We read your daily step count to display it on the main dashboard, " +
                    "giving you a complete view of your daily movement."
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onDoneClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Done")
        }
    }
}

@Composable
fun RationaleItem(permission: String, explanation: String) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = permission,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = explanation,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RationaleScreenPreview() {
    HealthConnectPOCTheme {
        RationaleScreen(onDoneClick = {})
    }
}