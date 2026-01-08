package com.example.mindfulgrowth.ui.screens.customize

import android.graphics.BlurMaskFilter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.mindfulgrowth.R

// --- COLORS (Crimson Glass Palette) ---
private val CrimsonCore = Color(0xFFFF0007)
private val VoidBlack = Color(0xFF050505)
private val TextPrimary = Color.White
private val TextSecondary = Color.White.copy(alpha = 0.7f)

// --- TYPOGRAPHY ---
private val PixelFont = FontFamily.Monospace

// --- DATA MODELS ---
data class GrowthItem(
    val id: String,
    val name: String,
    val buff: String,
    val icon: ImageVector,
    val cost: Int,
    val isOwned: Boolean = false,
    val isSelected: Boolean = false
)

data class CustomizeUiState(
    val userCredits: Int = 1250,
    val selectedFilter: String = "All",
    val items: List<GrowthItem> = emptyList()
)

// --- VIEW MODEL ---
class CustomizeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CustomizeUiState())
    val uiState: StateFlow<CustomizeUiState> = _uiState.asStateFlow()

    init {
        loadItems()
    }

    private fun loadItems() {
        val mockItems = listOf(
            GrowthItem("1", "NEON SAPLING", "Base Growth", Icons.Rounded.Park, 0, isOwned = true, isSelected = true),
            GrowthItem("2", "DIGITAL BLOOM", "Focus +12%", Icons.Rounded.LocalFlorist, 500, isOwned = false),
            GrowthItem("3", "CYBER BONSAI", "Zen +20%", Icons.Rounded.NaturePeople, 1000, isOwned = false),
            GrowthItem("4", "GLITCH FERN", "XP Boost x1.5", Icons.Rounded.Forest, 1500, isOwned = false),
            GrowthItem("5", "PIXEL CACTUS", "Resilience +10", Icons.Rounded.Spa, 2000, isOwned = false),
            GrowthItem("6", "VOID ROSE", "Aesthetic Only", Icons.Rounded.FilterVintage, 3000, isOwned = false),
            GrowthItem("7", "DATA TREE", "Network +50", Icons.Rounded.AccountTree, 5000, isOwned = false),
            GrowthItem("8", "QUANTUM LEAF", "Time Warp -10%", Icons.Rounded.EnergySavingsLeaf, 8000, isOwned = false)
        )
        _uiState.value = _uiState.value.copy(items = mockItems)
    }

    fun selectItem(itemId: String) {
        val currentItems = _uiState.value.items.map {
            if (it.id == itemId && it.isOwned) {
                it.copy(isSelected = true)
            } else if (it.isOwned) {
                it.copy(isSelected = false)
            } else {
                it
            }
        }
        _uiState.value = _uiState.value.copy(items = currentItems)
    }

    fun setFilter(filter: String) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }
}

// --- UI SECTION ---
@Composable
fun CustomizeScreen(
    modifier: Modifier = Modifier,
    viewModel: CustomizeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val filters = listOf("All", "Owned", "Rare", "Animated")

    // --- HINT LOGIC ---
    var showHint by remember { mutableStateOf(false) }
    var hintTimer by remember { mutableLongStateOf(0L) }

    LaunchedEffect(hintTimer) {
        if (showHint) {
            delay(2000)
            showHint = false
        }
    }

    val onItemClick: (GrowthItem) -> Unit = { item ->
        if (item.isOwned) {
            viewModel.selectItem(item.id)
        } else {
            showHint = true
            hintTimer = System.currentTimeMillis()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Removed Background Image and Scrim to use MainActivity's global background

        // --- MAIN GRID ---
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(top = 180.dp, start = 16.dp, end = 16.dp, bottom = 150.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(
                items = uiState.items,
                span = { index, _ ->
                    GridItemSpan(if (index == 0) 2 else 1)
                }
            ) { index, item ->
                if (index == 0) {
                    HeroGlassCard(
                        item = item,
                        onClick = { onItemClick(item) }
                    )
                } else {
                    StandardGlassCard(
                        item = item,
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }

        // --- HEADER OVERLAY ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(VoidBlack.copy(alpha = 0.99f), VoidBlack.copy(alpha = 0.0f)),
                        startY = 0f,
                        endY = 500f
                    )
                )
                .drawBehind {
                    drawIntoCanvas { canvas ->
                        // FIX: Explicitly use Compose Paint
                        val paint = androidx.compose.ui.graphics.Paint()
                        // FIX: Apply mask filter via framework paint
                        paint.asFrameworkPaint().maskFilter = BlurMaskFilter(300f, BlurMaskFilter.Blur.NORMAL)
                        // FIX: Draw using the Compose Canvas and Compose Paint
                        canvas.drawRect(0f, 0f, size.width, size.height, paint)
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 20.dp, end = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GROWTH_LAB",
                        style = MaterialTheme.typography.displaySmall,
                        color = TextPrimary,
                        fontFamily = PixelFont,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    GlassChip(
                        text = "${uiState.userCredits}",
                        icon = Icons.Rounded.MonetizationOn,
                        isHighlight = true
                    )
                }

                Spacer(Modifier.height(20.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filters.size) { index ->
                        val filter = filters[index]
                        FilterChip(
                            text = filter,
                            isSelected = uiState.selectedFilter == filter,
                            onClick = { viewModel.setFilter(filter) }
                        )
                    }
                }
            }
        }

        // --- BOTTOM GRADIENT ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(250.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 1f))
                    )
                )
        )

        // --- HINT ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 104.dp)
        ) {
            AnimatedVisibility(
                visible = showHint,
                enter = fadeIn(),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                GlassHintPill(text = "LONG-PRESS FOR PREVIEW")
            }
        }
    }
}

// --- GLASS CARDS ---

@Composable
fun HeroGlassCard(
    item: GrowthItem,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "iconPulse"
    )

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(onClick = onClick),
        borderColor = CrimsonCore,
        glowAlpha = 0.06f,
        borderWidth = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.Center) {
                GlassChip(text = "EQUIPPED", icon = Icons.Rounded.Check, isHighlight = true)
                Spacer(Modifier.height(16.dp))
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontFamily = PixelFont,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = item.buff,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CrimsonCore,
                    fontFamily = PixelFont
                )
            }

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(pulseScale),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun StandardGlassCard(
    item: GrowthItem,
    onClick: () -> Unit
) {
    val borderColor = if (item.isSelected) CrimsonCore else Color.White.copy(alpha = 0.5f)
    val glowAlpha = if (item.isSelected) 0.7f else 0.0f
    val borderWidth = if (item.isSelected) 2.dp else 1.dp

    GlassCard(
        modifier = Modifier
            .aspectRatio(0.75f)
            .clickable(onClick = onClick),
        borderColor = borderColor,
        glowAlpha = glowAlpha,
        borderWidth = borderWidth
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                if (item.isOwned) {
                    Icon(Icons.Rounded.CheckCircle, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                } else {
                    Text(
                        text = "${item.cost}",
                        color = CrimsonCore,
                        fontFamily = PixelFont,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = if (item.isSelected) CrimsonCore else TextPrimary,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    fontFamily = PixelFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = item.buff,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontFamily = PixelFont,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }
    }
}

// --- CORE VISUAL COMPONENTS ---

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color? = null,
    glowAlpha: Float = 0.15f,
    borderWidth: Dp = 1.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .neonGlow(color = borderColor ?: CrimsonCore, radius = 100f, alpha = glowAlpha)
            .clip(RoundedCornerShape(24.dp))
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.9f))
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.05f), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .border(
                    width = borderWidth,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            (borderColor ?: Color.White).copy(alpha = 0.4f),
                            Color.Transparent,
                            (borderColor ?: Color.Black).copy(alpha = 0.4f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        )
        Box(modifier = Modifier.fillMaxWidth(), content = content)
    }
}

@Composable
fun FilterChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val bg = if (isSelected) CrimsonCore.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
    val border = if (isSelected) CrimsonCore else Color.White.copy(alpha = 0.2f)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = text.uppercase(),
            color = if (isSelected) CrimsonCore else TextSecondary,
            fontFamily = PixelFont,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun GlassChip(text: String, icon: ImageVector? = null, isHighlight: Boolean = false) {
    val bg = if (isHighlight) CrimsonCore.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.05f)
    val border = if (isHighlight) CrimsonCore.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.2f)
    val textColor = if (isHighlight) CrimsonCore else TextSecondary

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, null, tint = textColor, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
            }
            Text(
                text = text,
                color = textColor,
                fontFamily = PixelFont,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GlassHintPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.Black.copy(alpha = 0.6f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(50))
            .padding(horizontal = 24.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            color = TextSecondary,
            fontFamily = PixelFont,
            fontSize = 10.sp,
            letterSpacing = 1.sp
        )
    }
}

// --- UTILS ---
fun Modifier.neonGlow(
    color: Color,
    radius: Float = 20f,
    alpha: Float = 1f
) = this.drawBehind {
    drawIntoCanvas { canvas ->
        // FIX: Explicitly use Compose Paint
        val paint = androidx.compose.ui.graphics.Paint()
        paint.color = color.copy(alpha = alpha)

        // FIX: Access framework paint correctly for effects
        paint.asFrameworkPaint().maskFilter = BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)

        val center = Offset(size.width / 2, size.height / 2)
        val size = size.minDimension / 1.5f
        canvas.drawCircle(center, size, paint)
    }
}
