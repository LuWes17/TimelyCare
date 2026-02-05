package com.example.timelycare.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.android.gms.wearable.Wearable
import com.example.timelycare.service.MedicationDataService

class EmergencyContactRepository private constructor(
    private val context: Context?
) {
    private val _contacts = MutableStateFlow<List<EmergencyContact>>(emptyList())
    val contacts: StateFlow<List<EmergencyContact>> = _contacts.asStateFlow()

    private val medicationDataService = MedicationDataService()
    private val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        @Volatile
        private var INSTANCE: EmergencyContactRepository? = null

        fun getInstance(context: Context? = null): EmergencyContactRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: EmergencyContactRepository(context).also { INSTANCE = it }
            }
        }
    }

    fun addContact(contact: EmergencyContact) {
        val currentContacts = _contacts.value.toMutableList()
        if (currentContacts.size < 3) {
            currentContacts.add(contact)
            _contacts.value = currentContacts
            syncToWatch()
        }
    }

    fun updateContact(updatedContact: EmergencyContact) {
        val currentContacts = _contacts.value.toMutableList()
        val index = currentContacts.indexOfFirst { it.id == updatedContact.id }
        if (index != -1) {
            currentContacts[index] = updatedContact
            _contacts.value = currentContacts
            syncToWatch()
        }
    }

    fun deleteContact(contactId: String) {
        val currentContacts = _contacts.value.toMutableList()
        currentContacts.removeIf { it.id == contactId }
        _contacts.value = currentContacts
        syncToWatch()
    }

    fun canAddMoreContacts(): Boolean {
        return _contacts.value.size < 3
    }

    fun isPhoneNumberExists(phone: String, excludeId: String? = null): Boolean {
        return _contacts.value.any { contact ->
            contact.phone == phone && contact.id != excludeId
        }
    }

    private fun syncToWatch() {
        context?.let { ctx ->
            scope.launch {
                try {
                    val dataClient = Wearable.getDataClient(ctx)

                    medicationDataService.sendEmergencyContactsToWatch(
                        dataClient,
                        _contacts.value
                    )

                    Log.d(
                        "EmergencyContactSync",
                        "Synced ${_contacts.value.size} emergency contacts to watch"
                    )
                } catch (e: Exception) {
                    Log.w(
                        "EmergencyContactSync",
                        "Wear OS sync not available: ${e.message}"
                    )
                }
            }
        }
    }
}