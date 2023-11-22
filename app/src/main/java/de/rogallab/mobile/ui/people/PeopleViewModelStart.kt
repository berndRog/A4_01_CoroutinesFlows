package de.rogallab.mobile.ui.people

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.rogallab.mobile.domain.IPeopleRepository
import de.rogallab.mobile.domain.UiState
import de.rogallab.mobile.domain.entities.Person
import de.rogallab.mobile.domain.mapping.toDomain
import de.rogallab.mobile.domain.mapping.toModel
import de.rogallab.mobile.domain.utilities.UUIDEmpty
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.domain.utilities.logError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

class PeopleViewModel (
   private val _repository: IPeopleRepository
) : ViewModel() {

   private var _id: UUID = UUID.randomUUID()

   // State = Observables (DataBinding)
   private var _firstName: String by mutableStateOf(value = "")
   val firstName
      get() = _firstName
   fun onFirstNameChange(value: String) {
      if(value != _firstName )  _firstName = value }

   private var _lastName: String by mutableStateOf(value = "")
   val lastName
      get() = _lastName
   fun onLastNameChange(value: String) {
      if(value != _lastName )  _lastName = value
   }

   private var _email: String? by mutableStateOf(value = null)
   val email
      get() = _email
   fun onEmailChange(value: String) {
      if(value != _email )  _email = value
   }

   private var _phone: String? by mutableStateOf(value = null)
   val phone
      get() = _phone
   fun onPhoneChange(value: String) {
      if(value != _phone )  _phone = value
   }

   private var _imagePath: String? by mutableStateOf(value = null)
   val imagePath
      get() = _imagePath
   fun onImagePathChange(value: String?) {
      if(value != _imagePath )  _imagePath = value
   }

   // State errorMessage
   private var _errorMessage: String? by mutableStateOf(value = null)
   val errorMessage: String?
      get() = _errorMessage
   fun onErrorMessage(value: String?, pErrorFrom: String?) {
      if(value != _errorMessage) _errorMessage = value
      errorFrom = pErrorFrom
   }
   // error handling
   fun onErrorAction() {
      logDebug(tag, "onErrorAction()")
      // toDo
   }
   var errorFrom: String? = null  // which screen has an error


   // Coroutine EceptionHandler
   private val _exceptionHandler = CoroutineExceptionHandler { _, exception ->
      exception.localizedMessage?.let {
         logError(tag, it)
         val uiState = UiState.Error(it)
         // toDo: Send UiState.Empty to PersonInput- or PersonDetailScreen
      } ?: run {
         exception.stackTrace.forEach {
            logError(tag, it.toString())
         }
      }
   }

   // Coroutine Dispatcher via parameter
   private val _dispatcher: CoroutineDispatcher = Dispatchers.IO

   // Coroutine Context
   private val _coroutineContext: CoroutineContext =
      SupervisorJob() + _dispatcher + _exceptionHandler

   // Coroutine Scope
   private val _coroutineScope: CoroutineScope =
      CoroutineScope(_coroutineContext)

   override fun onCleared() {
      // cancel all coroutines, when lifecycle of the viewmodel ends
      logDebug(tag,"Cancel all child coroutines")
      _coroutineContext.cancelChildren()
      _coroutineContext.cancel()
   }

   // mutableStateList with observer
   // var snapShotPeople: SnapshotStateList<Person> = mutableStateListOf<Person>()
   var people = mutableListOf<Person>()

   init {
      people = initialzePeople()

      val jobInit = viewModelScope.launch(_dispatcher+_exceptionHandler) {

//         // write person by person to the repository
//         val deferredList: MutableList<Deferred<Unit>> = mutableListOf()
//         people.forEach { person ->
//            val personDto = person.toModel()
//            val deferred: Deferred<Unit> = _coroutineScope.async {
//               return@async _repository.add(personDto, _coroutineContext)
//            }
//            deferredList.add(deferred)
//         }
//         // wait for all operations to be completed
//         var count = 0
//         deferredList.forEach { deferred ->
//            try {
//               deferred.await()
//               count++
//            }
//            catch (e: Exception) {
//               logError(tag, "Error during add() operation")
//               throw e
//            }
//         }
//         logDebug(tag, "==> $count people were written to the repository")

         // write a people list to repository
//         val peopleDto = people.toModel()
//         val deferred = _coroutineScope.async {
//            return@async _repository.addAll(peopleDto, _coroutineContext)
//         }
//         deferred.await()

         // or simplified and summarized
         val peopleDto = people.toModel()
         _coroutineScope.async {
            return@async _repository.addAll(peopleDto, _coroutineContext)
         }.await()
         logDebug(PeopleViewModel.tag, "==> ${people.size} people were written to the repository")

         // reading from repository person by person
//         val deferredRead: Deferred<MutableList<Person>> = _coroutineScope.async {
//            var peopleCollected = mutableListOf<Person>()
//            // collect people as a stream of person objects
//            _repository.selectAll1(_dispatcher, _exceptionHandler).collect { personDto ->
//               logDebug(tag, "${personDto.asString()} collected")
//               peopleCollected.add(personDto.toDomain())
//            }
//            return@async peopleCollected
//         }
//         val peopleRead = deferredRead.await()

         // reading from repository person by person: simplified and summarized
//         val peopleRead: MutableList<Person> = _coroutineScope.async {
//            var peopleCollected = mutableListOf<Person>()
//            // collect people as a stream of person objects
//            _repository.selectAll1(_coroutineContext).collect { personDto ->
//               logDebug(tag, "${personDto.asString()} collected")
//               peopleCollected.add(personDto.toDomain())
//            }
//            return@async peopleCollected
//         }.await()
//

         // read from repository a list<person>
         val peopleRead: MutableList<Person> = _coroutineScope.async {
            var peopleCollected = listOf<Person>()
            _repository.selectAll2(_dispatcher,_exceptionHandler).collect { peopleDto ->
               logDebug(tag, "${peopleDto.size} collected")
               peopleCollected = peopleDto.toDomain()
            }
            return@async peopleCollected
         }.await().toMutableList()
         logDebug(tag, "==> ${peopleRead.size} people were read from the repository")

      }
      waitUntilJobIsCompleted(jobInit, "<--- Init")
      startStateListFlow()
   }
   /***************************************************************************
    * StateFlow                                                               *
    ***************************************************************************/
   // StateFlow for Input&Detail Screens
   private var _uiStateFlow: MutableStateFlow<UiState<Person>> =
      MutableStateFlow(value = UiState.Success(Person()))
   val uiStateFlow: StateFlow<UiState<Person>>
      get() = _uiStateFlow
   fun onUiStateFlowChange(uiStateInDe: UiState<Person>) {
      _uiStateFlow.value = uiStateInDe
   }
   // StateFlow for List Screens
   private var _uiStateListFlow: MutableStateFlow<UiState<List<Person>>> =
      MutableStateFlow(value = UiState.Empty)
   val uiStateListFlow: StateFlow<UiState<List<Person>>>
      get() = _uiStateListFlow
   fun onUiStateListFlowChange(uiState: UiState<List<Person>>) {
      _uiStateListFlow.value = uiState
   }

   fun startStateListFlow() {
      val job1 = viewModelScope.launch(_exceptionHandler) {
         delay(5000)

        val uiState1 =   UiState.Loading //_listOfUiStates.random()
        logDebug(tag,"Send UiState.Loading")
        _uiStateListFlow.value = uiState1
         delay(5000)

         val uiState2 =   UiState.Success(people)
         logDebug(tag,"Send UiState.Success")
         _uiStateListFlow.value = uiState2
         delay(5000)

         val uiState3 =   UiState.Error("Es ist ein Fehler aufgetreten")
         logDebug(tag,"Send UiState.Error")
         _uiStateListFlow.value = uiState3
         delay(5000)

      }
      waitUntilJobIsCompleted(job1, "<--- finished")
   }

   fun readById(id: UUID) {
      try {
         _coroutineScope.launch {
            logDebug(tag, "readById()")
            val personDto = _coroutineScope.async {
               return@async _repository.findById(id,_coroutineContext)
            }.await()
            personDto?.let{
               val person = it.toDomain()
               // person values are set as observable states in the viewmodel
               setStateFromPerson(person)
               logDebug(tag,"Success ${person.asString()}")
               val uiState = UiState.Empty
               // toDo: Send UiState.Empty to PersonInput- or PersonDetailScreen
            } ?: run {
               throw Exception("Person with given id not found")
            }
         }
      }
      catch (e: Exception) {
         val message = e.localizedMessage ?: e.stackTraceToString()
         logError(tag,message)
         val uiStateFlow =  UiState.Error(message)
         // toDo: Send UiState.Error to PersonInput- or PersonDetailScreen
      }
   }

   private fun waitUntilJobIsCompleted(job: Job?, text: String = "") {
      job?.let{
         _coroutineScope.launch(_coroutineContext) {
            it.join()
            logDebug(tag, "$text isActive:${it.isActive} " +
               "isCompleted:${it.isCompleted} isCanceled:${it.isCancelled}")
         }
      }
   }

   fun add() {
      logDebug(tag, "add()")
      try {
         val personDto = getPersonFromState().toModel()
         _coroutineScope.launch {
            _coroutineScope.async {
               _repository.add(personDto, _coroutineContext)
            }.await()
            val uiStateFlow = UiState.Empty
            // toDo: Send UiState.Empty to PersonInput- or PersonDetailScreen
         }
      }
      catch (e: Exception) {
         val message = e.localizedMessage ?: e.stackTraceToString()
         logError(tag,message)
         val uiState =  UiState.Error(message)
         // toDo: Send UiState.Error to PersonInput- or PersonDetailScreen
      }
   }

   fun getPersonFromState(): Person =
      Person(_firstName, _lastName, _email, _phone, _imagePath, _id)

   fun setStateFromPerson(person: Person?) {
      _firstName = person?.firstName ?: ""
      _lastName  = person?.lastName ?: ""
      _email     = person?.email
      _phone     = person?.phone
      _imagePath = person?.imagePath
      _id        = person?.id ?: UUIDEmpty
   }

   fun clearState() {
      logDebug(tag, "clearState")
      _firstName = ""
      _lastName  = ""
      _email     = null
      _phone     = null
      _imagePath = null
      _id        = UUID.randomUUID()
   }

   companion object {
      const val tag = "ok>PeopleViewModel    ."

      private val firstNames = mutableListOf(
         "Arne", "Berta", "Cord", "Dagmar", "Ernst", "Frieda", "Günter", "Hanna",
         "Ingo", "Johanna", "Klaus", "Luise", "Martin", "Norbert", "Paula", "Otto",
         "Rosi", "Stefan", "Therese", "Uwe", "Veronika", "Walter", "Zwantje")
      private val lastNames = mutableListOf(
         "Arndt", "Bauer", "Conrad", "Diehl", "Engel", "Fischer", "Grabe", "Hoffmann",
         "Imhof", "Jung", "Klein", "Lang", "Meier", "Neumann", "Peters", "Opitz",
         "Richter", "Schmidt", "Thormann", "Ulrich", "Vogel", "Wagner", "Zander")

      private val emailProvider = mutableListOf("gmail.com", "icloud.com", "outlook.com", "yahoo.com",
         "t-online.de", "gmx.de", "freenet.de", "mailbox.org")

      fun initialzePeople(): MutableList<Person> {
         val people = mutableListOf<Person>()
         for (index in 0..<firstNames.size) {
            val firstName = firstNames[index]
            val lastName = lastNames[index]
            val email =
               "${firstName.lowercase(Locale.getDefault())}." +
                  "${lastName.lowercase(Locale.getDefault())}@" +
                  "${emailProvider.random()}"
            val phone =
               "0${Random.nextInt(1234, 9999)} " +
                  "${Random.nextInt(100, 999)}-" +
                  "${Random.nextInt(10, 9999)}"

            val person = Person(firstName, lastName, email, phone)
            people.add(person)
         }
         val person = Person("Erika", "Mustermann", id =
         UUID.fromString("10000000-0000-0000-0000-000000000000"))
         return people
      }
   }
}
