package de.rogallab.mobile.domain

sealed class UiState<out T>() {
   // var value1 = 42
   // var value2 = "Test"
   data object Empty                          : UiState<Nothing>()   // Singleton
   data object Loading                        : UiState<Nothing>()  // Singleton
   data class  Success<out T>(val data: T)    : UiState<T>()
   data class  Error     (val message: String): UiState<Nothing>()
}




