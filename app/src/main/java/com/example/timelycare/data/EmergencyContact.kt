package com.example.timelycare.data

import androidx.compose.runtime.Stable
import java.util.UUID

@Stable
data class EmergencyContact(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val phone: String,
    val countryCode: String = "+63"
) {
    val fullPhoneNumber: String
        get() = "$countryCode $phone"
}

data class CountryCode(
    val code: String,
    val name: String,
    val flag: String = ""
) {
    val displayText: String
        get() = "$name ($code)"
}

object CountryCodes {
    val PHILIPPINES = CountryCode("+63", "Philippines")
    val USA = CountryCode("+1", "United States")
    val SINGAPORE = CountryCode("+65", "Singapore")
    val MALAYSIA = CountryCode("+60", "Malaysia")
    val AUSTRALIA = CountryCode("+61", "Australia")

    val all = listOf(PHILIPPINES, USA, SINGAPORE, MALAYSIA, AUSTRALIA)
}

data class PhonebookContact(
    val name: String,
    val phone: String
)

object HardcodedPhonebook {
    val contacts = listOf(
        PhonebookContact("Juan Dela Cruz", "9123456789"),
        PhonebookContact("Maria Santos", "9123456790"),
        PhonebookContact("John Smith", "1234567890"),
        PhonebookContact("Alice Brown", "9876543210")
    )
}