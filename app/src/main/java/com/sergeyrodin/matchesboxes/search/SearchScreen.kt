package com.sergeyrodin.matchesboxes.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.component.list.ComponentIcon
import com.sergeyrodin.matchesboxes.quantityitem.QuantityItemListScreen

@Composable
fun SearchScreen(viewModel: SearchViewModel) {
    val components by viewModel.items.observeAsState()
    QuantityItemListScreen(
        quantityItems = components,
        onItemClick = { id ->
            viewModel.selectComponent(id)
        },
        emptyStateText = stringResource(R.string.no_components_found),
        addItemDescription = stringResource(R.string.add_component),
        addItemClick = {
            viewModel.addComponent()
        },
        icon = {
            ComponentIcon()
        }
    )
}