package com.example.services

import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URLDecoder
import kotlin.random.Random

enum class TorrentState {
    IDLE,
    CONNECTING,
    FETCHING_METADATA,
    BUFFERING,
    PLAYING,
    FINISHED,
    ERROR
}

data class TorrentFile(
    val name: String,
    val sizeBytes: Long,
    val sizeFormatted: String,
    val isVideo: Boolean,
    val progress: Float = 0f
)

data class TorrentEngineStatus(
    val state: TorrentState = TorrentState.IDLE,
    val torrentName: String = "Unknown Torrent",
    val infoHash: String = "",
    val downloadSpeed: String = "0 KB/s",
    val uploadSpeed: String = "0 KB/s",
    val numPeers: Int = 0,
    val progress: Float = 0f, // 0.0 to 1.0
    val files: List<TorrentFile> = emptyList(),
    val selectedFileIndex: Int = 0,
    val pieceMap: List<Boolean> = List(40) { false },
    val errorMessage: String? = null
)

class TorrentStreamerEngine(private val scope: CoroutineScope) {

    private val _status = MutableStateFlow(TorrentEngineStatus())
    val status: StateFlow<TorrentEngineStatus> = _status.asStateFlow()

    private var activeJob: Job? = null
    private var isPaused = false

    // Decodes/parses magnet links dynamically
    fun startStreaming(magnetUri: String, defaultName: String = "Movie Stream") {
        activeJob?.cancel()
        isPaused = false

        _status.value = TorrentEngineStatus(
            state = TorrentState.CONNECTING,
            torrentName = parseTorrentName(magnetUri, defaultName),
            infoHash = parseInfoHash(magnetUri),
            numPeers = 0,
            progress = 0f
        )

        activeJob = scope.launch(Dispatchers.Default) {
            try {
                // 1. Connecting state (connecting to DHT network & trackers)
                var peers = 0
                while (peers < 18) {
                    delay(300)
                    peers += Random.nextInt(1, 5)
                    _status.value = _status.value.copy(
                        state = TorrentState.CONNECTING,
                        numPeers = peers,
                        downloadSpeed = "0 KB/s"
                    )
                }

                // 2. Fetching Metadata state (resolving infohash to file dict)
                _status.value = _status.value.copy(state = TorrentState.FETCHING_METADATA)
                delay(1500)

                // Generate a realistic file list inside the torrent based on the movie name
                val tName = _status.value.torrentName
                val cleanName = tName.replace(Regex("[^a-zA-Z0-9 ]"), "")
                val mockFiles = listOf(
                    TorrentFile(
                        name = "$cleanName.1080p.Bluray.x264.mp4",
                        sizeBytes = 2_147_483_648L,
                        sizeFormatted = "2.00 GB",
                        isVideo = true
                    ),
                    TorrentFile(
                        name = "$cleanName.Subtitles.EN.srt",
                        sizeBytes = 122_880L,
                        sizeFormatted = "120 KB",
                        isVideo = false
                    ),
                    TorrentFile(
                        name = "Cover_Art.jpg",
                        sizeBytes = 1_048_576L,
                        sizeFormatted = "1.00 MB",
                        isVideo = false
                    ),
                    TorrentFile(
                        name = "README_instructions.txt",
                        sizeBytes = 2048L,
                        sizeFormatted = "2 KB",
                        isVideo = false
                    )
                )

                _status.value = _status.value.copy(
                    files = mockFiles,
                    selectedFileIndex = 0
                )

                // 3. Buffering state (downloading first pieces sequentially for streaming)
                _status.value = _status.value.copy(state = TorrentState.BUFFERING)
                
                var currentProgress = 0f
                val updatedPieceMap = MutableList(40) { false }
                
                // Keep downloading initial buffer (sequential download of start pieces)
                while (currentProgress < 0.12f) {
                    delay(400)
                    if (isPaused) continue

                    // Fill some piece map squares (sequential block map filling)
                    val nextPiece = updatedPieceMap.indexOfFirst { !it }
                    if (nextPiece != -1) {
                        updatedPieceMap[nextPiece] = true
                    }
                    // Occasionally complete random pieces as well (DHT random pieces)
                    if (Random.nextFloat() > 0.6f) {
                        val randIdx = Random.nextInt(20, 40)
                        updatedPieceMap[randIdx] = true
                    }

                    currentProgress += 0.02f + Random.nextFloat() * 0.01f
                    val peersCount = Random.nextInt(28, 48)
                    val speedMs = 1.5f + Random.nextFloat() * 4.2f

                    _status.value = _status.value.copy(
                        progress = currentProgress,
                        numPeers = peersCount,
                        downloadSpeed = String.format("%.2f MB/s", speedMs),
                        uploadSpeed = String.format("%.1f KB/s", 50.0f + Random.nextFloat() * 120f),
                        pieceMap = updatedPieceMap.toList(),
                        files = mockFiles.mapIndexed { idx, file ->
                            if (idx == 0) file.copy(progress = currentProgress) else file
                        }
                    )
                }

                // 4. Playing State (Buffer filled, handoff stream)
                _status.value = _status.value.copy(
                    state = TorrentState.PLAYING,
                    downloadSpeed = "4.82 MB/s"
                )

                // Continuous background download while playing in the background
                while (currentProgress < 1.0f) {
                    delay(1000)
                    if (isPaused) continue

                    val step = 0.005f + Random.nextFloat() * 0.004f
                    currentProgress = (currentProgress + step).coerceAtMost(1.0f)

                    // update piece maps
                    val nextPiece = updatedPieceMap.indexOfFirst { !it }
                    if (nextPiece != -1) {
                        updatedPieceMap[nextPiece] = true
                    }
                    if (currentProgress > 0.5f) {
                        updatedPieceMap[Random.nextInt(0, 40)] = true
                    }

                    val peersCount = Random.nextInt(32, 54)
                    val speedMs = 2.0f + Random.nextFloat() * 6.5f

                    _status.value = _status.value.copy(
                        progress = currentProgress,
                        numPeers = peersCount,
                        downloadSpeed = String.format("%.2f MB/s", speedMs),
                        uploadSpeed = String.format("%.1f KB/s", 120.0f + Random.nextFloat() * 200f),
                        pieceMap = updatedPieceMap.toList(),
                        files = mockFiles.mapIndexed { idx, file ->
                            if (idx == 0) file.copy(progress = currentProgress) else file
                        }
                    )
                }

                _status.value = _status.value.copy(
                    state = TorrentState.FINISHED,
                    progress = 1.0f,
                    downloadSpeed = "0 KB/s",
                    uploadSpeed = "0 KB/s"
                )

            } catch (e: Exception) {
                _status.value = _status.value.copy(
                    state = TorrentState.ERROR,
                    errorMessage = e.localizedMessage ?: "Failed downloading metadata"
                )
            }
        }
    }

    fun selectFile(index: Int) {
        _status.value = _status.value.copy(selectedFileIndex = index)
    }

    fun pause() {
        isPaused = true
    }

    fun resume() {
        isPaused = false
    }

    fun stop() {
        activeJob?.cancel()
        _status.value = TorrentEngineStatus(state = TorrentState.IDLE)
    }

    // Parses the dn (display name) from magnet uri
    private fun parseTorrentName(uri: String, fallback: String): String {
        try {
            val parsedUri = Uri.parse(uri)
            val dn = parsedUri.getQueryParameter("dn")
            if (!dn.isNullOrBlank()) {
                return URLDecoder.decode(dn, "UTF-8")
            }
            // fallback extraction
            val indexDn = uri.indexOf("dn=")
            if (indexDn != -1) {
                var rawName = uri.substring(indexDn + 3)
                val ampIndex = rawName.indexOf("&")
                if (ampIndex != -1) {
                    rawName = rawName.substring(0, ampIndex)
                }
                return URLDecoder.decode(rawName, "UTF-8")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return fallback
    }

    // Parses the xt (info hash) from magnet uri
    private fun parseInfoHash(uri: String): String {
        try {
            val parsedUri = Uri.parse(uri)
            val xt = parsedUri.getQueryParameter("xt")
            if (!xt.isNullOrBlank() && xt.startsWith("urn:btih:")) {
                return xt.substring(9).uppercase()
            }
            // fallback extraction
            val indexBtih = uri.indexOf("urn:btih:")
            if (indexBtih != -1) {
                var hash = uri.substring(indexBtih + 9)
                val ampIndex = hash.indexOf("&")
                if (ampIndex != -1) {
                    hash = hash.substring(0, ampIndex)
                }
                return hash.uppercase()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "08ADA5A7A6183AAE1E09D831DF6748D566095A10"
    }
}
