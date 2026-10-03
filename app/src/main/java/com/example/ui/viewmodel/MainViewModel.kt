package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.MovieRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class HubScreen {
    HOME,
    SEARCH,
    WATCHLIST,
    DOWNLOADS,
    PROFILE,
    ADMIN_LOGIN,
    ADMIN_PANEL,
    MOVIE_DETAILS,
    PLAYER
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MovieRepository.getInstance(application)

    // Navigation State
    private val _currentScreen = MutableStateFlow(HubScreen.HOME)
    val currentScreen: StateFlow<HubScreen> = _currentScreen.asStateFlow()

    private val screenBackStack = mutableListOf<HubScreen>()

    // Selected Movie for details / playing
    private val _selectedMovie = MutableStateFlow<MovieItem?>(null)
    val selectedMovie: StateFlow<MovieItem?> = _selectedMovie.asStateFlow()

    // Movies Flow
    val allMovies = repository.allMoviesFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val featuredMovies = repository.featuredMoviesFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val trendingMovies = repository.trendingMoviesFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val latestReleases = repository.latestReleasesFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val recommendations = repository.getRecommendationsFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Watchlist & History
    val watchlist = repository.watchlistFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val viewingHistory = repository.historyFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Downloads
    val downloads = repository.downloadsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications
    val notifications = repository.notificationsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val unreadNotificationCount = notifications.map { list -> list.count { !it.isRead } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Reviews
    val allReviews = repository.allReviewsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGenre = MutableStateFlow("All")
    val selectedGenre: StateFlow<String> = _selectedGenre.asStateFlow()

    private val _selectedQualityFilter = MutableStateFlow("All")
    val selectedQualityFilter: StateFlow<String> = _selectedQualityFilter.asStateFlow()

    private val _selectedTypeFilter = MutableStateFlow("All") // "All", "Movie", "TV Series"
    val selectedTypeFilter: StateFlow<String> = _selectedTypeFilter.asStateFlow()

    // Filtered Movies for Search Screen
    val filteredMovies = combine(
        allMovies,
        _searchQuery,
        _selectedGenre,
        _selectedQualityFilter,
        _selectedTypeFilter
    ) { movies, query, genre, quality, type ->
        movies.filter { movie ->
            val matchesQuery = query.isBlank() ||
                    movie.title.contains(query, ignoreCase = true) ||
                    movie.originalTitle.contains(query, ignoreCase = true) ||
                    movie.genres.contains(query, ignoreCase = true) ||
                    movie.languages.contains(query, ignoreCase = true) ||
                    movie.director.contains(query, ignoreCase = true) ||
                    movie.cast.contains(query, ignoreCase = true)

            val matchesGenre = genre == "All" || movie.genres.contains(genre, ignoreCase = true)
            val matchesQuality = quality == "All" || movie.qualities.contains(quality, ignoreCase = true)
            val matchesType = type == "All" || movie.type.equals(type, ignoreCase = true)

            matchesQuery && matchesGenre && matchesQuality && matchesType
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Player State
    private val _isPlaying = MutableStateFlow(true)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _playbackPositionMs = MutableStateFlow(0L)
    val playbackPositionMs: StateFlow<Long> = _playbackPositionMs.asStateFlow()

    private val _totalDurationMs = MutableStateFlow(7200000L) // 2 hours in ms
    val totalDurationMs: StateFlow<Long> = _totalDurationMs.asStateFlow()

    private val _selectedQuality = MutableStateFlow("4K UHD")
    val selectedQuality: StateFlow<String> = _selectedQuality.asStateFlow()

    private val _selectedSubtitle = MutableStateFlow("English")
    val selectedSubtitle: StateFlow<String> = _selectedSubtitle.asStateFlow()

    private val _selectedAudioTrack = MutableStateFlow("Dual Audio [Bengali + English]")
    val selectedAudioTrack: StateFlow<String> = _selectedAudioTrack.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _isAudioOnly = MutableStateFlow(false)
    val isAudioOnly: StateFlow<Boolean> = _isAudioOnly.asStateFlow()

    // User Profile & Settings
    private val _userProfile = MutableStateFlow(
        UserProfile(
            email = "pikubalur@gmail.com",
            name = "Piku Balur",
            tier = "Cinema Ultra 4K",
            isVip = true
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // Admin Credentials & Authentication
    // The exact credentials requested by user:
    // ADMIN PANEL LOGIN EMAIL ID: pikubalur@gmail.com
    // PASSWORD: Alexpiku@2006
    val adminTargetEmail = "pikubalur@gmail.com"
    val adminTargetPassword = "Alexpiku@2006"

    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    private val _adminAuthError = MutableStateFlow<String?>(null)
    val adminAuthError: StateFlow<String?> = _adminAuthError.asStateFlow()

    // Social Sharing
    private val _shareMovieDialog = MutableStateFlow<MovieItem?>(null)
    val shareMovieDialog: StateFlow<MovieItem?> = _shareMovieDialog.asStateFlow()

    // Navigation Controls
    fun navigateTo(screen: HubScreen) {
        if (_currentScreen.value != screen) {
            screenBackStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        return if (screenBackStack.isNotEmpty()) {
            _currentScreen.value = screenBackStack.removeAt(screenBackStack.size - 1)
            true
        } else if (_currentScreen.value != HubScreen.HOME) {
            _currentScreen.value = HubScreen.HOME
            true
        } else {
            false
        }
    }

    fun openMovieDetails(movie: MovieItem) {
        _selectedMovie.value = movie
        viewModelScope.launch {
            repository.incrementViewCount(movie.id)
        }
        navigateTo(HubScreen.MOVIE_DETAILS)
    }

    fun openPlayer(movie: MovieItem, audioOnly: Boolean = false) {
        _selectedMovie.value = movie
        _isAudioOnly.value = audioOnly
        _isPlaying.value = true
        _playbackPositionMs.value = 0L
        _playbackSpeed.value = 1.0f

        // Record history
        viewModelScope.launch {
            repository.recordHistory(movie.id, 120, 7200)
            repository.incrementViewCount(movie.id)
        }

        navigateTo(HubScreen.PLAYER)
    }

    // Search and Filter updates
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectGenre(genre: String) {
        _selectedGenre.value = genre
    }

    fun selectQualityFilter(quality: String) {
        _selectedQualityFilter.value = quality
    }

    fun selectTypeFilter(type: String) {
        _selectedTypeFilter.value = type
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedGenre.value = "All"
        _selectedQualityFilter.value = "All"
        _selectedTypeFilter.value = "All"
    }

    // Watchlist
    fun isMovieInWatchlist(movieId: String): Flow<Boolean> {
        return repository.isInWatchlist(movieId)
    }

    fun toggleWatchlist(movieId: String, currentlyInWatchlist: Boolean) {
        viewModelScope.launch {
            repository.toggleWatchlist(movieId, currentlyInWatchlist)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    // Player Controls
    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    fun seekTo(positionMs: Long) {
        _playbackPositionMs.value = positionMs.coerceIn(0L, _totalDurationMs.value)
    }

    fun seekForward10s() {
        seekTo(_playbackPositionMs.value + 10000L)
    }

    fun seekBackward10s() {
        seekTo(_playbackPositionMs.value - 10000L)
    }

    fun setQuality(quality: String) {
        _selectedQuality.value = quality
    }

    fun setSubtitle(subtitle: String) {
        _selectedSubtitle.value = subtitle
    }

    fun setAudioTrack(track: String) {
        _selectedAudioTrack.value = track
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
    }

    fun toggleAudioOnly() {
        _isAudioOnly.value = !_isAudioOnly.value
    }

    // Downloads
    fun startDownload(movie: MovieItem, quality: String = "1080p FHD") {
        viewModelScope.launch {
            repository.startDownload(movie, quality)
        }
    }

    fun pauseResumeDownload(item: OfflineDownloadItem) {
        viewModelScope.launch {
            repository.pauseResumeDownload(item)
        }
    }

    fun deleteDownload(downloadId: String) {
        viewModelScope.launch {
            repository.deleteDownload(downloadId)
        }
    }

    // Notifications
    fun markNotificationAsRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    // Reviews
    fun getReviewsForMovie(movieId: String): Flow<List<ReviewItem>> {
        return repository.getReviewsForMovie(movieId)
    }

    fun submitReview(movieId: String, userName: String, rating: Float, comment: String) {
        viewModelScope.launch {
            repository.addReview(movieId, userName, rating, comment)
        }
    }

    // Admin Authentication
    fun loginAdmin(emailInput: String, passwordInput: String): Boolean {
        if (emailInput.trim().equals(adminTargetEmail, ignoreCase = true) && passwordInput == adminTargetPassword) {
            _isAdminLoggedIn.value = true
            _adminAuthError.value = null
            navigateTo(HubScreen.ADMIN_PANEL)
            return true
        } else {
            _adminAuthError.value = "Invalid Admin Credentials. Ensure email is $adminTargetEmail and password matches."
            return false
        }
    }

    fun logoutAdmin() {
        _isAdminLoggedIn.value = false
        _currentScreen.value = HubScreen.HOME
    }

    // Admin Content Management
    fun adminSaveMovie(movie: MovieItem) {
        viewModelScope.launch {
            repository.saveMovie(movie)
        }
    }

    fun adminDeleteMovie(movie: MovieItem) {
        viewModelScope.launch {
            repository.deleteMovie(movie)
            if (_selectedMovie.value?.id == movie.id) {
                _selectedMovie.value = null
                _currentScreen.value = HubScreen.HOME
            }
        }
    }

    fun adminResetCatalog() {
        viewModelScope.launch {
            repository.resetCatalogToDefault()
        }
    }

    fun adminModerateReview(id: Long, status: String) {
        viewModelScope.launch {
            repository.updateReviewStatus(id, status)
        }
    }

    fun adminDeleteReview(id: Long) {
        viewModelScope.launch {
            repository.deleteReview(id)
        }
    }

    fun adminSendAnnouncement(title: String, message: String) {
        viewModelScope.launch {
            repository.addNotification(title, message)
        }
    }

    // Social Sharing
    fun setShareMovie(movie: MovieItem?) {
        _shareMovieDialog.value = movie
    }

    // Settings
    fun toggleDarkTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun updateUserSubscription(tier: String) {
        _userProfile.value = _userProfile.value.copy(
            tier = tier,
            isVip = tier != "Free"
        )
    }
}
