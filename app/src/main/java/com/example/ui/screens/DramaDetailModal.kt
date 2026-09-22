package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DramaSeries
import com.example.data.sample.SampleDramaData
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
fun DramaDetailModal(
    drama: DramaSeries,
    isBookmarked: Boolean,
    onBack: () -> Unit,
    onPlayEpisode: (Int) -> Unit,
    onToggleBookmark: () -> Unit,
    onOpenVipDialog: () -> Unit
) {
    val episodes = SampleDramaData.getEpisodesForDrama(drama.id)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .navigationBarsPadding()
    ) {
        // Hero Poster Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
            ) {
                Image(
                    painter = painterResource(id = drama.posterRes),
                    contentDescription = drama.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark Gradients
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.7f),
                                    Color.Transparent,
                                    ObsidianDark
                                )
                            )
                        )
                )

                // Top Bar with Back Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = VipGoldBright,
                        modifier = Modifier.clickable { onOpenVipDialog() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "VIP",
                                tint = ObsidianDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "VIP FREE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = ObsidianDark
                            )
                        }
                    }
                }
            }
        }

        // Drama Metadata & Actions
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = drama.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Stats Row: Rating, Views, Category, Year
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ReelCrimson
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = VipGoldBright,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${drama.rating}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    Text(
                        text = "👁 ${drama.viewsCountFormatted} views",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Text(
                        text = "• ${drama.category}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Text(
                        text = "• ${drama.totalEpisodes} Episodes",
                        fontSize = 12.sp,
                        color = VipGoldBright,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons: Play Ep 1 + Bookmark
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { onPlayEpisode(0) },
                        colors = ButtonDefaults.buttonColors(containerColor = ReelRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.4f)
                            .height(48.dp)
                            .testTag("detail_watch_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Play Episode 1",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    OutlinedButton(
                        onClick = onToggleBookmark,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = DarkSurfaceVariant,
                            contentColor = if (isBookmarked) VipGoldBright else TextPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isBookmarked) VipGoldBright else Color.White.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("detail_bookmark_button")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBookmarked) "Saved" else "+ My List",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // VIP Free Perks Notice
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = VipGold.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VipGold.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "VIP Unlocked",
                            tint = VipGoldBright,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "VIP Status Active: All ${drama.totalEpisodes} Episodes Unlocked",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = VipGoldBright
                            )
                            Text(
                                text = "Zero coins required. Watch in high-speed 4K HDR.",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Synopsis
                Text(
                    text = "Storyline",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = drama.synopsis,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Director & Cast
                Text(
                    text = "Director: ${drama.director}",
                    fontSize = 12.sp,
                    color = TextMuted
                )
                Text(
                    text = "Cast: ${drama.cast.joinToString(", ")}",
                    fontSize = 12.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Episodes Grid Title
                Text(
                    text = "Episodes (${episodes.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Episodes Grid in Detail Modal
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                // Responsive grid rows of 4
                episodes.chunked(4).forEach { rowEpisodes ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowEpisodes.forEach { ep ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(54.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DarkSurfaceVariant)
                                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                                    .clickable { onPlayEpisode(ep.episodeNumber - 1) }
                                    .testTag("detail_episode_${ep.episodeNumber}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Ep ${ep.episodeNumber}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "FREE VIP",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = VipGoldBright
                                    )
                                }
                            }
                        }
                        // Pad row if less than 4
                        for (i in 0 until (4 - rowEpisodes.size)) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
