package com.codewithprashant.musicapp

import android.content.ContentUris
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert // Import MoreVert icon
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun SongItem(
    song: Song,
    onSongClick: (Song) -> Unit,
    onPlayClick: (Song) -> Unit,
    onMoreOptionsClick: (Song) -> Unit // New callback for options
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onSongClick(song) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album Art
            Card(
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                val albumArtUri = ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    song.albumId
                )
                if (song.data.startsWith("/demo/")) {
                    val colors = listOf(
                        Color(0xFF6200EA), Color(0xFF00BCD4), Color(0xFF4CAF50),
                        Color(0xFFFF5722), Color(0xFF3F51B5), Color(0xFFE91E63),
                        Color(0xFF9C27B0), Color(0xFF2196F3), Color(0xFFFF9800),
                        Color(0xFF795548), Color(0xFF607D8B), Color(0xFFFFEB3B),
                        Color(0xFF8BC34A), Color(0xFFCDDC39), Color(0xFFFFC107)
                    )
                    val colorIndex = (song.id % colors.size).toInt()
                    val backgroundColor = colors[colorIndex]
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(backgroundColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.MusicNote,
                            contentDescription = "Album Art",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = albumArtUri,
                            contentDescription = "Album Art",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            error = ColorPainter(MaterialTheme.colorScheme.primary)
                        )
                        Icon(
                            Icons.Default.MusicNote,
                            contentDescription = "Music",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            // Song Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = song.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = song.artist,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = song.getFormattedDuration(),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Play Button
            IconButton(
                onClick = { onPlayClick(song) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            // More Options Button
            IconButton(
                onClick = { onMoreOptionsClick(song) }, // Call the new callback
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.MoreVert, // Use MoreVert icon
                    contentDescription = "More options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant // Or another appropriate color
                )
            }
        }
    }
}

@Composable
fun SongsList(
    songs: List<Song>,
    onSongClick: (Song) -> Unit,
    onPlayClick: (Song) -> Unit,
    onMoreOptionsClick: (Song) -> Unit // Pass callback to SongsList
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(songs) { song ->
            SongItem(
                song = song,
                onSongClick = onSongClick,
                onPlayClick = onPlayClick,
                onMoreOptionsClick = onMoreOptionsClick // Pass to SongItem
            )
        }
    }
}
// ... (rest of the file remains the same)