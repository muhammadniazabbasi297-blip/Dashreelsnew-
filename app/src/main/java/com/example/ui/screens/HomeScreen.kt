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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DramaSeries
import com.example.data.model.VipStatus
import com.example.data.model.WatchProgressEntity
import com.example.ui.components.DramaCard
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
fun HomeScreen(
    dramas: List<DramaSeries>,
    vipStatus: VipStatus,
    watchProgress: List<WatchProgressEntity>,
    bookmarkedIds: Set<String>,
    selectedCategory: String,
    searchQuery: String,
    onCategorySelected: (String) -> Unit,
    onSearchChanged: (String) -> Unit,
    onDramaClick: (DramaSeries) -> Unit,
    onPlayDrama: (DramaSeries, Int) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onOpenVipDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("All", "CEO Romance", "Urban Action", "Rebirth & Revenge", "Thriller Romance")

    val filteredDramas = dramas.filter { drama ->
        val matchesCategory = selectedCategory == "All" || drama.category.contains(selectedCategory, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                drama.title.contains(searchQuery, ignoreCase = true) ||
                drama.tags.any { it.contains(searchQuery, ignoreCase = true) }
        matchesCategory && matchesSearch
    }

    val heroDrama = dramas.firstOrNull() ?: return

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDark),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // TOP HEADER: Branding & VIP Free Badge
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo & Slogan
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = ReelCrimson,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Logo",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "DASHREELS",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "PREMIUM REELS FREE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                color = ReelRed
                            )
                        }
                    }

                    // VIP Status Gold Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = VipGold.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VipGoldBright),
                        modifier = Modifier
                            .clickable { onOpenVipDialog() }
                            .testTag("home_vip_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "VIP Status",
                                tint = VipGoldBright,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "VIP FREE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = VipGoldBright
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChanged,
                    placeholder = {
                        Text(
                            text = "Search billionaire, revenge, action...",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("home_search_input"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedBorderColor = ReelRed,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { category ->
                        val isSelected = category == selectedCategory
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) ReelRed else DarkSurfaceVariant,
                            border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)) else null,
                            modifier = Modifier
                                .clickable { onCategorySelected(category) }
                                .testTag("category_chip_$category")
                        ) {
                            Text(
                                text = category,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }
        }

        // HERO FEATURED BANNER
        if (searchQuery.isBlank() && selectedCategory == "All") {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                        .clickable { onDramaClick(heroDrama) }
                        .testTag("hero_banner")
                ) {
                    Image(
                        painter = painterResource(id = heroDrama.posterRes),
                        contentDescription = heroDrama.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Cinematic Gradient Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.4f),
                                        Color.Black.copy(alpha = 0.95f)
                                    ),
                                    startY = 100f
                                )
                            )
                    )

                    // Hero Content
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        // Trending Badge
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ReelCrimson
                            ) {
                                Text(
                                    text = "🔥 #1 TRENDING REEL",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = VipGoldBright
                            ) {
                                Text(
                                    text = "100% VIP FREE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ObsidianDark,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = heroDrama.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = heroDrama.synopsis,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Actions: Play Now + My List
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { onPlayDrama(heroDrama, 0) },
                                colors = ButtonDefaults.buttonColors(containerColor = ReelRed),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(44.dp)
                                    .testTag("hero_play_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Watch Ep. 1",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            val isBookmarked = bookmarkedIds.contains(heroDrama.id)
                            OutlinedButton(
                                onClick = { onToggleBookmark(heroDrama.id) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.Black.copy(alpha = 0.4f),
                                    contentColor = if (isBookmarked) VipGoldBright else TextPrimary
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isBookmarked) VipGoldBright else Color.White.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("hero_bookmark_button")
                            ) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "My List",
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
                    }
                }
            }
        }

        // CONTINUE WATCHING ROW (If user has progress)
        if (watchProgress.isNotEmpty() && searchQuery.isBlank()) {
            item {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Continue Watching",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(watchProgress) { progress ->
                            val drama = dramas.find { it.id == progress.dramaId }
                            if (drama != null) {
                                Box(
                                    modifier = Modifier
                                        .width(150.dp)
                                        .height(110.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(DarkSurfaceVariant)
                                        .clickable { onPlayDrama(drama, (progress.episodeNumber - 1).coerceAtLeast(0)) }
                                ) {
                                    Image(
                                        painter = painterResource(id = drama.posterRes),
                                        contentDescription = drama.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                                )
                                            )
                                    )

                                    // Center Play Icon
                                    Surface(
                                        shape = CircleShape,
                                        color = ReelRed,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .align(Alignment.Center)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Resume",
                                                tint = TextPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    // Bottom Text
                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(6.dp)
                                    ) {
                                        Text(
                                            text = drama.title,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Ep ${progress.episodeNumber} / ${drama.totalEpisodes}",
                                            fontSize = 9.sp,
                                            color = VipGoldBright,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // TOP RANKED DRAMAS
        item {
            Column(modifier = Modifier.padding(top = 20.dp)) {
                Text(
                    text = "🔥 Top 10 Weekly Buzz",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    itemsIndexed(filteredDramas) { index, drama ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${index + 1}",
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Black,
                                color = ReelRed.copy(alpha = 0.9f),
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            DramaCard(
                                drama = drama,
                                onClick = { onDramaClick(drama) },
                                cardWidth = 125.dp,
                                cardHeight = 175.dp
                            )
                        }
                    }
                }
            }
        }

        // ALL DRAMAS / EXPLORE SHELF
        item {
            Column(modifier = Modifier.padding(top = 20.dp)) {
                Text(
                    text = if (searchQuery.isNotBlank()) "Search Results (${filteredDramas.size})" else "Trending Short Drama Reels",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredDramas) { drama ->
                        DramaCard(
                            drama = drama,
                            onClick = { onDramaClick(drama) }
                        )
                    }
                }
            }
        }

        // VIP EXCLUSIVE FREE PERKS PROMO CARD
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, VipGold.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
                    .clickable { onOpenVipDialog() }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = VipGoldBright,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "VIP Free",
                                tint = ObsidianDark,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DashReels VIP: 100% Free",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = VipGoldBright
                        )
                        Text(
                            text = "Enjoy unlimited 4K streaming, zero ad interruptions, and all full seasons unlocked.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
