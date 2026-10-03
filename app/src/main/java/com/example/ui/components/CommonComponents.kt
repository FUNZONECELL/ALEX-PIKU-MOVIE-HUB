package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.MovieItem
import com.example.data.model.NotificationItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.HubScreen

@Composable
fun QualityBadge(quality: String, modifier: Modifier = Modifier) {
    val is4K = quality.contains("4K", ignoreCase = true)
    val bgColor = if (is4K) AmberGold else CyberCyan
    val textColor = Color.Black

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
    ) {
        Text(
            text = if (is4K) "4K UHD" else "1080p FHD",
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            color = textColor,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun RatingBadge(rating: Double, modifier: Modifier = Modifier) {
    Surface(
        color = Color(0xCC000000),
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Rating",
                tint = AmberGold,
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "$rating",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun MovieCard(
    movie: MovieItem,
    onClick: () -> Unit,
    onWatchlistToggle: (() -> Unit)? = null,
    isInWatchlist: Boolean = false,
    modifier: Modifier = Modifier,
    cardWidth: Int = 140,
    cardHeight: Int = 210
) {
    val context = LocalContext.current
    val imageRequest = remember(movie.posterUrl) {
        coil.request.ImageRequest.Builder(context)
            .data(movie.posterUrl.ifBlank { com.example.R.drawable.hero_feature_banner })
            .placeholder(com.example.R.drawable.hero_feature_banner)
            .error(com.example.R.drawable.hero_feature_banner)
            .fallback(com.example.R.drawable.hero_feature_banner)
            .crossfade(false)
            .build()
    }

    Card(
        modifier = modifier
            .width(cardWidth.dp)
            .clickable(onClick = onClick)
            .testTag("movie_card_${movie.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardHeight.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(SurfaceVariantDark)
            ) {
                AsyncImage(
                    model = imageRequest,
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Top Quality & Rating Overlays
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QualityBadge(quality = movie.qualities)
                    RatingBadge(rating = movie.imdbRating)
                }

                // Dual Audio Pill at bottom of image
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xDD000000))
                            )
                        )
                        .padding(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (movie.languages.contains("Bengali", ignoreCase = true)) "Bengali + Multi" else "Multi-Audio",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CyberCyan,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Title and Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = movie.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${movie.releaseYear} • ${movie.type}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    if (onWatchlistToggle != null) {
                        IconButton(
                            onClick = onWatchlistToggle,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (isInWatchlist) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Watchlist",
                                tint = if (isInWatchlist) CrimsonRed else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HubTopBar(
    currentScreen: HubScreen,
    unreadNotifications: Int,
    isAdminLoggedIn: Boolean,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onAdminClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier
            .background(ObsidianBlack)
            .statusBarsPadding(),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianBlack),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { /* Brand logo home */ }
            ) {
                // Neon Red Hub Icon
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CrimsonRed,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Logo",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ALEXPIKU",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "MOVEHUB",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = CrimsonRed,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "INTERNATIONAL CINEMA & SERIES",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberGold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        },
        actions = {
            if (currentScreen != HubScreen.SEARCH) {
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier.testTag("topbar_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextPrimary
                    )
                }
            }

            // Notification Bell with Badge
            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier.testTag("topbar_notifications_button")
            ) {
                BadgedBox(
                    badge = {
                        if (unreadNotifications > 0) {
                            Badge(
                                containerColor = CrimsonRed,
                                contentColor = Color.White
                            ) {
                                Text("$unreadNotifications")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = TextPrimary
                    )
                }
            }

            // Admin Control Button
            IconButton(
                onClick = onAdminClick,
                modifier = Modifier.testTag("topbar_admin_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "Admin Panel",
                    tint = if (isAdminLoggedIn) AmberGold else TextMuted
                )
            }
        }
    )
}

@Composable
fun HubBottomBar(
    currentScreen: HubScreen,
    onTabSelected: (HubScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = SurfaceDark,
        tonalElevation = 8.dp,
        modifier = modifier.navigationBarsPadding()
    ) {
        val navItems = listOf(
            Triple(HubScreen.HOME, Icons.Filled.Home, "Home"),
            Triple(HubScreen.SEARCH, Icons.Filled.Search, "Search"),
            Triple(HubScreen.WATCHLIST, Icons.Filled.Bookmark, "Watchlist"),
            Triple(HubScreen.DOWNLOADS, Icons.Filled.Download, "Downloads"),
            Triple(HubScreen.PROFILE, Icons.Filled.Person, "VIP Hub")
        )

        navItems.forEach { (screen, icon, label) ->
            val selected = currentScreen == screen
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(screen) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (selected) CrimsonRed else TextMuted
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected) CrimsonRed else TextMuted
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = CrimsonRed.copy(alpha = 0.15f)
                ),
                modifier = Modifier.testTag("nav_item_${label.lowercase()}")
            )
        }
    }
}

@Composable
fun ShareMovieDialog(
    movie: MovieItem,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val shareText = "🎬 Watch '${movie.title}' (${movie.releaseYear}) on ALEXPIKUMOVEHUB!\n" +
            "🌟 IMDb: ${movie.imdbRating}/10 | Hub: ${movie.hubRating}/10\n" +
            "💎 Qualities: ${movie.qualities}\n" +
            "🔊 Audio: ${movie.languages}\n" +
            "⬇️ Direct 4K/1080p Download Link: ${movie.downloadUrl1080p}\n" +
            "Stream now on ALEXPIKUMOVEHUB International Cinema!"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Share International Cinema",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Movie preview card in dialog
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariantDark)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = movie.posterUrl,
                        contentDescription = movie.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(54.dp, 80.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = movie.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${movie.releaseYear} • ${movie.type} • ${movie.fileSize}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        QualityBadge(quality = movie.qualities)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Social Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SocialShareItem(
                        icon = Icons.Default.Share,
                        name = "System",
                        color = CyberCyan
                    ) {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share ${movie.title} via ALEXPIKUMOVEHUB")
                        context.startActivity(shareIntent)
                        onDismiss()
                    }

                    SocialShareItem(
                        icon = Icons.Default.Send,
                        name = "Telegram",
                        color = Color(0xFF2AABEE)
                    ) {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                            setPackage("org.telegram.messenger")
                        }
                        try {
                            context.startActivity(sendIntent)
                        } catch (e: Exception) {
                            val generalIntent = Intent.createChooser(sendIntent, "Share to Telegram")
                            context.startActivity(generalIntent)
                        }
                        onDismiss()
                    }

                    SocialShareItem(
                        icon = Icons.Default.Chat,
                        name = "WhatsApp",
                        color = Color(0xFF25D366)
                    ) {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                            setPackage("com.whatsapp")
                        }
                        try {
                            context.startActivity(sendIntent)
                        } catch (e: Exception) {
                            val generalIntent = Intent.createChooser(sendIntent, "Share to WhatsApp")
                            context.startActivity(generalIntent)
                        }
                        onDismiss()
                    }

                    SocialShareItem(
                        icon = Icons.Default.ContentCopy,
                        name = "Copy Link",
                        color = AmberGold
                    ) {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("ALEXPIKUMOVEHUB Link", movie.downloadUrl1080p)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Stream link copied to clipboard!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                }
            }
        }
    }
}

@Composable
fun SocialShareItem(
    icon: ImageVector,
    name: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            shape = CircleShape,
            color = color.copy(alpha = 0.15f),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = name,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = name, fontSize = 11.sp, color = TextSecondary)
    }
}

@Composable
fun NotificationSheet(
    notifications: List<NotificationItem>,
    onDismiss: () -> Unit,
    onMarkAllRead: () -> Unit,
    onNotificationClick: (NotificationItem) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Release Radar & Alerts",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Real-time push notifications",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onMarkAllRead) {
                        Text("Mark all as read", fontSize = 12.sp, color = CyberCyan)
                    }
                }

                HorizontalDivider(color = BorderSubtle)

                if (notifications.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No notifications yet", color = TextMuted)
                    }
                } else {
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notifications.size) { index ->
                            val item = notifications[index]
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (item.isRead) SurfaceVariantDark else CardBackground,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNotificationClick(item) }
                                    .border(
                                        width = 1.dp,
                                        color = if (item.isRead) Color.Transparent else CrimsonRed.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = when (item.type) {
                                            "NEW_RELEASE" -> Icons.Default.Movie
                                            "EPISODE" -> Icons.Default.Tv
                                            "RECOMMENDATION" -> Icons.Default.Star
                                            else -> Icons.Default.Notifications
                                        },
                                        contentDescription = null,
                                        tint = if (item.isRead) TextMuted else CrimsonRed,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = TextPrimary
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = item.message,
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
        }
    }
}
