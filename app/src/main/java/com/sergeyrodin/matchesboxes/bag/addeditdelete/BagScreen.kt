package com.sergeyrodin.matchesboxes.bag.addeditdelete

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.sergeyrodin.matchesboxes.nametextfield.NameTextFieldScaffold

@Composable
fun BagScreen(viewModel: BagManipulatorViewModel) {
    val name by viewModel.name.observeAsState()
    NameTextFieldScaffold(
        name = name,
        onValueChange = { newName ->
            viewModel.setNewName(newName)
        },
        saveItemClick = {
            viewModel.saveBag()
        }
    )
}
