package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    isDarkTheme: Boolean,
    isAdminLoggedIn: Boolean,
    onToggleDarkTheme: () -> Unit,
    onAdminLoginClick: () -> Unit,
    onAdminDashboardClick: () -> Unit,
    onUpgradeTier: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var notifyReleases by remember { mutableStateOf(userProfile.notifyOnReleases) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))

            // User Profile Header Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = CrimsonRed,
                            modifier = Modifier.size(60.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = userProfile.name.take(1),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = userProfile.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = AmberGold
                                ) {
                                    Text(
                                        text = "VIP PRO",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = userProfile.email,
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Plan: ${userProfile.tier} (Unrestricted 4K HDR)",
                                fontSize = 11.sp,
                                color = CyberCyan,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Subscription tier selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Cinema Ultra 4K", "VIP Gold", "Free Member").forEach { tier ->
                            val isSelected = userProfile.tier == tier
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) CrimsonRed else SurfaceVariantDark,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onUpgradeTier(tier) }
                            ) {
                                Text(
                                    text = tier,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else TextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Streaming & Download Preferences
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text("Streaming & Hub Preferences", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CardBackground,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Dark theme toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.DarkMode, contentDescription = null, tint = AmberGold)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("OLED Obsidian Dark Theme", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text("Ultra-deep cinema black for AMOLED displays", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { onToggleDarkTheme() },
                            colors = SwitchDefaults.colors(checkedThumbColor = CrimsonRed, checkedTrackColor = CrimsonRed.copy(alpha = 0.3f))
                        )
                    }

                    HorizontalDivider(color = BorderSubtle, modifier = Modifier.padding(vertical = 12.dp))

                    // Push notifications toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = CyberCyan)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Release Radar Push Notifications", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text("Get alerted when new 4K episodes release", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                        Switch(
                            checked = notifyReleases,
                            onCheckedChange = { notifyReleases = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyberCyan, checkedTrackColor = CyberCyan.copy(alpha = 0.3f))
                        )
                    }

                    HorizontalDivider(color = BorderSubtle, modifier = Modifier.padding(vertical = 12.dp))

                    // Audio track preference
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = AmberGold)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Default Audio Track", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text("Dual Audio [Bengali + English]", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                        Text("Configured", fontSize = 11.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Admin Access Section
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Platform Administration & Security", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = CrimsonRed)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("ADMIN CONTROL PANEL", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                            Text(
                                if (isAdminLoggedIn) "Logged in as pikubalur@gmail.com (Super Admin)"
                                else "Restricted access: pikubalur@gmail.com",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isAdminLoggedIn) {
                        Button(
                            onClick = onAdminDashboardClick,
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_open_dashboard_button")
                        ) {
                            Icon(imageVector = Icons.Default.Dashboard, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open Admin Control Dashboard", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = onAdminLoginClick,
                            colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_portal_login_button")
                        ) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Admin Secure Sign-In", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
