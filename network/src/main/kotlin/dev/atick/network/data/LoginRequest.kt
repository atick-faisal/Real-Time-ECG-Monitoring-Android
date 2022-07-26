package dev.atick.network.data

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("UserID")
    val username: String,

    @SerializedName("Password")
    val password: String
)
