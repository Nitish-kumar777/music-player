package com.codewithprashant.musicapp

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val duration: Long,
    val data: String,
    val dateAdded: Long = 0L,
    val playCount: Int = 0,
    val year: Int = 0
) {
    fun getFormattedDuration(): String {
        val seconds = (duration / 1000) % 60
        val minutes = (duration / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}