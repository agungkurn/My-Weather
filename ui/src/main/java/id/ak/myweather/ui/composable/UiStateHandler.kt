package id.ak.myweather.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import id.ak.myweather.ui.state.UiState

@Composable
fun UiStateHandler(uiState: UiState, onError: (String) -> Unit, showLoadingDialog: Boolean = true) {
    when (uiState) {
        is UiState.Error -> onError(uiState.message)
        UiState.Loading -> {
            if (showLoadingDialog) {
                Dialog(
                    onDismissRequest = {},
                    properties = DialogProperties(
                        dismissOnBackPress = false,
                        dismissOnClickOutside = false
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        UiState.NotLoading -> {}
    }
}