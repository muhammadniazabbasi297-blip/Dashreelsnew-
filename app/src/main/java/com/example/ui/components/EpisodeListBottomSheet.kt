package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DramaSeries
import com.example.data.model.Episode
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.ReelCrimson
import com.example.ui.theme.ReelRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VipGold
import com.example.ui.theme.VipGoldBright

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodeListBottomSheet(
    drama: DramaSeries,
    episodes: List<Episode>,
    currentEpisodeIndex: Int,
    onSelectEpisode: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedRangeIndex by remember { mutableStateOf(0) }

    val batchSize = 20
    val ranges = (0 until episodes.size step batchSize).map { start ->
        val end = (start + batchSize).coerceAtMost(episodes.size)
        "$start-$end"
    }

    val displayedEpisodes = remember(selectedRangeIndex, episodes) {
        val start = selectedRangeIndex * batchSize
        val end = (start + batchSize).coerceAtMost(episodes.size)
        if (start < episodes.size) {
            episodes.subList(start, end)
        } else {
            episodes
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface,
        dragHandle = null,
        modifier = Modifier.fillMaxHeight(0.78f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Title & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Episodes (${episodes.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = drama.title,
                        fontSize = 13.sp,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_episodes_sheet")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Premium Free VIP Unlocked Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = VipGold.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, VipGold.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = VipGoldBright,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "VIP Free",
                                tint = ObsidianDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "DashReels VIP: 100% Free Access",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = VipGoldBright
                        )
                        Text(
                            text = "All episodes are unlocked without coins or ads.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Range filter chips (e.g. 1-20, 21-40)
            if (ranges.size > 1) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ranges.size) { index ->
                        val rangeStr = ranges[index]
                        val isSelected = index == selectedRangeIndex
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) ReelRed else DarkSurfaceVariant,
                            modifier = Modifier.clickable { selectedRangeIndex = index }
                        ) {
                            Text(
                                text = "Ep $rangeStr",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Episode Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("episodes_grid")
            ) {
                itemsIndexed(displayedEpisodes) { localIdx, ep ->
                    val globalIdx = (selectedRangeIndex * batchSize) + localIdx
                    val isCurrent = globalIdx == currentEpisodeIndex
                    val isWatched = globalIdx < currentEpisodeIndex

                    Box(
                        modifier = Modifier
                            .height(56.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isCurrent) {
                                    Brush.verticalGradient(listOf(ReelRed, ReelCrimson))
                                } else {
                                    Brush.verticalGradient(listOf(DarkSurfaceVariant, DarkSurfaceCard))
                                }
                            )
                            .border(
                                width = if (isCurrent) 1.5.dp else 1.dp,
                                color = if (isCurrent) Color.White else Color.White.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                onSelectEpisode(globalIdx)
                                onDismiss()
                            }
                            .testTag("episode_item_$globalIdx"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isCurrent) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Playing",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                }
                                Text(
                                    text = "${ep.episodeNumber}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) TextPrimary else if (isWatched) TextSecondary else TextPrimary
                                )
                            }

                            // Tiny Free VIP label
                            Text(
                                text = if (isCurrent) "PLAYING" else "FREE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isCurrent) Color.White else VipGoldBright
                            )
                        }
                    }
                }
            }
        }
    }
}
