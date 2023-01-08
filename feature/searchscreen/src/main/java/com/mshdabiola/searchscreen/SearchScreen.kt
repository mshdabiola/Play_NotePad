package com.mshdabiola.searchscreen


import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mshdabiola.designsystem.component.NoteCard
import com.mshdabiola.designsystem.component.NoteTextField
import com.mshdabiola.designsystem.component.state.NotePadUiState
import com.mshdabiola.designsystem.component.state.NoteUiState
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.designsystem.theme.NotePadAppTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList


@Composable
fun SearchScreen(
    onBack: () -> Unit = {},
    navigateToEdit: (Long, String, Long) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    SearchScreen(
        onBack = onBack,
        navigateToEdit = navigateToEdit,
        searchUiState = viewModel.searchUiState,
        onSearchTextChange = viewModel::onSearchTextChange,
        onClearSearchText = viewModel::onClearSearchText,
        onItemLabelClick = viewModel::onItemLabelClick,
        onItemTypeClick = viewModel::onItemTypeClick
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    searchUiState: SearchUiState,
    onBack: () -> Unit,
    navigateToEdit: (Long, String, Long) -> Unit = { _, _, _ -> },
    onSearchTextChange: (String) -> Unit = {},
    onClearSearchText: () -> Unit = {},
    onItemLabelClick: (Int) -> Unit = {},
    onItemTypeClick: (Int) -> Unit = {}

) {
    val focusRequester = remember {
        FocusRequester()
    }
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
    LaunchedEffect(key1 = searchUiState, block = {
        Log.e(this::class.simpleName, "$searchUiState from screen")
    })
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                    }
                },
                title = {
                    NoteTextField(
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .fillMaxWidth(),
                        value = searchUiState.search,
                        placeholder = { Text(text = searchUiState.placeholder) },
                        onValueChange = onSearchTextChange,
                        textStyle = MaterialTheme.typography.bodyLarge,
                        trailingIcon = {
                            IconButton(onClick = { onClearSearchText() }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "delete"
                                )
                            }
                        }
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .padding(8.dp)
        ) {

            if (searchUiState.notes.isNotEmpty()) {
                LazyVerticalStaggeredGrid(

                    columns = StaggeredGridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)

                ) {

                    items(searchUiState.notes) { notePadUiState ->
                        NoteCard(
                            notePad = notePadUiState,
                            onCardClick = { navigateToEdit(it, "", 0) })
                    }

                }
            } else {
                EmptySearchScreen(
                    labels = searchUiState.labels,
                    onItemLabelClick = onItemLabelClick,
                    onItemTypeClick = onItemTypeClick
                )
            }


        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    NotePadAppTheme {
        SearchScreen(
            onBack = {},
            searchUiState = SearchUiState(
                search = "Search Text",
                notes = listOf(
                    NotePadUiState(
                        note = NoteUiState(
                            id = null,
                            title = "Ashwin",
                            detail = "Kaya",
                            editDate = 6901L,
                            isCheck = false,
                            isPin = false,
                        )
                    ),
                    NotePadUiState(
                        note = NoteUiState(
                            id = null,
                            title = "Ashwin",
                            detail = "Kaya",
                            editDate = 6901L,
                            isCheck = false,
                            isPin = false,
                        )
                    )
                ).toImmutableList()
            )
        )
    }


}

@Composable
fun EmptySearchScreen(
    labels: ImmutableList<String> = emptyList<String>().toImmutableList(),
    onItemLabelClick: (Int) -> Unit = {},
    onItemTypeClick: (Int) -> Unit = {}
) {

    val labelPair = remember(labels) {

        labels.map { Pair(it, NoteIcon.Label) }

    }
    LabelBox(
        title = "Types",
        labelIcon = listOf(
            Pair("Reminders", NoteIcon.Notification),
            Pair("Lists", NoteIcon.Check),
            Pair("Images", NoteIcon.Image),
            Pair("Voice", NoteIcon.Voice),
            Pair("Drawings", NoteIcon.Brush),

            ),
        onItemClick = onItemTypeClick
    )
    LabelBox(
        title = "Labels",
        labelIcon = labelPair,
        onItemClick = onItemLabelClick
    )


}

@Preview(device = "spec:parent=pixel_5,orientation=portrait")
@Composable
fun EmptyScreenPreview() {
    Column {
        EmptySearchScreen(
            labels = listOf("Voice", "Voice", "Voice", "Voice", "Voice").toImmutableList()
        )
    }
}

@Composable
fun LabelBox(
    title: String = "Label",
    labelIcon: List<Pair<String, Int>> = emptyList(),
    onItemClick: (Int) -> Unit = {}
) {
    val configuration = LocalConfiguration.current

    val number = remember {
        ((configuration.screenWidthDp.dp) / 72.dp).toInt() - 1
    }
    var h by remember {
        mutableStateOf(number)
    }

    val datas = remember(key1 = h, key2 = labelIcon) {
        labelIcon.take(h).chunked(number)
    }
    if (labelIcon.isNotEmpty()) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.weight(1f), text = title)
                if (labelIcon.size > number) {
                    TextButton(onClick = { h = if (h == number) labelIcon.size else number }) {
                        Text(text = if (h == number) "More" else "Less")
                    }
                }

            }
            Column(Modifier.animateContentSize()) {
                datas.forEachIndexed { index1, pairList ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        pairList.forEachIndexed { index, pair ->

                            SearchLabel(
                                modifier = Modifier.clickable { onItemClick(index + (index1 * number)) },
                                iconId = pair.second,
                                name = pair.first
                            )

                        }

                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

        }

    }
}

@Preview(device = "spec:parent=pixel_5")
@Composable
fun LabelBoxPreview() {
    LabelBox(
        labelIcon = listOf(
            Pair("Voice", NoteIcon.Voice),
            Pair("Voice", NoteIcon.Voice),
            Pair("Voice", NoteIcon.Voice),
            Pair("Voice", NoteIcon.Voice),
            Pair("Voice", NoteIcon.Voice),
            Pair("Voice", NoteIcon.Voice),
            Pair("Voice", NoteIcon.Voice),

            )
    )
}

@Composable
fun SearchLabel(
    modifier: Modifier = Modifier,
    iconId: Int = NoteIcon.Label,
    name: String = "Label"
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier
                .width(72.dp)
                .aspectRatio(1f)
        ) {

            Icon(
                painter = painterResource(id = iconId),
                contentDescription = "label icon",
                modifier = Modifier.padding(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = name)
    }
}

@Preview
@Composable
fun SearchLabelPreview() {
    SearchLabel()
}