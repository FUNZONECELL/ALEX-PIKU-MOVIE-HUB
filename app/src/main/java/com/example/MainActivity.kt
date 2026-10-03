package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.HubBottomBar
import com.example.ui.components.HubTopBar
import com.example.ui.components.NotificationSheet
import com.example.ui.components.ShareMovieDialog
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.admin.AdminLoginScreen
import com.example.ui.screens.details.MovieDetailsScreen
import com.example.ui.screens.downloads.DownloadsScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.player.VideoPlayerScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.search.SearchScreen
import com.example.ui.screens.watchlist.WatchlistScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBlack
import com.example.ui.viewmodel.HubScreen
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = isDarkTheme) {
                val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
                val allMovies by viewModel.allMovies.collectAsStateWithLifecycle()
                val featuredMovies by viewModel.featuredMovies.collectAsStateWithLifecycle()
                val trendingMovies by viewModel.trendingMovies.collectAsStateWithLifecycle()
                val latestReleases by viewModel.latestReleases.collectAsStateWithLifecycle()
                val recommendations by viewModel.recommendations.collectAsStateWithLifecycle()
                val watchlist by viewModel.watchlist.collectAsStateWithLifecycle()
                val viewingHistory by viewModel.viewingHistory.collectAsStateWithLifecycle()
                val downloads by viewModel.downloads.collectAsStateWithLifecycle()
                val notifications by viewModel.notifications.collectAsStateWithLifecycle()
                val unreadNotifications by viewModel.unreadNotificationCount.collectAsStateWithLifecycle()
                val allReviews by viewModel.allReviews.collectAsStateWithLifecycle()

                val filteredMovies by viewModel.filteredMovies.collectAsStateWithLifecycle()
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()
                val selectedQualityFilter by viewModel.selectedQualityFilter.collectAsStateWithLifecycle()
                val selectedTypeFilter by viewModel.selectedTypeFilter.collectAsStateWithLifecycle()

                val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
                val playbackPos by viewModel.playbackPositionMs.collectAsStateWithLifecycle()
                val totalDuration by viewModel.totalDurationMs.collectAsStateWithLifecycle()
                val selectedQuality by viewModel.selectedQuality.collectAsStateWithLifecycle()
                val selectedSubtitle by viewModel.selectedSubtitle.collectAsStateWithLifecycle()
                val selectedAudioTrack by viewModel.selectedAudioTrack.collectAsStateWithLifecycle()
                val playbackSpeed by viewModel.playbackSpeed.collectAsStateWithLifecycle()
                val isAudioOnly by viewModel.isAudioOnly.collectAsStateWithLifecycle()

                val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
                val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()
                val adminAuthError by viewModel.adminAuthError.collectAsStateWithLifecycle()

                val selectedMovie by viewModel.selectedMovie.collectAsStateWithLifecycle()
                val shareMovie by viewModel.shareMovieDialog.collectAsStateWithLifecycle()

                var showNotificationsSheet by remember { mutableStateOf(false) }

                val watchlistIds = remember(watchlist) { watchlist.map { it.movieId }.toSet() }

                // System Back Button Handling
                BackHandler(enabled = currentScreen != HubScreen.HOME) {
                    viewModel.navigateBack()
                }

                val showTopBar = currentScreen != HubScreen.PLAYER && currentScreen != HubScreen.ADMIN_PANEL && currentScreen != HubScreen.ADMIN_LOGIN
                val showBottomBar = currentScreen in listOf(
                    HubScreen.HOME,
                    HubScreen.SEARCH,
                    HubScreen.WATCHLIST,
                    HubScreen.DOWNLOADS,
                    HubScreen.PROFILE
                )

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ObsidianBlack),
                    containerColor = ObsidianBlack,
                    topBar = {
                        if (showTopBar) {
                            HubTopBar(
                                currentScreen = currentScreen,
                                unreadNotifications = unreadNotifications,
                                isAdminLoggedIn = isAdminLoggedIn,
                                onSearchClick = { viewModel.navigateTo(HubScreen.SEARCH) },
                                onNotificationsClick = { showNotificationsSheet = true },
                                onAdminClick = {
                                    if (isAdminLoggedIn) {
                                        viewModel.navigateTo(HubScreen.ADMIN_PANEL)
                                    } else {
                                        viewModel.navigateTo(HubScreen.ADMIN_LOGIN)
                                    }
                                },
                                onBackClick = { viewModel.navigateBack() }
                            )
                        }
                    },
                    bottomBar = {
                        if (showBottomBar) {
                            HubBottomBar(
                                currentScreen = currentScreen,
                                onTabSelected = { screen -> viewModel.navigateTo(screen) }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(if (showTopBar) innerPadding else androidx.compose.foundation.layout.PaddingValues())
                    ) {
                        when (currentScreen) {
                            HubScreen.HOME -> {
                                HomeScreen(
                                    featuredMovies = featuredMovies,
                                    trendingMovies = trendingMovies,
                                    latestReleases = latestReleases,
                                    allMovies = allMovies,
                                    recommendations = recommendations,
                                    watchlistMovieIds = watchlistIds,
                                    onMovieClick = { movie -> viewModel.openMovieDetails(movie) },
                                    onPlayClick = { movie -> viewModel.openPlayer(movie, audioOnly = false) },
                                    onWatchlistToggle = { movieId, isInList ->
                                        viewModel.toggleWatchlist(movieId, isInList)
                                    },
                                    onShareClick = { movie -> viewModel.setShareMovie(movie) },
                                    onGenreSelected = { genre ->
                                        viewModel.selectGenre(genre)
                                        viewModel.navigateTo(HubScreen.SEARCH)
                                    }
                                )
                            }

                            HubScreen.SEARCH -> {
                                SearchScreen(
                                    searchQuery = searchQuery,
                                    onQueryChange = { viewModel.updateSearchQuery(it) },
                                    selectedGenre = selectedGenre,
                                    onGenreSelect = { viewModel.selectGenre(it) },
                                    selectedQuality = selectedQualityFilter,
                                    onQualitySelect = { viewModel.selectQualityFilter(it) },
                                    selectedType = selectedTypeFilter,
                                    onTypeSelect = { viewModel.selectTypeFilter(it) },
                                    filteredMovies = filteredMovies,
                                    watchlistMovieIds = watchlistIds,
                                    onMovieClick = { movie -> viewModel.openMovieDetails(movie) },
                                    onWatchlistToggle = { movieId, isInList ->
                                        viewModel.toggleWatchlist(movieId, isInList)
                                    },
                                    onClearFilters = { viewModel.clearFilters() }
                                )
                            }

                            HubScreen.WATCHLIST -> {
                                WatchlistScreen(
                                    watchlistItems = watchlist,
                                    historyItems = viewingHistory,
                                    allMovies = allMovies,
                                    onMovieClick = { movie -> viewModel.openMovieDetails(movie) },
                                    onPlayClick = { movie -> viewModel.openPlayer(movie) },
                                    onRemoveFromWatchlist = { movieId -> viewModel.toggleWatchlist(movieId, true) },
                                    onClearHistory = { viewModel.clearHistory() },
                                    onExplore = { viewModel.navigateTo(HubScreen.HOME) }
                                )
                            }

                            HubScreen.DOWNLOADS -> {
                                DownloadsScreen(
                                    downloads = downloads,
                                    onPlayOffline = { movieId, isAudio ->
                                        val movie = allMovies.find { it.id == movieId } ?: selectedMovie
                                        if (movie != null) {
                                            viewModel.openPlayer(movie, audioOnly = isAudio)
                                        }
                                    },
                                    onPauseResume = { dl -> viewModel.pauseResumeDownload(dl) },
                                    onDeleteDownload = { dlId -> viewModel.deleteDownload(dlId) },
                                    onExploreCatalog = { viewModel.navigateTo(HubScreen.HOME) }
                                )
                            }

                            HubScreen.PROFILE -> {
                                ProfileScreen(
                                    userProfile = userProfile,
                                    isDarkTheme = isDarkTheme,
                                    isAdminLoggedIn = isAdminLoggedIn,
                                    onToggleDarkTheme = { viewModel.toggleDarkTheme() },
                                    onAdminLoginClick = { viewModel.navigateTo(HubScreen.ADMIN_LOGIN) },
                                    onAdminDashboardClick = { viewModel.navigateTo(HubScreen.ADMIN_PANEL) },
                                    onUpgradeTier = { tier -> viewModel.updateUserSubscription(tier) }
                                )
                            }

                            HubScreen.MOVIE_DETAILS -> {
                                if (selectedMovie != null) {
                                    val reviewsFlow = remember(selectedMovie!!.id) {
                                        viewModel.getReviewsForMovie(selectedMovie!!.id)
                                    }
                                    val reviewsForMovie by reviewsFlow.collectAsStateWithLifecycle(emptyList())

                                    MovieDetailsScreen(
                                        movie = selectedMovie!!,
                                        isInWatchlist = watchlistIds.contains(selectedMovie!!.id),
                                        reviews = reviewsForMovie,
                                        onBack = { viewModel.navigateBack() },
                                        onPlay = { isAudio -> viewModel.openPlayer(selectedMovie!!, audioOnly = isAudio) },
                                        onWatchlistToggle = {
                                            viewModel.toggleWatchlist(
                                                selectedMovie!!.id,
                                                watchlistIds.contains(selectedMovie!!.id)
                                            )
                                        },
                                        onDownloadClick = { quality ->
                                            viewModel.startDownload(selectedMovie!!, quality)
                                            viewModel.navigateTo(HubScreen.DOWNLOADS)
                                        },
                                        onShareClick = { viewModel.setShareMovie(selectedMovie!!) },
                                        onSubmitReview = { name, rating, comment ->
                                            viewModel.submitReview(selectedMovie!!.id, name, rating, comment)
                                        }
                                    )
                                }
                            }

                            HubScreen.PLAYER -> {
                                if (selectedMovie != null) {
                                    VideoPlayerScreen(
                                        movie = selectedMovie!!,
                                        isPlaying = isPlaying,
                                        playbackPositionMs = playbackPos,
                                        totalDurationMs = totalDuration,
                                        selectedQuality = selectedQuality,
                                        selectedSubtitle = selectedSubtitle,
                                        selectedAudioTrack = selectedAudioTrack,
                                        playbackSpeed = playbackSpeed,
                                        isAudioOnly = isAudioOnly,
                                        onBack = { viewModel.navigateBack() },
                                        onTogglePlayPause = { viewModel.togglePlayPause() },
                                        onSeek = { viewModel.seekTo(it) },
                                        onSeekForward = { viewModel.seekForward10s() },
                                        onSeekBackward = { viewModel.seekBackward10s() },
                                        onSelectQuality = { viewModel.setQuality(it) },
                                        onSelectSubtitle = { viewModel.setSubtitle(it) },
                                        onSelectAudioTrack = { viewModel.setAudioTrack(it) },
                                        onSelectSpeed = { viewModel.setPlaybackSpeed(it) },
                                        onToggleAudioOnly = { viewModel.toggleAudioOnly() }
                                    )
                                }
                            }

                            HubScreen.ADMIN_LOGIN -> {
                                AdminLoginScreen(
                                    targetEmail = viewModel.adminTargetEmail,
                                    targetPass = viewModel.adminTargetPassword,
                                    errorMessage = adminAuthError,
                                    onLogin = { email, pass -> viewModel.loginAdmin(email, pass) },
                                    onBack = { viewModel.navigateBack() }
                                )
                            }

                            HubScreen.ADMIN_PANEL -> {
                                AdminDashboardScreen(
                                    allMovies = allMovies,
                                    allReviews = allReviews,
                                    onSaveMovie = { movie -> viewModel.adminSaveMovie(movie) },
                                    onDeleteMovie = { movie -> viewModel.adminDeleteMovie(movie) },
                                    onModerateReview = { id, status -> viewModel.adminModerateReview(id, status) },
                                    onDeleteReview = { id -> viewModel.adminDeleteReview(id) },
                                    onResetCatalog = { viewModel.adminResetCatalog() },
                                    onSendAnnouncement = { title, msg -> viewModel.adminSendAnnouncement(title, msg) },
                                    onLogout = { viewModel.logoutAdmin() },
                                    onBack = { viewModel.navigateBack() }
                                )
                            }
                        }
                    }
                }

                // Share Dialog
                if (shareMovie != null) {
                    ShareMovieDialog(
                        movie = shareMovie!!,
                        onDismiss = { viewModel.setShareMovie(null) }
                    )
                }

                // Notifications Dialog
                if (showNotificationsSheet) {
                    NotificationSheet(
                        notifications = notifications,
                        onDismiss = { showNotificationsSheet = false },
                        onMarkAllRead = { viewModel.markAllNotificationsAsRead() },
                        onNotificationClick = { notif ->
                            viewModel.markNotificationAsRead(notif.id)
                            if (notif.movieId != null) {
                                val m = allMovies.find { it.id == notif.movieId }
                                if (m != null) {
                                    showNotificationsSheet = false
                                    viewModel.openMovieDetails(m)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
