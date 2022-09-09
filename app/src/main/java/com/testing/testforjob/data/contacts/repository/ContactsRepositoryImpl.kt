package com.testing.testforjob.data.contacts.repository

import android.content.Context
import com.testing.testforjob.domain.contacts.ContactsRepository
import contacts.core.*
import contacts.core.entities.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ContactsRepositoryImpl @Inject constructor(private val context: Context) :
    ContactsRepository {
    override suspend fun getAllContacts(): Flow<List<Contact>> {
        return flow {
            val response = Contacts(context).query().where {
                (Contact.HasPhoneNumber equalTo true)
            }.find()
            emit(response)
        }
    }
}