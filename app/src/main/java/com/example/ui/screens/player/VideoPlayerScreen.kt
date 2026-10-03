package com.example.ui.screens.player

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.MovieItem
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerScreen(
    movie: MovieItem,
    isPlaying: Boolean,
    playbackPositionMs: Long,
    totalDurationMs: Long,
    selectedQuality: String,
    selectedSubtitle: String,
    selectedAudioTrack: String,
    playbackSpeed: Float,
    isAudioOnly: Boolean,
    onBack: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onSeekForward: () -> Unit,
    onSeekBackward: () -> Unit,
    onSelectQuality: (String) -> Unit,
    onSelectSubtitle: (String) -> Unit,
    onSelectAudioTrack: (String) -> Unit,
    onSelectSpeed: (Float) -> Unit,
    onToggleAudioOnly: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showControls by remember { mutableStateOf(true) }
    var showQualityDialog by remember { mutableStateOf(false) }
    var showSubtitleDialog by remember { mutableStateOf(false) }
    var showAudioDialog by remember { mutableStateOf(false) }
    var showSpeedDialog by remember { mutableStateOf(false) }

    // Auto-advance simulated playback position when playing
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(1000)
            val nextPos = (playbackPositionMs + (1000 * playbackSpeed).toLong()).coerceAtMost(totalDurationMs)
            onSeek(nextPos)
        }
    }

    // Auto hide controls after 4 seconds
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(4000)
            showControls = false
        }
    }

    val formatTime: (Long) -> String = { ms ->
        val totalSec = ms / 1000
        val hr = totalSec / 3600
        val min = (totalSec % 3600) / 60
        val sec = totalSec % 60
        if (hr > 0) String.format("%02d:%02d:%02d", hr, min, sec) else String.format("%02d:%02d", min, sec)
    }

    // Sample dynamic subtitles according to selected language
    val subtitleText = when (selectedSubtitle) {
        "Bengali" -> "নিউরন ইন্টারফেস সক্রিয় করা হয়েছে... পরবর্তী গন্তব্য টোকিও এবং ঢাকা কোয়ান্টাম গ্রিড।"
        "Hindi" -> "न्यूरॉन इंटरफ़ेस सक्रिय हो गया है... अगला स्टेशन टोक्यो क्वांटम ग्रिड।"
        "Spanish" -> "Interfaz neural activada... próxima parada en la red cuántica."
        "Japanese" -> "ニューロンインターフェース起動完了... 次のステーションは東京量子グリッド。"
        "Off" -> ""
        else -> "Neural core interface connected. Synchronizing quantum stream buffers..."
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showControls = !showControls
            }
    ) {
        // Video Stage
        if (isAudioOnly) {
            // Audio-Only Listening Mode View
            AudioOnlyVisualizer(movie = movie, isPlaying = isPlaying)
        } else {
            // Video Frame Simulation
            AsyncImage(
                model = movie.bannerUrl.ifBlank { movie.posterUrl },
                contentDescription = "Video playback",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Dimming when controls are visible
            if (showControls) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x77000000))
                )
            }

            // Real-Time Subtitle Bar
            if (selectedSubtitle != "Off" && subtitleText.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = if (showControls) 110.dp else 40.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xB3000000))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = subtitleText,
                        color = Color.Yellow,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Overlay Controls
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Top Header Controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = movie.title,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = CrimsonRed,
                                    shape = RoundedCornerShape(3.dp)
                                ) {
                                    Text(
                                        text = selectedQuality,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${playbackSpeed}x • $selectedAudioTrack",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Settings quick actions
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Audio Only Toggle
                        IconButton(onClick = onToggleAudioOnly) {
                            Icon(
                                imageVector = if (isAudioOnly) Icons.Filled.Headphones else Icons.Outlined.Headphones,
                                contentDescription = "Audio Mode",
                                tint = if (isAudioOnly) CyberCyan else Color.White
                            )
                        }

                        // Subtitle Button
                        IconButton(onClick = { showSubtitleDialog = true }) {
                            Icon(imageVector = Icons.Default.Subtitles, contentDescription = "Subtitles", tint = if (selectedSubtitle != "Off") CyberCyan else Color.White)
                        }

                        // Audio Track Button
                        IconButton(onClick = { showAudioDialog = true }) {
                            Icon(imageVector = Icons.Default.Audiotrack, contentDescription = "Audio Track", tint = AmberGold)
                        }

                        // Quality Switcher Button
                        IconButton(onClick = { showQualityDialog = true }) {
                            Icon(imageVector = Icons.Default.HighQuality, contentDescription = "Quality", tint = Color.White)
                        }

                        // Speed Button
                        IconButton(onClick = { showSpeedDialog = true }) {
                            Icon(imageVector = Icons.Default.Speed, contentDescription = "Speed", tint = Color.White)
                        }
                    }
                }

                // Center Play/Rewind/Forward Controls
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rewind 10s
                    IconButton(
                        onClick = onSeekBackward,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0x55000000))
                    ) {
                        Icon(imageVector = Icons.Default.Replay10, contentDescription = "Rewind 10s", tint = Color.White, modifier = Modifier.size(32.dp))
                    }

                    // Play/Pause Center
                    IconButton(
                        onClick = onTogglePlayPause,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(CrimsonRed)
                            .testTag("player_play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    // Fast-Forward 10s
                    IconButton(
                        onClick = onSeekForward,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0x55000000))
                    ) {
                        Icon(imageVector = Icons.Default.Forward10, contentDescription = "Forward 10s", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }

                // Bottom Timeline & Scrubber Controls
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = formatTime(playbackPositionMs), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = formatTime(totalDurationMs), color = TextSecondary, fontSize = 12.sp)
                    }

                    Slider(
                        value = if (totalDurationMs > 0) playbackPositionMs.toFloat() / totalDurationMs else 0f,
                        onValueChange = { frac ->
                            onSeek((frac * totalDurationMs).toLong())
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = CrimsonRed,
                            activeTrackColor = CrimsonRed,
                            inactiveTrackColor = Color(0x55FFFFFF)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    // Quality Selector Dialog
    if (showQualityDialog) {
        val qualities = listOf("4K UHD 2160p (60fps HDR)", "1080p Full HD (Original)", "720p HD (Smooth)", "480p SD (Data Saver)")
        PlayerOptionDialog(
            title = "Video Stream Quality",
            options = qualities,
            selectedOption = selectedQuality,
            onSelect = {
                onSelectQuality(it.split(" ")[0])
                showQualityDialog = false
            },
            onDismiss = { showQualityDialog = false }
        )
    }

    // Subtitle Selector Dialog
    if (showSubtitleDialog) {
        val subtitles = listOf("English", "Bengali", "Hindi", "Spanish", "French", "Japanese", "Off")
        PlayerOptionDialog(
            title = "Multi-Language Subtitles",
            options = subtitles,
            selectedOption = selectedSubtitle,
            onSelect = {
                onSelectSubtitle(it)
                showSubtitleDialog = false
            },
            onDismiss = { showSubtitleDialog = false }
        )
    }

    // Audio Track Selector Dialog
    if (showAudioDialog) {
        val audioTracks = listOf(
            "Dual Audio [Bengali + English]",
            "Original English Dolby 5.1",
            "Hindi Dubbed 7.1 Atmos",
            "Japanese Original Audio",
            "Director Audio Commentary"
        )
        PlayerOptionDialog(
            title = "Audio Track Selector",
            options = audioTracks,
            selectedOption = selectedAudioTrack,
            onSelect = {
                onSelectAudioTrack(it)
                showAudioDialog = false
            },
            onDismiss = { showAudioDialog = false }
        )
    }

    // Playback Speed Dialog
    if (showSpeedDialog) {
        val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
        PlayerOptionDialog(
            title = "Playback Speed",
            options = speeds.map { "${it}x" },
            selectedOption = "${playbackSpeed}x",
            onSelect = {
                onSelectSpeed(it.replace("x", "").toFloat())
                showSpeedDialog = false
            },
            onDismiss = { showSpeedDialog = false }
        )
    }
}

@Composable
fun AudioOnlyVisualizer(movie: MovieItem, isPlaying: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(180.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = movie.title,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = TextPrimary
        )
        Text(
            text = "Offline Background Audio Listening Mode",
            fontSize = 12.sp,
            color = CyberCyan
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Audio Waves Indicator
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val heights = if (isPlaying) listOf(24.dp, 40.dp, 16.dp, 32.dp, 48.dp, 20.dp, 36.dp, 12.dp) else listOf(12.dp, 12.dp, 12.dp, 12.dp, 12.dp, 12.dp, 12.dp, 12.dp)
            heights.forEach { h ->
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(h)
                        .clip(RoundedCornerShape(2.dp))
                        .background(CrimsonRed)
                )
            }
        }
    }
}

@Composable
fun PlayerOptionDialog(
    title: String,
    options: List<String>,
    selectedOption: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                options.forEach { opt ->
                    val isSelected = selectedOption.contains(opt) || opt.contains(selectedOption)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(opt) }
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = opt,
                            color = if (isSelected) CrimsonRed else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                        if (isSelected) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = CrimsonRed, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Close", color = TextSecondary)
                    }
                }
            }
        }
    }
}
