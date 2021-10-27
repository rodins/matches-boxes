package com.sergeyrodin.matchesboxes.matchesboxset.list

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.quantityitem.QuantityItemListScreen

@Composable
fun SetsScreen(viewModel: MatchesBoxSetsListViewModel) {
    val sets by viewModel.setsList.observeAsState()
    QuantityItemListScreen(
        quantityItems = sets,
        onItemClick = { id ->
            viewModel.selectSet(id)
        },
        emptyStateText = stringResource(R.string.no_matches_box_sets_added),
        addItemDescription = stringResource(R.string.add_set),
        addItemClick = {
            viewModel.addSet()
        },
        icon = {
            SetIcon()
        }
    )
}

@Composable
fun SetIcon() {
    Image(
        painter = painterResource(R.drawable.ic_set),
        contentDescription = stringResource(R.string.set_icon_description)
    )
}