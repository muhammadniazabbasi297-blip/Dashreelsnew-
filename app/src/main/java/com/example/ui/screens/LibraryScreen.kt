package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DramaSeries
import com.example.data.model.VipStatus
import com.example.data.model.WatchProgressEntity
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

@Composable
fun LibraryScreen(
    dramas: List<DramaSeries>,
    vipStatus: VipStatus,
    bookmarkedIds: Set<String>,
    watchProgress: List<WatchProgressEntity>,
    onPlayDrama: (DramaSeries, Int) -> Unit,
    onOpenDramaDetail: (DramaSeries) -> Unit,
    onClaimDailyCoins: () -> Unit,
    onOpenVipDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("My List", "Watch History")

    val bookmarkedDramas = dramas.filter { bookmarkedIds.contains(it.id) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDark),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // TOP HEADER & VIP PROFILE CARD
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Text(
                    text = "My Library",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // VIP Member Card
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, VipGoldBright.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenVipDialog() }
                        .testTag("library_vip_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = VipGoldBright,
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.WorkspacePremium,
                                            contentDescription = "VIP",
                                            tint = ObsidianDark,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = "VIP LIFETIME FREE",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = VipGoldBright,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "Member ID: DR-984210",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            // Coin Balance
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DarkSurfaceCard,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🪙 ${vipStatus.coinsBalance}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VipGoldBright
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Daily Check-in Button
                        Button(
                            onClick = onClaimDailyCoins,
                            enabled = !vipStatus.dailyCheckinClaimed,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!vipStatus.dailyCheckinClaimed) ReelRed else DarkSurfaceCard,
                                contentColor = if (!vipStatus.dailyCheckinClaimed) TextPrimary else TextMuted
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Claim",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (!vipStatus.dailyCheckinClaimed) "Daily Check-in: Claim 1,000 Coins" else "Today's VIP Bonus Claimed ✓",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Tabs: My List & Watch History
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = TextPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = ReelRed,
                            height = 3.dp
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 14.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) TextPrimary else TextMuted
                                )
                            }
                        )
                    }
                }
            }
        }

        // TAB CONTENT
        if (selectedTab == 0) {
            // MY LIST
            if (bookmarkedDramas.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Empty",
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Your list is empty",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Bookmark your favorite drama reels to watch anytime.",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            } else {
                items(bookmarkedDramas) { drama ->
                    LibraryDramaRow(
                        drama = drama,
                        onPlay = { onPlayDrama(drama, 0) },
                        onDetail = { onOpenDramaDetail(drama) }
                    )
                }
            }
        } else {
            // WATCH HISTORY
            if (watchProgress.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "History",
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No watch history yet",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Dramas you watch will appear here with saved progress.",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            } else {
                items(watchProgress) { progress ->
                    val drama = dramas.find { it.id == progress.dramaId }
                    if (drama != null) {
                        WatchHistoryRow(
                            drama = drama,
                            progress = progress,
                            onResume = { onPlayDrama(drama, (progress.episodeNumber - 1).coerceAtLeast(0)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryDramaRow(
    drama: DramaSeries,
    onPlay: () -> Unit,
    onDetail: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DarkSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onDetail() }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 65.dp, height = 90.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = painterResource(id = drama.posterRes),
                    contentDescription = drama.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = drama.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${drama.category} • ${drama.totalEpisodes} Eps",
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = VipGoldBright,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${drama.rating} • ${drama.viewsCountFormatted} views",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            IconButton(
                onClick = onPlay,
                modifier = Modifier
                    .background(ReelRed, CircleShape)
                    .size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun WatchHistoryRow(
    drama: DramaSeries,
    progress: WatchProgressEntity,
    onResume: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DarkSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onResume() }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 65.dp, height = 90.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = painterResource(id = drama.posterRes),
                    contentDescription = drama.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = drama.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = ReelCrimson
                ) {
                    Text(
                        text = "Watched Episode ${progress.episodeNumber}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Total ${drama.totalEpisodes} Episodes • VIP Free",
                    fontSize = 11.sp,
                    color = VipGoldBright
                )
            }

            IconButton(
                onClick = onResume,
                modifier = Modifier
                    .background(ReelRed, CircleShape)
                    .size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Resume",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
