package com.example.quetek.models

enum class Program(val displayName: String) {
    NONE("SELECT"),
    BS_COMPUTER_SCIENCE("BS Computer Science"),
    BS_INFORMATION_TECHNOLOGY("BS Information Technology"),
    BS_CIVIL_ENGINEERING("BS Civil Engineering"),
    BS_NURSING("BS Nursing"),
    BS_MEDICAL_TECHNOLOGY("BS Medical Technology");

    companion object {
        fun fromDisplayName(display: String): Program {
            return entries.firstOrNull { it.displayName == display } ?: NONE
        }

        fun getDisplayNames(): List<String> {
            return entries.map { it.displayName }
        }
    }
}
