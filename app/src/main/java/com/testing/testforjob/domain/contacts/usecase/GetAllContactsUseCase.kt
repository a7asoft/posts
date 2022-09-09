package com.testing.testforjob.domain.contacts.usecase

import com.testing.testforjob.domain.contacts.ContactsRepository
import contacts.core.entities.Contact
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllContactsUseCase @Inject constructor(private val contactsRepository: ContactsRepository) {
    suspend fun invoke(): Flow<List<Contact>> {
        return contactsRepository.getAllContacts()
    }
}