package com.sergeyrodin.matchesboxes.bag.list

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.quantityitem.QuantityItemListScreen

@Composable
fun BagsScreen(viewModel: BagsListViewModel) {
    val bags by viewModel.bagsList.observeAsState()
    QuantityItemListScreen (
        quantityItems = bags,
        onItemClick = { id ->
            viewModel.selectBag(id)
        },
        addItemClick = {
            viewModel.addBag()
        },
        addItemDescription = stringResource(R.string.add_bag),
        emptyStateText = stringResource(R.string.no_bags_added),
        icon = {
            BagIcon()
        }
    )
}

@Composable
fun BagIcon() {
    Image(
        painter = painterResource(R.drawable.ic_bag),
        contentDescription = stringResource(R.string.bag_icon_description)
    )
}
