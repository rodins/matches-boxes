package com.sergeyrodin.matchesboxes.nametextfield

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.sergeyrodin.matchesboxes.R

const val NAME_TEXT_FIELD_TAG = "nameTextFieldTag"

@Composable
fun NameTextFieldScaffold(
    name: String?,
    onValueChange: (String) -> Unit = {},
    saveItemClick: () -> Unit = {}
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = saveItemClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_done),
                    contentDescription = stringResource(R.string.save_name)
                )
            }
        }
    ) {
        NameTextField(
            name = name,
            onValueChange = onValueChange,
            placeHolderTextRes = R.string.enter_bag_name
        )
    }
}

@Composable
private fun NameTextField(
    name: String?,
    onValueChange: (String) -> Unit = {},
    placeHolderTextRes: Int = R.string.enter_name
) {
    name?.let { text ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            TextField(
                value = text,
                onValueChange = onValueChange,
                placeholder = {
                    Text(stringResource(id = placeHolderTextRes))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
                    .testTag(NAME_TEXT_FIELD_TAG)
            )
        }
    }
}

@Preview
@Composable
fun NameTextHintPreview() {
    NameTextField("")
}

@Preview
@Composable
fun NameScufflePreview() {
    AppCompatTheme {
        NameTextFieldScaffold(name = "Bag")
    }
}
