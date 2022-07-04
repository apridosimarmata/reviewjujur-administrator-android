package id.sireto.reviewjujuradministrator.models

import com.google.gson.annotations.SerializedName

data class ReviewResponse(
    @SerializedName("userUid")
    val userUid : String,

    @SerializedName("businessUid")
    val businessUid : String,

    @SerializedName("text")
    val text : String,

    @SerializedName("uid")
    val uid : String,

    @SerializedName("score")
    val score : Int,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("status")
    val status: String
)
