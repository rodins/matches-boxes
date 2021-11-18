package com.sergeyrodin.matchesboxes.matchesboxset.addeditdelete

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.nametextfield.NameTextFieldScaffold

@Composable
fun SetNameScreen(viewModel: MatchesBoxSetManipulatorViewModel) {
    val name by viewModel.name.observeAsState()
    NameTextFieldScaffold(
        name = name,
        placeHolderTextRes = R.string.enter_matches_box_set_name,
        onValueChange = { newName ->
            viewModel.setNewName(newName)
        },
        saveItemClick = {
            viewModel.saveMatchesBoxSet()
        }
    )
}