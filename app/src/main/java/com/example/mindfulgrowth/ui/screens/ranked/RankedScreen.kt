
package com.example.mindfulgrowth.ui.screens.ranked

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Rival(
    val rank: Int,
    val name: String,
    val tier: RankTier,
    val focusTime: String
)

@Composable
fun RankedScreen() {
    val rivals = remember {
        (1..50).map {
            Rival(
                rank = it,
                name = "User_${1000 + it * 3}",
                tier = when {
                    it <= 3 -> RankTier.VOID
                    it <= 10 -> RankTier.DIAMOND
                    it <= 25 -> RankTier.GOLD
                    else -> RankTier.IRON
                },
                focusTime = "${(50 - it) * 100 + (it * 13)} min"
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "GLOBAL LEAGUE",
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 24.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(bottom = 150.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(rivals) { index, rival ->
                    LeaderboardItem(
                        rank = rival.rank,
                        name = rival.name,
                        tier = rival.tier,
                        focusTime = rival.focusTime,
                        isTopThree = index < 3
                    )
                }
            }
        }
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            UserPinnedCard(rank = 42, pointsToNext = 120)
        }
    }
}
