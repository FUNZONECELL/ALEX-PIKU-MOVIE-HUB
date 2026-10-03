package com.example.ui.screens.watchlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.MovieItem
import com.example.data.model.ViewingHistoryItem
import com.example.data.model.WatchlistItem
import com.example.ui.components.QualityBadge
import com.example.ui.components.RatingBadge
import com.example.ui.theme.*

@Composable
fun WatchlistScreen(
    watchlistItems: List<WatchlistItem>,
    historyItems: List<ViewingHistoryItem>,
    allMovies: List<MovieItem>,
    onMovieClick: (MovieItem) -> Unit,
    onPlayClick: (MovieItem) -> Unit,
    onRemoveFromWatchlist: (String) -> Unit,
    onClearHistory: () -> Unit,
    onExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val movieMap = remember(allMovies) { allMovies.associateBy { it.id } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "My Cinema Library",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Tabs: Watchlist vs History
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceDark,
            contentColor = CrimsonRed,
            indicator = {
                // simple line or default
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Watchlist (${watchlistItems.size})", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Viewing History (${historyItems.size})", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTab == 0) {
            // Watchlist Tab
            if (watchlistItems.isEmpty()) {
                EmptyState(
                    title = "Your Watchlist is Empty",
                    subtitle = "Tap the bookmark icon on any movie to save it for streaming later.",
                    buttonText = "Browse Movies",
                    onAction = onExplore
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(watchlistItems, key = { it.id }) { item ->
                        val movie = movieMap[item.movieId]
                        if (movie != null) {
                            WatchlistMovieCard(
                                movie = movie,
                                onClick = { onMovieClick(movie) },
                                onPlay = { onPlayClick(movie) },
                                onRemove = { onRemoveFromWatchlist(movie.id) }
                            )
                        }
                    }
                }
            }
        } else {
            // Viewing History Tab
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recommendation Engine uses this history",
                    fontSize = 11.sp,
                    color = CyberCyan
                )
                if (historyItems.isNotEmpty()) {
                    TextButton(onClick = onClearHistory) {
                        Text("Clear History", color = CrimsonRed, fontSize = 11.sp)
                    }
                }
            }

            if (historyItems.isEmpty()) {
                EmptyState(
                    title = "No Viewing History",
                    subtitle = "Movies and series you watch will appear here to resume playback.",
                    buttonText = "Start Streaming",
                    onAction = onExplore
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(historyItems, key = { it.id }) { h ->
                        val movie = movieMap[h.movieId]
                        if (movie != null) {
                            HistoryItemCard(
                                movie = movie,
                                historyItem = h,
                                onClick = { onMovieClick(movie) },
                                onResume = { onPlayClick(movie) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WatchlistMovieCard(
    movie: MovieItem,
    onClick: () -> Unit,
    onPlay: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CardBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp, 84.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    QualityBadge(quality = movie.qualities)
                    Spacer(modifier = Modifier.width(6.dp))
                    RatingBadge(rating = movie.imdbRating)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = movie.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${movie.releaseYear} • ${movie.duration} • ${movie.languages}",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }

            IconButton(onClick = onPlay) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = CrimsonRed)
            }

            IconButton(onClick = onRemove) {
                Icon(imageVector = Icons.Default.BookmarkRemove, contentDescription = "Remove", tint = TextMuted)
            }
        }
    }
}

@Composable
fun HistoryItemCard(
    movie: MovieItem,
    historyItem: ViewingHistoryItem,
    onClick: () -> Unit,
    onResume: () -> Unit
) {
    val progress = if (historyItem.totalDurationSeconds > 0) {
        historyItem.watchedDurationSeconds.toFloat() / historyItem.totalDurationSeconds
    } else 0.1f

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CardBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = movie.posterUrl,
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp, 84.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = movie.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = "Watched ${historyItem.watchedDurationSeconds / 60}m of ${movie.duration}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    QualityBadge(quality = movie.qualities)
                }

                Button(
                    onClick = onResume,
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Resume", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = CrimsonRed,
                trackColor = SurfaceVariantDark
            )
        }
    }
}

@Composable
fun EmptyState(title: String, subtitle: String, buttonText: String, onAction: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                subtitle,
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
            ) {
                Text(buttonText)
            }
        }
    }
}
