package com.example.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.MovieItem
import com.example.data.model.ReviewItem
import com.example.ui.components.QualityBadge
import com.example.ui.theme.*

@Composable
fun AdminLoginScreen(
    targetEmail: String,
    targetPass: String,
    errorMessage: String?,
    onLogin: (String, String) -> Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var emailInput by remember { mutableStateOf(targetEmail) }
    var passwordInput by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .statusBarsPadding()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CrimsonRed,
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "ALEXPIKUMOVEHUB",
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
            color = Color.White
        )
        Text(
            text = "Super Administrator Control Portal",
            fontSize = 13.sp,
            color = AmberGold,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Admin Authentication", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                Text("Authorized management credentials required", fontSize = 11.sp, color = TextSecondary)

                Spacer(modifier = Modifier.height(16.dp))

                // Email Input
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Admin Email ID") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = CrimsonRed) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_email_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = CrimsonRed,
                        unfocusedBorderColor = BorderSubtle
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password Input
                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = AmberGold) },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = TextMuted
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_password_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = CrimsonRed,
                        unfocusedBorderColor = BorderSubtle
                    )
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMessage, color = CrimsonLight, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 1-Tap Fill Credentials for instant convenience
                TextButton(
                    onClick = {
                        emailInput = targetEmail
                        passwordInput = targetPass
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Auto-Fill Admin Credentials", fontSize = 11.sp, color = CyberCyan)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { onLogin(emailInput, passwordInput) },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admin_login_submit_button")
                ) {
                    Text("Sign In to Control Center", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Return to Hub", color = TextSecondary)
                }
            }
        }
    }
}

@Composable
fun AdminDashboardScreen(
    allMovies: List<MovieItem>,
    allReviews: List<ReviewItem>,
    onSaveMovie: (MovieItem) -> Unit,
    onDeleteMovie: (MovieItem) -> Unit,
    onModerateReview: (Long, String) -> Unit,
    onDeleteReview: (Long) -> Unit,
    onResetCatalog: () -> Unit,
    onSendAnnouncement: (String, String) -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var movieToEdit by remember { mutableStateOf<MovieItem?>(null) }
    var showAddMovieDialog by remember { mutableStateOf(false) }
    var showAnnouncementDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .statusBarsPadding()
    ) {
        // Admin Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("ADMIN CONTROL CENTER", fontWeight = FontWeight.Black, fontSize = 16.sp, color = CrimsonRed)
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(4.dp), color = AmberGold) {
                            Text("SUPERUSER", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                        }
                    }
                    Text("pikubalur@gmail.com • alexpiku-core-node", fontSize = 10.sp, color = TextMuted)
                }
            }

            IconButton(onClick = onLogout) {
                Icon(imageVector = Icons.Default.Logout, contentDescription = "Logout", tint = CrimsonLight)
            }
        }

        // Dashboard Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceDark,
            contentColor = CrimsonRed,
            edgePadding = 16.dp
        ) {
            val tabs = listOf(
                "Content Manager (${allMovies.size})",
                "Analytics & CDN",
                "Moderation (${allReviews.size})",
                "Database Ops"
            )
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp) }
                )
            }
        }

        // Tab Contents
        when (selectedTab) {
            0 -> {
                // Content Library Manager
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Movie & Series Catalog", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                            Text("Add, edit, delete or change any link in the database", fontSize = 11.sp, color = TextSecondary)
                        }

                        Button(
                            onClick = { showAddMovieDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("admin_add_movie_button")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Movie", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(allMovies, key = { it.id }) { movie ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = CardBackground,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = movie.posterUrl,
                                        contentDescription = movie.title,
                                        modifier = Modifier
                                            .size(46.dp, 68.dp)
                                            .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(movie.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary, maxLines = 1)
                                        Text("${movie.releaseYear} • ${movie.type} • ${movie.fileSize}", fontSize = 11.sp, color = TextSecondary)
                                        Text("IMDb: ${movie.imdbRating} • Views: ${movie.viewCount}", fontSize = 10.sp, color = AmberGold)
                                    }

                                    IconButton(onClick = { movieToEdit = movie }) {
                                        Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit", tint = CyberCyan)
                                    }

                                    IconButton(onClick = {
                                        onDeleteMovie(movie)
                                        Toast.makeText(context, "Deleted '${movie.title}'", Toast.LENGTH_SHORT).show()
                                    }) {
                                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete", tint = CrimsonLight)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Analytics & CDN Health Tab
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    item {
                        Text("Real-Time Streaming Metrics", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetricCard(label = "Active 4K Streamers", value = "4,821", icon = Icons.Default.Stream, color = CrimsonRed, modifier = Modifier.weight(1f))
                            MetricCard(label = "Total Bandwidth", value = "628.4 TB", icon = Icons.Default.CloudDownload, color = CyberCyan, modifier = Modifier.weight(1f))
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetricCard(label = "VIP Members", value = "32,950", icon = Icons.Default.Star, color = AmberGold, modifier = Modifier.weight(1f))
                            MetricCard(label = "Offline Downloads", value = "118,400", icon = Icons.Default.Download, color = EmeraldGreen, modifier = Modifier.weight(1f))
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("CDN Edge Cluster Health", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                    }

                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CardBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                CdnNodeRow(node = "Asia-East 1 (Tokyo)", ping = "11ms", status = "ONLINE 100%")
                                HorizontalDivider(color = BorderSubtle, modifier = Modifier.padding(vertical = 8.dp))
                                CdnNodeRow(node = "South-Asia 2 (Dhaka & Mumbai)", ping = "9ms", status = "ONLINE 100%")
                                HorizontalDivider(color = BorderSubtle, modifier = Modifier.padding(vertical = 8.dp))
                                CdnNodeRow(node = "Europe-West 3 (Frankfurt)", ping = "22ms", status = "ONLINE 99.98%")
                                HorizontalDivider(color = BorderSubtle, modifier = Modifier.padding(vertical = 8.dp))
                                CdnNodeRow(node = "US-East (Virginia Vault)", ping = "31ms", status = "ONLINE 100%")
                            }
                        }
                    }
                }
            }

            2 -> {
                // Content Moderation & Reviews Tab
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Automated Community Moderation", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                    Text("Approve, flag, or remove community reviews", fontSize = 11.sp, color = TextSecondary)

                    Spacer(modifier = Modifier.height(12.dp))

                    if (allReviews.isEmpty()) {
                        Text("No reviews in moderation queue", color = TextMuted)
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 100.dp)
                        ) {
                            items(allReviews, key = { it.id }) { rev ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = CardBackground,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(rev.userName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = if (rev.status == "APPROVED") EmeraldGreen.copy(alpha = 0.2f) else CrimsonRed.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = rev.status,
                                                    fontSize = 10.sp,
                                                    color = if (rev.status == "APPROVED") EmeraldGreen else CrimsonRed,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(rev.comment, fontSize = 12.sp, color = TextSecondary)

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (rev.status != "APPROVED") {
                                                TextButton(onClick = { onModerateReview(rev.id, "APPROVED") }) {
                                                    Text("Approve", color = EmeraldGreen, fontSize = 11.sp)
                                                }
                                            }
                                            TextButton(onClick = { onDeleteReview(rev.id) }) {
                                                Text("Delete", color = CrimsonLight, fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // Database Ops Tab
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    item {
                        Text("Database Management & Platform Configuration", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                    }

                    item {
                        DatabaseActionCard(
                            title = "Push System Release Push Notification",
                            subtitle = "Broadcast real-time notification to all active member devices",
                            icon = Icons.Default.Campaign,
                            buttonText = "Broadcast",
                            color = CyberCyan,
                            onClick = { showAnnouncementDialog = true }
                        )
                    }

                    item {
                        DatabaseActionCard(
                            title = "Restore Seed Catalog Defaults",
                            subtitle = "Re-populates benchmark international catalog and sample data",
                            icon = Icons.Default.Restore,
                            buttonText = "Reset Catalog",
                            color = AmberGold,
                            onClick = {
                                onResetCatalog()
                                Toast.makeText(context, "Catalog restored to default!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    item {
                        DatabaseActionCard(
                            title = "Flush Global CDN Cache & Index",
                            subtitle = "Purges stale edges across Tokyo, Dhaka, Frankfurt and Virginia",
                            icon = Icons.Default.Sync,
                            buttonText = "Flush Cache",
                            color = EmeraldGreen,
                            onClick = {
                                Toast.makeText(context, "CDN Caches successfully purged.", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    // Add Movie Dialog
    if (showAddMovieDialog) {
        MovieFormDialog(
            movie = null,
            onDismiss = { showAddMovieDialog = false },
            onSave = { newMovie ->
                onSaveMovie(newMovie)
                showAddMovieDialog = false
                Toast.makeText(context, "Added '${newMovie.title}' to Hub Catalog!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Edit Movie Dialog
    if (movieToEdit != null) {
        MovieFormDialog(
            movie = movieToEdit,
            onDismiss = { movieToEdit = null },
            onSave = { updatedMovie ->
                onSaveMovie(updatedMovie)
                movieToEdit = null
                Toast.makeText(context, "Updated '${updatedMovie.title}'!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Push Announcement Dialog
    if (showAnnouncementDialog) {
        var annTitle by remember { mutableStateOf("🚀 Premiere Alert: New International Blockbuster") }
        var annMsg by remember { mutableStateOf("Stream exclusively on ALEXPIKUMOVEHUB in 4K UHD with Dual Audio & Subtitles.") }

        Dialog(onDismissRequest = { showAnnouncementDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Broadcast Release Notification", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = annTitle,
                        onValueChange = { annTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = annMsg,
                        onValueChange = { annMsg = it },
                        label = { Text("Message Body") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAnnouncementDialog = false }) {
                            Text("Cancel", color = TextMuted)
                        }
                        Button(
                            onClick = {
                                onSendAnnouncement(annTitle, annMsg)
                                showAnnouncementDialog = false
                                Toast.makeText(context, "Broadcast sent to all members!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                        ) {
                            Text("Send Push Notification")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieFormDialog(
    movie: MovieItem?,
    onDismiss: () -> Unit,
    onSave: (MovieItem) -> Unit
) {
    var title by remember { mutableStateOf(movie?.title ?: "") }
    var originalTitle by remember { mutableStateOf(movie?.originalTitle ?: "") }
    var type by remember { mutableStateOf(movie?.type ?: "Movie") }
    var releaseYear by remember { mutableStateOf(movie?.releaseYear?.toString() ?: "2026") }
    var duration by remember { mutableStateOf(movie?.duration ?: "2h 10m") }
    var imdbRating by remember { mutableStateOf(movie?.imdbRating?.toString() ?: "8.6") }
    var genres by remember { mutableStateOf(movie?.genres ?: "Action, Sci-Fi, Thriller") }
    var languages by remember { mutableStateOf(movie?.languages ?: "Dual Audio [Bengali + English]") }
    var qualities by remember { mutableStateOf(movie?.qualities ?: "4K UHD, 1080p FHD, 720p HD") }
    var posterUrl by remember { mutableStateOf(movie?.posterUrl ?: "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800") }
    var bannerUrl by remember { mutableStateOf(movie?.bannerUrl ?: "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=1200") }
    var downloadUrl1080p by remember { mutableStateOf(movie?.downloadUrl1080p ?: "https://cdn.alexpikumovehub.com/vault/movie_1080p.mkv") }
    var fileSize by remember { mutableStateOf(movie?.fileSize ?: "2.4 GB") }
    var description by remember { mutableStateOf(movie?.description ?: "Exciting new blockbuster release on ALEXPIKUMOVEHUB with multi-language audio and 4K visuals.") }
    var isFeatured by remember { mutableStateOf(movie?.isFeatured ?: false) }
    var isLatestRelease by remember { mutableStateOf(movie?.isLatestRelease ?: true) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (movie == null) "Add Movie / Series to Hub" else "Edit Hub Content",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title *") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = originalTitle,
                            onValueChange = { originalTitle = it },
                            label = { Text("Original / Bengali Title") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = releaseYear,
                                onValueChange = { releaseYear = it },
                                label = { Text("Year") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                            )
                            OutlinedTextField(
                                value = imdbRating,
                                onValueChange = { imdbRating = it },
                                label = { Text("IMDb") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                            )
                            OutlinedTextField(
                                value = duration,
                                onValueChange = { duration = it },
                                label = { Text("Duration") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = genres,
                            onValueChange = { genres = it },
                            label = { Text("Genres (comma separated)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = languages,
                            onValueChange = { languages = it },
                            label = { Text("Audio Tracks (Dual Audio Bengali/English)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = posterUrl,
                            onValueChange = { posterUrl = it },
                            label = { Text("Poster Image URL") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = downloadUrl1080p,
                            onValueChange = { downloadUrl1080p = it },
                            label = { Text("Download Link (1080p / 4K)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Storyline & Synopsis") },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                    }

                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isFeatured, onCheckedChange = { isFeatured = it })
                            Text("Feature on Home Carousel", color = TextPrimary, fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isLatestRelease, onCheckedChange = { isLatestRelease = it })
                            Text("Mark as Latest 2026 Premiere", color = TextPrimary, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextMuted)
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                val item = movie?.copy(
                                    title = title,
                                    originalTitle = originalTitle,
                                    releaseYear = releaseYear.toIntOrNull() ?: 2026,
                                    imdbRating = imdbRating.toDoubleOrNull() ?: 8.0,
                                    duration = duration,
                                    genres = genres,
                                    languages = languages,
                                    posterUrl = posterUrl,
                                    downloadUrl1080p = downloadUrl1080p,
                                    description = description,
                                    isFeatured = isFeatured,
                                    isLatestRelease = isLatestRelease
                                ) ?: MovieItem(
                                    id = "mov_custom_${System.currentTimeMillis()}",
                                    title = title,
                                    originalTitle = originalTitle,
                                    releaseYear = releaseYear.toIntOrNull() ?: 2026,
                                    imdbRating = imdbRating.toDoubleOrNull() ?: 8.0,
                                    duration = duration,
                                    genres = genres,
                                    languages = languages,
                                    posterUrl = posterUrl,
                                    downloadUrl1080p = downloadUrl1080p,
                                    description = description,
                                    isFeatured = isFeatured,
                                    isLatestRelease = isLatestRelease
                                )
                                onSave(item)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                    ) {
                        Text("Save to Catalog")
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CardBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            Text(text = label, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

@Composable
fun CdnNodeRow(node: String, ping: String, status: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(node, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
            Text("Latency: $ping", fontSize = 10.sp, color = CyberCyan)
        }
        Surface(shape = RoundedCornerShape(4.dp), color = EmeraldGreen.copy(alpha = 0.2f)) {
            Text(status, fontSize = 9.sp, fontWeight = FontWeight.Black, color = EmeraldGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
        }
    }
}

@Composable
fun DatabaseActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    buttonText: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CardBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                    Text(subtitle, fontSize = 11.sp, color = TextSecondary)
                }
            }

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(buttonText, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
