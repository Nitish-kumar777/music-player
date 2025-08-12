package com.example.lerning.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.lerning.model.AudioFile
import com.example.lerning.ui.components.SongListItem

@Composable
fun FavoritesTab(audioFiles: List<AudioFile>) {
    val favoriteSongs = remember(audioFiles) { audioFiles.take(3) }

    if (favoriteSongs.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No favorites yet", color = Color.White)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(favoriteSongs) { song ->
                SongListItem(song, modifier = Modifier.padding(vertical = 8.dp))
                Divider(color = Color.Gray.copy(alpha = 0.2f))
            }
        }
    }
}