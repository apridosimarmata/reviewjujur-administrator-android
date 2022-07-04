package id.sireto.reviewjujuradministrator.models

import com.google.gson.annotations.SerializedName

data class SuspendUserRequest(
    @field:SerializedName("uid")
    val uid: String
)
