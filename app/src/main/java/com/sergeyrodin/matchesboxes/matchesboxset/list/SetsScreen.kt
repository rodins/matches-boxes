package com.sergeyrodin.matchesboxes.matchesboxset.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        contentDescription = stringResource(R.string.set_icon_description),
        modifier = Modifier.padding(start = 8.dp, top = 6.dp)
    )
}

@Preview
@Composable
fun SetIconPreview() {
    SetIcon()
}