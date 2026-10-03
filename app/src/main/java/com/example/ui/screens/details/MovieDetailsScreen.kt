package com.example.ui.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.Share
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.MovieItem
import com.example.data.model.ReviewItem
import com.example.ui.components.QualityBadge
import com.example.ui.components.RatingBadge
import com.example.ui.theme.*

@Composable
fun MovieDetailsScreen(
    movie: MovieItem,
    isInWatchlist: Boolean,
    reviews: List<ReviewItem>,
    onBack: () -> Unit,
    onPlay: (Boolean) -> Unit, // false = video, true = audio-only
    onWatchlistToggle: () -> Unit,
    onDownloadClick: (String) -> Unit,
    onShareClick: () -> Unit,
    onSubmitReview: (String, Float, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showReviewDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Hero Backdrop
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                val context = androidx.compose.ui.platform.LocalContext.current
                val backdropRequest = remember(movie.bannerUrl, movie.posterUrl) {
                    coil.request.ImageRequest.Builder(context)
                        .data(movie.bannerUrl.ifBlank { com.example.R.drawable.hero_feature_banner })
                        .placeholder(com.example.R.drawable.hero_feature_banner)
                        .error(com.example.R.drawable.hero_feature_banner)
                        .fallback(com.example.R.drawable.hero_feature_banner)
                        .crossfade(false)
                        .build()
                }
                AsyncImage(
                    model = backdropRequest,
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Vignette gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x99090A0F),
                                    Color.Transparent,
                                    Color(0xCC090A0F),
                                    ObsidianBlack
                                )
                            )
                        )
                )

                // Top navigation bar overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0x66000000))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = onShareClick,
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0x66000000))
                        ) {
                            Icon(imageVector = Icons.Outlined.Share, contentDescription = "Share", tint = Color.White)
                        }

                        IconButton(
                            onClick = onWatchlistToggle,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (isInWatchlist) CrimsonRed else Color(0x66000000)
                            )
                        ) {
                            Icon(
                                imageVector = if (isInWatchlist) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Watchlist",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Title and Core Details Card
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QualityBadge(quality = movie.qualities)
                    RatingBadge(rating = movie.imdbRating)
                    Surface(
                        color = SurfaceVariantDark,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Hub ${movie.hubRating}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Surface(
                        color = SurfaceVariantDark,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = movie.ageRating,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = movie.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )

                if (movie.originalTitle.isNotBlank() && movie.originalTitle != movie.title) {
                    Text(
                        text = "Original: ${movie.originalTitle}",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${movie.releaseYear} • ${movie.duration} • ${movie.genres}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Audio Tracks & Multi-Subtitles Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Audio: ${movie.languages}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Subtitles,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Subtitles: ${movie.availableSubtitles}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons: Watch Stream & Audio Only
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onPlay(false) },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("details_play_stream_button")
                    ) {
                        Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Play", tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Watch Stream", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    OutlinedButton(
                        onClick = { onPlay(true) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("details_audio_only_button")
                    ) {
                        Icon(imageVector = Icons.Outlined.Headphones, contentDescription = "Audio Mode")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Audio Mode", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Synopsis
                Text(
                    text = "Storyline & Synopsis",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = movie.description,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Cast & Crew
                Text(
                    text = "Cast: ${movie.cast}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = "Director: ${movie.director}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // MovelinkBD Style Direct Download Links Section
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "📥 Direct High-Speed Download Links",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberGold
                )
                Text(
                    text = "High-speed encrypted CDN links for offline playback",
                    fontSize = 11.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Download Card for 4K UHD
                DownloadOptionCard(
                    quality = "4K UHD 2160p (HDR / 10-Bit)",
                    fileSize = "4.8 GB",
                    format = "MKV • HEVC x265 • Dual Audio",
                    badgeColor = AmberGold,
                    onDownload = { onDownloadClick("4K UHD") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Download Card for 1080p FHD
                DownloadOptionCard(
                    quality = "1080p Full HD (x264 / AAC 5.1)",
                    fileSize = "2.2 GB",
                    format = "MKV • Dual Audio [Bengali + English]",
                    badgeColor = CyberCyan,
                    onDownload = { onDownloadClick("1080p FHD") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Download Card for 720p HD
                DownloadOptionCard(
                    quality = "720p HD (Data Saver / Mobile Optimized)",
                    fileSize = "980 MB",
                    format = "MP4 • Multi-Subtitles Included",
                    badgeColor = TextSecondary,
                    onDownload = { onDownloadClick("720p HD") }
                )
            }
        }

        // Community Reviews Section
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💬 Community Reviews (${reviews.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    TextButton(onClick = { showReviewDialog = true }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = CrimsonRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Write Review", color = CrimsonRed, fontSize = 12.sp)
                    }
                }

                if (reviews.isEmpty()) {
                    Text(
                        text = "No reviews yet. Be the first member to review this title!",
                        fontSize = 12.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    reviews.forEach { rev ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CardBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = rev.userName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextPrimary
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = AmberGold, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(text = "${rev.rating}", fontSize = 11.sp, color = AmberGold, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = rev.comment,
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Write Review Dialog
    if (showReviewDialog) {
        var reviewerName by remember { mutableStateOf("") }
        var reviewText by remember { mutableStateOf("") }
        var ratingVal by remember { mutableStateOf(5.0f) }

        Dialog(onDismissRequest = { showReviewDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Write a Review", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                    Text("Share your thoughts on ${movie.title}", fontSize = 12.sp, color = TextSecondary)

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = reviewerName,
                        onValueChange = { reviewerName = it },
                        label = { Text("Your Name or Member Handle") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        label = { Text("Review comment...") },
                        maxLines = 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showReviewDialog = false }) {
                            Text("Cancel", color = TextMuted)
                        }
                        Button(
                            onClick = {
                                if (reviewerName.isNotBlank() && reviewText.isNotBlank()) {
                                    onSubmitReview(reviewerName, ratingVal, reviewText)
                                    showReviewDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                        ) {
                            Text("Submit Review")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadOptionCard(
    quality: String,
    fileSize: String,
    format: String,
    badgeColor: Color,
    onDownload: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = CardBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = badgeColor
                    ) {
                        Text(
                            text = fileSize,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = quality,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = format,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Button(
                onClick = onDownload,
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Download,
                    contentDescription = "Download",
                    tint = CyberCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Get", fontSize = 12.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
            }
        }
    }
}
