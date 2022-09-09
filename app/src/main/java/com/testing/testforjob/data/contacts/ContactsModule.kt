package com.testing.testforjob.data.contacts

import android.content.Context
import com.testing.testforjob.data.contacts.repository.ContactsRepositoryImpl
import com.testing.testforjob.domain.contacts.ContactsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ContactsModule {
    @Singleton
    @Provides
    fun provideContactsRepository(@ApplicationContext context: Context) : ContactsRepository {
        return ContactsRepositoryImpl(context)
    }
}