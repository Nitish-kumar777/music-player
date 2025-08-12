package com.example.lerning.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.lerning.model.AudioFile
import com.example.lerning.ui.components.RecentlyPlayedItem
import com.example.lerning.ui.components.SearchBar
import com.example.lerning.ui.components.SongListItem

@Composable
fun SongsTab(audioFiles: List<AudioFile>) {
    Column {
        SearchBar()

        Text(
            "RECENTLY PLAYED",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        LazyRow(
            modifier = Modifier.padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(audioFiles.take(3)) { song ->
                RecentlyPlayedItem(song)
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "YOUR SONGS",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            items(audioFiles) { song ->
                SongListItem(song, modifier = Modifier.padding(vertical = 8.dp))
                Divider(color = Color.Gray.copy(alpha = 0.2f))
            }
        }
    }
}