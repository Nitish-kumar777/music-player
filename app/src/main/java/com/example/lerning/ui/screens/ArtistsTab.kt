package com.example.lerning.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lerning.model.AudioFile
import com.example.lerning.ui.components.ArtistItem

@Composable
fun ArtistsTab(
    audioFiles: List<AudioFile>,
    modifier: Modifier = Modifier
) {
    val artistsWithSongs = remember(audioFiles) {
        audioFiles.groupBy { it.artist }
            .toList()
            .sortedBy { it.first }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = rememberLazyListState(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(artistsWithSongs) { (artist, songs) ->
            ArtistItem(artist, songs)
        }
    }
}
