package de.rogallab.mobile.ui.people

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.rogallab.mobile.domain.IPeopleRepository

class PeopleViewModelFactory(
   private val _repository: IPeopleRepository
) : ViewModelProvider.Factory {

   override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(PeopleViewModel::class.java)) {
         return PeopleViewModel(_repository) as T
      }
      throw IllegalArgumentException(
         "Unknown ViewModel class: ${modelClass.simpleName}")
   }
}