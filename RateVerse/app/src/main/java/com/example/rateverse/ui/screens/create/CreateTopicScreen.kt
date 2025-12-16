package com.example.rateverse.ui.screens.create

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.rateverse.ui.components.AddItemDialog
import com.example.rateverse.ui.components.DraftItemCard
import com.example.rateverse.ui.navigation.Screen

@Composable
fun CreateTopicScreen(
    navController: NavController,
    viewModel: CreateTopicViewModel = hiltViewModel()
) {
    var showAddItemDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val flag = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(uri, flag)
            viewModel.topicCoverUrl.value = uri.toString()
        }
    }

    LaunchedEffect(viewModel.creationSuccess.value) {
        if (viewModel.creationSuccess.value) {
            navController.popBackStack(Screen.Home.route, inclusive = false)
        }
    }

    Scaffold(
        topBar = {
            CreateTopicTopAppBar(
                onBackClick = { navController.popBackStack() },
                onSaveClick = viewModel::createTopic
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                TopicFormFields(
                    viewModel = viewModel,
                    onAddCoverClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
                )
            }

            item {
                AddItemButton(onClick = { showAddItemDialog = true })
            }

            item {
                Text(
                    text = "Items to be added (${viewModel.draftItems.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(viewModel.draftItems, key = { it.tempId }) { item ->
                DraftItemCard(
                    item = item,
                    onRemove = { viewModel.removeItem(item) }
                )
            }

            item {
                viewModel.errorMessage.value?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (showAddItemDialog) {
        AddItemDialog(
            onDismiss = { showAddItemDialog = false },
            onAddItem = { name, imageUrl, description ->
                viewModel.addItem(name, imageUrl, description)
                showAddItemDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTopicTopAppBar(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    TopAppBar(
        title = { Text("Create new topic") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back"
                )
            }
        },
        actions = {
            TextButton(onClick = onSaveClick) {
                Text("Done")
            }
        }
    )
}

@Composable
fun TopicFormFields(
    viewModel: CreateTopicViewModel,
    onAddCoverClick: () -> Unit
) {
    Column {

        OutlinedTextField(
            value = viewModel.topicTitle.value,
            onValueChange = { viewModel.topicTitle.value = it },
            label = { Text("Topic title (required)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.topicDescription.value,
            onValueChange = { viewModel.topicDescription.value = it },
            label = { Text("Topic description (optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Topic cover image (optional)",
            style = MaterialTheme.typography.titleMedium
        )

        if (viewModel.topicCoverUrl.value != null) {
            AsyncImage(
                model = viewModel.topicCoverUrl.value,
                contentDescription = "Topic cover image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { onAddCoverClick() },
                contentScale = ContentScale.Crop
            )
        } else {
            Button(
                onClick = onAddCoverClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add cover image from gallery")
            }
        }

        Divider(Modifier.padding(vertical = 16.dp))
    }
}

@Composable
fun AddItemButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add item",
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = "Add item",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium
        )
    }

    Divider()
}
