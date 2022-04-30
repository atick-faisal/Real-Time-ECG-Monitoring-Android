package dev.atick.movesense.data

import com.google.gson.annotations.SerializedName

data class EcgResponse(
    @SerializedName("Body")
    val body: EcgBody
)

data class EcgBody(
    @SerializedName("Samples")
    val samples: List<Int>
)
