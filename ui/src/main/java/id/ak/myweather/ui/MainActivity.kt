package id.ak.myweather.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import id.ak.myweather.ui.composable.MainScreen
import id.ak.myweather.ui.composable.UiStateHandler
import id.ak.myweather.ui.theme.MyWeatherTheme
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    @ExperimentalMaterial3Api
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyWeatherTheme {
                val coroutineScope = rememberCoroutineScope()
                val viewModel = viewModel<MainViewModel>()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                val (showLocationSheet, setShowLocationSheet) = remember { mutableStateOf(false) }

                val permissionLauncher =
                    rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                        val fineLocationGranted =
                            it[Manifest.permission.ACCESS_FINE_LOCATION] == true
                        val coarseLocationGranted =
                            it[Manifest.permission.ACCESS_COARSE_LOCATION] == true

                        if (fineLocationGranted || coarseLocationGranted) {
                            coroutineScope.launch {
                                try {
                                    getCurrentLocation().also {
                                        viewModel.fetchCurrentWeather(
                                            latitude = it.latitude,
                                            longitude = it.longitude,
                                            isUserSelected = false
                                        )
                                    }
                                } catch (_: Exception) {
                                }
                            }
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
                            onHasBeenGranted = {
                                coroutineScope.launch {
                                    try {
                                        getCurrentLocation().also {
                                            viewModel.fetchCurrentWeather(
                                                latitude = it.latitude,
                                                longitude = it.longitude,
                                                isUserSelected = false
                                            )
                                        }
                                    } catch (_: Exception) {
                                    }
                                }
                            },
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

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(): Location {
        return suspendCancellableCoroutine<Location> {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    it.resume(location)
                }.addOnFailureListener { e ->
                    it.resumeWithException(e)
                }
        }
    }
}