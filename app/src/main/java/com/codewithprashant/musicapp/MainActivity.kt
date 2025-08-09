package com.codewithprashant.musicapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codewithprashant.musicapp.ui.theme.MusicAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MusicApp()
                }
            }
        }
    }
}

@Composable
fun MusicApp(viewModel: MusicViewModel = viewModel()) {
    val context = LocalContext.current
    val songs by viewModel.songs.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val hasPermission by viewModel.hasPermission.observeAsState(false)

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.onPermissionGranted(context)
        } else {
            Toast.makeText(context, "Permission denied!", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkPermission(context)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "My Music",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Text(
                    text = when {
                        !hasPermission -> "Permission required"
                        isLoading -> "Loading songs..."
                        songs.isEmpty() -> "No songs found"
                        else -> "${songs.size} songs"
                    },
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when {
                !hasPermission -> {
                    PermissionDeniedScreen {
                        permissionLauncher.launch(permission)
                    }
                }
                isLoading -> {
                    LoadingScreen()
                }
                songs.isEmpty() -> {
                    EmptyState()
                }
                else -> {
                    SongsList(
                        songs = songs,
                        onSongClick = { song ->
                            Toast.makeText(
                                context,
                                "Playing: ${song.title}",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onMoreOptionsClick = { song ->
                            // Implement what should happen when more options is clicked
                            // For example, show a toast or a menu
                            Toast.makeText(
                                context,
                                "More options for: ${song.title}",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onPlayClick = TODO()
                    )
                }
            }
        }
    }
}