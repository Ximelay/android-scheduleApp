package com.example.irkpo_management.db

import com.google.gson.annotations.SerializedName

data class UserConsent(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("user_id")
    val userId: String? = null, // UUID в виде строки

    @SerializedName("consent_timestamp")
    val consentTimestamp: String? = null,

    @SerializedName("ip_address")
    val ipAddress: String = "",

    @SerializedName("device_id")
    val deviceId: String,

    @SerializedName("consent_version")
    val consentVersion: Long? = null
)
data class CreateUserConsentRequest(
    @SerializedName("user_id")
    val userId: String? = null,

    @SerializedName("ip_address")
    val ipAddress: String = "",

    @SerializedName("device_id")
    val deviceId: String,

    @SerializedName("consent_version")
    val consentVersion: Long = 1
)
