package de.rogallab.mobile.ui.people

import NavScreen
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import de.rogallab.mobile.R
import de.rogallab.mobile.domain.UiState
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.domain.utilities.logInfo
import de.rogallab.mobile.ui.people.composables.InputNameMailPhone
import de.rogallab.mobile.ui.people.composables.checkInput
import showErrorMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonInputScreen(
   navController: NavController,
   viewModel: PeopleViewModel
) {
   val tag = "ok>PersonInputScreen  ."

   BackHandler(
      enabled = true,
      onBack = {
         logInfo(tag, "Back Navigation (Abort)")
         navController.popBackStack(
            route = NavScreen.PeopleList.route,
            inclusive = false
         )
      }
   )

   val context = LocalContext.current
   val snackbarHostState = remember { SnackbarHostState() }

   val uiStateFlow by viewModel.uiStateFlow.collectAsStateWithLifecycle()

   Scaffold(
      topBar = {
         TopAppBar(
            title = { Text(stringResource(R.string.person_input)) },
            navigationIcon = {
               IconButton(onClick = {
                  logInfo(tag, "Reverse Navigation (Up) viewModel.add()")
                  checkInput(context, viewModel)
                  if (uiStateFlow !is UiState.Error) {
                     viewModel.add()
                  }
                  if (uiStateFlow !is UiState.Error) {
                     navController.navigate(route = NavScreen.PeopleList.route) {
                        popUpTo(route = NavScreen.PeopleList.route) { inclusive = true }
                     }
                  }
               }) {
                  Icon(imageVector = Icons.Default.ArrowBack,
                     contentDescription = stringResource(R.string.back))
               }
            }
         )
      },
      snackbarHost = {
         SnackbarHost(hostState = snackbarHostState) { data ->
            Snackbar(
               snackbarData = data,
               actionOnNewLine = true
            )
         }
      }
   ) { innerPadding ->

      when (uiStateFlow) {
         UiState.Empty, UiState.Loading -> {
            logDebug(tag,"Empty ir Loading")
         } // nothing to do
         UiState.Empty, is UiState.Success -> {
            Column(
               modifier = Modifier
                  .padding(top = innerPadding.calculateTopPadding())
                  .padding(bottom = innerPadding.calculateBottomPadding())
                  .padding(horizontal = 8.dp)
                  .fillMaxWidth()
                  .verticalScroll(state = rememberScrollState())
            ) {
               InputNameMailPhone(
                  firstName = viewModel.firstName,                    // State ↓
                  onFirstNameChange = viewModel::onFirstNameChange,   // Event ↑
                  lastName = viewModel.lastName,                      // State ↓
                  onLastNameChange = viewModel::onLastNameChange,    // Event ↑
                  email = viewModel.email,                            // State ↓
                  onEmailChange = viewModel::onEmailChange,           // Event ↑
                  phone = viewModel.phone,                            // State ↓
                  onPhoneChange = viewModel::onPhoneChange            // Event ↑
               )
            }
         }

         is UiState.Error -> {
            val message = (uiStateFlow as UiState.Error).message

            LaunchedEffect(true ) {
               showErrorMessage(
                  snackbarHostState = snackbarHostState,
                  errorMessage = message,
                  actionLabel = null,
                  onErrorAction = { }
               )
            }
         }
      }
   }
}