package id.ak.myweather.ui.composable

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.LottieRetrySignal
import com.airbnb.lottie.compose.rememberLottieComposition
import id.ak.myweather.ui.R

@Composable
fun CurrentWeatherSummary(
    @RawRes animationResId: Int,
    lottieRetrySignal: LottieRetrySignal,
    temperature: Int,
    feelsLike: Int,
    maxTemperature: Int,
    minTemperature: Int,
    weatherDescription: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(animationResId),
            onRetry = { failCount, exception ->
                lottieRetrySignal.awaitRetry()
                true
            }
        )
        LottieAnimation(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 32.dp),
            composition = composition,
            iterations = LottieConstants.IterateForever
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "$temperature",
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = stringResource(R.string.celcius),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = weatherDescription.capitalize(Locale.current),
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = "Feels like $feelsLike °C",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = buildString {
                        append("H: $maxTemperature °C")
                        append(" | ")
                        append("L: $minTemperature °C")
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}