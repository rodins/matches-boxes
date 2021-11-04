package com.sergeyrodin.matchesboxes.component.list

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.quantityitem.QuantityItemListScreen

@Composable
fun ComponentsScreen(viewModel: RadioComponentsListViewModel) {
    val components by viewModel.componentsList.observeAsState()
    QuantityItemListScreen(
        quantityItems = components,
        onItemClick = { id ->
            viewModel.selectComponent(id)
        },
        addItemDescription = stringResource(R.string.add_component),
        addItemClick = {
            viewModel.addComponent()
        },
        emptyStateText = stringResource(R.string.no_components_added),
        icon = {
            ComponentIcon()
        }
    )
}

@Composable
fun ComponentIcon() {
    Image(
        painter = painterResource(R.drawable.ic_component),
        contentDescription = stringResource(R.string.component_icon_description)
    )
}

@Preview
@Composable
fun ComponentIconPreview() {
    ComponentIcon()
}
