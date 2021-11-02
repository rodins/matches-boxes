package com.sergeyrodin.matchesboxes.matchesbox.list

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.quantityitem.QuantityItemListScreen

@Composable
fun BoxesScreen(viewModel: MatchesBoxListViewModel) {
    val boxes by viewModel.boxesList.observeAsState()
    QuantityItemListScreen(
        quantityItems = boxes,
        onItemClick = { id ->
            viewModel.selectBox(id)
        },
        addItemDescription = stringResource(R.string.add_box),
        addItemClick = {
            viewModel.addBox()
        },
        emptyStateText = stringResource(R.string.no_matches_boxes_added),
        icon = {
            BoxIcon()
        }
    )
}

@Composable
fun BoxIcon() {
    Image(
        painter = painterResource(R.drawable.ic_matchesbox),
        contentDescription = stringResource(R.string.box_icon_description)
    )
}