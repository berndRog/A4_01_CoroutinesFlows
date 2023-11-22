package de.rogallab.mobile.data.repositories

import de.rogallab.android.data.models.PersonDto
import de.rogallab.mobile.domain.IPeopleRepository
import de.rogallab.mobile.domain.utilities.logDebug
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.coroutines.CoroutineContext

class PeopleRepositoryImpl(
//   private val _dispatcher: CoroutineDispatcher = Dispatchers.IO
): IPeopleRepository {
            //12345678901234567890123
   val tag = "ok>PeopleRepositoryImpl"

   private val _peopleDto = mutableListOf<PersonDto>()
   private val _mutex = Mutex()

   // producer of a cold Flow<PersonDto>
   override fun selectAll1(
      dispatcher: CoroutineDispatcher,
      exceptionHandler: CoroutineExceptionHandler
   ): Flow<PersonDto> = flow {
      delay(1000L) // simulate a long running operation
      _peopleDto.sortBy { it.lastName }
      _peopleDto.forEach { personDto ->
         logDebug(tag, "selectAll1():Flow emit ${personDto.asString()}")
         emit(personDto)
      }
   }.flowOn(dispatcher+exceptionHandler)

   // producer of a cold Flow<PersonDto>
   override fun selectAll2(
      dispatcher: CoroutineDispatcher,
      exceptionHandler: CoroutineExceptionHandler
   ): Flow<MutableList<PersonDto>> = flow {
      delay(1000L) // simulate a long running operation
      _peopleDto.sortBy { it.lastName }
      logDebug(tag, "selectAll2():Flow emit ${_peopleDto.size}")
      emit(_peopleDto)
   }.flowOn(dispatcher+exceptionHandler)

   override suspend fun findById(id: UUID, coroutineContext: CoroutineContext): PersonDto? =
      withContext(coroutineContext) {
         delay(1000L) // simulate a long running operation
         logDebug(tag,"suspend findById()")
         return@withContext _peopleDto.firstOrNull { it.id == id }
      }

   override suspend fun add(personDto: PersonDto, coroutineContext: CoroutineContext): Unit =
      withContext(coroutineContext) {
         logDebug(tag,"suspend add()")
         delay(100L) // simulate a long running operation
         _mutex.withLock { _peopleDto.add(personDto) }
      }

   override suspend fun addAll(peopleDto: List<PersonDto>, coroutineContext: CoroutineContext): Unit =
      withContext(coroutineContext) {
         logDebug(tag,"suspend addAll()")
         delay(1000L) // simulate a long running operation
         _mutex.withLock { _peopleDto.addAll(peopleDto) }
      }

   override suspend fun update(upPersonDto: PersonDto, coroutineContext: CoroutineContext): Unit =
      withContext(coroutineContext) {
         logDebug(tag,"suspend remove()")
         delay(10L) // simulate a long running operation
         _mutex.withLock {
            val personDto = _peopleDto.firstOrNull { it.id == upPersonDto.id }
            personDto?.let{
               _peopleDto.remove(it)
               _peopleDto.add(upPersonDto)
            }
         }
      }

   override suspend fun remove(personDto: PersonDto, coroutineContext: CoroutineContext): Unit =
      withContext(coroutineContext) {
         logDebug(tag,"suspend remove()")
         delay(10L) // simulate a long running operation
         _mutex.withLock { _peopleDto.remove(personDto) }
      }
}