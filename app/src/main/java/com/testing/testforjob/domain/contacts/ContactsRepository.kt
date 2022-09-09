package com.testing.testforjob.domain.contacts

import contacts.core.entities.Contact
import kotlinx.coroutines.flow.Flow

interface ContactsRepository {
    suspend fun getAllContacts() : Flow<List<Contact>>
}