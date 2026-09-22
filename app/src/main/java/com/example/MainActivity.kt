package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.VipStatus
import com.example.ui.components.VipFreeDialog
import com.example.ui.screens.DramaDetailModal
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.ReelsFeedScreen
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.ReelCrimson
import com.example.ui.theme.ReelRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.VipGold
import com.example.ui.theme.VipGoldBright
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.DashReelsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DashReelsApp()
            }
        }
    }
}

@Composable
fun DashReelsApp(
    viewModel: DashReelsViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val selectedDramaForDetail by viewModel.selectedDramaForDetail.collectAsState()
    val vipStatus by viewModel.vipStatus.collectAsState()
    val bookmarkedIds by viewModel.bookmarkedIds.collectAsState()
    val watchProgressList by viewModel.watchProgressList.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val playerState by viewModel.playerState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark)
    ) {
        if (selectedDramaForDetail != null) {
            val drama = selectedDramaForDetail!!
            DramaDetailModal(
                drama = drama,
                isBookmarked = bookmarkedIds.contains(drama.id),
                onBack = { viewModel.closeDramaDetail() },
                onPlayEpisode = { epIdx -> viewModel.playDrama(drama, epIdx) },
                onToggleBookmark = { viewModel.toggleBookmark(drama.id) },
                onOpenVipDialog = { viewModel.showVipDialog(true) }
            )
        } else {
            Scaffold(
                containerColor = ObsidianDark,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                bottomBar = {
                    DashReelsBottomBar(
                        currentTab = currentTab,
                        onTabSelect = { viewModel.setTab(it) }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = if (currentTab == AppTab.REELS) 0.dp else innerPadding.calculateBottomPadding())
                ) {
                    when (currentTab) {
                        AppTab.HOME -> {
                            HomeScreen(
                                dramas = viewModel.allDramas,
                                vipStatus = vipStatus,
                                watchProgress = watchProgressList,
                                bookmarkedIds = bookmarkedIds,
                                selectedCategory = selectedCategory,
                                searchQuery = searchQuery,
                                onCategorySelected = { viewModel.setCategory(it) },
                                onSearchChanged = { viewModel.setSearchQuery(it) },
                                onDramaClick = { viewModel.openDramaDetail(it) },
                                onPlayDrama = { drama, epIdx -> viewModel.playDrama(drama, epIdx) },
                                onToggleBookmark = { viewModel.toggleBookmark(it) },
                                onOpenVipDialog = { viewModel.showVipDialog(true) }
                            )
                        }

                        AppTab.REELS -> {
                            ReelsFeedScreen(viewModel = viewModel)
                        }

                        AppTab.LIBRARY -> {
                            LibraryScreen(
                                dramas = viewModel.allDramas,
                                vipStatus = vipStatus,
                                bookmarkedIds = bookmarkedIds,
                                watchProgress = watchProgressList,
                                onPlayDrama = { drama, epIdx -> viewModel.playDrama(drama, epIdx) },
                                onOpenDramaDetail = { viewModel.openDramaDetail(it) },
                                onClaimDailyCoins = { viewModel.claimDailyBonus() },
                                onOpenVipDialog = { viewModel.showVipDialog(true) }
                            )
                        }
                    }
                }
            }
        }

        // Global VIP dialog if triggered
        if (playerState.showVipDialog) {
            VipFreeDialog(
                vipStatus = vipStatus,
                onClaimBonus = { viewModel.claimDailyBonus() },
                onDismiss = { viewModel.showVipDialog(false) }
            )
        }
    }
}

@Composable
fun DashReelsBottomBar(
    currentTab: AppTab,
    onTabSelect: (AppTab) -> Unit
) {
    NavigationBar(
        containerColor = DarkSurface.copy(alpha = 0.96f),
        contentColor = TextPrimary,
        tonalElevation = 8.dp,
        modifier = Modifier
            .navigationBarsPadding()
            .height(64.dp)
            .border(
                width = 0.8.dp,
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .testTag("main_navigation_bar")
    ) {
        // HOME TAB
        NavigationBarItem(
            selected = currentTab == AppTab.HOME,
            onClick = { onTabSelect(AppTab.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentTab == AppTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Discover",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Discover",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == AppTab.HOME) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ReelRed,
                selectedTextColor = ReelRed,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = ReelRed.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_item_home")
        )

        // REELS TAB (Center highlight)
        NavigationBarItem(
            selected = currentTab == AppTab.REELS,
            onClick = { onTabSelect(AppTab.REELS) },
            icon = {
                Box(contentAlignment = Alignment.TopEnd) {
                    Icon(
                        imageVector = if (currentTab == AppTab.REELS) Icons.Filled.PlayCircle else Icons.Outlined.PlayCircle,
                        contentDescription = "Watch Reels",
                        modifier = Modifier.size(26.dp)
                    )
                    // Tiny VIP Free Dot
                    Surface(
                        shape = CircleShape,
                        color = VipGoldBright,
                        modifier = Modifier.size(6.dp)
                    ) {}
                }
            },
            label = {
                Text(
                    text = "Reels",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == AppTab.REELS) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ReelRed,
                selectedTextColor = ReelRed,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = ReelRed.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_item_reels")
        )

        // LIBRARY TAB
        NavigationBarItem(
            selected = currentTab == AppTab.LIBRARY,
            onClick = { onTabSelect(AppTab.LIBRARY) },
            icon = {
                Icon(
                    imageVector = if (currentTab == AppTab.LIBRARY) Icons.Filled.VideoLibrary else Icons.Outlined.VideoLibrary,
                    contentDescription = "My Library",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "My Library",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == AppTab.LIBRARY) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ReelRed,
                selectedTextColor = ReelRed,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = ReelRed.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_item_library")
        )
    }
}

// Retained for GreetingScreenshotTest compatibility
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier, color = TextPrimary)
}
