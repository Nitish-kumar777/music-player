package com.example.lerning.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.lerning.model.AudioFile
import com.example.lerning.ui.utils.loadAudioFiles

@Composable
fun AudioPlayerScreen() {
    val context = LocalContext.current
    var audioFiles by remember { mutableStateOf(emptyList<AudioFile>()) }
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        audioFiles = loadAudioFiles(context)
    }

    Scaffold(
        bottomBar = {
            CustomBottomNav(selectedTab) { selectedTab = it }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF1A3E1A), Color(0xFF0F2E0F))
                ))
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> SongsTab(audioFiles)
                1 -> FavoritesTab(audioFiles)
                2 -> ArtistsTab(audioFiles)
            }
        }
    }
}


@Composable
fun CustomBottomNav(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF1A3E1A), Color(0xFF0F2E0F))
                    )
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icons = listOf(
                Icons.Default.Home,
                Icons.Default.Favorite,
                Icons.Default.Person
            )

            icons.forEachIndexed { index, icon ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (selectedTab == index) Color(0xFFB8FF4D) else Color.Transparent)
                        .clickable { onTabSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (selectedTab == index) Color.Black else Color(0xFFB8FF4D),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}