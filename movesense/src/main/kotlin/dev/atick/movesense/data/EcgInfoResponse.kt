package dev.atick.movesense.data

import com.google.gson.annotations.SerializedName

data class EcgInfoResponse(
    @SerializedName("Content")
    val content: Content
)
