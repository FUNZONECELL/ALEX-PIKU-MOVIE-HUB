package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY addedTimestamp DESC")
    fun getAllMoviesFlow(): Flow<List<MovieItem>>

    @Query("SELECT * FROM movies WHERE id = :id LIMIT 1")
    suspend fun getMovieById(id: String): MovieItem?

    @Query("SELECT * FROM movies WHERE isFeatured = 1 ORDER BY addedTimestamp DESC")
    fun getFeaturedMoviesFlow(): Flow<List<MovieItem>>

    @Query("SELECT * FROM movies WHERE isTrending = 1 ORDER BY viewCount DESC")
    fun getTrendingMoviesFlow(): Flow<List<MovieItem>>

    @Query("SELECT * FROM movies WHERE isLatestRelease = 1 ORDER BY addedTimestamp DESC")
    fun getLatestReleasesFlow(): Flow<List<MovieItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieItem)

    @Update
    suspend fun updateMovie(movie: MovieItem)

    @Delete
    suspend fun deleteMovie(movie: MovieItem)

    @Query("DELETE FROM movies WHERE id = :id")
    suspend fun deleteMovieById(id: String)

    @Query("UPDATE movies SET viewCount = viewCount + 1 WHERE id = :id")
    suspend fun incrementViewCount(id: String)

    @Query("SELECT COUNT(*) FROM movies")
    suspend fun getMovieCount(): Int
}

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist ORDER BY addedAt DESC")
    fun getWatchlistFlow(): Flow<List<WatchlistItem>>

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE movieId = :movieId)")
    fun isInWatchlistFlow(movieId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWatchlist(item: WatchlistItem)

    @Query("DELETE FROM watchlist WHERE movieId = :movieId")
    suspend fun removeFromWatchlist(movieId: String)

    @Query("DELETE FROM watchlist")
    suspend fun clearWatchlist()
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM viewing_history ORDER BY lastWatchedTimestamp DESC")
    fun getHistoryFlow(): Flow<List<ViewingHistoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordHistory(item: ViewingHistoryItem)

    @Query("DELETE FROM viewing_history WHERE movieId = :movieId")
    suspend fun deleteHistoryForMovie(movieId: String)

    @Query("DELETE FROM viewing_history")
    suspend fun clearHistory()
}

@Dao
interface DownloadDao {
    @Query("SELECT * FROM offline_downloads ORDER BY createdAt DESC")
    fun getAllDownloadsFlow(): Flow<List<OfflineDownloadItem>>

    @Query("SELECT * FROM offline_downloads WHERE movieId = :movieId LIMIT 1")
    suspend fun getDownloadByMovieId(movieId: String): OfflineDownloadItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(item: OfflineDownloadItem)

    @Update
    suspend fun updateDownload(item: OfflineDownloadItem)

    @Query("DELETE FROM offline_downloads WHERE downloadId = :downloadId")
    suspend fun deleteDownload(downloadId: String)

    @Query("DELETE FROM offline_downloads")
    suspend fun clearAllDownloads()
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getNotificationsFlow(): Flow<List<NotificationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(item: NotificationItem)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: Long)

    @Query("DELETE FROM notifications")
    suspend fun clearAll()
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE movieId = :movieId AND status = 'APPROVED' ORDER BY timestamp DESC")
    fun getApprovedReviewsForMovieFlow(movieId: String): Flow<List<ReviewItem>>

    @Query("SELECT * FROM reviews ORDER BY timestamp DESC")
    fun getAllReviewsFlow(): Flow<List<ReviewItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(item: ReviewItem)

    @Query("UPDATE reviews SET status = :status WHERE id = :id")
    suspend fun updateReviewStatus(id: Long, status: String)

    @Query("DELETE FROM reviews WHERE id = :id")
    suspend fun deleteReview(id: Long)
}

@Database(
    entities = [
        MovieItem::class,
        WatchlistItem::class,
        ViewingHistoryItem::class,
        OfflineDownloadItem::class,
        NotificationItem::class,
        ReviewItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun watchlistDao(): WatchlistDao
    abstract fun historyDao(): HistoryDao
    abstract fun downloadDao(): DownloadDao
    abstract fun notificationDao(): NotificationDao
    abstract fun reviewDao(): ReviewDao
}
