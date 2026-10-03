package com.example.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MovieItem
import com.example.ui.components.MovieCard
import com.example.ui.theme.*

@Composable
fun SearchScreen(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    selectedGenre: String,
    onGenreSelect: (String) -> Unit,
    selectedQuality: String,
    onQualitySelect: (String) -> Unit,
    selectedType: String,
    onTypeSelect: (String) -> Unit,
    filteredMovies: List<MovieItem>,
    watchlistMovieIds: Set<String>,
    onMovieClick: (MovieItem) -> Unit,
    onWatchlistToggle: (String, Boolean) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val genres = listOf("All", "Action", "Sci-Fi", "Bengali Cinema", "Thriller", "Horror", "Anime", "Crime", "Mystery")
    val qualities = listOf("All", "4K UHD", "1080p FHD", "720p HD")
    val types = listOf("All", "Movie", "TV Series")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Search Bar Input Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_text_input"),
            placeholder = {
                Text("Search by movie, actor, director, genre...", color = TextMuted, fontSize = 13.sp)
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = CrimsonRed)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceDark,
                unfocusedContainerColor = SurfaceDark,
                focusedBorderColor = CrimsonRed,
                unfocusedBorderColor = BorderSubtle,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Content Type Filter (All, Movies, TV Series)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            types.forEach { type ->
                val isSelected = selectedType == type
                FilterChip(
                    selected = isSelected,
                    onClick = { onTypeSelect(type) },
                    label = { Text(type, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CrimsonRed,
                        selectedLabelColor = Color.White,
                        containerColor = SurfaceDark,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = BorderSubtle,
                        selectedBorderColor = CrimsonRed
                    )
                )
            }
        }

        // Genre Filter Row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(genres) { genre ->
                val isSelected = selectedGenre == genre
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) AmberGold else SurfaceDark,
                    modifier = Modifier.clickable { onGenreSelect(genre) }
                ) {
                    Text(
                        text = genre,
                        color = if (isSelected) Color.Black else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Quality Filter Row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(qualities) { quality ->
                val isSelected = selectedQuality == quality
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) CyberCyan else SurfaceDark,
                    modifier = Modifier.clickable { onQualitySelect(quality) }
                ) {
                    Text(
                        text = quality,
                        color = if (isSelected) Color.Black else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Results Summary Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredMovies.size} Releases Found",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (searchQuery.isNotEmpty() || selectedGenre != "All" || selectedQuality != "All" || selectedType != "All") {
                TextButton(onClick = onClearFilters) {
                    Text("Reset Filters", color = CrimsonRed, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Results Grid
        if (filteredMovies.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 60.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "No Results",
                        tint = TextMuted,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No movies or series found",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try adjusting your search terms or genre filter.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onClearFilters,
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                    ) {
                        Text("Reset Search")
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp, top = 6.dp)
            ) {
                items(filteredMovies, key = { it.id }) { movie ->
                    MovieCard(
                        movie = movie,
                        onClick = { onMovieClick(movie) },
                        onWatchlistToggle = {
                            onWatchlistToggle(movie.id, watchlistMovieIds.contains(movie.id))
                        },
                        isInWatchlist = watchlistMovieIds.contains(movie.id),
                        cardWidth = 160,
                        cardHeight = 220
                    )
                }
            }
        }
    }
}
