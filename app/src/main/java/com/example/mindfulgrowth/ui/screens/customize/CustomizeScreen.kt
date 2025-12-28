package com.example.mindfulgrowth.ui.screens.customize

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mindfulgrowth.ui.components.*
import com.example.mindfulgrowth.ui.theme.MindfulTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Data Models
data class TreeItem(
    val id: String,
    val name: String,
    val emoji: String,
    val rarity: TreeRarity,
    val price: Int,
    val description: String,
    val isUnlocked: Boolean = false,
    val isSelected: Boolean = false
)

enum class TreeRarity(val color: Color, val label: String) {
    COMMON(Color(0xFF9E9E9E), "Common"),
    RARE(Color(0xFF2196F3), "Rare"),
    EPIC(Color(0xFF9C27B0), "Epic"),
    LEGENDARY(Color(0xFFFFD700), "Legendary")
}

// ViewModel
data class CustomizeUiState(
    val trees: List<TreeItem> = emptyList(),
    val userCoins: Int = 1500,
    val selectedTreeId: String? = null,
    val isLoading: Boolean = false,
    val showPurchaseDialog: Boolean = false,
    val pendingPurchase: TreeItem? = null,
    val purchaseSuccess: Boolean = false
)

class CustomizeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CustomizeUiState())
    val uiState: StateFlow<CustomizeUiState> = _uiState.asStateFlow()
    
    init {
        loadTrees()
    }
    
    private fun loadTrees() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // Simulate loading
            delay(500)
            
            val trees = listOf(
                TreeItem(
                    id = "oak",
                    name = "Oak Sapling",
                    emoji = "🌳",
                    rarity = TreeRarity.COMMON,
                    price = 0,
                    description = "Classic and sturdy",
                    isUnlocked = true,
                    isSelected = true
                ),
                TreeItem(
                    id = "pine",
                    name = "Pine Tree",
                    emoji = "🌲",
                    rarity = TreeRarity.COMMON,
                    price = 500,
                    description = "Evergreen beauty",
                    isUnlocked = false
                ),
                TreeItem(
                    id = "cherry",
                    name = "Cherry Blossom",
                    emoji = "🌸",
                    rarity = TreeRarity.RARE,
                    price = 1200,
                    description = "Delicate pink blooms",
                    isUnlocked = false
                ),
                TreeItem(
                    id = "palm",
                    name = "Palm Tree",
                    emoji = "🌴",
                    rarity = TreeRarity.RARE,
                    price = 1500,
                    description = "Tropical paradise",
                    isUnlocked = false
                ),
                TreeItem(
                    id = "bonsai",
                    name = "Bonsai",
                    emoji = "🎋",
                    rarity = TreeRarity.EPIC,
                    price = 2500,
                    description = "Ancient wisdom",
                    isUnlocked = false
                ),
                TreeItem(
                    id = "cactus",
                    name = "Desert Cactus",
                    emoji = "🌵",
                    rarity = TreeRarity.COMMON,
                    price = 300,
                    description = "Low maintenance",
                    isUnlocked = false
                ),
                TreeItem(
                    id = "maple",
                    name = "Maple Tree",
                    emoji = "🍁",
                    rarity = TreeRarity.RARE,
                    price = 1800,
                    description = "Autumn's glory",
                    isUnlocked = false
                ),
                TreeItem(
                    id = "golden",
                    name = "Golden Tree",
                    emoji = "✨",
                    rarity = TreeRarity.LEGENDARY,
                    price = 9999,
                    description = "Ultimate prestige",
                    isUnlocked = false
                )
            )
            
            _uiState.value = _uiState.value.copy(
                trees = trees,
                selectedTreeId = trees.find { it.isSelected }?.id,
                isLoading = false
            )
        }
    }
    
    fun selectTree(treeId: String) {
        val updatedTrees = _uiState.value.trees.map { tree ->
            tree.copy(isSelected = tree.id == treeId)
        }
        _uiState.value = _uiState.value.copy(
            trees = updatedTrees,
            selectedTreeId = treeId
        )
    }
    
    fun showPurchaseDialog(tree: TreeItem) {
        _uiState.value = _uiState.value.copy(
            showPurchaseDialog = true,
            pendingPurchase = tree
        )
    }
    
    fun dismissPurchaseDialog() {
        _uiState.value = _uiState.value.copy(
            showPurchaseDialog = false,
            pendingPurchase = null,
            purchaseSuccess = false
        )
    }
    
    fun purchaseTree() {
        viewModelScope.launch {
            val tree = _uiState.value.pendingPurchase ?: return@launch
            val currentCoins = _uiState.value.userCoins
            
            if (currentCoins >= tree.price) {
                // Deduct coins
                _uiState.value = _uiState.value.copy(
                    userCoins = currentCoins - tree.price,
                    purchaseSuccess = true
                )
                
                delay(500)
                
                // Unlock tree
                val updatedTrees = _uiState.value.trees.map {
                    if (it.id == tree.id) it.copy(isUnlocked = true) else it
                }
                
                _uiState.value = _uiState.value.copy(trees = updatedTrees)
                
                delay(1500)
                dismissPurchaseDialog()
            }
        }
    }
}

// Main Screen Composable
@Composable
fun CustomizeScreen(
    viewModel: CustomizeViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    
    CustomizeScreenContent(
        uiState = uiState,
        onTreeClick = { tree ->
            if (tree.isUnlocked) {
                viewModel.selectTree(tree.id)
            } else {
                viewModel.showPurchaseDialog(tree)
            }
        },
        onDismissDialog = viewModel::dismissPurchaseDialog,
        onConfirmPurchase = viewModel::purchaseTree,
        modifier = modifier
    )
}

@Composable
private fun CustomizeScreenContent(
    uiState: CustomizeUiState,
    onTreeClick: (TreeItem) -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmPurchase: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MindfulTheme.colors
    val spacing = MindfulTheme.spacing
    
    Box(modifier = modifier.fillMaxSize()) {
        // Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colors.gradientStart,
                                colors.gradientMid,
                                colors.gradientEnd
                            )
                        )
                    )
                }
        )
        
        FloatingParticles()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing.lg)
        ) {
            // Header
            CustomizeHeader(
                userCoins = uiState.userCoins,
                modifier = Modifier.padding(bottom = spacing.xl)
            )
            
            // Tree Grid
            if (uiState.isLoading) {
                LoadingGrid()
            } else {
                TreeGrid(
                    trees = uiState.trees,
                    selectedTreeId = uiState.selectedTreeId,
                    onTreeClick = onTreeClick
                )
            }
        }
        
        // Purchase Dialog
        if (uiState.showPurchaseDialog && uiState.pendingPurchase != null) {
            PurchaseDialog(
                tree = uiState.pendingPurchase,
                userCoins = uiState.userCoins,
                onDismiss = onDismissDialog,
                onConfirm = onConfirmPurchase,
                purchaseSuccess = uiState.purchaseSuccess
            )
        }
    }
}

@Composable
private fun CustomizeHeader(
    userCoins: Int,
    modifier: Modifier = Modifier
) {
    val colors = MindfulTheme.colors
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Customize Your Growth",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = colors.textGold
            )
            
            Text(
                text = "Choose your favorite tree",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        
        // Coin display
        CoinsDisplay(coins = userCoins)
    }
}

@Composable
private fun CoinsDisplay(
    coins: Int,
    modifier: Modifier = Modifier
) {
    val animatedCoins by animateIntAsState(
        targetValue = coins,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "coinAnimation"
    )
    
    GlassCard(
        modifier = modifier,
        cornerRadius = MindfulTheme.shapes.full,
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        bloom = true,
        bloomIntensity = 0.4f
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🪙",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = animatedCoins.toString(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MindfulTheme.colors.goldPrimary
            )
        }
    }
}

@Composable
private fun TreeGrid(
    trees: List<TreeItem>,
    selectedTreeId: String?,
    onTreeClick: (TreeItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(MindfulTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(MindfulTheme.spacing.md)
    ) {
        items(
            items = trees,
            key = { it.id }
        ) { tree ->
            TreeCard(
                tree = tree,
                isSelected = tree.id == selectedTreeId,
                onClick = { onTreeClick(tree) },
                modifier = Modifier.animateItemPlacement(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            )
        }
    }
}

@Composable
private fun TreeCard(
    tree: TreeItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MindfulTheme.colors
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardScale"
    )
    
    val borderColor by animateColorAsState(
        targetValue = when {
            isSelected -> colors.goldPrimary
            tree.isUnlocked -> colors.greenAccent.copy(alpha = 0.5f)
            else -> colors.glassBorder
        },
        label = "borderColor"
    )
    
    Box(
        modifier = modifier
            .aspectRatio(0.75f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        GlassCard(
            onClick = onClick,
            cornerRadius = MindfulTheme.shapes.large,
            bloom = isSelected,
            bloomIntensity = if (isSelected) 0.5f else 0.2f
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(MindfulTheme.spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Rarity badge
                RarityBadge(
                    rarity = tree.rarity,
                    modifier = Modifier.align(Alignment.End)
                )
                
                Spacer(Modifier.height(8.dp))
                
                // Tree emoji
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    tree.rarity.color.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tree.emoji,
                        style = MaterialTheme.typography.displayLarge,
                        modifier = Modifier.graphicsLayer {
                            alpha = if (tree.isUnlocked) 1f else 0.4f
                        }
                    )
                    
                    // Lock icon overlay
                    if (!tree.isUnlocked) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = colors.textSecondary,
                            modifier = Modifier
                                .size(32.dp)
                                .offset(y = 4.dp)
                        )
                    }
                }
                
                Spacer(Modifier.weight(1f))
                
                // Tree name
                Text(
                    text = tree.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = if (tree.isUnlocked) {
                        colors.textPrimary
                    } else {
                        colors.textSecondary
                    },
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
                
                Spacer(Modifier.height(4.dp))
                
                // Status (Price, Owned, Selected)
                TreeStatusChip(
                    tree = tree,
                    isSelected = isSelected
                )
            }
        }
        
        // Selection indicator
        AnimatedVisibility(
            visible = isSelected,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(colors.goldPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color(0xFF121212),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun RarityBadge(
    rarity: TreeRarity,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(rarity.color.copy(alpha = 0.2f))
            .border(
                width = 1.dp,
                color = rarity.color.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = rarity.label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = rarity.color
        )
    }
}

@Composable
private fun TreeStatusChip(
    tree: TreeItem,
    isSelected: Boolean
) {
    val colors = MindfulTheme.colors
    
    val (text, bgColor, textColor) = when {
        isSelected -> Triple("SELECTED", colors.goldPrimary, Color(0xFF121212))
        tree.isUnlocked -> Triple("OWNED", colors.greenAccent.copy(alpha = 0.2f), colors.greenAccent)
        else -> Triple("${tree.price} 🪙", colors.glassBackground, colors.goldPrimary)
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = textColor
        )
    }
}

@Composable
private fun LoadingGrid(
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(MindfulTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(MindfulTheme.spacing.md)
    ) {
        items(6) {
            Box(
                modifier = Modifier
                    .aspectRatio(0.75f)
                    .clip(RoundedCornerShape(MindfulTheme.shapes.large))
                    .background(MindfulTheme.colors.glassBackground)
            ) {
                ShimmerEffect()
            }
        }
    }
}

@Composable
private fun PurchaseDialog(
    tree: TreeItem,
    userCoins: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    purchaseSuccess: Boolean
) {
    val colors = MindfulTheme.colors
    val canAfford = userCoins >= tree.price
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.surfaceOverlay)
            .clickable(
                onClick = onDismiss,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clickable(
                    onClick = {},
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            bloom = true,
            bloomIntensity = 0.6f,
            contentPadding = PaddingValues(MindfulTheme.spacing.xl)
        ) {
            AnimatedContent(
                targetState = purchaseSuccess,
                transitionSpec = {
                    fadeIn() + scaleIn() with fadeOut() + scaleOut()
                },
                label = "purchaseState"
            ) { success ->
                if (success) {
                    // Success state
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier.size(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            PulsingGlow(
                                color = colors.greenAccent,
                                modifier = Modifier.size(120.dp)
                            )
                            Text(
                                text = "✓",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = colors.greenAccent
                            )
                        }
                        
                        Spacer(Modifier.height(MindfulTheme.spacing.lg))
                        
                        Text(
                            text = "Purchase Successful!",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = colors.textPrimary,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(Modifier.height(MindfulTheme.spacing.sm))
                        
                        Text(
                            text = "${tree.name} unlocked",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Purchase confirmation state
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = tree.emoji,
                            style = MaterialTheme.typography.displayLarge
                        )
                        
                        Spacer(Modifier.height(MindfulTheme.spacing.md))
                        
                        Text(
                            text = "Purchase ${tree.name}?",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = colors.textPrimary,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(Modifier.height(MindfulTheme.spacing.sm))
                        
                        Text(
                            text = tree.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(Modifier.height(MindfulTheme.spacing.lg))
                        
                        // Price display
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Price:",
                                style = MaterialTheme.typography.bodyLarge,
                                color = colors.textSecondary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🪙", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = tree.price.toString(),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = colors.goldPrimary
                                )
                            }
                        }
                        
                        Spacer(Modifier.height(MindfulTheme.spacing.sm))
                        
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Your balance:",
                                style = MaterialTheme.typography.bodyLarge,
                                color = colors.textSecondary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🪙", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = userCoins.toString(),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (canAfford) colors.greenAccent else colors.error
                                )
                            }
                        }
                        
                        if (!canAfford) {
                            Spacer(Modifier.height(MindfulTheme.spacing.md))
                            Text(
                                text = "Insufficient coins!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.error
                            )
                        }
                        
                        Spacer(Modifier.height(MindfulTheme.spacing.xl))
                        
                        // Action buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(MindfulTheme.spacing.md),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            GlassButton(
                                text = "Cancel",
                                onClick = onDismiss,
                                style = GlassButtonStyle.OUTLINED,
                                modifier = Modifier.weight(1f)
                            )
                            
                            GlassButton(
                                text = "Purchase",
                                onClick = onConfirm,
                                style = GlassButtonStyle.PRIMARY,
                                enabled = canAfford,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}