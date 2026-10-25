package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ExpressiveShapes
import com.example.ui.theme.ExpressiveTheme

enum class MainTab(val title: String, val icon: ImageVector) {
    APK_VAULT("APK Vault", Icons.Default.Archive),
    INSTALLED_APPS("Installed Apps", Icons.Default.Apps)
}

@Composable
fun ExpressiveSegmentedSelector(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    vaultCount: Int,
    installedCount: Int,
    modifier: Modifier = Modifier
) {
    val tabs = MainTab.values()
    val total = tabs.size

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp), // Spacing between segments: 4dp
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = tab == selectedTab
            val segmentShape = ExpressiveShapes.selectorSegmentShape(index, total, isSelected)

            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) {
                    ExpressiveTheme.colors.selectorSelected
                } else {
                    ExpressiveTheme.colors.selectorUnselected
                },
                animationSpec = tween(durationMillis = 200),
                label = "segment_bg"
            )

            val contentColor by animateColorAsState(
                targetValue = if (isSelected) {
                    ExpressiveTheme.colors.onSelectorSelected
                } else {
                    ExpressiveTheme.colors.onSelectorUnselected
                },
                animationSpec = tween(durationMillis = 200),
                label = "segment_fg"
            )

            val count = if (tab == MainTab.APK_VAULT) vaultCount else installedCount

            Box(
                modifier = Modifier
                    .weight(1f) // Equal width distribution
                    .height(44.dp)
                    .clip(segmentShape)
                    .background(backgroundColor)
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 8.dp)
                    .testTag("tab_segment_${tab.name.lowercase()}"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        tint = contentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = tab.title,
                        color = contentColor,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (count > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (isSelected) ExpressiveTheme.colors.nestedElementSurface
                                    else ExpressiveTheme.colors.buttonTertiarySubtle
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = count.toString(),
                                color = if (isSelected) ExpressiveTheme.colors.textPrimary
                                else ExpressiveTheme.colors.onButtonTertiarySubtle,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
