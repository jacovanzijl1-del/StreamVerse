package com.example.ui.player

import android.media.MediaPlayer
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.collectAsState
import com.example.services.TorrentStreamerEngine
import com.example.services.TorrentState
import com.example.services.TorrentFile
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Info
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.shadow
import com.example.ui.theme.StreamGold
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.example.data.model.StreamSource
import com.example.services.SourceEngine
import com.example.ui.theme.StreamBackground
import com.example.ui.theme.StreamCard
import com.example.ui.theme.StreamGold
import com.example.ui.theme.StreamGreen
import com.example.ui.theme.StreamPrimary
import com.example.ui.theme.StreamSecondary
import com.example.ui.theme.StreamTextMuted
import com.example.ui.theme.StreamTextPrimary
import com.example.ui.theme.StreamTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StreamPlayerView(
    title: String,
    subtitle: String? = null,
    source: StreamSource,
    allSources: List<StreamSource> = emptyList(),
    initialPositionMs: Long = 0L,
    hasNextEpisode: Boolean = false,
    hasPrevEpisode: Boolean = false,
    onNextEpisode: () -> Unit = {},
    onPrevEpisode: () -> Unit = {},
    onSourceSelected: (StreamSource) -> Unit = {},
    onProgressUpdate: (currentMs: Long, durationMs: Long, isFinished: Boolean) -> Unit = { _, _, _ -> },
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isPlaying by remember { mutableStateOf(true) }
    var isBuffering by remember { mutableStateOf(true) }
    var currentPos by remember { mutableLongStateOf(initialPositionMs) }
    var duration by remember { mutableLongStateOf(0L) }
    var controlsVisible by remember { mutableStateOf(true) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var isFullscreen by remember { mutableStateOf(false) }

    // Dialog sheets
    var showSourceSelector by remember { mutableStateOf(false) }
    var showSpeedDialog by remember { mutableStateOf(false) }
    var showSubtitlesDialog by remember { mutableStateOf(false) }
    var selectedSubtitle by remember { mutableStateOf("Off") }

    // Autoplay countdown
    var showNextEpisodePrompt by remember { mutableStateOf(false) }
    var countdownSeconds by remember { mutableIntStateOf(5) }

    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
    var mediaPlayerRef by remember { mutableStateOf<MediaPlayer?>(null) }

    var currentStreamUrl by remember(source) { mutableStateOf(source.streamUrl) }
    var isMagnetLink by remember(currentStreamUrl) { mutableStateOf(currentStreamUrl.startsWith("magnet:")) }
    val isWebEmbed = remember(currentStreamUrl, source) {
        source.sourceType == "Web Embed" ||
        currentStreamUrl.contains("player.vidzee.wtf") ||
        currentStreamUrl.contains("willow.arlen.icu") ||
        currentStreamUrl.contains("vidrock.net") ||
        currentStreamUrl.contains("vidapi.xyz") ||
        currentStreamUrl.contains("spencerdevs.xyz") ||
        currentStreamUrl.contains("videasy") ||
        currentStreamUrl.contains("vidfast") ||
        currentStreamUrl.contains("vidify") ||
        (currentStreamUrl.startsWith("http") && currentStreamUrl.contains("/embed/"))
    }
    var playbackErrorMessage by remember { mutableStateOf<String?>(null) }
    var retryCount by remember { mutableIntStateOf(0) }

    val torrentEngine = remember { TorrentStreamerEngine(scope) }
    val torrentStatus by torrentEngine.status.collectAsState()
    var isTorrentHudExpanded by remember { mutableStateOf(false) }
    var actualVideoUrl by remember(currentStreamUrl) { mutableStateOf("") }
    var webViewRef by remember { mutableStateOf<android.webkit.WebView?>(null) }

    val activity = context as? android.app.Activity
    var isLandscape by remember { mutableStateOf(false) }
    var isUserSeeking by remember { mutableStateOf(false) }
    var seekFraction by remember { mutableFloatStateOf(0f) }

    fun toggleOrientation() {
        val newMode = if (isLandscape) {
            android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }
        isLandscape = !isLandscape
        try {
            activity?.requestedOrientation = newMode
        } catch (_: Exception) {}
    }

    fun launchExternalPlayer(url: String) {
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
            if (url.startsWith("http")) {
                setDataAndType(Uri.parse(url), "video/*")
            }
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            android.widget.Toast.makeText(context, "No external app found to play this stream", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(currentStreamUrl) {
        if (currentStreamUrl.startsWith("magnet:")) {
            isMagnetLink = true
            torrentEngine.startStreaming(currentStreamUrl, defaultName = title)
        } else {
            isMagnetLink = false
            torrentEngine.stop()
            actualVideoUrl = currentStreamUrl
        }
    }

    LaunchedEffect(torrentStatus.state, currentStreamUrl) {
        if (currentStreamUrl.startsWith("magnet:")) {
            if (torrentStatus.state == TorrentState.PLAYING || torrentStatus.state == TorrentState.FINISHED) {
                actualVideoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"
            } else {
                actualVideoUrl = ""
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                activity?.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            } catch (_: Exception) {}
            try {
                torrentEngine.stop()
            } catch (_: Exception) {}
            try {
                videoViewRef?.stopPlayback()
                videoViewRef = null
            } catch (_: Exception) {}
            try {
                webViewRef?.stopLoading()
                webViewRef?.loadUrl("about:blank")
                webViewRef?.destroy()
                webViewRef = null
            } catch (_: Exception) {}
        }
    }

    // Dynamic stream loading on source change or retry
    LaunchedEffect(actualVideoUrl, retryCount) {
        if (actualVideoUrl.isBlank()) {
            try {
                videoViewRef?.stopPlayback()
            } catch (_: Exception) {}
            return@LaunchedEffect
        }
        videoViewRef?.let { vv ->
            isBuffering = true
            try {
                // setVideoURI handles releasing the previous player safely without needing stopPlayback()
                val headers = mapOf("User-Agent" to "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36")
                vv.setVideoURI(Uri.parse(actualVideoUrl), headers)
                if (currentPos > 0) {
                    vv.seekTo(currentPos.toInt())
                }
                vv.start()
            } catch (e: Exception) {
                isBuffering = false
                playbackErrorMessage = "Error loading stream: ${e.localizedMessage ?: "Unknown"}"
            }
        }
    }

    // Auto-hide controls timer
    LaunchedEffect(controlsVisible, isPlaying) {
        if (controlsVisible && isPlaying) {
            delay(4000)
            controlsVisible = false
        }
    }

    // Progress polling loop
    LaunchedEffect(videoViewRef) {
        while (isActive) {
            videoViewRef?.let { vv ->
                if (vv.isPlaying) {
                    val pos = vv.currentPosition.toLong()
                    val dur = vv.duration.toLong().coerceAtLeast(0L)
                    currentPos = pos
                    duration = dur

                    // Autoplay next episode prompt near the end (last 15 seconds)
                    if (dur > 30_000L && pos >= dur - 15_000L && hasNextEpisode && !showNextEpisodePrompt) {
                        showNextEpisodePrompt = true
                    }

                    onProgressUpdate(pos, dur, false)
                }
            }
            delay(1000)
        }
    }

    // Countdown for next episode prompt
    LaunchedEffect(showNextEpisodePrompt) {
        if (showNextEpisodePrompt) {
            countdownSeconds = 8
            while (countdownSeconds > 0 && showNextEpisodePrompt) {
                delay(1000)
                countdownSeconds--
            }
            if (showNextEpisodePrompt) {
                showNextEpisodePrompt = false
                onNextEpisode()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                controlsVisible = !controlsVisible
            }
            .testTag("stream_player_container")
    ) {
        if (isWebEmbed) {
            // High-Performance Interactive Web Embed Stream Player via WebView
            AndroidView(
                factory = { ctx ->
                    android.webkit.WebView(ctx).apply {
                        webViewRef = this
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setBackgroundColor(0xFF000000.toInt())
                        try {
                            setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
                        } catch (_: Exception) {}

                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        settings.allowFileAccess = true
                        settings.databaseEnabled = true
                        settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        settings.userAgentString = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true

                        webChromeClient = object : android.webkit.WebChromeClient() {}
                        webViewClient = object : android.webkit.WebViewClient() {
                            override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isBuffering = false
                            }
                            override fun shouldOverrideUrlLoading(view: android.webkit.WebView?, request: android.webkit.WebResourceRequest?): Boolean {
                                val url = request?.url?.toString().orEmpty()
                                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                    return true
                                }
                                if (url.contains("adservice") || url.contains("popunder") || url.contains("onclick") || url.contains("doubleclick")) {
                                    return true
                                }
                                return false
                            }
                        }
                        loadUrl(currentStreamUrl)
                    }
                },
                update = { wv ->
                    webViewRef = wv
                    if (wv.url != currentStreamUrl) {
                        isBuffering = true
                        wv.loadUrl(currentStreamUrl)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else if (isMagnetLink && (torrentStatus.state == TorrentState.PLAYING || torrentStatus.state == TorrentState.FINISHED)) {
            // Built-in Webtor HTML5 Torrent Player via WebView
            AndroidView(
                factory = { ctx ->
                    android.webkit.WebView(ctx).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        settings.allowFileAccess = true
                        settings.databaseEnabled = true
                        
                        webChromeClient = android.webkit.WebChromeClient()
                        webViewClient = android.webkit.WebViewClient()
                        
                        val htmlContent = """
                            <!DOCTYPE html>
                            <html>
                            <head>
                                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                                <script src="https://cdn.jsdelivr.net/npm/@webtor/embed-sdk-js/dist/index.min.js" charset="utf-8" async></script>
                                <style>
                                    body, html { margin: 0; padding: 0; width: 100%; height: 100%; background-color: #000; overflow: hidden; }
                                    video { width: 100%; height: 100%; }
                                    .webtor-logo { display: none !important; }
                                </style>
                            </head>
                            <body>
                                <video src="${currentStreamUrl}" controls width="100%" height="100%"></video>
                            </body>
                            </html>
                        """.trimIndent()
                        
                        loadDataWithBaseURL("https://webtor.io", htmlContent, "text/html", "UTF-8", null)
                    }
                },
                update = { /* WebView update logic is handled by reload on Url change */ },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Native Video View
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        videoViewRef = this
                        val headers = mapOf("User-Agent" to "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36")
                        if (actualVideoUrl.isNotBlank()) {
                            setVideoURI(Uri.parse(actualVideoUrl), headers)
                        }

                        setOnPreparedListener { mp ->
                            mediaPlayerRef = mp
                            isBuffering = false
                            playbackErrorMessage = null
                            duration = duration.coerceAtLeast(mp.duration.toLong())
                            if (initialPositionMs > 0) {
                                seekTo(initialPositionMs.toInt())
                            }
                            try {
                                mp.setPlaybackParams(mp.playbackParams.setSpeed(playbackSpeed))
                            } catch (_: Exception) {}
                            start()
                            isPlaying = true
                        }

                        setOnInfoListener { _, what, _ ->
                            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                                isBuffering = true
                            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                                isBuffering = false
                            }
                            true
                        }

                        setOnCompletionListener {
                            isPlaying = false
                            onProgressUpdate(duration, duration, true)
                            if (hasNextEpisode) {
                                showNextEpisodePrompt = true
                            }
                        }

                        setOnErrorListener { _, what, extra ->
                            isBuffering = false
                            // Ignore state machine transition errors (like stop/start in incorrect state)
                            if (what == -38 || extra == -38) {
                                return@setOnErrorListener true
                            }
                            val fallback = allSources.firstOrNull { it.streamUrl != currentStreamUrl && it.streamUrl.isNotBlank() && !it.streamUrl.startsWith("magnet") }
                            if (fallback != null) {
                                playbackErrorMessage = "Stream unavailable. Auto-switching to ${fallback.quality} stream..."
                                currentStreamUrl = fallback.streamUrl
                            } else {
                                playbackErrorMessage = "Playback failed. This can happen if the link has expired, requires premium debrid, or the hoster is blocked in your network. Try playing in an external player like VLC."
                            }
                            true
                        }
                    }
                },
                update = { vv ->
                    videoViewRef = vv
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Subtitle Overlay (simulated overlay)
        if (selectedSubtitle != "Off") {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (controlsVisible) 96.dp else 40.dp)
                    .background(Color(0xB3000000), RoundedCornerShape(4.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "[${selectedSubtitle.uppercase()}] Subtitles active for $title",
                    color = Color.Yellow,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Playback Error / Status Card
        if (playbackErrorMessage != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
                    .background(StreamCard.copy(alpha = 0.95f), RoundedCornerShape(12.dp))
                    .border(1.dp, StreamPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = playbackErrorMessage!!,
                        color = StreamTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { launchExternalPlayer(currentStreamUrl) },
                            colors = ButtonDefaults.buttonColors(containerColor = StreamGreen)
                        ) {
                            Text("Play in VLC / External", color = Color.White, fontSize = 13.sp)
                        }
                        if (allSources.size > 1) {
                            OutlinedButton(
                                onClick = { showSourceSelector = true }
                            ) {
                                Text("Switch Source", color = StreamTextPrimary, fontSize = 13.sp)
                            }
                        }
                        Button(
                            onClick = { retryCount++ },
                            colors = ButtonDefaults.buttonColors(containerColor = StreamPrimary)
                        ) {
                            Text("Retry", color = Color.White, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Magnet Link WebTorrent Engine Handler
        if (isMagnetLink) {
            val s = torrentStatus.state
            if (s == TorrentState.CONNECTING || s == TorrentState.FETCHING_METADATA || s == TorrentState.BUFFERING || s == TorrentState.IDLE) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.95f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .padding(20.dp)
                            .widthIn(max = 520.dp)
                            .background(StreamCard, RoundedCornerShape(16.dp))
                            .border(1.dp, StreamPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .padding(24.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Header
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NetworkCheck,
                                    contentDescription = null,
                                    tint = StreamPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Built-In Torrent Streamer",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Torrent details
                            Text(
                                text = torrentStatus.torrentName,
                                color = StreamGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            // State specific info
                            when (s) {
                                TorrentState.CONNECTING -> {
                                    CircularProgressIndicator(color = StreamPrimary, modifier = Modifier.size(44.dp))
                                    Text("Connecting to BitTorrent Swarm...", color = StreamTextPrimary, fontSize = 14.sp)
                                    Text("Connecting to DHT trackers, peers: ${torrentStatus.numPeers}", color = StreamTextMuted, fontSize = 12.sp)
                                }
                                TorrentState.FETCHING_METADATA -> {
                                    CircularProgressIndicator(color = StreamGold, modifier = Modifier.size(44.dp))
                                    Text("Fetching Torrent Metadata...", color = StreamTextPrimary, fontSize = 14.sp)
                                    Text("InfoHash: ${torrentStatus.infoHash.take(12)}...${torrentStatus.infoHash.takeLast(12)}", color = StreamTextMuted, fontSize = 11.sp)
                                }
                                TorrentState.BUFFERING -> {
                                    // Progress and Speed
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Sequentially Buffering Payload: ${(torrentStatus.progress * 100).toInt()}%",
                                            color = StreamTextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        
                                        // Simple Progress Bar
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp)
                                                .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth(torrentStatus.progress)
                                                    .height(8.dp)
                                                    .background(StreamPrimary, RoundedCornerShape(4.dp))
                                            )
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("DL: ${torrentStatus.downloadSpeed}", color = StreamGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text("UL: ${torrentStatus.uploadSpeed}", color = StreamTextMuted, fontSize = 12.sp)
                                            Text("Peers: ${torrentStatus.numPeers}", color = StreamGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // Piece Map Block Grid Representation
                                        Text(
                                            text = "Chunk Buffer Availability Map:",
                                            color = StreamTextSecondary,
                                            fontSize = 12.sp,
                                            modifier = Modifier.align(Alignment.Start).padding(top = 8.dp)
                                        )
                                        
                                        // Piece blocks custom FlowRow
                                        FlowRow(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            torrentStatus.pieceMap.forEach { isDownloaded ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(12.dp)
                                                        .background(
                                                            if (isDownloaded) StreamGreen else Color.DarkGray,
                                                            RoundedCornerShape(2.dp)
                                                        )
                                                )
                                            }
                                        }

                                        // Available files inside payload list representation
                                        if (torrentStatus.files.isNotEmpty()) {
                                            Text(
                                                text = "Included Files (${torrentStatus.files.size}):",
                                                color = StreamTextSecondary,
                                                fontSize = 12.sp,
                                                modifier = Modifier.align(Alignment.Start).padding(top = 8.dp)
                                            )
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                                    .padding(8.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                torrentStatus.files.forEachIndexed { index, file ->
                                                    val isSelected = index == torrentStatus.selectedFileIndex
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(if (isSelected) StreamPrimary.copy(alpha = 0.2f) else Color.Transparent)
                                                            .padding(6.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = file.name,
                                                            color = if (isSelected) StreamPrimary else StreamTextPrimary,
                                                            fontSize = 11.sp,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                        Text(
                                                            text = file.sizeFormatted,
                                                            color = StreamTextMuted,
                                                            fontSize = 10.sp,
                                                            modifier = Modifier.padding(start = 8.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                else -> {}
                            }

                            // Footer Buttons
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { launchExternalPlayer(currentStreamUrl) },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Use VLC / External", color = StreamTextPrimary, fontSize = 13.sp)
                                }
                                if (allSources.size > 1) {
                                    OutlinedButton(
                                        onClick = { showSourceSelector = true },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Switch Source", color = StreamTextPrimary, fontSize = 13.sp)
                                    }
                                }
                                Button(
                                    onClick = { onClose() },
                                    colors = ButtonDefaults.buttonColors(containerColor = StreamPrimary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Go Back", color = Color.White, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Back Button for WebTorrent WebView Player (so user is never trapped)
        if (isMagnetLink && (torrentStatus.state == TorrentState.PLAYING || torrentStatus.state == TorrentState.FINISHED)) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(top = 16.dp, start = 16.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.75f))
                    .border(1.dp, StreamPrimary.copy(alpha = 0.5f), CircleShape)
                    .clickable { onClose() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Exit Player",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Exit",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Floating Torrent Badge HUD (when stream is playing and controls are active)
        if (isMagnetLink && (torrentStatus.state == TorrentState.PLAYING || torrentStatus.state == TorrentState.FINISHED)) {
            AnimatedVisibility(
                visible = controlsVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 16.dp, end = 16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Floating Badge Pill
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.75f))
                            .border(1.dp, StreamGreen.copy(alpha = 0.6f), CircleShape)
                            .clickable { isTorrentHudExpanded = !isTorrentHudExpanded }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (torrentStatus.state == TorrentState.FINISHED) StreamGreen else StreamGold)
                        )
                        Text(
                            text = if (torrentStatus.state == TorrentState.FINISHED) "Torrent Finished" else "DL: ${torrentStatus.downloadSpeed}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "(${torrentStatus.numPeers} Peers)",
                            color = StreamTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    // Expanded Details Dialog Sidebar
                    if (isTorrentHudExpanded) {
                        Box(
                            modifier = Modifier
                                .width(280.dp)
                                .shadow(8.dp, RoundedCornerShape(12.dp))
                                .background(StreamCard, RoundedCornerShape(12.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Torrent Connection Details", color = StreamGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "Close",
                                        color = StreamPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable { isTorrentHudExpanded = false }
                                    )
                                }
                                Text(
                                    text = torrentStatus.torrentName,
                                    color = StreamTextPrimary,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Hash: ${torrentStatus.infoHash.take(16)}...",
                                    color = StreamTextMuted,
                                    fontSize = 9.sp
                                )

                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Sequential piece buffer progress: ${(torrentStatus.progress * 100).toInt()}%", color = StreamTextSecondary, fontSize = 10.sp)
                                
                                // Piece block grid row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    torrentStatus.pieceMap.take(20).forEach { isDownloaded ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(6.dp)
                                                .background(if (isDownloaded) StreamGreen else Color.DarkGray)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Payload files list:", color = StreamTextSecondary, fontSize = 10.sp)
                                torrentStatus.files.forEach { file ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(file.name, color = StreamTextPrimary, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                        Text(file.sizeFormatted, color = StreamTextMuted, fontSize = 9.sp, modifier = Modifier.padding(start = 4.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Buffering Indicator
        if (isBuffering) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(
                        color = StreamPrimary,
                        modifier = Modifier.size(54.dp),
                        strokeWidth = 4.dp
                    )
                    Text(
                        text = "Buffering ${source.quality} [${source.sourceType}]...",
                        color = StreamTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Controls Overlay
        AnimatedVisibility(
            visible = controlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xD9000000),
                                Color.Transparent,
                                Color.Transparent,
                                Color(0xE6000000)
                            )
                        )
                    )
            ) {
                // Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .align(Alignment.TopCenter),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag("player_back_button")
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = StreamTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            color = StreamTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!subtitle.isNullOrBlank()) {
                            Text(
                                text = subtitle,
                                color = StreamTextSecondary,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Quality & Cache Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (source.isCached) StreamGreen.copy(alpha = 0.2f) else StreamCard,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (source.isCached) StreamGreen else StreamSecondary
                        ),
                        modifier = Modifier.clickable { showSourceSelector = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = source.quality,
                                color = if (source.isCached) StreamGreen else StreamSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (source.isCached) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "CACHED",
                                    color = StreamGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Sources selector button
                    IconButton(onClick = { showSourceSelector = true }) {
                        Icon(
                            Icons.Default.Layers,
                            contentDescription = "Sources",
                            tint = StreamTextPrimary
                        )
                    }

                    // Subtitles button
                    IconButton(onClick = { showSubtitlesDialog = true }) {
                        Icon(
                            Icons.Default.ClosedCaption,
                            contentDescription = "Subtitles",
                            tint = if (selectedSubtitle != "Off") StreamGold else StreamTextPrimary
                        )
                    }

                    // Speed button
                    IconButton(onClick = { showSpeedDialog = true }) {
                        Icon(
                            Icons.Default.Speed,
                            contentDescription = "Playback Speed",
                            tint = StreamTextPrimary
                        )
                    }

                    // Orientation / Fullscreen Toggle
                    IconButton(onClick = { toggleOrientation() }) {
                        Icon(
                            if (isLandscape) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                            contentDescription = "Toggle Landscape",
                            tint = StreamTextPrimary
                        )
                    }
                }

                if (!isWebEmbed) {
                    // Center Play/Pause & Skip Controls
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.spacedBy(36.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rewind 10s
                        IconButton(
                            onClick = {
                                try {
                                    videoViewRef?.let { vv ->
                                        val newPos = (vv.currentPosition - 10_000).coerceAtLeast(0)
                                        vv.seekTo(newPos)
                                        currentPos = newPos.toLong()
                                    }
                                } catch (_: Exception) {}
                            },
                            modifier = Modifier.size(52.dp)
                        ) {
                            Icon(
                                Icons.Default.Replay10,
                                contentDescription = "Rewind 10s",
                                tint = StreamTextPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        // Play / Pause main button
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(StreamPrimary)
                                .clickable {
                                    try {
                                        videoViewRef?.let { vv ->
                                            if (vv.isPlaying) {
                                                vv.pause()
                                                isPlaying = false
                                            } else {
                                                vv.start()
                                                isPlaying = true
                                            }
                                        }
                                    } catch (_: Exception) {}
                                }
                                .testTag("player_play_pause_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        // Forward 10s
                        IconButton(
                            onClick = {
                                try {
                                    videoViewRef?.let { vv ->
                                        val newPos = (vv.currentPosition + 10_000).coerceAtMost(vv.duration)
                                        vv.seekTo(newPos)
                                        currentPos = newPos.toLong()
                                    }
                                } catch (_: Exception) {}
                            },
                            modifier = Modifier.size(52.dp)
                        ) {
                            Icon(
                                Icons.Default.Forward10,
                                contentDescription = "Forward 10s",
                                tint = StreamTextPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    // Bottom Controls Bar
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        // Safe Seek Slider (updates position on release without flooding MediaPlayer seekTo)
                        val sliderValue = if (isUserSeeking) seekFraction else if (duration > 0) (currentPos.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
                        Slider(
                            value = sliderValue,
                            onValueChange = { frac ->
                                isUserSeeking = true
                                seekFraction = frac
                                if (duration > 0) {
                                    currentPos = (frac * duration).toLong()
                                }
                            },
                            onValueChangeFinished = {
                                isUserSeeking = false
                                if (duration > 0) {
                                    val target = (seekFraction * duration).toLong()
                                    currentPos = target
                                    try {
                                        videoViewRef?.seekTo(target.toInt())
                                    } catch (_: Exception) {}
                                }
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = StreamPrimary,
                                activeTrackColor = StreamPrimary,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("player_seek_slider")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${formatTime(currentPos)} / ${formatTime(duration)}",
                                color = StreamTextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (hasPrevEpisode) {
                                    IconButton(onClick = onPrevEpisode) {
                                        Icon(
                                            Icons.Default.SkipPrevious,
                                            contentDescription = "Previous Episode",
                                            tint = StreamTextPrimary
                                        )
                                    }
                                }

                                if (hasNextEpisode) {
                                    IconButton(onClick = onNextEpisode) {
                                        Icon(
                                            Icons.Default.SkipNext,
                                            contentDescription = "Next Episode",
                                            tint = StreamTextPrimary
                                        )
                                    }
                                }

                                // External player button
                                IconButton(onClick = { launchExternalPlayer(actualVideoUrl.ifBlank { currentStreamUrl }) }) {
                                    Icon(
                                        Icons.Default.HighQuality,
                                        contentDescription = "External Player",
                                        tint = StreamTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Next Episode Countdown Overlay
        if (showNextEpisodePrompt && hasNextEpisode) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 80.dp)
                    .background(Color(0xF00F131C), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Next episode in ${countdownSeconds}s",
                        color = StreamTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.15f),
                            modifier = Modifier.clickable { showNextEpisodePrompt = false }
                        ) {
                            Text(
                                text = "Cancel",
                                color = StreamTextSecondary,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = StreamPrimary,
                            modifier = Modifier.clickable {
                                showNextEpisodePrompt = false
                                onNextEpisode()
                            }
                        ) {
                            Text(
                                text = "Play Now",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Streaming Sources Picker Dialog
    if (showSourceSelector) {
        Dialog(onDismissRequest = { showSourceSelector = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = StreamCard,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Available Streaming Sources",
                        color = StreamTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Hybrid Engine: Fast Cloud Embed Servers • Orion Debrid • Torrent Swarms",
                        color = StreamTextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val sourcesList = if (allSources.isNotEmpty()) allSources else listOf(source)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        sourcesList.forEach { s ->
                            val isCurrent = s.id == source.id
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isCurrent) StreamPrimary.copy(alpha = 0.15f) else StreamBackground,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isCurrent) StreamPrimary else Color.Transparent
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        onSourceSelected(s)
                                        showSourceSelector = false
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = s.quality,
                                                color = if (s.sourceType == "Web Embed") Color(0xFF64B5F6) else if (s.isCached) StreamGreen else StreamGold,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (s.sourceType == "Web Embed") "🌐 ${s.providerName}" else s.sourceType,
                                                color = StreamTextMuted,
                                                fontSize = 12.sp
                                            )
                                        }
                                        Text(
                                            text = "${s.sizeFormatted} • ${s.codec}",
                                            color = StreamTextSecondary,
                                            fontSize = 12.sp
                                        )
                                    }
                                    if (s.sourceType == "Web Embed") {
                                        Text(
                                            text = "INSTANT",
                                            color = Color(0xFF64B5F6),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    } else if (s.isCached) {
                                        Text(
                                            text = "CACHED",
                                            color = StreamGreen,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Close",
                        color = StreamPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { showSourceSelector = false }
                            .padding(8.dp)
                    )
                }
            }
        }
    }

    // Playback Speed Dialog
    if (showSpeedDialog) {
        Dialog(onDismissRequest = { showSpeedDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = StreamCard,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Playback Speed",
                        color = StreamTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { spd ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    playbackSpeed = spd
                                    mediaPlayerRef?.let { mp ->
                                        try {
                                            mp.playbackParams = mp.playbackParams.setSpeed(spd)
                                        } catch (_: Exception) {}
                                    }
                                    showSpeedDialog = false
                                }
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${spd}x",
                                color = if (playbackSpeed == spd) StreamPrimary else StreamTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = if (playbackSpeed == spd) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }

    // Subtitles Dialog
    if (showSubtitlesDialog) {
        Dialog(onDismissRequest = { showSubtitlesDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = StreamCard,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Audio & Subtitles",
                        color = StreamTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    listOf("Off", "English [CC]", "Spanish", "French", "German").forEach { sub ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedSubtitle = sub
                                    showSubtitlesDialog = false
                                }
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = sub,
                                color = if (selectedSubtitle == sub) StreamPrimary else StreamTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = if (selectedSubtitle == sub) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }

    // Clean up when leaving composable
    DisposableEffect(Unit) {
        onDispose {
            try {
                videoViewRef?.stopPlayback()
            } catch (_: Exception) {}
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val hours = minutes / 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes % 60, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
