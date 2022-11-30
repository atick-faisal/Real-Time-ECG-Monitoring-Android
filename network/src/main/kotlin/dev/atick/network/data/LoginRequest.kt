package dev.atick.network.data

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email")
    val username: String,

    @SerializedName("password")
    val password: String
)
