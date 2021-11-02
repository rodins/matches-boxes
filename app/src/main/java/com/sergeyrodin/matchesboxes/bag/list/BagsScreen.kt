package com.sergeyrodin.matchesboxes.bag.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        contentDescription = stringResource(R.string.bag_icon_description),
        modifier = Modifier.padding(start = 8.dp, top = 6.dp)
    )
}

@Preview
@Composable
fun BagIconPreview() {
    BagIcon()
}
