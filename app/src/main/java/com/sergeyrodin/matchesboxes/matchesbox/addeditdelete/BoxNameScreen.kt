package com.sergeyrodin.matchesboxes.matchesbox.addeditdelete

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.nametextfield.NameTextFieldScaffold

@Composable
fun BoxNameScreen(viewModel: MatchesBoxManipulatorViewModel) {
    val name by viewModel.name.observeAsState()
    NameTextFieldScaffold(
        name = name,
        placeHolderTextRes = R.string.enter_box_name,
        onValueChange = { newName ->
            viewModel.setNewName(newName)
        },
        saveItemClick = {
            viewModel.saveMatchesBox()
        }
    )
}