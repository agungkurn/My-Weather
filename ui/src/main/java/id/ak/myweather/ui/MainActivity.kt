package id.ak.myweather.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import id.ak.myweather.ui.composable.MainScreen
import id.ak.myweather.ui.composable.UiStateHandler
import id.ak.myweather.ui.theme.MyWeatherTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    private val settingsClient by lazy {
        LocationServices.getSettingsClient(this)
    }

    private val intentSenderForResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == RESULT_OK) {
                getCurrentLocation()
            }
        }

    @ExperimentalMaterial3Api
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyWeatherTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                val (showLocationSheet, setShowLocationSheet) = remember { mutableStateOf(false) }

                val permissionLauncher =
                    rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                        val fineLocationGranted =
                            it[Manifest.permission.ACCESS_FINE_LOCATION] == true
                        val coarseLocationGranted =
                            it[Manifest.permission.ACCESS_COARSE_LOCATION] == true

                        if (fineLocationGranted || coarseLocationGranted) {
                            checkLocationSettings()
                        } else {
                            setShowLocationSheet(true)
                        }
                    }

                UiStateHandler(
                    uiState = uiState,
                    showLoadingDialog = false,
                    onError = {
                        Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                        viewModel.onErrorShown()
                    }
                )

                MainScreen(
                    showLocationSheet = showLocationSheet,
                    setShowLocationSheet = setShowLocationSheet,
                    requestLocationPermission = {
                        val permissions = buildList {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                add(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            add(Manifest.permission.ACCESS_FINE_LOCATION)
                            add(Manifest.permission.ACCESS_COARSE_LOCATION)
                        }
                        checkPermission(
                            permissions = permissions,
                            permissionLauncher = permissionLauncher,
                            onHasBeenGranted = { checkLocationSettings() },
                            onHasBeenDenied = {
                                permissionLauncher.launch(permissions.toTypedArray())
                            }
                        )
                    }
                )
            }
        }
    }

    private fun checkPermission(
        permissions: Iterable<String>,
        permissionLauncher: ActivityResultLauncher<Array<String>>,
        onHasBeenGranted: () -> Unit,
        onHasBeenDenied: () -> Unit
    ) {
        val deniedPermission = mutableListOf<String>().apply {
            addAll(permissions)
        }

        permissions.forEach {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    it
                ) == PackageManager.PERMISSION_GRANTED -> {
                    deniedPermission.remove(it)
                    onHasBeenGranted()
                }

                shouldShowRequestPermissionRationale(it) -> {
                    onHasBeenDenied()
                }
            }
        }

        if (deniedPermission.isNotEmpty()) {
            permissionLauncher.launch(deniedPermission.toTypedArray())
        }
    }

    private fun checkLocationSettings() {
        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 1000)
                .build()
        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()
        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener { _ ->
                getCurrentLocation()
            }.addOnFailureListener { e ->
                if (e is ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        val request = IntentSenderRequest.Builder(e.resolution).build()
                        intentSenderForResult.launch(request)
                    } catch (_: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener {
                it?.let {
                    viewModel.fetchCurrentWeather(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        isUserSelected = false
                    )
                }
            }.addOnFailureListener {
            }
    }
}