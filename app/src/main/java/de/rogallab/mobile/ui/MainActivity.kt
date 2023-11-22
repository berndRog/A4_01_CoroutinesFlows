package de.rogallab.mobile.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import de.rogallab.mobile.data.repositories.PeopleRepositoryImpl
import de.rogallab.mobile.domain.IPeopleRepository
import de.rogallab.mobile.ui.navigation.AppNavHost
import de.rogallab.mobile.ui.people.PeopleViewModel
import de.rogallab.mobile.ui.people.PeopleViewModelFactory
import de.rogallab.mobile.ui.theme.AppTheme

class MainActivity : BaseActivity(tag) {

   private val _repository: IPeopleRepository = PeopleRepositoryImpl()

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      // use insets to show to snackbar above ime keyboard
      WindowCompat.setDecorFitsSystemWindows(window, false)

      setContent {

//         val personViewModel: PersonViewModel = viewModel(
//            factory = PersonViewModelFactory(_repository)
//         )
         val peopleViewModel: PeopleViewModel = viewModel(
            factory = PeopleViewModelFactory(_repository)
         )

         AppTheme {
            Surface(modifier = Modifier
               .fillMaxSize()
               .safeDrawingPadding())
            {
               AppNavHost(peopleViewModel)
            }
         }
      }
   }

   companion object {
      //                       12345678901234567890123
      private const val tag = "ok>MainActivity       ."
   }
}


//
//@Composable
//fun Greeting(name: String) {
//   Text(text = "Hello $name!")
//}
//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//   B4_00_FlowTheme {
//      Greeting("Android")
//   }
//}