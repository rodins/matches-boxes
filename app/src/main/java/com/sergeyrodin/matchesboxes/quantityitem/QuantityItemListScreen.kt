package com.sergeyrodin.matchesboxes.quantityitem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.bag.list.BagIcon
import com.sergeyrodin.matchesboxes.data.QuantityItemModel

@Composable
fun QuantityItemListScreen(
    quantityItems: List<QuantityItemModel>?,
    onItemClick: (Int) -> Unit = {},
    addItemClick: () -> Unit = {},
    addItemDescription: String = "",
    emptyStateText: String = "",
    icon: @Composable () -> Unit = {}
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = addItemClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = addItemDescription
                )
            }
        }
    ) {
        when {
            quantityItems == null -> {
                LoadingIndicator()
            }
            quantityItems.isEmpty() -> {
                EmptyState(emptyStateText)
            }
            else -> {
                QuantityItemList(quantityItems, onItemClick, icon)
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val loadingIndicatorDescription = stringResource(R.string.loading_indicator)
        CircularProgressIndicator(
            modifier = Modifier.semantics {
                contentDescription = loadingIndicatorDescription
            }
        )
    }
}

@Composable
private fun QuantityItemList(
    quantityItems: List<QuantityItemModel>,
    onItemClick: (Int) -> Unit,
    icon: @Composable () -> Unit
) {
    LazyColumn {
        items(quantityItems) { item ->
            QuantityItem(
                name = item.name,
                quantity = item.componentsQuantity,
                onClick = {
                    onItemClick(item.id)
                },
                icon = icon
            )
        }
    }
}

@Composable
private fun EmptyState(emptyStateText: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = emptyStateText,
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Composable
fun QuantityItem(
    name: String,
    quantity: String,
    onClick: () -> Unit = {},
    icon: @Composable () -> Unit = {}
) {
    val resources = LocalContext.current.resources
    val quantityRes = resources.getQuantityString(
        R.plurals.components_quantity,
        quantity.toInt(),
        quantity)

    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(all = 8.dp)
            .background(Color.White)
    ) {
        icon()
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = name,
                style = MaterialTheme.typography.subtitle1
            )
            Spacer(modifier = Modifier.height(4.dp))
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = quantityRes,
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}

@Preview
@Composable
fun DisplayQuantityItemPreview() {
    QuantityItem("Bag 1", "5") {
        BagIcon()
    }
}

@Preview
@Composable
fun QuantityItemListPreview() {
    val items = listOf(
        QuantityItemModel(1, "Bag1", "5"),
        QuantityItemModel(2, "Bag2", "6")
    )
    QuantityItemListScreen(
        quantityItems = items,
        addItemDescription = stringResource(R.string.add_bag),
        emptyStateText = stringResource(R.string.no_bags_added),
        icon = {
            BagIcon()
        }
    )
}

@Preview
@Composable
fun QuantityItemListEmptyPreview() {
    QuantityItemListScreen(listOf())
}

@Preview
@Composable
fun QuantityItemListLoadingPreview() {
    QuantityItemListScreen(null)
}
