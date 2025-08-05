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

class MusicViewModel : ViewModel() {

    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> = _songs

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

                // First try to load real songs from device
                val projection = arrayOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA
                )

                val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} > 10000"
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

                    while (it.moveToNext()) {
                        val id = it.getLong(idColumn)
                        val title = it.getString(titleColumn) ?: "Unknown Title"
                        val artist = it.getString(artistColumn) ?: "Unknown Artist"
                        val album = it.getString(albumColumn) ?: "Unknown Album"
                        val albumId = it.getLong(albumIdColumn)
                        val duration = it.getLong(durationColumn)
                        val data = it.getString(dataColumn) ?: ""

                        val song = Song(id, title, artist, album, albumId, duration, data)
                        songsList.add(song)
                    }
                }

                // If no real songs found, add demo songs
                if (songsList.isEmpty()) {
                    songsList.addAll(getDemoSongs())
                }

                // Add a small delay to show loading animation
                delay(1500)

                _songs.postValue(songsList)
            } catch (e: Exception) {
                e.printStackTrace()
                // If error occurred, show demo songs
                _songs.postValue(getDemoSongs())
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private fun getDemoSongs(): List<Song> {
        return listOf(
            Song(
                id = 1,
                title = "Tum Hi Ho",
                artist = "Arijit Singh",
                album = "Aashiqui 2",
                albumId = 1001,
                duration = 262000, // 4:22
                data = "/demo/tum_hi_ho.mp3"
            ),
            Song(
                id = 2,
                title = "Kesariya",
                artist = "Arijit Singh",
                album = "Brahmastra",
                albumId = 1002,
                duration = 298000, // 4:58
                data = "/demo/kesariya.mp3"
            ),
            Song(
                id = 3,
                title = "Apna Bana Le",
                artist = "Arijit Singh",
                album = "Bhediya",
                albumId = 1003,
                duration = 254000, // 4:14
                data = "/demo/apna_bana_le.mp3"
            ),
            Song(
                id = 4,
                title = "Raataan Lambiyan",
                artist = "Jubin Nautiyal",
                album = "Shershaah",
                albumId = 1004,
                duration = 276000, // 4:36
                data = "/demo/raataan_lambiyan.mp3"
            ),
            Song(
                id = 5,
                title = "Manike",
                artist = "Yohani",
                album = "Thank God",
                albumId = 1005,
                duration = 189000, // 3:09
                data = "/demo/manike.mp3"
            ),
            Song(
                id = 6,
                title = "Dil Diyan Gallan",
                artist = "Atif Aslam",
                album = "Tiger Zinda Hai",
                albumId = 1006,
                duration = 245000, // 4:05
                data = "/demo/dil_diyan_gallan.mp3"
            ),
            Song(
                id = 7,
                title = "Ve Maahi",
                artist = "Arijit Singh",
                album = "Kesari",
                albumId = 1007,
                duration = 267000, // 4:27
                data = "/demo/ve_maahi.mp3"
            ),
            Song(
                id = 8,
                title = "Pal Pal Dil Ke Paas",
                artist = "Arijit Singh",
                album = "Pal Pal Dil Ke Paas",
                albumId = 1008,
                duration = 293000, // 4:53
                data = "/demo/pal_pal.mp3"
            ),
            Song(
                id = 9,
                title = "Bekhayali",
                artist = "Sachet Tandon",
                album = "Kabir Singh",
                albumId = 1009,
                duration = 401000, // 6:41
                data = "/demo/bekhayali.mp3"
            ),
            Song(
                id = 10,
                title = "Ghungroo",
                artist = "Arijit Singh",
                album = "War",
                albumId = 1010,
                duration = 302000, // 5:02
                data = "/demo/ghungroo.mp3"
            ),
            Song(
                id = 11,
                title = "Malang Sajna",
                artist = "Sachet Tandon",
                album = "Malang",
                albumId = 1011,
                duration = 234000, // 3:54
                data = "/demo/malang_sajna.mp3"
            ),
            Song(
                id = 12,
                title = "Tujhe Kitna Chahne Lage",
                artist = "Arijit Singh",
                album = "Kabir Singh",
                albumId = 1012,
                duration = 287000, // 4:47
                data = "/demo/tujhe_kitna.mp3"
            ),
            Song(
                id = 13,
                title = "Pasoori",
                artist = "Ali Sethi",
                album = "Coke Studio",
                albumId = 1013,
                duration = 395000, // 6:35
                data = "/demo/pasoori.mp3"
            ),
            Song(
                id = 14,
                title = "Tera Ban Jaunga",
                artist = "Akhil Sachdeva",
                album = "Kabir Singh",
                albumId = 1014,
                duration = 356000, // 5:56
                data = "/demo/tera_ban_jaunga.mp3"
            ),
            Song(
                id = 15,
                title = "Kalank Title Track",
                artist = "Arijit Singh",
                album = "Kalank",
                albumId = 1015,
                duration = 278000, // 4:38
                data = "/demo/kalank.mp3"
            )
        )
    }
}