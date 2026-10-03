package com.example.data.repository

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.local.CatalogSeedData
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MovieRepository private constructor(context: Context) {

    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "alexpikumovehub_database.db"
    ).fallbackToDestructiveMigration().build()

    private val movieDao = database.movieDao()
    private val watchlistDao = database.watchlistDao()
    private val historyDao = database.historyDao()
    private val downloadDao = database.downloadDao()
    private val notificationDao = database.notificationDao()
    private val reviewDao = database.reviewDao()

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    init {
        repositoryScope.launch {
            if (movieDao.getMovieCount() == 0) {
                movieDao.insertMovies(CatalogSeedData.initialMovies)
                CatalogSeedData.initialNotifications.forEach { notificationDao.insertNotification(it) }
                CatalogSeedData.initialReviews.forEach { reviewDao.insertReview(it) }
            }
        }
    }

    // Movies
    val allMoviesFlow: Flow<List<MovieItem>> = movieDao.getAllMoviesFlow()
    val featuredMoviesFlow: Flow<List<MovieItem>> = movieDao.getFeaturedMoviesFlow()
    val trendingMoviesFlow: Flow<List<MovieItem>> = movieDao.getTrendingMoviesFlow()
    val latestReleasesFlow: Flow<List<MovieItem>> = movieDao.getLatestReleasesFlow()

    suspend fun getMovieById(id: String): MovieItem? = movieDao.getMovieById(id)

    suspend fun incrementViewCount(id: String) = movieDao.incrementViewCount(id)

    suspend fun saveMovie(movie: MovieItem) {
        movieDao.insertMovie(movie)
        // Also send notification of new release if enabled
        if (movie.isLatestRelease) {
            notificationDao.insertNotification(
                NotificationItem(
                    title = "🎬 New Premiere Added: ${movie.title}",
                    message = "${movie.title} (${movie.releaseYear}) is now available in ${movie.qualities} with ${movie.languages}.",
                    movieId = movie.id,
                    type = "NEW_RELEASE"
                )
            )
        }
    }

    suspend fun updateMovie(movie: MovieItem) = movieDao.updateMovie(movie)

    suspend fun deleteMovie(movie: MovieItem) = movieDao.deleteMovie(movie)

    suspend fun deleteMovieById(id: String) = movieDao.deleteMovieById(id)

    suspend fun resetCatalogToDefault() {
        movieDao.insertMovies(CatalogSeedData.initialMovies)
    }

    // Watchlist
    val watchlistFlow: Flow<List<WatchlistItem>> = watchlistDao.getWatchlistFlow()

    fun isInWatchlist(movieId: String): Flow<Boolean> = watchlistDao.isInWatchlistFlow(movieId)

    suspend fun toggleWatchlist(movieId: String, currentlyInWatchlist: Boolean) {
        if (currentlyInWatchlist) {
            watchlistDao.removeFromWatchlist(movieId)
        } else {
            watchlistDao.addToWatchlist(WatchlistItem(movieId = movieId))
        }
    }

    // History
    val historyFlow: Flow<List<ViewingHistoryItem>> = historyDao.getHistoryFlow()

    suspend fun recordHistory(movieId: String, watchedSeconds: Long, totalSeconds: Long) {
        historyDao.recordHistory(
            ViewingHistoryItem(
                movieId = movieId,
                watchedDurationSeconds = watchedSeconds,
                totalDurationSeconds = totalSeconds,
                lastWatchedTimestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun clearHistory() = historyDao.clearHistory()

    // Recommendation Engine based on viewing history
    fun getRecommendationsFlow(): Flow<List<RecommendationMatch>> {
        return combine(allMoviesFlow, historyFlow) { movies, history ->
            if (movies.isEmpty()) return@combine emptyList<RecommendationMatch>()

            // If user has no history yet, recommend highest rated and trending
            if (history.isEmpty()) {
                return@combine movies.sortedByDescending { it.imdbRating }.take(6).map { movie ->
                    RecommendationMatch(
                        movie = movie,
                        matchPercentage = ((movie.imdbRating / 10.0) * 100).toInt().coerceIn(80, 99),
                        reason = "Top Rated on ALEXPIKUMOVEHUB"
                    )
                }
            }

            // Extract genres and keywords from history
            val watchedMovieIds = history.map { it.movieId }.toSet()
            val watchedMovies = movies.filter { it.id in watchedMovieIds }
            val preferredGenres = watchedMovies
                .flatMap { it.genres.split(",").map { g -> g.trim().lowercase() } }
                .groupingBy { it }
                .eachCount()

            val unwatched = movies.filter { it.id !in watchedMovieIds }
            val pool = if (unwatched.isNotEmpty()) unwatched else movies

            pool.map { movie ->
                val movieGenres = movie.genres.split(",").map { it.trim().lowercase() }
                var genreScore = 0
                movieGenres.forEach { g ->
                    genreScore += preferredGenres[g] ?: 0
                }

                val baseScore = 65
                val genreBonus = (genreScore * 10).coerceAtMost(25)
                val ratingBonus = ((movie.imdbRating - 7.0).coerceAtLeast(0.0) * 4).toInt()
                val totalMatch = (baseScore + genreBonus + ratingBonus).coerceIn(75, 99)

                val matchedGenre = movieGenres.firstOrNull { preferredGenres.containsKey(it) }?.replaceFirstChar { it.uppercase() }
                val reason = if (matchedGenre != null) {
                    "Because you watch $matchedGenre"
                } else if (movie.isTrending) {
                    "Trending International Cinema"
                } else {
                    "98% Algorithm Match"
                }

                RecommendationMatch(
                    movie = movie,
                    matchPercentage = totalMatch,
                    reason = reason
                )
            }.sortedByDescending { it.matchPercentage }.take(8)
        }
    }

    // Downloads
    val downloadsFlow: Flow<List<OfflineDownloadItem>> = downloadDao.getAllDownloadsFlow()

    suspend fun startDownload(movie: MovieItem, quality: String) {
        val sizeBytes = when (quality) {
            "4K UHD" -> 4_800_000_000L
            "1080p FHD" -> 2_200_000_000L
            "720p HD" -> 980_000_000L
            else -> 500_000_000L
        }

        val downloadId = "dl_${movie.id}_${System.currentTimeMillis()}"
        val initialItem = OfflineDownloadItem(
            downloadId = downloadId,
            movieId = movie.id,
            movieTitle = movie.title,
            posterUrl = movie.posterUrl,
            quality = quality,
            fileSizeBytes = sizeBytes,
            downloadedBytes = 0L,
            downloadStatus = "DOWNLOADING",
            downloadSpeed = "18.4 MB/s"
        )
        downloadDao.insertDownload(initialItem)

        // Launch simulated background download progress
        repositoryScope.launch {
            var currentBytes = 0L
            val step = sizeBytes / 10
            while (currentBytes < sizeBytes) {
                delay(700)
                currentBytes = (currentBytes + step).coerceAtMost(sizeBytes)
                val status = if (currentBytes >= sizeBytes) "COMPLETED" else "DOWNLOADING"
                val speed = if (status == "COMPLETED") "0 MB/s" else "${(15..28).random()}.${(1..9).random()} MB/s"
                downloadDao.updateDownload(
                    initialItem.copy(
                        downloadedBytes = currentBytes,
                        downloadStatus = status,
                        downloadSpeed = speed
                    )
                )
                if (status == "COMPLETED") {
                    notificationDao.insertNotification(
                        NotificationItem(
                            title = "✅ Download Complete: ${movie.title}",
                            message = "${movie.title} ($quality) is ready for offline playback without internet.",
                            movieId = movie.id,
                            type = "SYSTEM"
                        )
                    )
                    break
                }
            }
        }
    }

    suspend fun pauseResumeDownload(item: OfflineDownloadItem) {
        val newStatus = if (item.downloadStatus == "DOWNLOADING") "PAUSED" else "DOWNLOADING"
        val updated = item.copy(downloadStatus = newStatus)
        downloadDao.updateDownload(updated)

        if (newStatus == "DOWNLOADING" && item.downloadedBytes < item.fileSizeBytes) {
            repositoryScope.launch {
                var currentBytes = item.downloadedBytes
                val step = item.fileSizeBytes / 10
                while (currentBytes < item.fileSizeBytes) {
                    delay(700)
                    currentBytes = (currentBytes + step).coerceAtMost(item.fileSizeBytes)
                    val status = if (currentBytes >= item.fileSizeBytes) "COMPLETED" else "DOWNLOADING"
                    downloadDao.updateDownload(
                        item.copy(
                            downloadedBytes = currentBytes,
                            downloadStatus = status
                        )
                    )
                    if (status == "COMPLETED") break
                }
            }
        }
    }

    suspend fun deleteDownload(downloadId: String) = downloadDao.deleteDownload(downloadId)

    // Notifications
    val notificationsFlow: Flow<List<NotificationItem>> = notificationDao.getNotificationsFlow()

    suspend fun markNotificationAsRead(id: Long) = notificationDao.markAsRead(id)
    suspend fun markAllNotificationsAsRead() = notificationDao.markAllAsRead()
    suspend fun addNotification(title: String, message: String, movieId: String? = null) {
        notificationDao.insertNotification(
            NotificationItem(title = title, message = message, movieId = movieId, type = "SYSTEM")
        )
    }

    // Reviews & Community Moderation
    fun getReviewsForMovie(movieId: String): Flow<List<ReviewItem>> = reviewDao.getApprovedReviewsForMovieFlow(movieId)
    val allReviewsFlow: Flow<List<ReviewItem>> = reviewDao.getAllReviewsFlow()

    suspend fun addReview(movieId: String, userName: String, rating: Float, comment: String) {
        reviewDao.insertReview(
            ReviewItem(
                movieId = movieId,
                userName = userName,
                userEmail = "$userName@user.hub",
                rating = rating,
                comment = comment,
                status = "APPROVED"
            )
        )
    }

    suspend fun updateReviewStatus(id: Long, status: String) = reviewDao.updateReviewStatus(id, status)
    suspend fun deleteReview(id: Long) = reviewDao.deleteReview(id)

    companion object {
        @Volatile
        private var INSTANCE: MovieRepository? = null

        fun getInstance(context: Context): MovieRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MovieRepository(context).also { INSTANCE = it }
            }
        }
    }
}
