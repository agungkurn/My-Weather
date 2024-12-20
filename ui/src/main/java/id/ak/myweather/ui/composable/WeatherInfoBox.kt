package id.ak.myweather.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun WeatherInfoBox(
    dark: Boolean,
    title: String,
    content: String,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null
) {
    val cardColor = remember(dark) { (if (dark) Color.Black else Color.White).copy(alpha = .5f) }

    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = cardColor)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.invoke()
                Text(text = title, style = MaterialTheme.typography.titleMedium)
            }
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}