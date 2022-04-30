package dev.atick.movesense.data

import com.google.gson.annotations.SerializedName

data class HrResponse(
    @SerializedName("Body")
    val body: Body
)

data class Body(
    @SerializedName("average")
    val average: Float,

    @SerializedName("rrData")
    val rrData: List<Int>
)
