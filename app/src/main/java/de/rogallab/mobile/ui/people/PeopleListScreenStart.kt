package de.rogallab.mobile.ui.people

import NavScreen
import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import de.rogallab.mobile.R
import de.rogallab.mobile.domain.UiState
import de.rogallab.mobile.domain.entities.Person
import de.rogallab.mobile.domain.utilities.logDebug
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun PeopleListScreenStart(
   navController: NavController,
   viewModel: PeopleViewModel
) {
   val tag = "ok>PeopleListScreen   ."

   val id = UUID.fromString("10000000-0000-0000-0000-000000000000")

//   LaunchedEffect(true) {
//      logDebug(tag,"simulate input")
//      viewModel.onFirstNameChange( "Anna")
//      viewModel.onLastNameChange( "Aal")
//      viewModel.onEmailChange( "a.aal@t-online.de")
//      viewModel.onPhoneChange( "0581 1234-5678")
//      logDebug(tag, "viewModel.add()")
//      viewModel.add()
//      delay(1000L)
//      logDebug(tag, "readById called")
//      viewModel.readById1(id)
//   }

   /****************************************************************************
    * Consuming UiStateFlow<MutableList<Person>>                               *
    ****************************************************************************/
   val uiStateListFlow: UiState<List<Person>>
      by viewModel.uiStateListFlow.collectAsStateWithLifecycle(
         // Is the default value, so it can be omitted
         lifecycleOwner = LocalLifecycleOwner.current
      )

   when (uiStateListFlow) {
      UiState.Empty -> {
         logDebug(tag, "uiState->Empty")
      }

      UiState.Loading -> {
         logDebug(tag, "uiState->Loading")
      }

      is UiState.Success -> {
         val people = (uiStateListFlow as UiState.Success<List<Person>>).data
         logDebug(tag, "uiState->Success ${people.size}")
      }

      is UiState.Error -> {
         val message = (uiStateListFlow as UiState.Error).message
         logDebug(tag, "uiState->Error $message")
      }
   }

   /****************************************************************************
    * Consuming UiStateList<List<Person>>                                      *
    ****************************************************************************/
//   val uiStateList: UiState<List<Person>>
//      by viewModel.uiStateList.collectAsStateWithLifecycle()

   val snackbarHostState = remember { SnackbarHostState() }
   Scaffold(
      topBar = {
         TopAppBar(
            title = { Text(stringResource(R.string.people_list)) },
            navigationIcon = {
               val activity = LocalContext.current as Activity
               IconButton(
                  onClick = {
                     logDebug(tag, "Lateral Navigation: finish app")
                     // Finish the app
                     activity.finish()
                  }) {
                  Icon(imageVector = Icons.Default.Menu,
                     contentDescription = stringResource(R.string.back))
               }
            }
         )
      },
      floatingActionButton = {
         FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.tertiary,
            onClick = {
               // FAB clicked -> InputScreen initialized
               logDebug(tag, "Forward Navigation: FAB clicked")
               viewModel.clearState()
               // Navigate to PersonDetail and put PeopleList on the back stack
               navController.navigate(route = NavScreen.PersonInput.route)
            }
         ) {
            Icon(Icons.Default.Add, "Add a contact")
         }
      },
      snackbarHost = {
         SnackbarHost(hostState = snackbarHostState) { data ->
            Snackbar(
               snackbarData = data,
               actionOnNewLine = true
            )
         }
      }) { innerPadding ->

      Column(
         Modifier.padding(innerPadding.calculateTopPadding()),
         verticalArrangement = Arrangement.Center,
      ) {

         /*
      when (uiStateList) {

         UiState.Empty -> {
            logDebug(tag, "uiStateList->Empty")
         }

         UiState.Loading -> {
            logDebug(tag, "uiStateList->Loading")
         }

         is UiState.Success -> {
            val peopleFromUiState = (uiStateList as UiState.Success).data
            Divider()
            Text(
               modifier = Modifier.padding(vertical = 16.dp),
               text = "uiStateList->Success ${peopleFromUiState.size} ",
               style = MaterialTheme.typography.bodyLarge,
            )
            Divider()
         }

         is UiState.Error -> {
            val message = (uiStateList as UiState.Error).message
            logDebug(tag, "uiState.Error $message")
         }
      }

       */
      }
   }
}