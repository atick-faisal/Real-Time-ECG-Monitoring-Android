package dev.atick.movesense.data

import com.google.gson.annotations.SerializedName

data class HrResponse(
    @SerializedName("Body")
    val body: HrBody
)

data class HrBody(
    @SerializedName("average")
    val average: Float,

    @SerializedName("rrData")
    val rrData: List<Int>
)
