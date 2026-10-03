package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieItem(
    @PrimaryKey
    val id: String,
    val title: String,
    val originalTitle: String = "",
    val type: String = "Movie", // "Movie" or "TV Series"
    val posterUrl: String = "",
    val bannerUrl: String = "",
    val description: String = "",
    val releaseYear: Int = 2026,
    val duration: String = "2h 15m",
    val imdbRating: Double = 8.5,
    val hubRating: Double = 9.2,
    val genres: String = "Action, Sci-Fi",
    val languages: String = "Dual Audio [Bengali + English], Hindi, Japanese",
    val qualities: String = "4K UHD, 1080p FHD, 720p HD, 480p SD",
    val videoStreamUrl: String = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
    val downloadUrl4K: String = "https://cdn.alexpikumovehub.com/dl/4k/",
    val downloadUrl1080p: String = "https://cdn.alexpikumovehub.com/dl/1080p/",
    val downloadUrl720p: String = "https://cdn.alexpikumovehub.com/dl/720p/",
    val fileSize: String = "2.4 GB",
    val availableSubtitles: String = "English, Bengali, Hindi, Spanish, French, Japanese",
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val isLatestRelease: Boolean = false,
    val addedTimestamp: Long = System.currentTimeMillis(),
    val viewCount: Int = 1240,
    val director: String = "Alex Piku Production",
    val cast: String = "Christopher Nolan, Cillian Murphy, Ryan Gosling, Ana de Armas",
    val ageRating: String = "PG-13"
)

@Entity(tableName = "watchlist")
data class WatchlistItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val movieId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "viewing_history")
data class ViewingHistoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val movieId: String,
    val watchedDurationSeconds: Long = 0,
    val totalDurationSeconds: Long = 7200,
    val lastWatchedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "offline_downloads")
data class OfflineDownloadItem(
    @PrimaryKey
    val downloadId: String,
    val movieId: String,
    val movieTitle: String,
    val posterUrl: String,
    val quality: String = "1080p FHD",
    val fileSizeBytes: Long = 2_147_483_648L, // ~2 GB
    val downloadedBytes: Long = 0L,
    val downloadStatus: String = "DOWNLOADING", // "DOWNLOADING", "PAUSED", "COMPLETED", "FAILED"
    val downloadSpeed: String = "12.8 MB/s",
    val audioTrack: String = "Dual Audio [Bengali + English]",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val movieId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val type: String = "NEW_RELEASE" // "NEW_RELEASE", "EPISODE", "RECOMMENDATION", "SYSTEM"
)

@Entity(tableName = "reviews")
data class ReviewItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val movieId: String,
    val userName: String,
    val userEmail: String,
    val rating: Float = 5.0f,
    val comment: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "APPROVED" // "APPROVED", "PENDING", "REJECTED"
)

data class UserProfile(
    val email: String = "guest@alexpikumovehub.com",
    val name: String = "Alex Member",
    val tier: String = "Cinema Ultra 4K", // "Free", "VIP Gold", "Cinema Ultra 4K"
    val isVip: Boolean = true,
    val preferredAudio: String = "Dual Audio [Bengali + English]",
    val preferredQuality: String = "1080p FHD",
    val preferredSubtitle: String = "English",
    val darkThemeMode: String = "OLED_BLACK", // "OLED_BLACK", "MIDNIGHT", "SYSTEM"
    val notifyOnReleases: Boolean = true,
    val offlineAudioOnly: Boolean = false
)

data class RecommendationMatch(
    val movie: MovieItem,
    val matchPercentage: Int,
    val reason: String
)
