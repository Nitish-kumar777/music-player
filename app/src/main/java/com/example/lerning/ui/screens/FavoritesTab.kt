package com.example.lerning.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
fun FavoritesTab(
    favorites: List<AudioFile>,
    onRemoveFavorite: (AudioFile) -> Unit
) {
    Column {
        favorites.forEach { song ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = song.title,
                    modifier = Modifier.weight(1f),
                    color = Color.White
                )
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Remove Favorite",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onRemoveFavorite(song) }
                )
            }
        }
    }
}
