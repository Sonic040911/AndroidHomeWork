package com.example.homework6.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.homework6.R
import com.example.homework6.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavHostController, vm: ProfileViewModel) {
    val state by vm.ui.collectAsState()
    var name by rememberSaveable { mutableStateOf(state.name) }
    var bio by rememberSaveable { mutableStateOf(state.bio) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Edit Profile") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "avatar",
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Preview")
                    Text(name)
                    Text(
                        text = bio,
                        maxLines = 2
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4
            )

            Spacer(Modifier.height(20.dp))

            Row {
                Button(onClick = {
                    vm.setName(name)
                    vm.setBio(bio)
                    vm.sendEvent(
                        com.example.homework6.viewmodel.UiEvent.ShowSnackbar("Profile updated")
                    )
                    navController.popBackStack()
                }) {
                    Text("Save")
                }

                Spacer(Modifier.width(8.dp))

                OutlinedButton(onClick = { navController.popBackStack() }) {
                    Text("Cancel")
                }
            }
        }
    }
}
