package com.example.lerning.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.lerning.model.AudioFile
import com.example.lerning.ui.components.RecentlyPlayedItem
import com.example.lerning.ui.components.SearchBar
import com.example.lerning.ui.components.SongListItem

@Composable
fun SongsTab(
    audioFiles: List<AudioFile>,
    favorites: List<AudioFile>,
    onToggleFavorite: (AudioFile) -> Unit
) {
    var query by remember { mutableStateOf("") }

    // Filter songs by title or artist
    val filteredSongs = remember(query, audioFiles) {
        if (query.isBlank()) audioFiles
        else audioFiles.filter { song ->
            song.title.contains(query, ignoreCase = true) ||
                    song.artist.contains(query, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar
        SearchBar(
            query = query,
            onQueryChange = { query = it }
        )

        // Recently Played Section
        Text(
            "RECENTLY PLAYED",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            modifier = Modifier.padding(bottom = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(audioFiles.take(3)) { song ->
                RecentlyPlayedItem(song)
            }
        }

        // Songs List Section
        Text(
            "YOUR SONGS",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyColumn(
            state = rememberLazyListState(),
            contentPadding = PaddingValues(bottom = 80.dp, start = 16.dp, end = 16.dp)
        ) {
            items(filteredSongs) { song ->
                SongListItem(
                    song = song,
                    isFavorite = favorites.contains(song),
                    onToggleFavorite = { onToggleFavorite(song) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                Divider(color = Color.Gray.copy(alpha = 0.2f))
            }
        }
    }
}
