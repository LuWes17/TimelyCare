package com.example.timelycare.ui.screens.contacts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import com.example.timelycare.R
import com.example.timelycare.data.EmergencyContact
import com.example.timelycare.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactForm(
    onAddContact: (EmergencyContact) -> Unit,
    onSelectFromPhonebook: () -> Unit,
    isPhoneNumberExists: (String) -> Boolean,
    editingContact: EmergencyContact? = null,
    onCancelEdit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var name by remember(editingContact) { mutableStateOf(editingContact?.name ?: "") }
    var phone by remember(editingContact) {
        mutableStateOf(editingContact?.phone ?: "")
    }
    var relationship by remember(editingContact) {
        mutableStateOf(editingContact?.relationship ?: "")
    }

    var nameError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var relationshipError by remember { mutableStateOf("") }
    val context = LocalContext.current

    fun validateForm(): Boolean {
        var isValid = true

        nameError = when {
            name.isBlank() -> {
                isValid = false
                context.getString(R.string.contact_name_required)
            }
            name.length < 2 -> {
                isValid = false
                context.getString(R.string.name_min_length)
            }
            else -> ""
        }

        phoneError = when {
            phone.isBlank() -> {
                isValid = false
                context.getString(R.string.phone_number_required)
            }
            phone.length != 10 -> {
                isValid = false
                "Phone number must be exactly 10 digits"
            }
            !phone.all { it.isDigit() } -> {
                isValid = false
                context.getString(R.string.phone_digits_only)
            }
            isPhoneNumberExists(phone) -> {
                isValid = false
                context.getString(R.string.phone_number_exists)
            }
            else -> ""
        }

        relationshipError = when {
            relationship.isBlank() -> {
                isValid = false
                "Relationship is required"
            }
            relationship.length < 2 -> {
                isValid = false
                "Relationship must be at least 2 characters"
            }
            else -> ""
        }

        return isValid
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TimelyCareWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (editingContact != null) stringResource(R.string.edit_contact) else stringResource(R.string.add_contact),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TimelyCareTextPrimary
                )

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(R.string.contact_form),
                    tint = TimelyCareBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Contact name field
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = ""
                },
                label = { Text(stringResource(R.string.contact_name)) },
                isError = nameError.isNotEmpty(),
                supportingText = if (nameError.isNotEmpty()) {
                    { Text(nameError, color = MaterialTheme.colorScheme.error) }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TimelyCareBlue,
                    unfocusedBorderColor = TimelyCareGray,
                    focusedTextColor = TimelyCareTextPrimary,
                    unfocusedTextColor = TimelyCareTextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )


            // Phone number field
            OutlinedTextField(
                value = phone,
                onValueChange = { newValue ->
                    val digitsOnly = newValue.filter { it.isDigit() }.take(10)
                    phone = digitsOnly
                    phoneError = ""
                },
                label = { Text("Phone Number") },
                placeholder = { Text("9123456789") },
                leadingIcon = {
                    Text(
                        text = "+63 ",
                        color = TimelyCareTextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = phoneError.isNotEmpty(),
                supportingText = if (phoneError.isNotEmpty()) {
                    { Text(phoneError, color = MaterialTheme.colorScheme.error) }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TimelyCareBlue,
                    unfocusedBorderColor = TimelyCareGray,
                    focusedTextColor = TimelyCareTextPrimary,
                    unfocusedTextColor = TimelyCareTextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Relationship field
            OutlinedTextField(
                value = relationship,
                onValueChange = {
                    relationship = it
                    relationshipError = ""
                },
                label = { Text("Relationship") },
                placeholder = { Text("e.g., Father, Mother, Friend") },
                isError = relationshipError.isNotEmpty(),
                supportingText = if (relationshipError.isNotEmpty()) {
                    { Text(relationshipError, color = MaterialTheme.colorScheme.error) }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TimelyCareBlue,
                    unfocusedBorderColor = TimelyCareGray,
                    focusedTextColor = TimelyCareTextPrimary,
                    unfocusedTextColor = TimelyCareTextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Action buttons
            if (editingContact != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancelEdit,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(stringResource(R.string.cancel))
                    }

                    Button(
                        onClick = {
                            if (validateForm()) {
                                onAddContact(
                                    editingContact.copy(
                                        name = name.trim(),
                                        phone = phone,
                                        relationship = relationship.trim()
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TimelyCareBlue
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.update_contact),
                            color = TimelyCareWhite,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (validateForm()) {
                                onAddContact(
                                    EmergencyContact(
                                        name = name.trim(),
                                        phone = phone,
                                        relationship = relationship.trim()
                                    )
                                )
                                name = ""
                                phone = ""
                                relationship = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TimelyCareBlue
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.add_contact),
                            color = TimelyCareWhite,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(4.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = onSelectFromPhonebook,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.select_from_phonebook),
                            color = TimelyCareBlue,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}

