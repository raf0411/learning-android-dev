package android.app.healthconnectpoc

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.ViewModel
import androidx.core.net.toUri
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.metadata.Device
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Energy
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZonedDateTime

private val PERMISSIONS =
    setOf(
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getWritePermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getWritePermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getWritePermission(ExerciseSessionRecord::class)
    )

class HealthViewModel : ViewModel() {
    private val providerPackageName = "com.google.android.apps.healthdata"

    val availability = mutableStateOf<HealthConnectAvailability>(HealthConnectAvailability.NotChecked)
    var healthConnectClient: HealthConnectClient? = null
        private set
    val permissions = PERMISSIONS
    val todaySteps = mutableLongStateOf(0L)
    val todayActiveCalories = mutableDoubleStateOf(0.0)

    suspend fun hasAllPermissions(): Boolean {
        return healthConnectClient?.permissionController?.getGrantedPermissions()
            ?.containsAll(permissions) ?: false
    }

    fun checkAvailability(context: Context) {
        val sdkStatus = HealthConnectClient.getSdkStatus(context, providerPackageName)

        availability.value = when (sdkStatus) {
            HealthConnectClient.SDK_AVAILABLE -> {
                healthConnectClient = HealthConnectClient.getOrCreate(context)
                HealthConnectAvailability.Installed
            }
            HealthConnectClient.SDK_UNAVAILABLE -> HealthConnectAvailability.NotSupported
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> HealthConnectAvailability.NeedsUpdate
            else -> HealthConnectAvailability.NotSupported
        }
    }

    fun installHealthConnect(context: Context) {
        val uriString = "market://details?id=$providerPackageName&url=healthconnect%3A%2F%2Fonboarding"
        context.startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                setPackage("com.android.vending")
                data = uriString.toUri()
                putExtra("overlay", true)
                putExtra("callerId", context.packageName)
            }
        )
    }

    fun logActiveCalories(context: Context) {
        viewModelScope.launch {
            try {
                insertActiveCalories(context)
            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }

    private suspend fun insertActiveCalories(context: Context) {
        val now = Instant.now()

        val caloriesRecord = ActiveCaloriesBurnedRecord(
            energy = Energy.kilocalories(200.0),
            startTime = now,
            endTime = now,
            startZoneOffset = null,
            endZoneOffset = null,
            metadata = Metadata.manualEntry(),
        )

        healthConnectClient?.insertRecords(listOf(caloriesRecord))
    }

    fun readTodayActiveCalories() {
        viewModelScope.launch {
            try {
                val calories = aggregateActiveCaloriesToday()
                todayActiveCalories.doubleValue = calories
            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }

    fun readTodaySteps() {
        viewModelScope.launch {
            try {
                val steps = aggregateStepsToday()
                todaySteps.longValue = steps
            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }

    private suspend fun aggregateActiveCaloriesToday(): Double {
        val today = ZonedDateTime.now()
        val startOfDay = today.toLocalDate().atStartOfDay(today.zone).toInstant()
        val response = healthConnectClient?.aggregate(
            AggregateRequest(
                metrics = setOf(ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startOfDay, Instant.now())
            )
        )
        return response?.get(ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL)?.inKilocalories ?: 0.0
    }

    private suspend fun aggregateStepsToday(): Long {
        val today = ZonedDateTime.now()
        val startOfDay = today.toLocalDate().atStartOfDay(today.zone).toInstant()
        val response = healthConnectClient?.aggregate(
            AggregateRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startOfDay, Instant.now())
            )
        )
        return response?.get(StepsRecord.COUNT_TOTAL) ?: 0L
    }
}

sealed class HealthConnectAvailability {
    object NotChecked : HealthConnectAvailability()
    object Installed : HealthConnectAvailability()
    object NotSupported : HealthConnectAvailability()
    object NeedsUpdate : HealthConnectAvailability()
}
