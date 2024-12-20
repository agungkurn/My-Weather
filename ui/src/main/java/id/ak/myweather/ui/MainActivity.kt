package id.ak.myweather.ui

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import id.ak.myweather.ui.composable.MainScreen
import id.ak.myweather.ui.composable.UiStateHandler
import id.ak.myweather.ui.theme.MyWeatherTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyWeatherTheme {
                val viewModel = viewModel<MainViewModel>()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                var location by remember { mutableStateOf<Location?>(null) }

                val permissionLauncher =
                    rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                        if (granted) {
                            fusedLocationClient.getCurrentLocation(
                                Priority.PRIORITY_HIGH_ACCURACY,
                                null
                            ).addOnSuccessListener {
                                location = it
                                location?.let {
                                    viewModel.setCurrentLocation(it)
                                }
                            }.addOnFailureListener {
                                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                LaunchedEffect(Unit) {
                    permissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                }

                UiStateHandler(
                    uiState = uiState,
                    showLoadingDialog = false,
                    onError = {
                        Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                        viewModel.onErrorShown()
                    }
                )

                MainScreen()
            }
        }
    }
}