package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Info
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.MovieItem
import com.example.data.model.RecommendationMatch
import com.example.ui.components.MovieCard
import com.example.ui.components.QualityBadge
import com.example.ui.components.RatingBadge
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    featuredMovies: List<MovieItem>,
    trendingMovies: List<MovieItem>,
    latestReleases: List<MovieItem>,
    allMovies: List<MovieItem>,
    recommendations: List<RecommendationMatch>,
    watchlistMovieIds: Set<String>,
    onMovieClick: (MovieItem) -> Unit,
    onPlayClick: (MovieItem) -> Unit,
    onWatchlistToggle: (String, Boolean) -> Unit,
    onShareClick: (MovieItem) -> Unit,
    onGenreSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val genres = listOf(
        "All", "Action", "Sci-Fi", "Bengali Cinema", "Thriller", "Cyberpunk", "Anime", "Crime", "Mystery"
    )
    var selectedGenreChip by remember { mutableStateOf("All") }

    val heroMovie = remember(featuredMovies, allMovies) {
        featuredMovies.firstOrNull() ?: allMovies.firstOrNull()
    }
    val bengaliHits = remember(allMovies) {
        allMovies.filter { it.languages.contains("Bengali", ignoreCase = true) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Hero Section
        if (heroMovie != null) {
            item(key = "hero_banner") {
                HeroBanner(
                    movie = heroMovie,
                    isInWatchlist = watchlistMovieIds.contains(heroMovie.id),
                    onPlay = { onPlayClick(heroMovie) },
                    onDetails = { onMovieClick(heroMovie) },
                    onWatchlistToggle = {
                        onWatchlistToggle(heroMovie.id, watchlistMovieIds.contains(heroMovie.id))
                    },
                    onShare = { onShareClick(heroMovie) }
                )
            }
        }

        // Quick Category Pills
        item(key = "category_pills") {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(genres, key = { it }) { genre ->
                    val isSelected = selectedGenreChip == genre
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) CrimsonRed else SurfaceVariantDark,
                        modifier = Modifier.clickable {
                            selectedGenreChip = genre
                            onGenreSelected(genre)
                        }
                    ) {
                        Text(
                            text = genre,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Recommendation Engine: "Personalized For You"
        if (recommendations.isNotEmpty()) {
            item(key = "section_recommendations") {
                SectionHeader(
                    title = "✨ Recommended For You",
                    subtitle = "AI Engine based on viewing history & genre affinity"
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    items(recommendations, key = { it.movie.id }) { rec ->
                        Box {
                            MovieCard(
                                movie = rec.movie,
                                onClick = { onMovieClick(rec.movie) },
                                onWatchlistToggle = {
                                    onWatchlistToggle(rec.movie.id, watchlistMovieIds.contains(rec.movie.id))
                                },
                                isInWatchlist = watchlistMovieIds.contains(rec.movie.id)
                            )

                            // Match Percentage Pill overlay
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EmeraldGreen,
                                modifier = Modifier
                                    .padding(start = 6.dp, top = 28.dp)
                            ) {
                                Text(
                                    text = "${rec.matchPercentage}% Match",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Trending on ALEXPIKUMOVEHUB
        if (trendingMovies.isNotEmpty()) {
            item(key = "section_trending") {
                SectionHeader(
                    title = "🔥 Trending International Cinema",
                    subtitle = "Most streamed in 4K UHD with Dual Audio"
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    items(trendingMovies, key = { it.id }) { movie ->
                        MovieCard(
                            movie = movie,
                            onClick = { onMovieClick(movie) },
                            onWatchlistToggle = {
                                onWatchlistToggle(movie.id, watchlistMovieIds.contains(movie.id))
                            },
                            isInWatchlist = watchlistMovieIds.contains(movie.id)
                        )
                    }
                }
            }
        }

        // Latest Releases
        if (latestReleases.isNotEmpty()) {
            item(key = "section_latest") {
                SectionHeader(
                    title = "⚡ Latest 2026 Premieres",
                    subtitle = "Freshly encoded Multi-Sub and HDR master releases"
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    items(latestReleases, key = { it.id }) { movie ->
                        MovieCard(
                            movie = movie,
                            onClick = { onMovieClick(movie) },
                            onWatchlistToggle = {
                                onWatchlistToggle(movie.id, watchlistMovieIds.contains(movie.id))
                            },
                            isInWatchlist = watchlistMovieIds.contains(movie.id)
                        )
                    }
                }
            }
        }

        // Bengali & South Asian Special
        if (bengaliHits.isNotEmpty()) {
            item(key = "section_bengali") {
                SectionHeader(
                    title = "🇧🇩 Bengali Cinema & Dual Audio Vault",
                    subtitle = "Original Bengali audio & localized dual audio tracks"
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    items(bengaliHits, key = { it.id }) { movie ->
                        MovieCard(
                            movie = movie,
                            onClick = { onMovieClick(movie) },
                            onWatchlistToggle = {
                                onWatchlistToggle(movie.id, watchlistMovieIds.contains(movie.id))
                            },
                            isInWatchlist = watchlistMovieIds.contains(movie.id)
                        )
                    }
                }
            }
        }

        // All High Performance Library
        item(key = "section_all") {
            SectionHeader(
                title = "🌐 All Hub Titles (${allMovies.size})",
                subtitle = "Browse complete catalog with fast direct links"
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(allMovies, key = { it.id }) { movie ->
                    MovieCard(
                        movie = movie,
                        onClick = { onMovieClick(movie) },
                        onWatchlistToggle = {
                            onWatchlistToggle(movie.id, watchlistMovieIds.contains(movie.id))
                        },
                        isInWatchlist = watchlistMovieIds.contains(movie.id)
                    )
                }
            }
        }
    }
}

@Composable
fun HeroBanner(
    movie: MovieItem,
    isInWatchlist: Boolean,
    onPlay: () -> Unit,
    onDetails: () -> Unit,
    onWatchlistToggle: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(430.dp)
    ) {
        val context = androidx.compose.ui.platform.LocalContext.current
        val bannerRequest = remember(movie.bannerUrl, movie.posterUrl) {
            coil.request.ImageRequest.Builder(context)
                .data(movie.bannerUrl.ifBlank { com.example.R.drawable.hero_feature_banner })
                .placeholder(com.example.R.drawable.hero_feature_banner)
                .error(com.example.R.drawable.hero_feature_banner)
                .fallback(com.example.R.drawable.hero_feature_banner)
                .crossfade(false)
                .build()
        }
        AsyncImage(
            model = bannerRequest,
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient Dark Vignette
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x66090A0F),
                            Color(0xEE090A0F),
                            ObsidianBlack
                        ),
                        startY = 100f
                    )
                )
        )

        // Content on Hero
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Badges Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = CrimsonRed,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "HUB EXCLUSIVE PREMIERE",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                QualityBadge(quality = movie.qualities)
                RatingBadge(rating = movie.imdbRating)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Movie Title
            Text(
                text = movie.title,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                color = TextPrimary,
                lineHeight = 30.sp
            )

            // Metadata row
            Text(
                text = "${movie.releaseYear} • ${movie.duration} • ${movie.genres}",
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Audio track info
            Text(
                text = "🔊 ${movie.languages}",
                fontSize = 11.sp,
                color = CyberCyan,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Hero Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Play Button
                Button(
                    onClick = onPlay,
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("hero_play_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Watch Stream",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }

                // Watchlist Button
                FilledTonalIconButton(
                    onClick = onWatchlistToggle,
                    shape = RoundedCornerShape(8.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = if (isInWatchlist) CrimsonRed.copy(alpha = 0.2f) else SurfaceVariantDark
                    ),
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = if (isInWatchlist) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Watchlist",
                        tint = if (isInWatchlist) CrimsonRed else TextPrimary
                    )
                }

                // Info / Details Button
                FilledTonalIconButton(
                    onClick = onDetails,
                    shape = RoundedCornerShape(8.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = SurfaceVariantDark),
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Info",
                        tint = TextPrimary
                    )
                }

                // Share Button
                FilledTonalIconButton(
                    onClick = onShare,
                    shape = RoundedCornerShape(8.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = SurfaceVariantDark),
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
                        tint = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = subtitle,
            fontSize = 11.sp,
            color = TextSecondary
        )
    }
}
