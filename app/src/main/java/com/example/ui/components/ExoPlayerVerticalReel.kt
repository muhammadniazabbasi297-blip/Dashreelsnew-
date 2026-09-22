package com.example.ui.components

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.ui.theme.ReelRed
import com.example.ui.theme.VipGoldBright
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

data class ExoHeartBurst(
    val id: Long,
    val xOffset: Float,
    val yOffset: Float,
    val rotation: Float
)

/**
 * High-performance vertical video player using AndroidX Media3 ExoPlayer.
 * Designed specifically for short-drama vertical micro-reels with:
 * - Hardware-accelerated PlayerView with AspectRatioFrameLayout.RESIZE_MODE_ZOOM
 * - Instant MediaItem preparation and seamless looping (REPEAT_MODE_ONE)
 * - Single-tap play/pause and double-tap heart reaction bursts
 * - Dynamic playback speed (0.75x to 2.0x) and volume control
 * - Progress tracking listener for micro-reel playback timeline
 */
@OptIn(UnstableApi::class)
@Composable
fun ExoPlayerVerticalReel(
    videoUrl: String,
    fallbackPosterRes: Int,
    isPlaying: Boolean,
    isMuted: Boolean,
    playbackSpeed: Float,
    onTogglePlayPause: () -> Unit,
    onDoubleTapLike: () -> Unit,
    modifier: Modifier = Modifier,
    onProgressUpdate: (currentMs: Long, durationMs: Long) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isBuffering by remember { mutableStateOf(true) }
    var isPlayerReady by remember { mutableStateOf(false) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(1L) }

    // Heart burst reactions on double tap
    val hearts = remember { mutableStateListOf<ExoHeartBurst>() }

    // Remember ExoPlayer instance tied to current context and lifecycle
    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context)
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_ONE
                playWhenReady = isPlaying
                volume = if (isMuted) 0f else 1f
                playbackParameters = PlaybackParameters(playbackSpeed)
            }
    }

    // Attach player listener for state updates
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        isBuffering = true
                    }
                    Player.STATE_READY -> {
                        isBuffering = false
                        isPlayerReady = true
                        durationMs = exoPlayer.duration.coerceAtLeast(1L)
                    }
                    Player.STATE_ENDED -> {
                        isBuffering = false
                    }
                    Player.STATE_IDLE -> {
                        isBuffering = false
                    }
                }
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                // Keep UI synchronized if needed
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    // Update MediaItem when videoUrl changes
    LaunchedEffect(videoUrl) {
        isPlayerReady = false
        isBuffering = true
        try {
            val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = isPlaying
        } catch (e: Exception) {
            isBuffering = false
        }
    }

    // Sync playWhenReady
    LaunchedEffect(isPlaying) {
        exoPlayer.playWhenReady = isPlaying
    }

    // Sync volume/mute
    LaunchedEffect(isMuted) {
        exoPlayer.volume = if (isMuted) 0f else 1f
    }

    // Sync playback speed
    LaunchedEffect(playbackSpeed) {
        exoPlayer.playbackParameters = PlaybackParameters(playbackSpeed)
    }

    // Periodic progress updater coroutine
    LaunchedEffect(isPlaying, videoUrl) {
        while (isActive) {
            if (exoPlayer.playbackState == Player.STATE_READY && exoPlayer.isPlaying) {
                val current = exoPlayer.currentPosition
                val dur = exoPlayer.duration.coerceAtLeast(1L)
                currentPositionMs = current
                durationMs = dur
                onProgressUpdate(current, dur)
            }
            delay(250L)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onTogglePlayPause()
                    },
                    onDoubleTap = { offset ->
                        onDoubleTapLike()
                        val randomRot = (Random.nextFloat() * 40f) - 20f
                        val randomX = offset.x + (Random.nextFloat() * 40f - 20f)
                        val randomY = offset.y + (Random.nextFloat() * 40f - 20f)
                        val burst = ExoHeartBurst(
                            id = System.currentTimeMillis() + Random.nextLong(1000),
                            xOffset = randomX,
                            yOffset = randomY,
                            rotation = randomRot
                        )
                        hearts.add(burst)
                        coroutineScope.launch {
                            delay(900)
                            hearts.remove(burst)
                        }
                    }
                )
            }
            .testTag("exo_vertical_player_box")
    ) {
        // Fallback backdrop poster while video buffers or transitions
        Image(
            painter = painterResource(id = fallbackPosterRes),
            contentDescription = "Video Poster Backdrop",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // ExoPlayer AndroidView
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Shimmering buffer overlay
        if (isBuffering && !isPlayerReady) {
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.15f,
                targetValue = 0.45f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = alpha)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = ReelRed,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        // Animated Hearts on Double Tap
        hearts.forEach { heart ->
            ExoHeartAnimation(heart = heart)
        }

        // Pause Indicator Overlay
        AnimatedVisibility(
            visible = !isPlaying,
            enter = fadeIn(tween(150)) + scaleIn(tween(150), initialScale = 0.8f),
            exit = fadeOut(tween(150)) + scaleOut(tween(150), targetScale = 0.8f),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(Color.Black.copy(alpha = 0.6f), androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Paused",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(46.dp)
                )
            }
        }
    }
}

@Composable
private fun ExoHeartAnimation(heart: ExoHeartBurst) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(heart.id) {
        launch {
            scale.animateTo(
                targetValue = 1.3f,
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            )
            scale.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(200)
            )
        }
        launch {
            delay(400)
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(400)
            )
        }
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(heart.xOffset.roundToInt() - 40, heart.yOffset.roundToInt() - 40) }
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                this.alpha = alpha.value
                rotationZ = heart.rotation
            }
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Heart Burst",
            tint = ReelRed,
            modifier = Modifier.size(54.dp)
        )
    }
}
