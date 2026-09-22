package com.example.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DramaSeries
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ReelCrimson
import com.example.ui.theme.ReelRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.VipGold
import com.example.ui.theme.VipGoldBright

@Composable
fun DramaCard(
    drama: DramaSeries,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cardWidth: Dp = 135.dp,
    cardHeight: Dp = 195.dp
) {
    Column(
        modifier = modifier
            .width(cardWidth)
            .clickable { onClick() }
            .testTag("drama_card_${drama.id}")
    ) {
        // Poster Box
        Box(
            modifier = Modifier
                .width(cardWidth)
                .height(cardHeight)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
        ) {
            Image(
                painter = painterResource(id = drama.posterRes),
                contentDescription = drama.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Scrim gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.4f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Top Badges: VIP Free + Rating
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Golden VIP Free Badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = VipGoldBright
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "VIP",
                            tint = Color.Black,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "FREE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }

                // Rating
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${drama.rating}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Bottom Badges: Total Episodes
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "${drama.totalEpisodes} Eps",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Title
        Text(
            text = drama.title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Category Tag
        Text(
            text = drama.category,
            fontSize = 11.sp,
            color = TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
