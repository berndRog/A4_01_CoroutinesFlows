package de.rogallab.mobile.domain

import de.rogallab.android.data.models.PersonDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import kotlin.coroutines.CoroutineContext

interface IPeopleRepository {
   fun selectAll1(
      dispatcher: CoroutineDispatcher,
      exceptionHandler: CoroutineExceptionHandler
   ): Flow<PersonDto>
   fun selectAll2(
      dispatcher: CoroutineDispatcher,
      exceptionHandler: CoroutineExceptionHandler
   ): Flow<MutableList<PersonDto>>
   suspend fun findById(id: UUID, coroutineContext: CoroutineContext): PersonDto?
   suspend fun add(personDto: PersonDto, coroutineContext: CoroutineContext)
   suspend fun addAll(peopleDto: List<PersonDto>, coroutineContext: CoroutineContext)
   suspend fun update(upPersonDto: PersonDto, coroutineContext: CoroutineContext)
   suspend fun remove(personDto: PersonDto, coroutineContext: CoroutineContext)
}