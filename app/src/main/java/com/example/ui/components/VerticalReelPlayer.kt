package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.DramaSeries
import com.example.data.model.Episode
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.ReelCrimson
import com.example.ui.theme.ReelRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VipGold
import com.example.ui.theme.VipGoldBright
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VerticalReelPlayer(
    drama: DramaSeries,
    episode: Episode,
    isPlaying: Boolean,
    isLiked: Boolean,
    isBookmarked: Boolean,
    playbackSpeed: Float,
    isSubtitleVisible: Boolean,
    subtitleLanguage: String,
    isMuted: Boolean,
    totalEpisodes: Int,
    onTogglePlayPause: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleBookmark: () -> Unit,
    onOpenEpisodeDrawer: () -> Unit,
    onOpenCommentsDrawer: () -> Unit,
    onOpenVipDialog: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onToggleSubtitle: () -> Unit,
    onToggleMute: () -> Unit,
    onNextEpisode: () -> Unit,
    onPreviousEpisode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showHeartBurst by remember { mutableStateOf(false) }
    var currentProgress by remember { mutableFloatStateOf(0.15f) }
    var isSeeking by remember { mutableStateOf(false) }
    var speedMenuExpanded by remember { mutableStateOf(false) }
    var isSynopsisExpanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Smooth simulated video progress ticker
    LaunchedEffect(isPlaying, playbackSpeed, episode.id) {
        currentProgress = 0f
        while (isPlaying) {
            delay(200)
            if (!isSeeking) {
                currentProgress = (currentProgress + (0.2f / episode.durationSeconds.toFloat()) * playbackSpeed)
                    .coerceIn(0f, 1f)
                if (currentProgress >= 1f) {
                    onNextEpisode()
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDark)
    ) {
        // Video View / Visual Surface
        VideoSurfacePlayer(
            videoUrl = episode.videoUrl,
            fallbackPosterRes = drama.posterRes,
            isPlaying = isPlaying,
            isMuted = isMuted,
            playbackSpeed = playbackSpeed
        )

        // Subtle dark gradients for clear text and controls readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.75f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.92f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        // Big Tap Area for Play/Pause and Double Tap for Heart Like
        Box(
            modifier = Modifier
                .fillMaxSize()
                .testTag("reel_tap_surface")
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onTogglePlayPause() },
                    onDoubleClick = {
                        if (!isLiked) {
                            onToggleLike()
                        }
                        showHeartBurst = true
                        coroutineScope.launch {
                            delay(800)
                            showHeartBurst = false
                        }
                    }
                )
        )

        // Center Play/Pause Indicator if paused
        AnimatedVisibility(
            visible = !isPlaying,
            enter = fadeIn() + scaleIn(initialScale = 0.7f),
            exit = fadeOut() + scaleOut(targetScale = 1.3f),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.65f),
                modifier = Modifier.size(76.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = TextPrimary,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }
        }

        // Heart Burst Animation on Double Tap
        AnimatedVisibility(
            visible = showHeartBurst,
            enter = scaleIn(initialScale = 0.3f, animationSpec = tween(300, easing = FastOutSlowInEasing)) + fadeIn(),
            exit = fadeOut(animationSpec = tween(400)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Liked",
                tint = ReelRed,
                modifier = Modifier.size(110.dp)
            )
        }

        // TOP BAR: Drama Title, VIP Free Pill, 4K Badge, Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Golden VIP Free Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = VipGold.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VipGoldBright),
                    modifier = Modifier
                        .clickable { onOpenVipDialog() }
                        .testTag("vip_free_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "VIP Free",
                            tint = VipGoldBright,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "PREMIUM FREE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = VipGoldBright,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 4K Ultra HD Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "4K HDR",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            // Quick Player Actions
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Subtitle Toggle
                IconButton(
                    onClick = onToggleSubtitle,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ClosedCaption,
                        contentDescription = "Subtitles",
                        tint = if (isSubtitleVisible) ReelRed else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Speed Selector Button
                Box {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .clickable { speedMenuExpanded = true }
                            .padding(start = 4.dp)
                    ) {
                        Text(
                            text = "${playbackSpeed}x",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = speedMenuExpanded,
                        onDismissRequest = { speedMenuExpanded = false },
                        modifier = Modifier.background(DarkSurface)
                    ) {
                        listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${speed}x ${if (speed == 1.0f) "(Normal)" else ""}",
                                        color = if (playbackSpeed == speed) ReelRed else TextPrimary,
                                        fontWeight = if (playbackSpeed == speed) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    onSpeedChange(speed)
                                    speedMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Mute / Unmute Button
                IconButton(
                    onClick = onToggleMute,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                        contentDescription = "Audio Volume",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // RIGHT-SIDE ACTION BAR (Like, Comments, My List, Episodes, Share)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Series Creator / Drama Avatar with VIP crown
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(2.dp, VipGoldBright, CircleShape)
                    .clickable { onOpenEpisodeDrawer() }
            ) {
                Image(
                    painter = painterResource(id = drama.posterRes),
                    contentDescription = drama.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Like Floating Action Button
            val likeScale by animateFloatAsState(
                targetValue = if (isLiked) 1.15f else 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "likeFabScale"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FloatingActionButton(
                    onClick = onToggleLike,
                    shape = CircleShape,
                    containerColor = if (isLiked) ReelRed else Color.Black.copy(alpha = 0.55f),
                    contentColor = if (isLiked) Color.White else TextPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 12.dp
                    ),
                    modifier = Modifier
                        .size(48.dp)
                        .graphicsLayer {
                            scaleX = likeScale
                            scaleY = likeScale
                        }
                        .testTag("like_floating_action_button")
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isLiked) "Unlike Reel" else "Like Reel",
                        tint = if (isLiked) Color.White else ReelRed,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isLiked) "32.4K" else "32.3K",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLiked) ReelRed else TextPrimary
                )
            }

            // Comments Action
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onOpenCommentsDrawer() }
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.45f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ChatBubble,
                            contentDescription = "Comments",
                            tint = TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "1.8K",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }

            // Bookmark / My List Action
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onToggleBookmark() }
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.45f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save to My List",
                            tint = if (isBookmarked) VipGoldBright else TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isBookmarked) "Saved" else "My List",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isBookmarked) VipGoldBright else TextPrimary
                )
            }

            // Episodes Drawer Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onOpenEpisodeDrawer() }
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.45f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = "Episodes",
                            tint = TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ep.${episode.episodeNumber}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }

            // Share Action
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Watch '${drama.title}' Episode ${episode.episodeNumber} on DashReels! VIP Premium Free unlocked: https://dashreels.app/watch/${drama.id}"
                        )
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share DashReels Episode"))
                }
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.45f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = TextPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Share",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
        }

        // BOTTOM OVERLAY: Subtitle, Drama Details, Scrubber
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 16.dp, end = 74.dp, bottom = 12.dp)
        ) {
            // Live Subtitles Display
            if (isSubtitleVisible && episode.subtitleText.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        text = episode.subtitleText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFFFFA65),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            // Drama Title & Episode Pill
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "@${drama.title}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ReelCrimson
                ) {
                    Text(
                        text = "EP ${episode.episodeNumber}/$totalEpisodes",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Episode Title
            Text(
                text = episode.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Synopsis / Tags
            Text(
                text = if (isSynopsisExpanded) drama.synopsis else "${drama.synopsis.take(75)}...",
                fontSize = 12.sp,
                color = TextMuted,
                maxLines = if (isSynopsisExpanded) 4 else 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable { isSynopsisExpanded = !isSynopsisExpanded }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Scrubber Bar & Next/Prev Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive Scrubber
                Slider(
                    value = currentProgress,
                    onValueChange = {
                        isSeeking = true
                        currentProgress = it
                    },
                    onValueChangeFinished = {
                        isSeeking = false
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = ReelRed,
                        activeTrackColor = ReelRed,
                        inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                val curSec = (currentProgress * episode.durationSeconds).toInt()
                Text(
                    text = "${curSec / 60}:${String.format("%02d", curSec % 60)} / ${episode.durationSeconds / 60}:${String.format("%02d", episode.durationSeconds % 60)}",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun VideoSurfacePlayer(
    videoUrl: String,
    fallbackPosterRes: Int,
    isPlaying: Boolean,
    isMuted: Boolean,
    playbackSpeed: Float
) {
    val context = LocalContext.current
    var isVideoReady by remember { mutableStateOf(false) }

    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = isPlaying
            volume = if (isMuted) 0f else 1f
            playbackParameters = PlaybackParameters(playbackSpeed)
        }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    isVideoReady = true
                }
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    LaunchedEffect(videoUrl) {
        isVideoReady = false
        try {
            exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
            exoPlayer.prepare()
            exoPlayer.playWhenReady = isPlaying
        } catch (_: Exception) {}
    }

    LaunchedEffect(isPlaying) {
        exoPlayer.playWhenReady = isPlaying
    }

    LaunchedEffect(isMuted) {
        exoPlayer.volume = if (isMuted) 0f else 1f
    }

    LaunchedEffect(playbackSpeed) {
        exoPlayer.playbackParameters = PlaybackParameters(playbackSpeed)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fallback poster image always visible as background & smooth loader
        Image(
            painter = painterResource(id = fallbackPosterRes),
            contentDescription = "Video Poster",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // ExoPlayer PlayerView with AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Cinematic pulse glow if video is preparing
        if (!isVideoReady) {
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 0.5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = alpha))
            )
        }
    }
}
