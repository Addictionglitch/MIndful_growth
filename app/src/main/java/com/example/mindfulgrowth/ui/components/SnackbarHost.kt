
package com.example.mindfulgrowth.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mindfulgrowth.ui.theme.SystemConfigColors
import com.example.mindfulgrowth.ui.theme.spacing
import kotlinx.coroutines.delay

enum class SnackbarType {
    SUCCESS, ERROR, INFO, WARNING
}

data class SnackbarData(
    val message: String,
    val type: SnackbarType = SnackbarType.INFO,
    val duration: Long = 3000
)

@Composable
fun CustomSnackbarHost(
    snackbarData: SnackbarData?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val visible = snackbarData != null

    LaunchedEffect(snackbarData) {
        if (snackbarData != null) {
            delay(snackbarData.duration)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring()
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it }
        ) + fadeOut(),
        modifier = modifier
    ) {
        snackbarData?.let { data ->
            CustomSnackbar(
                message = data.message,
                type = data.type,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
private fun CustomSnackbar(
    message: String,
    type: SnackbarType,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon: ImageVector
    val iconColor: Color

    when (type) {
        SnackbarType.SUCCESS -> {
            icon = Icons.Default.CheckCircle
            iconColor = Color(SystemConfigColors.NEON_GREEN_ACCENT)
        }
        SnackbarType.ERROR -> {
            icon = Icons.Default.Error
            iconColor = Color(SystemConfigColors.ACCENT_RED_PRIMARY)
        }
        SnackbarType.WARNING -> {
            icon = Icons.Default.Warning
            iconColor = Color(SystemConfigColors.ACCENT_RED_SECONDARY)
        }
        SnackbarType.INFO -> {
            icon = Icons.Default.Info
            iconColor = Color(SystemConfigColors.TEXT_SECONDARY)
        }
    }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(spacing.medium),
        cornerRadius = 16.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color(SystemConfigColors.TEXT_PRIMARY)
                )
            }

            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Color(SystemConfigColors.TEXT_SECONDARY).copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Usage in a screen:
@Composable
fun ScreenWithSnackbar() {
    var snackbarData by remember { mutableStateOf<SnackbarData?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Your screen content

        // Snackbar at top
        CustomSnackbarHost(
            snackbarData = snackbarData,
            onDismiss = { snackbarData = null },
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

    // Trigger snackbar:
    // snackbarData = SnackbarData("Tree purchased!", SnackbarType.SUCCESS)
}
