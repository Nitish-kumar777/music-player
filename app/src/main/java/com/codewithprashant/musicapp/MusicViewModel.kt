package com.codewithprashant.musicapp

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.chromium.base.Log


class MusicViewModel : ViewModel() {

    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> = _songs

    private val _popularSongs = MutableLiveData<List<Song>>()
    val popularSongs: LiveData<List<Song>> = _popularSongs

    private val _newCollection = MutableLiveData<List<Song>>()
    val newCollection: LiveData<List<Song>> = _newCollection

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _hasPermission = MutableLiveData<Boolean>()
    val hasPermission: LiveData<Boolean> = _hasPermission

    fun checkPermission(context: Context) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        val granted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        _hasPermission.value = granted


        if (granted) {
            loadSongs(context.contentResolver)
        }
        Log.d("MusicViewModel", "Permission granted: $granted")
        _hasPermission.value = granted
    }

    fun onPermissionGranted(context: Context) {
        _hasPermission.value = true
        loadSongs(context.contentResolver)
    }

    private fun loadSongs(contentResolver: ContentResolver) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)

            try {
                val songsList = mutableListOf<Song>()
                val popularSongsList = mutableListOf<Song>()
                val newCollectionList = mutableListOf<Song>()

                // Base projection
                val projection = arrayOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DATE_ADDED,
                    MediaStore.Audio.Media.YEAR,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) "play_count" else null
                ).filterNotNull().toTypedArray()

                val selection =
                    "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} > 10000"
                val sortOrder = "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"

                val cursor: Cursor? = contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    null,
                    sortOrder
                )

                cursor?.use {
                    val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                    val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                    val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                    val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                    val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                    val dateAddedColumn =
                        it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                    val yearColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)

                    // Get PLAY_COUNT column index
                    val playCountColumn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        it.getColumnIndex("play_count").takeIf { idx -> idx != -1 } ?: -1
                    } else {
                        -1
                    }

                    while (it.moveToNext()) {
                        val id = it.getLong(idColumn)
                        val title = it.getString(titleColumn) ?: "Unknown Title"
                        val artist = it.getString(artistColumn) ?: "Unknown Artist"
                        val album = it.getString(albumColumn) ?: "Unknown Album"
                        val albumId = it.getLong(albumIdColumn)
                        val duration = it.getLong(durationColumn)
                        val data = it.getString(dataColumn) ?: ""
                        val dateAdded = it.getLong(dateAddedColumn)
                        val year = it.getInt(yearColumn)

                        val playCount = if (playCountColumn != -1) {
                            it.getInt(playCountColumn)
                        } else {
                            0
                        }

                        songsList.add(
                            Song(
                                id = id,
                                title = title,
                                artist = artist,
                                album = album,
                                albumId = albumId,
                                duration = duration,
                                data = data,
                                dateAdded = dateAdded,
                                playCount = playCount,
                                year = year
                            )
                        )
                    }
                }

                // Determine popular songs
                popularSongsList.addAll(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && songsList.any { it.playCount > 0 }) {
                        songsList.sortedByDescending { it.playCount }.take(5)
                    } else {
                        songsList.sortedByDescending { it.year }.take(5)
                    }
                )

                // Determine new collection
                newCollectionList.addAll(
                    songsList.sortedByDescending { it.dateAdded }.take(5)
                )

                delay(1500) // For loading animation

                _songs.postValue(songsList)
                _popularSongs.postValue(popularSongsList)
                _newCollection.postValue(newCollectionList)
            } catch (e: Exception) {
                e.printStackTrace()
                _songs.postValue(emptyList())
                _popularSongs.postValue(emptyList())
                _newCollection.postValue(emptyList())
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}