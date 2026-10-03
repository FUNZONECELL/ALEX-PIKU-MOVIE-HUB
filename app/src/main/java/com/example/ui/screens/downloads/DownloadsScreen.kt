package com.example.ui.screens.downloads

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.OfflineDownloadItem
import com.example.ui.components.QualityBadge
import com.example.ui.theme.*

@Composable
fun DownloadsScreen(
    downloads: List<OfflineDownloadItem>,
    onPlayOffline: (String, Boolean) -> Unit, // movieId, isAudioOnly
    onPauseResume: (OfflineDownloadItem) -> Unit,
    onDeleteDownload: (String) -> Unit,
    onExploreCatalog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var offlineModeOnly by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Title and Offline Mode Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Offline Vault & Downloads",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Watch & listen without internet connectivity",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Airplane Mode", fontSize = 11.sp, color = if (offlineModeOnly) AmberGold else TextMuted)
                Spacer(modifier = Modifier.width(6.dp))
                Switch(
                    checked = offlineModeOnly,
                    onCheckedChange = { offlineModeOnly = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AmberGold,
                        checkedTrackColor = AmberGold.copy(alpha = 0.3f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Device Storage Gauge Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Device Storage Usage", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text("18.4 GB Free of 128 GB", fontSize = 11.sp, color = CyberCyan)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Storage progress bar
                LinearProgressIndicator(
                    progress = { 0.42f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = CrimsonRed,
                    trackColor = SurfaceVariantDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("ALEXPIKUMOVEHUB Vault: 4.8 GB", fontSize = 10.sp, color = TextMuted)
                    Text("Optimal 4K/1080p Storage Engine", fontSize = 10.sp, color = AmberGold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Downloads List
        if (downloads.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 60.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.DownloadForOffline,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "No Offline Downloads Yet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Download movies and episodes in 4K or 1080p to enjoy seamless offline streaming on flights or commute.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onExploreCatalog,
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                    ) {
                        Text("Browse Trending Movies")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(downloads, key = { it.downloadId }) { dl ->
                    DownloadItemCard(
                        item = dl,
                        onPlay = { isAudio -> onPlayOffline(dl.movieId, isAudio) },
                        onPauseResume = { onPauseResume(dl) },
                        onDelete = { onDeleteDownload(dl.downloadId) }
                    )
                }
            }
        }
    }
}

@Composable
fun DownloadItemCard(
    item: OfflineDownloadItem,
    onPlay: (Boolean) -> Unit,
    onPauseResume: () -> Unit,
    onDelete: () -> Unit
) {
    val progress = if (item.fileSizeBytes > 0) item.downloadedBytes.toFloat() / item.fileSizeBytes else 0f
    val isCompleted = item.downloadStatus == "COMPLETED"

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CardBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("download_card_${item.downloadId}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = item.posterUrl,
                    contentDescription = item.movieTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(54.dp, 80.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        QualityBadge(quality = item.quality)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isCompleted) "Ready Offline" else item.downloadStatus,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCompleted) EmeraldGreen else AmberGold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.movieTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "${item.quality} • ${(item.fileSizeBytes / (1024 * 1024))} MB",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    if (!isCompleted) {
                        Text(
                            text = "Speed: ${item.downloadSpeed}",
                            fontSize = 10.sp,
                            color = CyberCyan
                        )
                    }
                }

                // Delete Button
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete", tint = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (!isCompleted) {
                // Progress Bar
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = CrimsonRed,
                    trackColor = SurfaceVariantDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}% Downloaded",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    TextButton(onClick = onPauseResume) {
                        Icon(
                            imageVector = if (item.downloadStatus == "DOWNLOADING") Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (item.downloadStatus == "DOWNLOADING") "Pause" else "Resume",
                            fontSize = 11.sp,
                            color = CyberCyan
                        )
                    }
                }
            } else {
                // Play Offline Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onPlay(false) },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Watch Offline", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onPlay(true) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan)
                    ) {
                        Icon(imageVector = Icons.Outlined.Headphones, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Audio", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
