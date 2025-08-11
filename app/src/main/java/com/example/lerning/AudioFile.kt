package com.example.lerning

import android.net.Uri

data class AudioFile(
    val id: Long,
    val title: String,
    val artist: String?,
    val album: String?,
    val duration: Long,
    val uri: Uri,
    val albumArtUri: Uri?
)
