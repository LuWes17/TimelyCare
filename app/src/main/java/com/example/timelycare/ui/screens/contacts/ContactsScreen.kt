package com.example.timelycare.ui.screens.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.timelycare.data.EmergencyContact
import com.example.timelycare.data.EmergencyContactRepository
import com.example.timelycare.data.PhonebookContact
import com.example.timelycare.ui.theme.*

@Composable
fun ContactsScreen() {
    val repository = remember { EmergencyContactRepository.getInstance() }
    val contacts by repository.contacts.collectAsStateWithLifecycle()

    var showPhonebookModal by remember { mutableStateOf(false) }
    var editingContact by remember { mutableStateOf<EmergencyContact?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Info section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TimelyCareBlue.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TimelyCareBlue.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Add contacts",
                    tint = TimelyCareBlue,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Add up to 3 emergency contacts",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TimelyCareBlue
                    )
                    Text(
                        text = "Manually add contacts or select from your phonebook",
                        fontSize = 14.sp,
                        color = TimelyCareBlue,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Contacts list or empty state
        if (contacts.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = TimelyCareWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No contacts added yet",
                        fontSize = 16.sp,
                        color = TimelyCareTextSecondary
                    )
                }
            }
        } else {
            contacts.forEach { contact ->
                EmergencyContactCard(
                    contact = contact,
                    onEdit = { editingContact = contact },
                    onDelete = { repository.deleteContact(contact.id) }
                )
            }
        }

        // Add contact form (only show if less than 3 contacts or editing)
        if (repository.canAddMoreContacts() || editingContact != null) {
            AddContactForm(
                onAddContact = { contact ->
                    if (editingContact != null) {
                        repository.updateContact(contact)
                        editingContact = null
                    } else {
                        repository.addContact(contact)
                    }
                },
                onSelectFromPhonebook = { showPhonebookModal = true },
                isPhoneNumberExists = { phone ->
                    repository.isPhoneNumberExists(phone, editingContact?.id)
                },
                editingContact = editingContact,
                onCancelEdit = { editingContact = null }
            )
        }
    }

    // Phonebook modal
    if (showPhonebookModal) {
        PhonebookSelectionModal(
            onContactSelected = { phonebookContact ->
                val emergencyContact = EmergencyContact(
                    name = phonebookContact.name,
                    phone = phonebookContact.phone,
                    countryCode = "+63"
                )

                if (!repository.isPhoneNumberExists(phonebookContact.phone)) {
                    repository.addContact(emergencyContact)
                }
                showPhonebookModal = false
            },
            onDismiss = { showPhonebookModal = false }
        )
    }
}