package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.EpisodeListBottomSheet
import com.example.ui.components.VerticalReelPlayer
import com.example.ui.components.VipFreeDialog
import com.example.ui.theme.ObsidianDark
import com.example.ui.viewmodel.DashReelsViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReelsFeedScreen(
    viewModel: DashReelsViewModel,
    modifier: Modifier = Modifier
) {
    val playerState by viewModel.playerState.collectAsState()
    val likedMap by viewModel.likedEpisodes.collectAsState()
    val bookmarkedIds by viewModel.bookmarkedIds.collectAsState()
    val vipStatus by viewModel.vipStatus.collectAsState()
    val currentComments by viewModel.getCommentsForCurrentEpisode().collectAsState()

    val currentEpisode = playerState.episodes.getOrNull(playerState.currentEpisodeIndex)
        ?: return

    val likeKey = "${playerState.activeDrama.id}_${currentEpisode.episodeNumber}"
    val isLiked = likedMap[likeKey] == true
    val isBookmarked = bookmarkedIds.contains(playerState.activeDrama.id)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .pointerInput(playerState.currentEpisodeIndex) {
                var totalDrag = 0f
                detectVerticalDragGestures(
                    onDragStart = { totalDrag = 0f },
                    onDragEnd = {
                        if (totalDrag < -100f) {
                            viewModel.playNextEpisode()
                        } else if (totalDrag > 100f) {
                            viewModel.playPreviousEpisode()
                        }
                    },
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        totalDrag += dragAmount
                    }
                )
            }
    ) {
        VerticalReelPlayer(
            drama = playerState.activeDrama,
            episode = currentEpisode,
            isPlaying = playerState.isPlaying,
            isLiked = isLiked,
            isBookmarked = isBookmarked,
            playbackSpeed = playerState.playbackSpeed,
            isSubtitleVisible = playerState.isSubtitleVisible,
            subtitleLanguage = playerState.subtitleLanguage,
            isMuted = playerState.isMuted,
            totalEpisodes = playerState.episodes.size,
            onTogglePlayPause = { viewModel.togglePlayPause() },
            onToggleLike = { viewModel.toggleLikeCurrent() },
            onToggleBookmark = { viewModel.toggleBookmark(playerState.activeDrama.id) },
            onOpenEpisodeDrawer = { viewModel.toggleEpisodeDrawer() },
            onOpenCommentsDrawer = { viewModel.toggleCommentsDrawer() },
            onOpenVipDialog = { viewModel.showVipDialog(true) },
            onSpeedChange = { viewModel.setPlaybackSpeed(it) },
            onToggleSubtitle = { viewModel.toggleSubtitle() },
            onToggleMute = { viewModel.toggleMute() },
            onNextEpisode = { viewModel.playNextEpisode() },
            onPreviousEpisode = { viewModel.playPreviousEpisode() }
        )

        // Episode List Sheet
        if (playerState.showEpisodeDrawer) {
            EpisodeListBottomSheet(
                drama = playerState.activeDrama,
                episodes = playerState.episodes,
                currentEpisodeIndex = playerState.currentEpisodeIndex,
                onSelectEpisode = { viewModel.selectEpisode(it) },
                onDismiss = { viewModel.toggleEpisodeDrawer() }
            )
        }

        // Comments Bottom Sheet
        if (playerState.showCommentsDrawer) {
            CommentsBottomSheet(
                comments = currentComments,
                episodeNumber = currentEpisode.episodeNumber,
                onPostComment = { viewModel.postComment(it) },
                onLikeComment = { viewModel.likeComment(it) },
                onDismiss = { viewModel.toggleCommentsDrawer() }
            )
        }

        // VIP Free Modal Dialog
        if (playerState.showVipDialog) {
            VipFreeDialog(
                vipStatus = vipStatus,
                onClaimBonus = { viewModel.claimDailyBonus() },
                onDismiss = { viewModel.showVipDialog(false) }
            )
        }
    }
}
