package com.testing.testforjob.presentation.contacts

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.testing.testforjob.domain.contacts.usecase.GetAllContactsUseCase
import contacts.core.Contacts
import contacts.core.entities.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(private val getAllContactsUseCase: GetAllContactsUseCase) :
    ViewModel() {
    private val state = MutableStateFlow<ContactsFragmentState>(ContactsFragmentState.Init)
    val mState: StateFlow<ContactsFragmentState> get() = state


    private val contacts = MutableStateFlow<List<Contact>>(mutableListOf())
    val mContacts: StateFlow<List<Contact>> get() = contacts


    init {
        fetchAllContacts()
    }


    private fun setLoading() {
        state.value = ContactsFragmentState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = ContactsFragmentState.IsLoading(false)
    }

    private fun showError(message: String) {
        state.value = ContactsFragmentState.ShowError(message)
    }


    fun fetchAllContacts() {
        Log.wtf("fetchAllContactsVM", "getAllContacts")

        viewModelScope.launch {
            getAllContactsUseCase.invoke()
                .onStart {
                    setLoading()
                }
                .catch { exception ->
                    hideLoading()
                    showError(exception.message.toString())
                }
                .collect { result ->
                    hideLoading()
                    contacts.value = result
                }
        }
    }
}


sealed class ContactsFragmentState {
    object Init : ContactsFragmentState()
    data class IsLoading(val isLoading: Boolean) : ContactsFragmentState()
    data class ShowError(val message: String) : ContactsFragmentState()
}