package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.VipStatus
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.ReelCrimson
import com.example.ui.theme.ReelRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VipGold
import com.example.ui.theme.VipGoldBright

@Composable
fun VipFreeDialog(
    vipStatus: VipStatus,
    onClaimBonus: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, VipGoldBright.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("vip_free_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Crown Hero Badge
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(VipGoldBright, VipGold.copy(alpha = 0.2f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "VIP Free Unlocked",
                        tint = ObsidianDark,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "DASHREELS VIP",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = VipGoldBright,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "Lifetime Premium 100% Free Unlocked",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Perks List
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PerkRow(
                        icon = Icons.Default.LockOpen,
                        title = "All Episodes Unlocked",
                        subtitle = "Binge full seasons with zero paywalls or coins"
                    )
                    PerkRow(
                        icon = Icons.Default.Hd,
                        title = "4K Ultra HD & Spatial Sound",
                        subtitle = "Maximum fidelity bitrate enabled"
                    )
                    PerkRow(
                        icon = Icons.Default.Block,
                        title = "100% Ad-Free Experience",
                        subtitle = "No interruptions or promotional popups"
                    )
                    PerkRow(
                        icon = Icons.Default.Download,
                        title = "Offline Fast Downloads",
                        subtitle = "Save reels directly for on-the-go viewing"
                    )
                    PerkRow(
                        icon = Icons.Default.CardGiftcard,
                        title = "Daily Free Bonus Coins",
                        subtitle = "Current Balance: ${vipStatus.coinsBalance} Coins"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Button(
                    onClick = {
                        onClaimBonus()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VipGoldBright,
                        contentColor = ObsidianDark
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("claim_vip_bonus_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Claimed",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (!vipStatus.dailyCheckinClaimed) "Claim +1,000 Daily Coins" else "Enjoy Premium Free",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Close",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted,
                    modifier = Modifier
                        .clickable { onDismiss() }
                        .padding(8.dp)
                        .testTag("dismiss_vip_dialog")
                )
            }
        }
    }
}

@Composable
private fun PerkRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = DarkSurfaceCard,
            modifier = Modifier.size(34.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = VipGoldBright,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextMuted
            )
        }
    }
}
