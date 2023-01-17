package com.mshdabiola.mainscreen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.speech.RecognizerIntent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ShareCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mshdabiola.designsystem.component.ColorDialog
import com.mshdabiola.designsystem.component.NoteCard
import com.mshdabiola.designsystem.component.NotificationDialog
import com.mshdabiola.designsystem.component.state.LabelUiState
import com.mshdabiola.designsystem.component.state.NotePadUiState
import com.mshdabiola.designsystem.component.state.NoteTypeUi
import com.mshdabiola.designsystem.component.state.NoteUiState
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.designsystem.theme.NotePadAppTheme
import com.mshdabiola.mainscreen.component.ImageDialog
import com.mshdabiola.mainscreen.component.MainNavigation
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    navigateToEdit: (Long, String, Long) -> Unit = { _, _, _ -> },
    navigateToLevel: (Boolean) -> Unit,
    navigateToSearch: () -> Unit,
    navigateToSelectLevel: (IntArray) -> Unit
) {

    val mainState = mainViewModel.mainState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit, block = {
        delay(2000)
        mainViewModel.deleteEmptyNote()
    })


    var showDialog by remember {
        mutableStateOf(false)
    }
    var showColor by remember {
        mutableStateOf(false)
    }
    var showRenameLabel by remember {
        mutableStateOf(false)
    }
    var showDeleteLabel by remember {
        mutableStateOf(false)
    }
    val selectId = remember(mainState.value.notePads) {
        mainState.value.notePads.filter { it.note.selected }.map { it.note.id.toInt() }
            .toIntArray()
    }
    val context = LocalContext.current
    val send = {
        val notePads = mainState.value.notePads.single { it.note.selected }
        val intent = ShareCompat.IntentBuilder(context)
            .setText(notePads.toString())
            .setType("text/*")
            .setChooserTitle("From Notepad")
            .createChooserIntent()
        context.startActivity(Intent(intent))

    }
    MainScreen(
        notePads = mainState.value.notePads,
        labels = mainState.value.labels,
        messages = mainState.value.message,
        navigateToEdit = navigateToEdit,
        navigateToLevel = navigateToLevel,
        navigateToSearch = navigateToSearch,
        saveImage = mainViewModel::savePhoto,
        saveVoice = mainViewModel::saveVoice,
        photoUri = mainViewModel::getPhotoUri,
        currentNoteType = mainState.value.noteType,
        onNavigationNoteType = mainViewModel::setNoteType,
        onSelectedCard = mainViewModel::onSelectCard,
        onClearSelected = mainViewModel::clearSelected,
        setAllPin = mainViewModel::setPin,
        setAllAlarm = { showDialog = true },
        setAllColor = { showColor = true },
        setAllLabel = { navigateToSelectLevel(selectId) },
        onCopy = mainViewModel::copyNote,
        onDelete = mainViewModel::setAllDelete,
        onArchive = mainViewModel::setAllArchive,
        onSend = {
            mainViewModel.clearSelected()
            send()
        },
        onRenameLabel = { showRenameLabel = true },
        onDeleteLabel = { showDeleteLabel = true },
        onEmptyTrash = mainViewModel::emptyTrash,
        onMessageDelive = mainViewModel::onMessageDeliver

    )

    val note = remember(mainState.value.notePads) {
        val noOfSelected = mainState.value.notePads.count { it.note.selected }
        if (noOfSelected == 1) {
            mainState.value.notePads.singleOrNull { it.note.selected }?.note
        } else {
            null
        }
    }

    val colorIndex = remember(mainState.value.notePads) {
        val noOfSelected = mainState.value.notePads.count { it.note.selected }
        if (noOfSelected == 1) {
            mainState.value.notePads.singleOrNull { it.note.selected }?.note?.color
        } else {
            null
        }
    }

    NotificationDialog(
        showDialog,
        onDismissRequest = { showDialog = false },
        remainder = note?.reminder ?: -1,
        interval = if (note?.interval == (-1L)) null else note?.interval,
        onSetAlarm = mainViewModel::setAlarm,
        onDeleteAlarm = mainViewModel::deleteAlarm
    )

    ColorDialog(
        show = showColor,
        onDismissRequest = { showColor = false },
        onColorClick = mainViewModel::setAllColor,
        currentColor = colorIndex ?: -1
    )

    RenameLabelAlertDialog(
        show = showRenameLabel,
        label = (mainState.value.noteType as? NoteTypeUi.LABEL)?.name ?: "",
        onDismissRequest = { showRenameLabel = false },
        onChangeName = mainViewModel::renameLabel
    )

    DeleteLabelAlertDialog(
        show = showDeleteLabel,
        onDismissRequest = { showDeleteLabel = false },
        onDelete = mainViewModel::deleteLabel
    )

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    notePads: ImmutableList<NotePadUiState>,
    labels: ImmutableList<LabelUiState>,
    messages: ImmutableList<String> = emptyList<String>().toImmutableList(),
    navigateToEdit: (Long, String, Long) -> Unit = { _, _, _ -> },
    navigateToLevel: (Boolean) -> Unit = {},
    saveImage: (Uri, Long) -> Unit = { _, _ -> },
    saveVoice: (Uri, Long) -> Unit = { _, _ -> },
    photoUri: (Long) -> Uri = { Uri.EMPTY },
    currentNoteType: NoteTypeUi = NoteTypeUi.NOTE,
    onNavigationNoteType: (NoteTypeUi) -> Unit = {},
    navigateToSearch: () -> Unit = {},
    onSelectedCard: (Long) -> Unit = {},
    onClearSelected: () -> Unit = {},
    setAllPin: () -> Unit = {},
    setAllAlarm: () -> Unit = {},
    setAllColor: () -> Unit = {},
    setAllLabel: () -> Unit = {},
    onArchive: () -> Unit = {},
    onDelete: () -> Unit = {},
    onSend: () -> Unit = {},
    onCopy: () -> Unit = {},
    onRenameLabel: () -> Unit = {},
    onDeleteLabel: () -> Unit = {},
    onEmptyTrash: () -> Unit = {},
    onMessageDelive: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val pinScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var showImageDialog by remember {
        mutableStateOf(false)
    }
    var photoId by remember {
        mutableStateOf(0L)
    }

    val context = LocalContext.current
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            it?.let {
                Log.e("imageUir", "$it")
                showImageDialog = false
                val time = System.currentTimeMillis()
                saveImage(it, time)
                navigateToEdit(-3, "image text", time)
            }

        })

    val snapPictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            if (it) {
                navigateToEdit(-3, "image text", photoId)
            }
        })


    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            it.data?.let { intent ->
                val strArr = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val audiouri = intent.data


                if (audiouri != null) {
                    val time = System.currentTimeMillis()
                    saveVoice(audiouri, time)
                    Log.e("voice ", "uri $audiouri ${strArr?.joinToString()}")
                    navigateToEdit(-4, strArr?.joinToString() ?: "", time)
                }


            }
        }
    )

    val audioPermission =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(),
            onResult = {
                if (it) {
                    voiceLauncher.launch(
                        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(
                                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                            )
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speck Now Now")
                            putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR")
                            putExtra("android.speech.extra.GET_AUDIO", true)

                        })


                }

            }
        )

    val pinNotePad by remember(notePads) {
        derivedStateOf {
            notePads.partition { it.note.isPin }
        }
    }
//    val notPinNotePad by remember(notePads) {
//        derivedStateOf { notePads.filter { !it.note.isPin } }
//    }

    val coroutineScope = rememberCoroutineScope()

    val noOfSelected = remember(notePads) {
        notePads.count { it.note.selected }
    }
    val isAllPin = remember(notePads) {
        notePads.filter { it.note.selected }
            .all { it.note.isPin }
    }
    val snackHostState = remember {
        SnackbarHostState()
    }
    LaunchedEffect(key1 = messages, block = {
        if (messages.isNotEmpty()) {
            when (snackHostState.showSnackbar(
                message = messages.first(), withDismissAction = true,
                duration = SnackbarDuration.Short
            )) {
                SnackbarResult.ActionPerformed -> {
                    Log.e("on Snacker", "click")
                }

                SnackbarResult.Dismissed -> {
                    onMessageDelive()
                }
            }
        }


    })


    ModalNavigationDrawer(
        drawerContent = {
            MainNavigation(
                labels = labels,
                currentType = currentNoteType,
                onNavigation = {
                    onNavigationNoteType(it)
                    coroutineScope.launch { drawerState.close() }
                },
                navigateToLevel = navigateToLevel

            )
        },
        drawerState = drawerState,
        gesturesEnabled = true
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(if (noOfSelected > 0) pinScrollBehavior.nestedScrollConnection else scrollBehavior.nestedScrollConnection),
            topBar = {

                if (noOfSelected > 0) {
                    SelectTopBar(
                        selectNumber = noOfSelected,
                        isAllPin = isAllPin,
                        scrollBehavior = pinScrollBehavior,
                        onClear = onClearSelected,
                        onPin = setAllPin,
                        onNoti = setAllAlarm,
                        onColor = setAllColor,
                        onLabel = setAllLabel,
                        onArchive = onArchive,
                        onDelete = onDelete,
                        onSend = onSend,
                        onCopy = onCopy
                    )
                } else {
//
                    when (currentNoteType) {
                        is NoteTypeUi.LABEL -> {
                            LabelTopAppBar(
                                label = labels.single { it.id == currentNoteType.id }.label,
                                onSearch = navigateToSearch,
                                onNavigate = { coroutineScope.launch { drawerState.open() } },
                                scrollBehavior = scrollBehavior,
                                onDeleteLabel = onDeleteLabel,
                                onRenameLabel = onRenameLabel
                            )
                        }

                        NoteTypeUi.TRASH -> {
                            TrashTopAppBar(
                                onNavigate = { coroutineScope.launch { drawerState.open() } },
                                scrollBehavior = scrollBehavior,
                                onEmptyTrash = onEmptyTrash
                            )
                        }

                        NoteTypeUi.NOTE -> {
                            MainTopAppBar(
                                navigateToSearch = navigateToSearch,
                                onNavigate = { coroutineScope.launch { drawerState.open() } },
                                scrollBehavior = scrollBehavior
                            )
                        }

                        NoteTypeUi.REMAINDER -> {
                            ArchiveTopAppBar(
                                name = "Remainder",
                                onSearch = navigateToSearch,
                                onNavigate = { coroutineScope.launch { drawerState.open() } },
                                scrollBehavior = scrollBehavior,

                                )
                        }

                        NoteTypeUi.ARCHIVE -> {
                            ArchiveTopAppBar(
                                onSearch = navigateToSearch,
                                onNavigate = { coroutineScope.launch { drawerState.open() } },
                                scrollBehavior = scrollBehavior
                            )
                        }
                    }
                }


            },
            snackbarHost = { SnackbarHost(hostState = snackHostState) },
            bottomBar = {
                BottomAppBar(
                    actions = {

                        IconButton(onClick = { navigateToEdit(-2, "", 0) }) {
                            Icon(
                                imageVector = ImageVector
                                    .vectorResource(id = NoteIcon.Check),
                                contentDescription = "note check"
                            )
                        }

                        IconButton(onClick = {
                            navigateToEdit(-5, "", 0)
                        }) {
                            Icon(
                                imageVector = ImageVector
                                    .vectorResource(id = NoteIcon.Brush),
                                contentDescription = "note drawing"
                            )
                        }


                        IconButton(onClick = {
                            //navigateToEdit(-4, "", Uri.EMPTY)
                            if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
                                PackageManager.PERMISSION_GRANTED
                            ) {
                                voiceLauncher.launch(
                                    Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                        putExtra(
                                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                        )
                                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speck Now Now")
                                        putExtra(
                                            "android.speech.extra.GET_AUDIO_FORMAT",
                                            "audio/AMR"
                                        )
                                        putExtra("android.speech.extra.GET_AUDIO", true)

                                    })

                            } else {
                                audioPermission.launch(Manifest.permission.RECORD_AUDIO)
                            }


                        }) {
                            Icon(
                                imageVector = ImageVector
                                    .vectorResource(id = NoteIcon.Voice),
                                contentDescription = "note check"
                            )
                        }

                        IconButton(onClick = {//
                            showImageDialog = true
                        }) {
                            Icon(
                                imageVector = ImageVector
                                    .vectorResource(id = NoteIcon.Image),
                                contentDescription = "note check"
                            )
                        }

                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = { navigateToEdit(-1, "", 0) }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "add note")
                        }
                    }
                )
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(8.dp)
            ) {


                LazyVerticalStaggeredGrid(

                    columns = StaggeredGridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)

                ) {


                    if (pinNotePad.first.isNotEmpty()) {
                        item {
                            Text(modifier = Modifier.fillMaxWidth(), text = "Pin")
                        }
                        item { Text(modifier = Modifier.fillMaxWidth(), text = "") }
                    }
                    items(pinNotePad.first) { notePadUiState ->
                        NoteCard(
                            notePad = notePadUiState,
                            onCardClick = {
                                if (noOfSelected > 0)
                                    onSelectedCard(it)
                                else
                                    navigateToEdit(it, "", 0)
                            },
                            onLongClick = onSelectedCard
                        )
                    }


                    if (pinNotePad.first.isNotEmpty() && pinNotePad.second.isNotEmpty()) {
                        item {
                            Text(modifier = Modifier.fillMaxWidth(), text = "Other")
                        }
                        item { Text(modifier = Modifier.fillMaxWidth(), text = "") }
                    }
                    items(pinNotePad.second) { notePadUiState ->
                        NoteCard(
                            notePad = notePadUiState,
                            onCardClick = {
                                if (noOfSelected > 0)
                                    onSelectedCard(it)
                                else
                                    navigateToEdit(it, "", 0)
                            },
                            onLongClick = onSelectedCard
                        )
                    }

                }

            }

            ImageDialog(
                show = showImageDialog,
                onDismissRequest = { showImageDialog = false },
                onChooseImage = {
                    imageLauncher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onSnapImage = {
                    photoId = System.currentTimeMillis()
                    snapPictureLauncher.launch(photoUri(photoId))
                }
            )
        }
    }
}


@Preview
@Composable
fun MainScreenPreview() {
    NotePadAppTheme {
        MainScreen(
            notePads =
            listOf(
                NotePadUiState(
                    note = NoteUiState(title = "hammed", detail = "adiola")
                ),
                NotePadUiState(
                    note = NoteUiState(title = "hammed", detail = "adiola", selected = true)
                ),
                NotePadUiState(
                    note = NoteUiState(title = "hammed", detail = "adiola")
                )

            )
                .toImmutableList(),
            labels = emptyList<LabelUiState>().toImmutableList(),
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTopBar(
    selectNumber: Int = 0,
    isAllPin: Boolean = false,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    onClear: () -> Unit = {},
    onPin: () -> Unit = {},
    onNoti: () -> Unit = {},
    onColor: () -> Unit = {},
    onLabel: () -> Unit = {},
    onArchive: () -> Unit = {},
    onDelete: () -> Unit = {},
    onSend: () -> Unit = {},
    onCopy: () -> Unit = {}

) {

    var showDropDown by remember {
        mutableStateOf(false)
    }
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onClear) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = "clear")
            }
        },
        title = {
            Text(text = "$selectNumber")
        },
        actions = {
            IconButton(onClick = onPin) {
                Icon(
                    painter = painterResource(id = if (isAllPin) NoteIcon.Pin else NoteIcon.PinFill),
                    contentDescription = "pin"
                )
            }
            IconButton(onClick = onNoti) {
                Icon(
                    painter = painterResource(id = NoteIcon.Notification),
                    contentDescription = "notification"
                )
            }
            IconButton(onClick = onColor) {
                Icon(
                    painter = painterResource(id = NoteIcon.ColorLens),
                    contentDescription = "color"
                )
            }
            IconButton(onClick = onLabel) {
                Icon(painter = painterResource(id = NoteIcon.Label), contentDescription = "Label")
            }
            Box {
                IconButton(onClick = { showDropDown = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "more")

                }
                DropdownMenu(expanded = showDropDown, onDismissRequest = { showDropDown = false }) {
                    DropdownMenuItem(text = { Text(text = "Archive") },
                        onClick = {
                            showDropDown = false
                            onArchive()
                        })
                    DropdownMenuItem(text = { Text(text = "Delete") },
                        onClick = {
                            showDropDown = false
                            onDelete()
                        })
                    if (selectNumber == 1) {
                        DropdownMenuItem(text = { Text(text = "Make a Copy") }, onClick = {
                            showDropDown = false
                            onCopy()
                        })
                        DropdownMenuItem(text = { Text(text = "Send") }, onClick = {
                            showDropDown = false
                            onSend()
                        })
                    }
                }
            }
        },
        scrollBehavior = scrollBehavior

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SelectTopAppBarPreview() {
    SelectTopBar()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelTopAppBar(
    label: String = "label",
    onNavigate: () -> Unit = {},
    onSearch: () -> Unit = {},
    onRenameLabel: () -> Unit = {},
    onDeleteLabel: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
) {
    var showDropDown by remember {
        mutableStateOf(false)
    }

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavigate) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "menu")
            }
        },
        title = { Text(text = label) },
        actions = {
            IconButton(onClick = onSearch) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "search"
                )
            }

            Box {
                IconButton(onClick = { showDropDown = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "more")

                }
                DropdownMenu(expanded = showDropDown, onDismissRequest = { showDropDown = false }) {
                    DropdownMenuItem(
                        text = { Text(text = "Rename Label") },
                        onClick = {
                            showDropDown = false
                            onRenameLabel()
                        })
                    DropdownMenuItem(
                        text = { Text(text = "Delete Label") },
                        onClick = {
                            showDropDown = false
                            onDeleteLabel()
                        })
                }

            }
        },
        scrollBehavior = scrollBehavior

    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun LabelTopAppBarPreview() {
    LabelTopAppBar()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveTopAppBar(
    name: String = "Archive",
    onNavigate: () -> Unit = {},
    onSearch: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
) {
    var showDropDown by remember {
        mutableStateOf(false)
    }

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavigate) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "menu")
            }
        },
        title = { Text(text = name) },
        actions = {
            IconButton(onClick = onSearch) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "search"
                )
            }
        },
        scrollBehavior = scrollBehavior

    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ArchiveTopAppBarPreview() {
    ArchiveTopAppBar()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashTopAppBar(
    onNavigate: () -> Unit = {},
    onEmptyTrash: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
) {
    var showDropDown by remember {
        mutableStateOf(false)
    }

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavigate) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "menu")
            }
        },
        title = { Text(text = "Trash") },
        actions = {

            Box {
                IconButton(onClick = { showDropDown = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "more")

                }
                DropdownMenu(expanded = showDropDown, onDismissRequest = { showDropDown = false }) {
                    DropdownMenuItem(text = { Text(text = "Empty Trash") },
                        onClick = {
                            showDropDown = false
                            onEmptyTrash()
                        })

                }

            }
        },
        scrollBehavior = scrollBehavior

    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TrashTopAppBarPreview() {
    TrashTopAppBar()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    navigateToSearch: () -> Unit = {},
    onNavigate: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
) {
    TopAppBar(
        modifier = Modifier,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { navigateToSearch() }
                    .fillMaxWidth()
                    .padding(4.dp)
                    .padding(end = 16.dp)
                    .clip(
                        RoundedCornerShape(
                            topEnd = 50f,
                            topStart = 50f,
                            bottomEnd = 50f,
                            bottomStart = 50f
                        )
                    )
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                IconButton(onClick = onNavigate) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "menu"
                    )
                }
                Text(
                    text = "Search your note",
                    style = MaterialTheme.typography.titleMedium
                )


            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MainTopAppBarPreview() {
    MainTopAppBar()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameLabelAlertDialog(
    show: Boolean = false,
    label: String = "Label",
    onDismissRequest: () -> Unit = {},
    onChangeName: (String) -> Unit = {}
) {
    var name by remember(label) {
        mutableStateOf(label)
    }

    AnimatedVisibility(visible = show) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = "Rename Label") },
            text = {
                TextField(value = name, onValueChange = { name = it })
            },
            confirmButton = {
                Button(onClick = {
                    onDismissRequest()
                    onChangeName(name)
                }) {
                    Text(text = "Rename")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismissRequest() }) {
                    Text(text = "Cancel")
                }
            }
        )

    }

}

@Preview
@Composable
fun RenameLabelPreview() {
    RenameLabelAlertDialog(show = true)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteLabelAlertDialog(
    show: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onDelete: () -> Unit = {}
) {


    AnimatedVisibility(visible = show) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = "Rename Label") },
            text = {
                Text(text = " We'll delete the label and remove it from all of from all of your keep notes. Your notes won't be deleted")
            },
            confirmButton = {
                TextButton(onClick = {
                    onDismissRequest()
                    onDelete()
                }) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismissRequest() }) {
                    Text(text = "Cancel")
                }
            }
        )

    }

}

@Preview
@Composable
fun DeleteLabelPreview() {
    DeleteLabelAlertDialog(show = true)
}