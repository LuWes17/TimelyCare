package com.example.timelycare.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EmergencyContactRepository private constructor() {
    private val _contacts = MutableStateFlow<List<EmergencyContact>>(emptyList())
    val contacts: StateFlow<List<EmergencyContact>> = _contacts.asStateFlow()

    companion object {
        @Volatile
        private var INSTANCE: EmergencyContactRepository? = null

        fun getInstance(): EmergencyContactRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: EmergencyContactRepository().also { INSTANCE = it }
            }
        }
    }

    fun addContact(contact: EmergencyContact) {
        val currentContacts = _contacts.value.toMutableList()
        if (currentContacts.size < 3) {
            currentContacts.add(contact)
            _contacts.value = currentContacts
        }
    }

    fun updateContact(updatedContact: EmergencyContact) {
        val currentContacts = _contacts.value.toMutableList()
        val index = currentContacts.indexOfFirst { it.id == updatedContact.id }
        if (index != -1) {
            currentContacts[index] = updatedContact
            _contacts.value = currentContacts
        }
    }

    fun deleteContact(contactId: String) {
        val currentContacts = _contacts.value.toMutableList()
        currentContacts.removeIf { it.id == contactId }
        _contacts.value = currentContacts
    }

    fun canAddMoreContacts(): Boolean {
        return _contacts.value.size < 3
    }

    fun isPhoneNumberExists(phone: String, excludeId: String? = null): Boolean {
        return _contacts.value.any { contact ->
            contact.phone == phone && contact.id != excludeId
        }
    }
}