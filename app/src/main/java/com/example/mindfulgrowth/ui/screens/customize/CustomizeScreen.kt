package com.example.mindfulgrowth.ui.screens.customize

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Park
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mindfulgrowth.ui.components.*
import com.example.mindfulgrowth.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ... (Data Models & ViewModel are unchanged)

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
    Box(modifier = modifier.fillMaxSize().background(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF021024), // DarkNavy
                Color(0xFF052659)  // RichBlue
            )
        )
    )) {
        FloatingParticles()

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            CustomizeHeader(userCoins = uiState.userCoins, modifier = Modifier.padding(bottom = 24.dp))

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

        if (uiState.showPurchaseDialog && uiState.pendingPurchase != null) {
            PurchaseDialog(
                tree = uiState.pendingPurchase!!,
                userCoins = uiState.userCoins,
                onDismiss = onDismissDialog,
                onConfirm = onConfirmPurchase,
                purchaseSuccess = uiState.purchaseSuccess
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
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = trees, key = { it.id }) { tree ->
            TreeCard(
                tree = tree,
                isSelected = tree.id == selectedTreeId,
                onClick = { onTreeClick(tree) },
                modifier = Modifier.animateItemPlacement()
            )
        }
    }
}

@Composable
fun TreeCard(
    tree: TreeItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                Color(0xFF2A2D2D).copy(alpha = 0.9f)
            else
                Color(0xFF2A2D2D).copy(alpha = 0.7f)
        ),
        border = if (isSelected)
            BorderStroke(2.dp, Color(0xFFD4AF37)) // Gold border
        else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Tree name at top
            Text(
                text = tree.name,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFF5F5F5) // White text
            )

            // Tree image in center (NO BLUR)
            Image(
                imageVector = tree.imageVector,
                contentDescription = tree.name,
                modifier = Modifier
                    .size(100.dp)
                    .padding(vertical = 8.dp),
                contentScale = ContentScale.Fit
            )

            // Price and buy button at bottom
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${tree.price} Coins",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA7A9A9) // Gray
                )

                if (!tree.isUnlocked) {
                    Button(
                        onClick = { /* purchase */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF052659) // RichBlue
                        ),
                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                    ) {
                        Text("BUY", color = Color.White)
                    }
                }
            }
        }
    }
}

// Data Models
data class TreeItem(
    val id: String,
    val name: String,
    val imageVector: ImageVector,
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
                    imageVector = Icons.Default.Park, // Placeholder
                    rarity = TreeRarity.COMMON,
                    price = 0,
                    description = "Classic and sturdy",
                    isUnlocked = true,
                    isSelected = true
                ),
                TreeItem(
                    id = "pine",
                    name = "Pine Tree",
                    imageVector = Icons.Default.Park, // Placeholder
                    rarity = TreeRarity.COMMON,
                    price = 500,
                    description = "Evergreen beauty",
                    isUnlocked = false
                ),
                TreeItem(
                    id = "cherry",
                    name = "Cherry Blossom",
                    imageVector = Icons.Default.Park,
                    rarity = TreeRarity.RARE,
                    price = 1200,
                    description = "Delicate pink blooms",
                    isUnlocked = false
                ),
                TreeItem(
                    id = "palm",
                    name = "Palm Tree",
                    imageVector = Icons.Default.Park,
                    rarity = TreeRarity.RARE,
                    price = 1500,
                    description = "Tropical paradise",
                    isUnlocked = false
                ),
                TreeItem(
                    id = "bonsai",
                    name = "Bonsai",
                    imageVector = Icons.Default.Park,
                    rarity = TreeRarity.EPIC,
                    price = 2500,
                    description = "Ancient wisdom",
                    isUnlocked = false
                ),
                TreeItem(
                    id = "cactus",
                    name = "Desert Cactus",
                    imageVector = Icons.Default.Park,
                    rarity = TreeRarity.COMMON,
                    price = 300,
                    description = "Low maintenance",
                    isUnlocked = false
                ),
                TreeItem(
                    id = "maple",
                    name = "Maple Tree",
                    imageVector = Icons.Default.Park,
                    rarity = TreeRarity.RARE,
                    price = 1800,
                    description = "Autumn's glory",
                    isUnlocked = false
                ),
                TreeItem(
                    id = "golden",
                    name = "Golden Tree",
                    imageVector = Icons.Default.Park,
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

@Composable
private fun CustomizeHeader(
    userCoins: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Customize Your Growth",
                style = MaterialTheme.typography.displayMedium,
                color = Color(0xFFD4AF37), // Gold
                modifier = Modifier.padding(16.dp)
            )

            Text(
                text = "Choose your favorite tree",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFA7A9A9), // Gray
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

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
        cornerRadius = 28.dp,
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        bloom = true,
        bloomIntensity = 0.08f
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
                color = accentOrange
            )
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
private fun LoadingGrid(
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        items(6) {
            Box(
                modifier = Modifier
                    .aspectRatio(0.75f)
                    .clip(MaterialTheme.shapes.large)
                    .background(surfaceCard)
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
    val canAfford = userCoins >= tree.price

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(onClick = onDismiss, indication = null, interactionSource = remember { MutableInteractionSource() }),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clickable(onClick = {}, indication = null, interactionSource = remember { MutableInteractionSource() }),
            bloom = true,
            bloomIntensity = 0.08f,
            contentPadding = PaddingValues(24.dp)
        ) {
            AnimatedContent(
                targetState = purchaseSuccess,
                transitionSpec = { fadeIn() + scaleIn() with fadeOut() + scaleOut() },
                label = "purchaseState"
            ) { success ->
                if (success) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                            PulsingGlow(color = accentBlue, modifier = Modifier.size(120.dp))
                            Text(text = "✓", style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold), color = accentBlue)
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(text = "Purchase Successful!", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(4.dp))
                        Text(text = "${tree.name} unlocked", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), textAlign = TextAlign.Center)
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(tree.imageVector, contentDescription = tree.name, modifier = Modifier.size(100.dp))
                        Spacer(Modifier.height(16.dp))
                        Text(text = "Purchase ${tree.name}?", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(4.dp))
                        Text(text = tree.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), textAlign = TextAlign.Center)
                        Spacer(Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(text = "Price:", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🪙", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.width(4.dp))
                                Text(text = tree.price.toString(), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = accentOrange)
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(text = "Your balance:", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🪙", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.width(4.dp))
                                Text(text = userCoins.toString(), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = if (canAfford) accentBlue else Color.Red)
                            }
                        }

                        if (!canAfford) {
                            Spacer(Modifier.height(16.dp))
                            Text(text = "Insufficient coins!", style = MaterialTheme.typography.bodyMedium, color = Color.Red)
                        }

                        Spacer(Modifier.height(24.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                            GlassButton(text = "Cancel", onClick = onDismiss, style = GlassButtonStyle.OUTLINED, modifier = Modifier.weight(1f))
                            GlassButton(text = "Purchase", onClick = onConfirm, style = GlassButtonStyle.PRIMARY, enabled = canAfford, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
