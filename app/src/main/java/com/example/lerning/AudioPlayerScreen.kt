package com.example.lerning

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter

@Composable
fun AudioPlayerScreen() {
    val context = LocalContext.current
    var audioFiles by remember { mutableStateOf(emptyList<AudioFile>()) }
    var hasPermission by remember { mutableStateOf(false) }
    var showRationale by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasPermission = isGranted
        if (isGranted) {
            audioFiles = loadAudioFiles(context)
        } else {
            showRationale = true
        }
    }

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    LaunchedEffect(Unit) {
        hasPermission = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) {
            permissionLauncher.launch(permission)
        } else {
            audioFiles = loadAudioFiles(context)
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.MusicNote, contentDescription = null) },
                    label = { Text("Songs") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text("Favorites") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Artists") }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
                    )
                )
                .padding(padding)
        ) {
            if (hasPermission) {
                if (audioFiles.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No audio files found")
                    }
                } else {
                    when (selectedTab) {
                        0 -> SongsTab(audioFiles)
                        1 -> FavoritesTab()
                        2 -> ArtistsTab()
                    }
                }
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (showRationale) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Permission is required to access audio files.")
                            Text("Please grant the permission in app settings.")
                        }
                    } else {
                        Text("Requesting permission...")
                    }
                }
            }
        }
    }
}

private fun loadAudioFiles(context: android.content.Context): List<AudioFile> {
    val audioList = mutableListOf<AudioFile>()

    try {
        val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val album = cursor.getString(albumColumn)
                val duration = cursor.getLong(durationColumn)
                val albumId = cursor.getLong(albumIdColumn)

                val contentUri: Uri = Uri.withAppendedPath(collection, id.toString())
                val albumArtUri = Uri.parse("content://media/external/audio/albumart/$albumId")

                audioList.add(AudioFile(id, title, artist, album, duration, contentUri, albumArtUri))
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return audioList
}

@Composable
fun AudioFileItem(audioFile: AudioFile) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(audioFile.albumArtUri),
            contentDescription = "Album Art",
            modifier = Modifier
                .size(60.dp)
                .padding(end = 8.dp)
        )
        Column {
            Text(text = audioFile.title, style = MaterialTheme.typography.titleMedium)
            Text(text = "Artist: ${audioFile.artist ?: "Unknown"}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Album: ${audioFile.album ?: "Unknown"}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Duration: ${audioFile.duration / 1000}s", style = MaterialTheme.typography.bodySmall)
        }
    }
    Divider()
}

@Composable
fun SongsTab(audioFiles: List<AudioFile>) {
    Column {
        Text("Recently Played", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
        LazyRow {
            items(audioFiles.take(5)) { song ->
                RecentlyPlayedItem(song)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("All Songs", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
        LazyColumn {
            items(audioFiles) { song ->
                SongListItem(song)
            }
        }
    }
}

@Composable
fun RecentlyPlayedItem(song: AudioFile) {
    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(song.albumArtUri),
            contentDescription = song.title,
            modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Text(song.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun SongListItem(song: AudioFile) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Image(
            painter = rememberAsyncImagePainter(song.albumArtUri),
            contentDescription = song.title,
            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(song.title, style = MaterialTheme.typography.bodyLarge)
            Text(song.artist ?: "Unknown", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun FavoritesTab() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Favorites will be shown here")
    }
}

@Composable
fun ArtistsTab() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Artists will be shown here")
    }
}

